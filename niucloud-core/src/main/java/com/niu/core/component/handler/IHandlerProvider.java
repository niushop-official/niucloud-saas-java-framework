package com.niu.core.component.handler;

public interface IHandlerProvider<M, R> {

    /**
     * 调用
     *
     * @param bean
     * @return
     */
    public R handle(M bean);

}
