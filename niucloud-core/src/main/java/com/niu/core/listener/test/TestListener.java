package com.niu.core.listener.test;

import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.event.test.TestEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@EventListen("core")
@Component
public class TestListener extends AbstractListener {

    public void handleEvent(TestEvent event) throws IOException {
        if ("test".equals(event.getName())){
            Files.createFile(Paths.get(event.getUrl().toUri()));
        }
    }
}
