package com.niu.core.common.config;

import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.config.executable.filter.WebSiteResourceFilter;
import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class NiuCoreConfig {

    /**
     * Spring环境上下文
     *
     * @param applicationContext
     * @return
     */
    @Bean(name = "springContext")
    public SpringContext springContext(ApplicationContext applicationContext) {
        SpringContext springUtils = new SpringContext();
        springUtils.setApplicationContext(applicationContext);
        return springUtils;
    }

    @Autowired
    public void applicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        SpringContext.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * @return
     */
    @Bean
    public TomcatServletWebServerFactory webServerFactory() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.addConnectorCustomizers((Connector connector) -> {
            connector.setProperty("relaxedPathChars", "\"<>[\\]^`{|}");
            connector.setProperty("relaxedQueryChars", "\"<>[\\]^`{|}");
        });
        return factory;
    }

    /**
     * 引入Filter机制
     *
     * @return
     */
    @Bean(name = "webSiteResourceFilter")
    public FilterRegistrationBean websiteResourceFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new WebSiteResourceFilter(SpringContext.get()));
        registration.addUrlPatterns("/*");
        //值越小，Filter越靠前。
        registration.setOrder(-10000);
        return registration;
    }

    /**
     * WebSocket支持
     *
     * @return
     */
    @Bean(name = "serverEndpointExporter")
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .anyRequest().permitAll()
                )
                .csrf().disable();

        return http.build();
    }
    @Bean("coreMessageSource")
    public MessageSource coreMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("core/i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

}
