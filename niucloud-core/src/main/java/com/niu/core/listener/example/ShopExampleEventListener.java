package com.niu.core.listener.example;

import com.niu.core.common.annotation.EventCallback;
import com.niu.core.event.example.ExampleEventDefiner;
import org.springframework.stereotype.Component;

/**
 * 一对多的事件监听，且有返回值
 */
@EventCallback("shop")
@Component
public class ShopExampleEventListener extends ExampleEventDefiner {

    @Override
    public ExampleEventResult handleCallback(ExampleEvent exampleEvent) {
        ExampleEventResult exampleEventResult = new ExampleEventResult();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        exampleEventResult.setName("name...........................");
        exampleEventResult.setMessage("ShopExampleEventListener");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        return exampleEventResult;
    }

}
