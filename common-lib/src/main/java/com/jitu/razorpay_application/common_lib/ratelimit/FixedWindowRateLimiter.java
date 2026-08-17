package com.jitu.razorpay_application.common_lib.ratelimit;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

//@Component
//@ConditionalOnProperty(name = "app.rate-limit.method", havingValue = "fixed")
@RequiredArgsConstructor
public class FixedWindowRateLimiter implements RateLimiter{

    private final StringRedisTemplate redis;


    @Override
    public RateLimitResult check(String key, int maxRequestAllowed, long windowSeconds) {
        String redisKey = "ratelimit:fixed:"+key;
        Long count = redis.opsForValue().increment(redisKey);
        if (count == null) return RateLimitResult.allowed(maxRequestAllowed); // redis unavailable
        //here in fixed window we are assuming that when the first req comes so the window opens
        //hence fix the widow here -- set the expiry
        if (count == 1) {
            redis.expire(redisKey, Duration.ofSeconds(windowSeconds));
        }
        //normal scenario
        if (count > maxRequestAllowed) {
            Long ttl = redis.getExpire(redisKey, TimeUnit.SECONDS);
            int retryAfter = (ttl != null & ttl > 0) ? ttl.intValue(): (int) windowSeconds;
            return RateLimitResult.denied(retryAfter);
        }
        return RateLimitResult.allowed((int) (maxRequestAllowed - count));
    }
}
