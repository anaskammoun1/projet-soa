package com.soutenance.security.auth;

import com.soutenance.security.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

    private String accessToken;

    private String refreshToken;

    @Builder.Default
    private String tokenType = "Bearer";

    private long expiresInMs;

    private long refreshExpiresInMs;

    private String username;

    private Role role;
}
