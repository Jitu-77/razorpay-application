//package com.jitu.razorpay_application.merchant_service.cache;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.stereotype.Component;
//import tools.jackson.databind.ObjectMapper;
//
//import java.time.Duration;
//import java.util.Optional;
//@Component
//@Slf4j
//@RequiredArgsConstructor
//public class RedisApiKeyCache implements ApiKeyCache{
//
//    private static final String PREFIX = "apikey:";
//    private static final Duration TTL = Duration.ofMinutes(5);
//
//    private final StringRedisTemplate stringRedisTemplate;
//    //ObjectMapper from jackson
//    // covert a json to any kind of class Object
//    private final ObjectMapper objectMapper;
//
//
//    @Override
//    public Optional<ApiKeyCacheEntry> get(String keyId) {
//        try {
//            String json = stringRedisTemplate.opsForValue().get(PREFIX+keyId);
//            if (json == null) {
//                return Optional.empty();
//            }
//            return Optional.of(objectMapper.readValue(json, ApiKeyCacheEntry.class));
//        } catch (Exception e) {
//            // we will not throw any exception
//            // as it will break the flow
//            // here we are communicating with the redis DB
//            log.warn("ApiKey cache read filed, keyId: {}", keyId);
//            return Optional.empty();
//        }
//    }
//
//    @Override
//    public void put(String keyId, ApiKeyCacheEntry entry) {
//        try {
//            stringRedisTemplate.opsForValue().set(PREFIX+keyId,
//                    objectMapper.writeValueAsString(entry), // converts this entry to JSON
//                    TTL);
//        } catch (Exception e) {
//            log.warn("ApiKey cache put filed, keyId: {}", keyId);
//        }
//    }
//
//    @Override
//    public void evict(String keyId) {
//        stringRedisTemplate.delete(PREFIX+keyId);
//    }
//}
