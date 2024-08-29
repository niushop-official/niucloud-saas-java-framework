package com.niu.core.config;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.niu.core.component.context.WebAppEnvs;
import com.niu.core.config.executable.StpInterfaceImpl;
import com.niu.core.config.executable.interceptor.SaTokenInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.Arrays;

/**
 * Web配置
 */
@Configuration
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private SaTokenInterceptor saTokenInterceptor;

    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()
                // 指定 [拦截路由] 与 [放行路由]
                .addInclude("/**").addExclude("/favicon.ico")
                // 认证函数: 每次请求执行
                .setAuth(obj -> {
                    String tokenValue = StpUtil.getTokenValue();
                    if (tokenValue == null) {
                        tokenValue = "";
                    }
                    SaManager.getLog().debug("请求path={}  提交token={}", SaHolder.getRequest().getRequestPath(), tokenValue);
                })
                // 异常处理函数：每次认证函数发生异常时执行此函数
                .setError(e -> {
                    return SaResult.error(e.getMessage());
                })
                // 前置函数：在每次认证函数之前执行
                .setBeforeAuth(obj -> {
                    SaHolder.getResponse()
                            // ---------- 设置跨域响应头 ----------
                            // 允许指定域访问跨域资源
                            .setHeader("Access-Control-Allow-Origin", "*")
                            // 允许所有请求方式
                            .setHeader("Access-Control-Allow-Methods", "*")
                            // 允许的header参数
                            .setHeader("Access-Control-Allow-Headers", "*")
                            // 有效时间
                            .setHeader("Access-Control-Max-Age", "3600")
                    ;
                    // 如果是预检请求，则立即返回到前端
                    SaRouter.match(SaHttpMethod.OPTIONS)
                            .free(r -> System.out.println("OPTIONS预检请求, 不做处理."))
                            .back();
                });

    }


    /**
     * 拦截器注册
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，打开注解式鉴权功能
        registry.addInterceptor(saTokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
//                        "/api/**",
                        // "/core/**",
                        "/swagger/**",
                        "/swagger-ui.html",
                        "/swagger-resources/**",
                        "/doc.html",
                        "/webjars/**",
                        "/v2/**",
                        "/v2/api-docs/**",
                        "/v2/api-docs-ext/**",
                        "/favicon.ico",
                        "/error",
                        "/adminapi/login/**",
                        "/adminapi/sys/web/**",
                        "/adminapi/captcha/**")
        ;
    }

    @Bean
    public StpInterface stpInterface() {
        StpInterfaceImpl stpInterface = new StpInterfaceImpl();
        return stpInterface;
    }

    /**
     * Sa-Token整合 jwt (Simple 简单模式)
     *
     * @return
     */
    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }

    /**
     * 配置允许跨域
     */
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedHeaders("*")
                .allowedMethods("GET", "POST", "DELETE", "PUT")
                .maxAge(3600);
    }

    /**
     * 动态映射静态资源
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("静态资源动态映射===================>begin===================>");
        WebAppEnvs webAppEnvs = WebAppEnvs.get();
        // public目录的映射
        File webRootDownPublicFile = new File(webAppEnvs.webRootDownPublic);
        String publicMapping = "file:" + webRootDownPublicFile.getAbsolutePath() + "/";
        File[] webRootDownPublicFiles = webRootDownPublicFile.listFiles();
        if (webRootDownPublicFiles != null && webRootDownPublicFiles.length > 0) {
            Arrays.stream(webRootDownPublicFiles).forEach(file -> {
                if (file.isFile()) {
                    registry.addResourceHandler("/" + file.getName()).addResourceLocations(publicMapping);
                    log.info("Static resource: " + "/" + file.getName() + ", Locations:" + publicMapping);
                } else {
                    registry.addResourceHandler("/" + file.getName() + "/**").addResourceLocations(publicMapping + file.getName() + "/");
                    log.info("Static resource: " + "/" + file.getName() + "/**, Locations:" + publicMapping + file.getName() + "/");
                }
            });
        }
        // resource目录的映射
        File webRootDownResourceFile = new File(webAppEnvs.webRootDownResource);
        String resourceMapping = "file:" + webRootDownResourceFile.getAbsolutePath() + "/";
        File[] webRootDownResourceFiles = webRootDownResourceFile.listFiles();
        if (webRootDownResourceFiles != null && webRootDownResourceFiles.length > 0) {
            Arrays.stream(webRootDownResourceFiles).forEach(file -> {
                if (file.isFile()) {
                    registry.addResourceHandler("/" + file.getName()).addResourceLocations(resourceMapping);
                    log.info("Static resource: " + "/" + file.getName() + ", Locations:" + resourceMapping);
                } else {
                    registry.addResourceHandler("/" + file.getName() + "/**").addResourceLocations(resourceMapping + file.getName() + "/");
                    log.info("Static resource: " + "/" + file.getName() + "/**, Locations:" + resourceMapping + file.getName() + "/");
                }
            });
        }
        log.info("静态资源动态映射<===================ended<===================");
    }

}
