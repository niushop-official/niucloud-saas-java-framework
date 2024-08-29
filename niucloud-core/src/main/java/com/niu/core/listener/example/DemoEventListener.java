package com.niu.core.listener.example;

import com.niu.core.common.annotation.EventCallback;
import com.niu.core.event.example.DemoEventDefiner;
import org.springframework.stereotype.Component;

/**
 * 一对多的事件监听，且有返回值
 */
@EventCallback("core")
@Component
public class DemoEventListener extends DemoEventDefiner {

    @Override
    public DemoEventResult handleCallback(DemoEvent demoEvent) {
        DemoEventResult demoEventResult = new DemoEventResult();
        System.out.println("--------------------------------------------------------------------------->");
        demoEventResult.setName("name...........................");
        demoEventResult.setMessage("DemoEventListener");
        System.out.println("--------------------------------------------------------------------------->");
        return demoEventResult;
    }


}
