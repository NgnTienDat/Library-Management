package com.ou.oulib.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TokenBlacklistService {

    RedisTemplate<String, String> redisTemplate;

    private static final String PREFIX = "blacklist:";

    public void blacklist(String jti, long ttlSeconds) {
        redisTemplate.opsForValue()
                .set(PREFIX + jti, "1", ttlSeconds, TimeUnit.SECONDS);
    }

    public boolean isBlacklisted(String jti) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey(PREFIX + jti)
        );
    }
}
