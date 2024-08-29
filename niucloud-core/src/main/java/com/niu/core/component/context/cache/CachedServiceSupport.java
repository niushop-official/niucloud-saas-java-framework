package com.niu.core.component.context.cache;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.core.common.utils.cache.CacheTag;

/**
 * 以组合的方式实现对缓存的支持
 */
public class CachedServiceSupport {

    /**
     * @return
     */
    public Cached getCacheOperator() {
        return CacheFactory.getCacheOperator();
    }

    /**
     * 根据ID查询缓存
     *
     * @param tClass
     * @param id
     * @return
     */
    public <T> T findUseCacheById(ServiceImpl serviceImpl, Class<T> tClass, Integer id) {
        T target = (T) getCacheOperator().tag(CacheTag.key(tClass)).get(String.valueOf(id));
        if (target == null) {
            target = (T) serviceImpl.getById(id);
            if (target != null) {
                getCacheOperator().tag(CacheTag.key(tClass)).put(String.valueOf(id), target);
            }
        }
        return target;
    }

    /**
     * 根据ID刷新缓存
     *
     * @param tClass
     * @param id
     * @return
     */
    public <T> T refreshCacheById(ServiceImpl serviceImpl, Class<T> tClass, Integer id) {
        T target = (T) serviceImpl.getById(id);
        if (target != null) {
            getCacheOperator().tag(CacheTag.key(tClass)).put(String.valueOf(id), target);
        }
        return target;
    }

    /**
     * @param tClass
     * @param ids
     */
    public <T> void refreshCacheByIds(ServiceImpl serviceImpl, Class<T> tClass, Integer... ids) {
        for (Integer id : ids) {
            this.refreshCacheById(serviceImpl, tClass, id);
        }
    }

    /**
     * 根据ID移除缓存
     *
     * @param tClass
     * @param id
     * @return
     */
    public <T> T removeCacheById(Class<T> tClass, Integer id) {
        T target = (T) getCacheOperator().tag(CacheTag.key(tClass)).get(String.valueOf(id));
        if (target != null) {
            getCacheOperator().tag(CacheTag.key(tClass)).remove(String.valueOf(id));
        }
        return target;
    }

    /**
     * 移除缓存
     *
     * @param tClass
     * @param ids
     */
    public <T> void removeCacheByIds(Class<T> tClass, Integer... ids) {
        for (Integer id : ids) {
            this.removeCacheById(tClass, id);
        }
    }

    /**
     * 清除缓存
     *
     * @param tClass
     */
    public <T> void clearCache(Class<T> tClass) {
        getCacheOperator().tag(CacheTag.key(tClass)).clear();
    }

}
