package com.niu.core.component.context.cache;

import cn.hutool.core.util.StrUtil;
import com.niu.core.component.context.SpringContext;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Cached {

    // 放入缓存
    public void put(String key, Object value);

    // 放入缓存，包含超时时间
    public void put(String key, Object value, long timeoutSeconds);

    // 获取缓存
    public Object get(String key);

    // 移除缓存
    public void remove(String... key);

    // 获取所有的Key
    public Collection<String> getAllKeys();

    // 获取所有的Value
    public Collection<Object> getAllValues();

    // 获取所有的Key和Value
    public Map<String, Object> getAllKeyValues();

    // 根据模式批量删除
    public void removeBatch(String pattern);

    /**
     * @param group
     * @return
     */
    default Tag tag(String group) {
        return new Tag(group);

    }

    public class Tag {

        private Cached cached;

        private String group;

        public Tag(String group) {
            this(group, SpringContext.bean("redisCacheOperator"));
        }

        public Tag(String group, Cached cached) {
            this.cached = cached;
            this.group = group;
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

        // 移除
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
    }

    /**
     * 指定Key值缓存
     *
     * @param key
     * @param cacheHelper
     * @param <T>
     * @return
     */
    default <T> T cache(String key, CacheHelper<T> cacheHelper) {
        return cache(true, "", key, cacheHelper);
    }

    /**
     * 指定Key值缓存
     *
     * @param cache
     * @param key
     * @param cacheHelper
     * @param <T>
     * @return
     */
    default <T> T cache(boolean cache, String key, CacheHelper<T> cacheHelper) {
        return cache(cache, "", key, cacheHelper);
    }

    /**
     * 指定组名称和Key值缓存
     *
     * @param group
     * @param key
     * @param cacheHelper
     * @param <T>
     * @return
     */
    default <T> T cache(String group, String key, CacheHelper<T> cacheHelper) {
        return cache(true, group, key, cacheHelper);
    }

    /**
     * @param group
     * @param key
     * @param cacheHelper
     * @param <T>
     * @return
     */
    default <T> T cache(boolean cache, String group, String key, CacheHelper<T> cacheHelper) {
        Cached cached = SpringContext.bean("redisCacheOperator");
        T target = null;
        if (cache) {
            if (StrUtil.isEmpty(group)) {
                target = (T) cached.get(key);
            } else {
                target = (T) cached.tag(group).get(key);
            }
            if (target == null) {
                target = (T) cacheHelper.execute(key);
                if (target != null) {
                    if (StrUtil.isEmpty(group)) {
                        cached.put(key, target);
                    } else {
                        cached.tag(group).put(key, target);
                    }
                }
            }
        } else {
            target = (T) cacheHelper.execute(key);
        }
        return target;
    }

    /**
     * @param paramList
     * @param cacheHelper
     * @param <T>
     * @return
     */
    default <T> List<T> remember(final List<Object> paramList, CacheHelper<T> cacheHelper) {
        return this.remember(true, paramList, cacheHelper);
    }

    /**
     * @param paramList
     * @param cacheHelper
     * @param <T>
     * @return
     */
    default <T> List<T> remember(boolean cache, final List<Object> paramList, CacheHelper<T> cacheHelper) {
        return this.remember("", paramList, cacheHelper);
    }

    /**
     * @param group
     * @param paramList
     * @param cacheHelper
     * @param <T>
     * @return
     */
    default <T> List<T> remember(String group, final List<Object> paramList, CacheHelper<T> cacheHelper) {
        return this.remember(true, group, paramList, cacheHelper);
    }

    /**
     * @param group
     * @param paramList
     * @param cacheHelper
     * @param <T>
     * @return
     */
    default <T> List<T> remember(boolean cache, String group, final List<Object> paramList, CacheHelper<T> cacheHelper) {
        return this.remember(group, paramList, SpringContext.bean("redisCacheOperator"), cacheHelper);
    }

    /**
     * @param group
     * @param paramList
     * @param cached
     * @param cacheHelper
     * @param <T>
     * @return
     */
    default <T> List<T> remember(String group, final List<Object> paramList, Cached cached, CacheHelper<T> cacheHelper) {
        return this.remember(true, group, paramList, cached, cacheHelper);
    }

    /**
     * @param cacheHelper
     * @param <T>
     * @return
     */
    default <T> List<T> remember(boolean cache, String group, final List<Object> paramList, Cached cached, CacheHelper<T> cacheHelper) {
        String uniqueKey = CacheUtils.computeUniqueKey(paramList);
        List<T> resultList = null;
        if (cache) {
            if (StrUtil.isEmpty(group)) {
                resultList = (List<T>) cached.get(uniqueKey);
            } else {
                resultList = (List<T>) cached.tag(group).get(uniqueKey);
            }
            if (resultList == null) {
                resultList = (List<T>) cacheHelper.execute(uniqueKey);
                if (resultList != null && resultList.size() > 0) {
                    if (StrUtil.isEmpty(group)) {
                        cached.put(uniqueKey, resultList);
                    } else {
                        cached.tag(group).put(uniqueKey, resultList);
                    }
                }
            }
        } else {
            resultList = (List<T>) cacheHelper.execute(uniqueKey);
        }
        return resultList;
    }

    /**
     * @param paramList
     * @param cacheHelper
     * @param <T>
     * @return
     */
    default <T> T rememberObject(final List<Object> paramList, CacheHelper<T> cacheHelper) {
        return rememberObject(true, "", paramList, SpringContext.bean("redisCacheOperator"), cacheHelper);
    }

    /**
     * @param cache
     * @param paramList
     * @param cacheHelper
     * @param <T>
     * @return
     */
    default <T> T rememberObject(boolean cache, final List<Object> paramList, CacheHelper<T> cacheHelper) {
        return rememberObject(cache, "", paramList, SpringContext.bean("redisCacheOperator"), cacheHelper);
    }

    /**
     * @param group
     * @param paramList
     * @param cacheHelper
     * @param <T>
     * @return
     */
    default <T> T rememberObject(String group, final List<Object> paramList, CacheHelper<T> cacheHelper) {
        return rememberObject(true, group, paramList, SpringContext.bean("redisCacheOperator"), cacheHelper);
    }

    /**
     * @param cache
     * @param group
     * @param paramList
     * @param cacheHelper
     * @param <T>
     * @return
     */
    default <T> T rememberObject(boolean cache, String group, final List<Object> paramList, CacheHelper<T> cacheHelper) {
        return rememberObject(cache, group, paramList, SpringContext.bean("redisCacheOperator"), cacheHelper);
    }

    /**
     * @param group
     * @param paramList
     * @param cached
     * @param cacheHelper
     * @param <T>
     * @return
     */
    default <T> T rememberObject(String group, final List<Object> paramList, Cached cached, CacheHelper<T> cacheHelper) {
        return rememberObject(true, group, paramList, cached, cacheHelper);
    }

    /**
     * @param cacheHelper
     * @param <T>
     * @return
     */
    default <T> T rememberObject(boolean cache, String group, final List<Object> paramList, Cached cached, CacheHelper<T> cacheHelper) {
        String uniqueKey = CacheUtils.computeUniqueKey(paramList);
        T result = null;
        if (cache) {
            if (StrUtil.isEmpty(group)) {
                result = (T) cached.get(uniqueKey);
            } else {
                result = (T) cached.tag(group).get(uniqueKey);
            }
            if (result == null) {
                result = (T) cacheHelper.execute(uniqueKey);
                if (result != null) {
                    if (StrUtil.isEmpty(group)) {
                        cached.put(uniqueKey, result);
                    } else {
                        cached.tag(group).put(uniqueKey, result);
                    }
                }
            }
        } else {
            result = (T) cacheHelper.execute(uniqueKey);
        }
        return result;
    }

}
