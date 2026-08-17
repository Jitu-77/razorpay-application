package com.jitu.razorpay_application.common_lib.ratelimit;

public interface RateLimiter {

    RateLimitResult check(String key, int maxRequestAllowed, long windowSeconds);
}
