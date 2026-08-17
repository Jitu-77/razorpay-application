package com.jitu.razorpay_application.common_lib.idempotency;
import com.jitu.razorpay_application.common_lib.ratelimit.*;
import com.jitu.razorpay_application.common_lib.context.MerchantContext;
import com.jitu.razorpay_application.common_lib.ratelimit.FixedWindowRateLimiter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerExceptionResolver;

@AutoConfiguration
public class SharedResilienceAutoConfiguration {
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }


    @Bean
    public IdempotencyStore idempotencyStore(StringRedisTemplate stringRedisTemplate) {
        return new RedisIdempotencyStore(stringRedisTemplate);
    }


    @Bean
    public IdempotencyFilter idempotencyFilter(MerchantContext merchantContext,
                                               IdempotencyStore idempotencyStore,
                                               @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver) {
        return new IdempotencyFilter(merchantContext, idempotencyStore, handlerExceptionResolver);
    }

    @Bean
    @ConditionalOnProperty(name = "app.rate-limit.method", havingValue = "fixed")
    public RateLimiter fixedWindowRateLimiter(StringRedisTemplate stringRedisTemplate) {

        return new FixedWindowRateLimiter(stringRedisTemplate);

    }

}
