package com.niu.core.config.dataization;

import com.niu.core.component.context.cache.Cached;
import com.niu.core.component.context.cache.CacheFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
@EnableCaching
@Slf4j
public class CachedConfig {

    @Value("${spring.cache.type}")
    private static String cacheType = "redis";

    @Bean
    public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
        EhCacheManagerFactoryBean cacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        ClassPathResource classPathResource = new ClassPathResource("core/config/ehcache.xml");
        cacheManagerFactoryBean.setConfigLocation(classPathResource);
        cacheManagerFactoryBean.setShared(true);
        return cacheManagerFactoryBean;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        cacheConfiguration = cacheConfiguration.serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        return RedisCacheManager.builder(connectionFactory).cacheDefaults(cacheConfiguration).build();
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(connectionFactory);
        return stringRedisTemplate;
    }

    @Bean(name = "cached")
    public Cached cached(EhCacheManagerFactoryBean bean, RedisTemplate<String, Object> redisTemplate) {
        log.warn("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        log.warn("系统默认配置的缓存为： " + cacheType);
        log.warn("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        if ("redis".equalsIgnoreCase(cacheType)) {
            return CacheFactory.buildWithRedis(redisTemplate);
        }
        EhCacheCacheManager ehCacheCacheManager = new EhCacheCacheManager(bean.getObject());
        return CacheFactory.buildWithEhcache(ehCacheCacheManager.getCacheManager());
    }

    @Bean(name = "ehcacheOperator")
    public Cached ehcacheOperator(EhCacheManagerFactoryBean bean) {
        EhCacheCacheManager ehCacheCacheManager = new EhCacheCacheManager(bean.getObject());
        return CacheFactory.buildWithEhcache(ehCacheCacheManager.getCacheManager());
    }

    @Bean(name = "redisCacheOperator")
    public Cached redisCacheOperator(RedisTemplate<String, Object> redisTemplate) {
        return CacheFactory.buildWithRedis(redisTemplate);
    }

}
