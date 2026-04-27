package com.soutenance.security.refresh;

public record IssuedRefreshToken(String rawToken, long expiresInMs) {
}
