package com.niu.core.service.admin.captcha.cache;

import com.anji.captcha.service.CaptchaCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

@Slf4j
public class CaptchaCacheServiceRedisImpl implements CaptchaCacheService {

    private String name;

    private StringRedisTemplate stringRedisTemplate;

    public CaptchaCacheServiceRedisImpl(String name, StringRedisTemplate stringRedisTemplate) {
        this.name = name;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public String type() {
        return this.name;
    }

    @Override
    public void set(String key, String value, long expiresInSeconds) {
        stringRedisTemplate.opsForValue().set(key, value, expiresInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public boolean exists(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    @Override
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    @Override
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public void setStringRedisTemplate(StringRedisTemplate redisTemplate) {
        this.stringRedisTemplate = redisTemplate;
    }

    /*@Override
    public Long increment(String key, long val) {
        return stringRedisTemplate.opsForValue().increment(key,val);
    }*/

}
