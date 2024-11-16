package com.niu.core.common.component.handler.impl;

import com.niu.core.common.component.handler.AbstractHandlerProvider;
import com.niu.core.common.component.handler.annotation.HandlerProvider;
import com.niu.core.common.component.handler.IHandlerProvider;
import com.niu.core.common.component.handler.model.ModelObject;
import com.niu.core.common.component.handler.model.ModelObjectResult;
import org.springframework.stereotype.Component;

@Component
@HandlerProvider("core")
public class Example2HandlerProviderImpl extends AbstractHandlerProvider implements IHandlerProvider<ModelObject, ModelObjectResult> {

    // 这里是为参考案例而提供的

    @Override
    public ModelObjectResult handle(ModelObject bean) {
        ModelObjectResult modelObjectResult = new ModelObjectResult();
        modelObjectResult.setMsg("消息2");
        return modelObjectResult;
    }

}
