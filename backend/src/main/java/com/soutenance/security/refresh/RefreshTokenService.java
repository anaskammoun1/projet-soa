package com.soutenance.security.refresh;

import com.soutenance.security.user.ApplicationUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationMs;

    public IssuedRefreshToken issueFor(ApplicationUser user) {
        String rawToken = generateRawToken();
        RefreshToken refreshToken = RefreshToken.builder()
                .tokenHash(hash(rawToken))
                .user(user)
                .expiresAt(LocalDateTime.now().plusNanos(refreshExpirationMs * 1_000_000))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);
        return new IssuedRefreshToken(rawToken, refreshExpirationMs);
    }

    public ApplicationUser consume(String rawToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(hash(rawToken))
                .orElseThrow(() -> new BadCredentialsException("Refresh token invalide"));

        if (refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("Refresh token invalide");
        }

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getUser();
    }

    private String generateRawToken() {
        byte[] bytes = new byte[64];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(rawToken.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to hash refresh token", ex);
        }
    }
}
