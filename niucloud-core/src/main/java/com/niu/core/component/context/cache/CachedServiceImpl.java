package com.niu.core.component.context.cache;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.core.common.utils.cache.CacheTag;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 以继承的方式实现对缓存的支持
 *
 * @param <M>
 * @param <T>
 */
public class CachedServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

    @Autowired
    protected Cached cached;

    /**
     * @return
     */
    public Cached getCacheOperator() {
        return cached;
    }

    /**
     * 根据ID查询缓存
     *
     * @param tClass
     * @param id
     * @return
     */
    public T findUseCacheById(Class<T> tClass, Integer id) {
        T target = (T) cached.tag(CacheTag.key(tClass)).get(String.valueOf(id));
        if (target == null) {
            target = super.getById(id);
            if (target != null) {
                cached.tag(CacheTag.key(tClass)).put(String.valueOf(id), target);
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
    public T refreshCacheById(Class<T> tClass, Integer id) {
        T target = super.getById(id);
        if (target != null) {
            cached.tag(CacheTag.key(tClass)).put(String.valueOf(id), target);
        }
        return target;
    }

    /**
     * @param tClass
     * @param ids
     */
    public void refreshCacheByIds(Class<T> tClass, Integer... ids) {
        for (Integer id : ids) {
            this.refreshCacheById(tClass, id);
        }
    }

    /**
     * 根据ID移除缓存
     *
     * @param tClass
     * @param id
     * @return
     */
    public T removeCacheById(Class<T> tClass, Integer id) {
        T target = (T) cached.tag(CacheTag.key(tClass)).get(String.valueOf(id));
        if (target != null) {
            cached.tag(CacheTag.key(tClass)).remove(String.valueOf(id));
        }
        return target;
    }

    /**
     * 移除缓存
     *
     * @param tClass
     * @param ids
     */
    public void removeCacheByIds(Class<T> tClass, Integer... ids) {
        for (Integer id : ids) {
            this.removeCacheById(tClass, id);
        }
    }

    /**
     * 清除缓存
     *
     * @param tClass
     */
    public void clearCache(Class<T> tClass) {
        cached.tag(CacheTag.key(tClass)).clear();
    }

}
