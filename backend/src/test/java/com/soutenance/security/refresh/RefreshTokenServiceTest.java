package com.soutenance.security.refresh;

import com.soutenance.security.Role;
import com.soutenance.security.user.ApplicationUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        refreshTokenService = new RefreshTokenService(refreshTokenRepository);
        ReflectionTestUtils.setField(refreshTokenService, "refreshExpirationMs", 604800000L);
    }

    @Test
    void issueAndConsumeRotatesRefreshToken() {
        ApplicationUser user = user();
        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IssuedRefreshToken issued = refreshTokenService.issueFor(user);
        org.mockito.Mockito.verify(refreshTokenRepository).save(captor.capture());
        RefreshToken stored = captor.getValue();
        when(refreshTokenRepository.findByTokenHash(stored.getTokenHash())).thenReturn(Optional.of(stored));

        ApplicationUser consumedUser = refreshTokenService.consume(issued.rawToken());

        assertThat(consumedUser).isSameAs(user);
        assertThat(stored.isRevoked()).isTrue();
        assertThat(issued.expiresInMs()).isEqualTo(604800000L);
    }

    @Test
    void consumeRejectsExpiredToken() {
        RefreshToken expired = RefreshToken.builder()
                .tokenHash("hash")
                .user(user())
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.of(expired));

        assertThatThrownBy(() -> refreshTokenService.consume("raw"))
                .isInstanceOf(BadCredentialsException.class);
    }

    private ApplicationUser user() {
        return ApplicationUser.builder()
                .username("teacher")
                .email("teacher@example.local")
                .role(Role.ENSEIGNANT)
                .passwordHash("hash")
                .enabled(true)
                .build();
    }
}
