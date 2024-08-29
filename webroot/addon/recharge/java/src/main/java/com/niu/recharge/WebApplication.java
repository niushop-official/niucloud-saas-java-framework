package com.niu.recharge;

import com.niu.core.common.annotation.SpringModuleApplication;
import org.springframework.boot.SpringApplication;

@SpringModuleApplication("recharge")
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
