package com.niu.core.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
            .group("管理端全部接口")  // 分组名称
            .pathsToMatch("/adminapi/**")
            .build();
    }

    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
            .group("客户端全部接口")  // 分组名称
            .pathsToMatch("/api/**")
            .build();
    }
}
