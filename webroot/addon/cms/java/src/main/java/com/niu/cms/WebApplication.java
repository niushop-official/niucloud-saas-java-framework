package com.niu.cms;

import com.niu.core.common.annotation.SpringModuleApplication;
import org.springframework.boot.SpringApplication;

@SpringModuleApplication("cms")
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
