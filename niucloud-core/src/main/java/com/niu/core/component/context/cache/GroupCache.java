package com.niu.core.component.context.cache;

import cn.hutool.core.util.StrUtil;
import com.niu.core.component.context.SpringContext;

public final class GroupCache {

    private String group;

    private Cached cached;

    public GroupCache(String group, Cached cached) {
        this.group = group;
        this.cached = cached;
    }

    // 放入缓存
    public void put(String key, Object value) {
        if (StrUtil.isNotEmpty(this.group)) {
            this.cached.put(this.group + ":" + key, value);
        } else {
            this.cached.put(key, value);
        }
    }

    // 放入缓存，包含超时时间
    public void put(String key, Object value, long timeoutSeconds) {
        if (StrUtil.isNotEmpty(this.group)) {
            this.cached.put(this.group + ":" + key, value, timeoutSeconds);
        } else {
            this.cached.put(key, value, timeoutSeconds);
        }

    }

    // 获取缓存
    public Object get(String key) {
        if (StrUtil.isNotEmpty(this.group)) {
            return this.cached.get(this.group + ":" + key);
        } else {
            return this.cached.get(key);
        }
    }

    public void remove(String key) {
        if (StrUtil.isNotEmpty(this.group)) {
            this.cached.remove(this.group + ":" + key);
        } else {
            this.cached.remove(key);
        }
    }

    // 移除缓存
    public void clear() {
        if (StrUtil.isNotEmpty(this.group)) {
            this.cached.removeBatch(this.group + ":*");
        }
    }

    public static GroupCache tag(String group) {
        Cached cached = SpringContext.bean("redisCacheOperator");
        return tag(group, cached);
    }

    public static GroupCache tag(String group, Cached cached) {
        return new GroupCache(group, cached);
    }

    /**
     * 例子
     */
    public static void test() {
        // 方法一：
        GroupCache.tag("user_group").get("key");
        GroupCache.tag("user_group").remove("key");
        GroupCache.tag("user_group").put("", "");
        GroupCache.tag("user_group").clear();
        // 方法二：
        Cached cached = SpringContext.bean("redisCacheOperator");
        cached.get("key");
        cached.tag("user_group").get("key");
        cached.tag("user_group").put("", "");
        cached.tag("user_group").clear();
        cached.tag("user_group").remove("key");
        //cacheOperator.get("user_group", "key");
        //cacheOperator.remove("user_group", "key");
        //cacheOperator.put("user_group", "key", "value");
        // cacheOperator.clear("user_group");
        //   cache::tag("user_group").get("key");
//            cache::tag("user_group").put("key");
//            cache::tag("user_group").clear();
    }

}
