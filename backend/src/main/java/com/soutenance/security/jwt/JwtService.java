package com.soutenance.security.jwt;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soutenance.security.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class JwtService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtService.class);
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();

    private final String secret;
    private final long expirationMs;
    private final ObjectMapper objectMapper;

    public JwtService(
            @Value("${app.jwt.secret:}") String secret,
            @Value("${app.jwt.expiration-ms:3600000}") long expirationMs,
            ObjectMapper objectMapper) {
        this.secret = StringUtils.hasText(secret) ? secret : generateEphemeralSecret();
        this.expirationMs = expirationMs;
        this.objectMapper = objectMapper;
    }

    public String generateToken(String username, Role role) {
        Instant now = Instant.now();

        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", username);
        payload.put("role", role.name());
        payload.put("iat", now.getEpochSecond());
        payload.put("exp", now.plusMillis(expirationMs).getEpochSecond());

        String unsignedToken = encodeJson(header) + "." + encodeJson(payload);
        return unsignedToken + "." + sign(unsignedToken);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername()) && !isExpired(token);
    }

    public String extractUsername(String token) {
        Object subject = claims(token).get("sub");
        if (!(subject instanceof String username) || !StringUtils.hasText(username)) {
            throw new JwtAuthenticationException("JWT subject is missing");
        }
        return username;
    }

    public Role extractRole(String token) {
        Object role = claims(token).get("role");
        if (!(role instanceof String roleName)) {
            throw new JwtAuthenticationException("JWT role is missing");
        }
        return Role.valueOf(roleName);
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    private boolean isExpired(String token) {
        Object exp = claims(token).get("exp");
        if (!(exp instanceof Number number)) {
            throw new JwtAuthenticationException("JWT expiration is missing");
        }
        long expiresAt = number.longValue();
        return Instant.now().getEpochSecond() >= expiresAt;
    }

    private Map<String, Object> claims(String token) {
        String[] parts = splitAndVerify(token);
        try {
            byte[] decoded = BASE64_URL_DECODER.decode(parts[1]);
            return objectMapper.readValue(decoded, new TypeReference<>() {});
        } catch (IllegalArgumentException ex) {
            throw new JwtAuthenticationException("Malformed JWT payload", ex);
        } catch (Exception ex) {
            throw new JwtAuthenticationException("Unable to read JWT claims", ex);
        }
    }

    private String[] splitAndVerify(String token) {
        if (!StringUtils.hasText(token)) {
            throw new JwtAuthenticationException("JWT is missing");
        }

        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new JwtAuthenticationException("Malformed JWT");
        }

        String unsignedToken = parts[0] + "." + parts[1];
        String expectedSignature = sign(unsignedToken);
        if (!MessageDigest.isEqual(
                expectedSignature.getBytes(StandardCharsets.UTF_8),
                parts[2].getBytes(StandardCharsets.UTF_8))) {
            throw new JwtAuthenticationException("Invalid JWT signature");
        }
        return parts;
    }

    private String encodeJson(Map<String, Object> value) {
        try {
            return BASE64_URL_ENCODER.encodeToString(objectMapper.writeValueAsBytes(value));
        } catch (Exception ex) {
            throw new JwtAuthenticationException("Unable to encode JWT", ex);
        }
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            return BASE64_URL_ENCODER.encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new JwtAuthenticationException("Unable to sign JWT", ex);
        }
    }

    private String generateEphemeralSecret() {
        byte[] bytes = new byte[48];
        new SecureRandom().nextBytes(bytes);
        LOGGER.warn("JWT_SECRET is not configured; using an ephemeral in-memory JWT secret for this process.");
        return Base64.getEncoder().encodeToString(bytes);
    }
}
