package com.rsmp.redditclone.auth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationToken {
    private String token;
    private String refreshToken;
    private Instant expiresAt;
    private String username;
}
