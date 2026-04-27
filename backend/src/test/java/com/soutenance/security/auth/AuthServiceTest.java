package com.soutenance.security.auth;

import com.soutenance.security.Role;
import com.soutenance.security.audit.AuditService;
import com.soutenance.security.jwt.JwtService;
import com.soutenance.security.refresh.IssuedRefreshToken;
import com.soutenance.security.refresh.RefreshTokenService;
import com.soutenance.security.user.ApplicationUser;
import com.soutenance.security.user.ApplicationUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private ApplicationUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginReturnsJwtWhenCredentialsAreValid() {
        LoginRequest request = request("admin", "secret");
        ApplicationUser user = ApplicationUser.builder()
                .username("admin")
                .email("admin@example.local")
                .passwordHash("hashed")
                .role(Role.ADMIN)
                .enabled(true)
                .build();

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "hashed")).thenReturn(true);
        when(jwtService.generateToken("admin", Role.ADMIN)).thenReturn("jwt-token");
        when(jwtService.getExpirationMs()).thenReturn(3600000L);
        when(refreshTokenService.issueFor(user)).thenReturn(new IssuedRefreshToken("refresh-token", 604800000L));

        AuthResponse response = authService.login(request);

        assertThat(response.getAccessToken()).isEqualTo("jwt-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getUsername()).isEqualTo("admin");
        assertThat(response.getRole()).isEqualTo(Role.ADMIN);
        assertThat(response.getExpiresInMs()).isEqualTo(3600000L);
        assertThat(response.getRefreshExpiresInMs()).isEqualTo(604800000L);
        verify(jwtService).generateToken("admin", Role.ADMIN);
        verify(auditService).log("AUTH_LOGIN_SUCCESS", "admin", Role.ADMIN, true, "Login succeeded");
    }

    @Test
    void loginRejectsUnknownUser() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request("missing", "secret")))
                .isInstanceOf(BadCredentialsException.class);
        verify(auditService).log("AUTH_LOGIN_FAILURE", "missing", null, false, "Invalid credentials");
    }

    @Test
    void loginRejectsInvalidPassword() {
        ApplicationUser user = ApplicationUser.builder()
                .username("admin")
                .passwordHash("hashed")
                .role(Role.ADMIN)
                .enabled(true)
                .build();

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request("admin", "wrong")))
                .isInstanceOf(BadCredentialsException.class);
        verify(auditService).log("AUTH_LOGIN_FAILURE", "admin", Role.ADMIN, false, "Invalid credentials");
    }

    @Test
    void refreshRotatesRefreshTokenAndIssuesNewAccessToken() {
        ApplicationUser user = ApplicationUser.builder()
                .username("teacher")
                .passwordHash("hashed")
                .role(Role.ENSEIGNANT)
                .enabled(true)
                .build();
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("old-refresh-token");

        when(refreshTokenService.consume("old-refresh-token")).thenReturn(user);
        when(jwtService.generateToken("teacher", Role.ENSEIGNANT)).thenReturn("new-access-token");
        when(jwtService.getExpirationMs()).thenReturn(900000L);
        when(refreshTokenService.issueFor(user)).thenReturn(new IssuedRefreshToken("new-refresh-token", 604800000L));

        AuthResponse response = authService.refresh(request);

        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");
        assertThat(response.getExpiresInMs()).isEqualTo(900000L);
        verify(auditService).log("AUTH_REFRESH_SUCCESS", "teacher", Role.ENSEIGNANT, true, "Refresh token rotated");
    }

    private LoginRequest request(String username, String password) {
        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);
        return request;
    }
}
