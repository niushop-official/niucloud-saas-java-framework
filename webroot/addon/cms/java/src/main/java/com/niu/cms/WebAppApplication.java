package com.niu.cms;

import com.niu.core.common.annotation.SpringModuleApplication;
import org.springframework.boot.SpringApplication;

@SpringModuleApplication("cms")
public class WebAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebAppApplication.class, args);
    }
}
