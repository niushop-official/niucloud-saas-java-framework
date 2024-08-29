package com.niu.app;

import com.niu.app.common.context.SpringBeanNameGenerator;
import com.niu.app.common.loader.PluginBufferingApplicationStartup;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@ComponentScan(basePackages = {"com.niu.*"}, nameGenerator = SpringBeanNameGenerator.class)
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@MapperScan(basePackages = {"com.*.**.mapper.*", "com.niu.*.mapper"})
@Slf4j
public class WebAppApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(WebAppApplication.class);
        application.setBannerMode(Banner.Mode.CONSOLE);
        // 查看程序启动耗时分布及插件
        application.setApplicationStartup(new PluginBufferingApplicationStartup(2048));
        // 获取到SpringContext
        ConfigurableApplicationContext context = application.run(args);
        // 环境
        Environment environment = context.getEnvironment();
        System.out.println("..............................Java版本：" + environment.getProperty("java.version"));
        System.out.println("..............................服务端口：" + environment.getProperty("server.port"));
    }

}
