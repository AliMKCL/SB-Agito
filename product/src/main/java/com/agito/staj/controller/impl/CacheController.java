package com.agito.staj.controller.impl;

import com.agito.staj.controller.ICacheController;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@Validated
public class CacheController implements ICacheController {

    private final StringRedisTemplate stringRedisTemplate;

    public CacheController(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public ResponseEntity<Set<String>> getCacheKeys() {
        Set<String> keys = stringRedisTemplate.keys("products::*");
        return ResponseEntity.ok(keys);
    }

    @Override
    public ResponseEntity<String> getCacheValue(String key) {
        String value = stringRedisTemplate.opsForValue().get(key);
        if (value == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Key not found in cache");
        }
        return ResponseEntity.ok(value);
    }

    @Override
    public ResponseEntity<String> deleteCacheKey(String key) {
        Boolean deleted = stringRedisTemplate.delete(key);
        if (deleted.equals(Boolean.TRUE)) {
            return ResponseEntity.ok("Key deleted successfully");
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Key not found in cache");
        }
    }
}
