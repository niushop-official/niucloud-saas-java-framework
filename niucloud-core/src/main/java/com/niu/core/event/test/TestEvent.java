package com.niu.core.event.test;

import com.niu.core.common.component.context.event.Event;
import lombok.Data;

import java.nio.file.Path;

@Data
public class TestEvent extends Event {
    private Path url;
}
