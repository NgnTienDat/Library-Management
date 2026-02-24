package com.ou.oulib.security;

import com.ou.oulib.service.TokenBlacklistService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;

@Component
@RequiredArgsConstructor
public class CustomJwtDecoder implements JwtDecoder {

    @Value("${auth.signer-key}")
    private String signerKey;

    private final TokenBlacklistService blacklistService;

    private NimbusJwtDecoder delegate;

    @PostConstruct
    public void init() {
        SecretKeySpec secretKeySpec =
                new SecretKeySpec(signerKey.getBytes(), "HmacSHA512");

        delegate = NimbusJwtDecoder
                .withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    @Override
    public Jwt decode(String token) throws JwtException {

        Jwt jwt = delegate.decode(token);

        String jti = jwt.getId();

        if (blacklistService.isBlacklisted(jti)) {
            throw new JwtException("Token blacklisted");
        }

        return jwt;
    }
}