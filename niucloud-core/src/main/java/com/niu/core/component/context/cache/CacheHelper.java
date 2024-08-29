package com.niu.core.component.context.cache;

public interface CacheHelper<T> {

    /**
     * @param uniqueKey
     * @return
     */
    public Object execute(String uniqueKey);

}
