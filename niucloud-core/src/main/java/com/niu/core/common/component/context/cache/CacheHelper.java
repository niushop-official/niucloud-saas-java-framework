package com.niu.core.common.component.context.cache;

public interface CacheHelper<T> {

    /**
     * @param uniqueKey
     * @return
     */
    public Object execute(String uniqueKey);

}
