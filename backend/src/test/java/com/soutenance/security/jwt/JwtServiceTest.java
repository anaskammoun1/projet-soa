package com.soutenance.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soutenance.security.Role;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRET = "test_secret_with_more_than_32_characters";

    @Test
    void validTokenExposesClaimsAndPassesValidation() {
        JwtService jwtService = service(3600000);
        String token = jwtService.generateToken("admin", Role.ADMIN);
        UserDetails user = User.withUsername("admin").password("n/a").authorities("ROLE_ADMIN").build();

        assertThat(jwtService.extractUsername(token)).isEqualTo("admin");
        assertThat(jwtService.extractRole(token)).isEqualTo(Role.ADMIN);
        assertThat(jwtService.isTokenValid(token, user)).isTrue();
    }

    @Test
    void expiredTokenFailsValidation() {
        JwtService jwtService = service(-1000);
        String token = jwtService.generateToken("admin", Role.ADMIN);
        UserDetails user = User.withUsername("admin").password("n/a").authorities("ROLE_ADMIN").build();

        assertThat(jwtService.isTokenValid(token, user)).isFalse();
    }

    @Test
    void malformedTokenIsRejected() {
        JwtService jwtService = service(3600000);

        assertThatThrownBy(() -> jwtService.extractUsername("not-a-jwt"))
                .isInstanceOf(JwtAuthenticationException.class)
                .hasMessageContaining("Malformed");
    }

    @Test
    void tokenWithInvalidSignatureIsRejected() {
        JwtService jwtService = service(3600000);
        JwtService otherSigner = new JwtService("other_secret_with_more_than_32_characters", 3600000, new ObjectMapper());
        String token = otherSigner.generateToken("admin", Role.ADMIN);

        assertThatThrownBy(() -> jwtService.extractUsername(token))
                .isInstanceOf(JwtAuthenticationException.class)
                .hasMessageContaining("signature");
    }

    private JwtService service(long expirationMs) {
        return new JwtService(SECRET, expirationMs, new ObjectMapper());
    }
}
