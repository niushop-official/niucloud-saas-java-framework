package com.niu.core.common.component.context.cache;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * redis实现
 */
public class RedisCache implements Cached {

    /**
     * 所有缓存Key的前缀
     */
    private static final String CACHE_KEY_PREFIX = "cache:";

    private RedisTemplate<String, Object> redisTemplate;

    public RedisCache(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void put(String key, Object value) {
        redisTemplate.boundValueOps(CACHE_KEY_PREFIX + key).set(value);
    }

    @Override
    public void put(String key, Object value, long timeoutSeconds) {
        redisTemplate.boundValueOps(CACHE_KEY_PREFIX + key).set(value, timeoutSeconds, TimeUnit.SECONDS);
    }

    @Override
    public Object get(String key) {
        return redisTemplate.boundValueOps(CACHE_KEY_PREFIX + key).get();
    }

    @Override
    public void remove(String... key) {
        ArrayList<String> keys = CollectionUtil.toList(key);
        List<String> withPrefixKeys = keys.stream().map(i -> CACHE_KEY_PREFIX + i).collect(Collectors.toList());
        redisTemplate.delete(withPrefixKeys);
    }

    @Override
    public Collection<String> getAllKeys() {
        Set<String> keys = redisTemplate.keys(CACHE_KEY_PREFIX + "*");
        if (keys != null) {
            // 去掉缓存key的common prefix前缀
            return keys.stream().map(key -> StrUtil.removePrefix(key, CACHE_KEY_PREFIX)).collect(Collectors.toSet());
        } else {
            return CollectionUtil.newHashSet();
        }
    }

    @Override
    public Collection<Object> getAllValues() {
        Set<String> keys = redisTemplate.keys(CACHE_KEY_PREFIX + "*");
        if (keys != null) {
            return redisTemplate.opsForValue().multiGet(keys);
        } else {
            return CollectionUtil.newArrayList();
        }
    }

    @Override
    public Map<String, Object> getAllKeyValues() {
        Collection<String> allKeys = this.getAllKeys();
        HashMap<String, Object> results = MapUtil.newHashMap();
        for (String key : allKeys) {
            results.put(key, this.get(key));
        }
        return results;
    }

    @Override
    public void removeBatch(String pattern) {
        Set<String> keys = redisTemplate.keys(CACHE_KEY_PREFIX + pattern);
        if (keys != null) {
            redisTemplate.delete(keys);
        }
    }

    @Override
    public Integer atomicDecrement(String key, Integer reduceAmount) {
        if (reduceAmount == null || reduceAmount <= 0) {
            return -1;
        }

        String fullKey = CACHE_KEY_PREFIX + key;

        return redisTemplate.execute(new SessionCallback<Integer>() {
            @Override
            @SuppressWarnings("unchecked")
            public Integer execute(RedisOperations operations) throws DataAccessException {
                // 监听key
                operations.watch(fullKey);

                // 获取当前值
                Object currentObj = operations.opsForValue().get(fullKey);
                if (currentObj == null) {
                    operations.unwatch();
                    return -1; // key不存在
                }

                int current;
                try {
                    current = ((Number) currentObj).intValue();
                } catch (Exception e) {
                    operations.unwatch();
                    return -1; // 值类型错误
                }

                if (current < reduceAmount) {
                    operations.unwatch();
                    return -2; // 库存不足
                }

                try {
                    // 开始事务
                    operations.multi();

                    // 扣减操作
                    operations.opsForValue().decrement(fullKey, reduceAmount);

                    // 执行事务
                    List<Object> results = operations.exec();

                    if (results == null || results.isEmpty()) {
                        // 事务执行失败（可能是被其他客户端修改了）
                        return -1;
                    }

                    // 获取扣减后的值
                    Object result = results.get(0);
                    return ((Number) result).intValue();

                } catch (Exception e) {
                    operations.discard();
                    return -1;
                }
            }
        });
    }

    @Override
    public Integer atomicDecrement(String group, String key, Integer reduceAmount) {
        String fullKey = group + ":" + key;
        return atomicDecrement(fullKey, reduceAmount);
    }

}
