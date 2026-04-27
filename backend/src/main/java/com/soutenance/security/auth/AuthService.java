package com.soutenance.security.auth;

import com.soutenance.security.jwt.JwtService;
import com.soutenance.security.audit.AuditService;
import com.soutenance.security.refresh.IssuedRefreshToken;
import com.soutenance.security.refresh.RefreshTokenService;
import com.soutenance.security.user.ApplicationUser;
import com.soutenance.security.user.ApplicationUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ApplicationUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuditService auditService;

    public AuthResponse login(LoginRequest request) {
        ApplicationUser user = userRepository.findByUsername(request.getUsername()).orElse(null);

        if (user == null || !user.isEnabled() || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            auditService.log("AUTH_LOGIN_FAILURE", request.getUsername(), user != null ? user.getRole() : null, false, "Invalid credentials");
            throw new BadCredentialsException("Identifiants invalides");
        }

        AuthResponse response = issueTokens(user);
        auditService.log("AUTH_LOGIN_SUCCESS", user.getUsername(), user.getRole(), true, "Login succeeded");
        return response;
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        ApplicationUser user = refreshTokenService.consume(request.getRefreshToken());
        if (!user.isEnabled()) {
            auditService.log("AUTH_REFRESH_FAILURE", user.getUsername(), user.getRole(), false, "Disabled user");
            throw new BadCredentialsException("Refresh token invalide");
        }

        AuthResponse response = issueTokens(user);
        auditService.log("AUTH_REFRESH_SUCCESS", user.getUsername(), user.getRole(), true, "Refresh token rotated");
        return response;
    }

    private AuthResponse issueTokens(ApplicationUser user) {
        String token = jwtService.generateToken(user.getUsername(), user.getRole());
        IssuedRefreshToken refreshToken = refreshTokenService.issueFor(user);
        return AuthResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken.rawToken())
                .expiresInMs(jwtService.getExpirationMs())
                .refreshExpiresInMs(refreshToken.expiresInMs())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}
