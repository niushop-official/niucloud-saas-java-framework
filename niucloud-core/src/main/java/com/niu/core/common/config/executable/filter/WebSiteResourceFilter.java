package com.niu.core.common.config.executable.filter;

import com.alibaba.fastjson2.JSON;
import com.niu.core.common.component.base.ThreadLocalHolder;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.domain.Result;
import com.niu.core.common.exception.BaseException;
import com.niu.core.common.config.GlobalConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 站点资源过滤
 */
@Slf4j
public class WebSiteResourceFilter implements Filter {

    private ApplicationContext applicationContext;

    public WebSiteResourceFilter(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // =======================================================================================//
        // =========访问默认页
        // =======================================================================================//
        String contextPath = request.getContextPath();
        String requestUri = request.getRequestURI();
        requestUri = requestUri.replace(contextPath, "");
        if ("".equals(requestUri) || "/".equals(requestUri)) {
            response.sendRedirect(GlobalConfig.defaultAccessPath());
            log.info("默认访问： " + GlobalConfig.defaultAccessPath());
            return;
        }
        // =======================================================================================//
        // =========rewrite
        // =======================================================================================//
        String accessPath = WebAppEnvs.get().webRootDownPublic + requestUri;
        File accessFile = new File(accessPath);
        if (accessFile.isFile() && accessFile.exists()) {
            request.getRequestDispatcher(requestUri).forward(request, response);
            return;
        }

        if (requestUri.startsWith("/admin") && !requestUri.startsWith("/adminapi")) {
            request.getRequestDispatcher("/admin/index.html").forward(request, response);
            return;
        }
        if (requestUri.startsWith("/site") || requestUri.startsWith("/home") || requestUri.startsWith("/directory")) {
            request.getRequestDispatcher("/admin/index.html").forward(request, response);
            return;
        }
        if (requestUri.startsWith("/wap")) {
            request.getRequestDispatcher("/wap/index.html").forward(request, response);
            return;
        }
        if (requestUri.startsWith("/web")) {
            request.getRequestDispatcher("/web/index.html").forward(request, response);
            return;
        }
        Matcher matcher = Pattern.compile("^/MP_verify_(\\w+)\\.txt$").matcher(requestUri);
        if (matcher.find()) {
            String verifyCode = matcher.group(1);
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write(verifyCode);
            return;
        }
        // =======================================================================================//
        // =========其他业务处理
        // =======================================================================================//
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            e.printStackTrace();
            Throwable cause = e.getCause();
            if (cause != null) {
                if (cause instanceof BaseException) {
                    BaseException baseException = (BaseException) cause;
                    Result result = Result.fail(baseException.getCode(), baseException.getMsg());
                    String jsonString = JSON.toJSONString(result);
                    contextTypeOfJSON(response, jsonString);
                }
            }
        }
        // 响应之后清除
        ThreadLocalHolder.remove();
    }

    /**
     * @param response
     * @param jsonString
     * @throws IOException
     */
    private void contextTypeOfJSON(HttpServletResponse response, String jsonString) throws IOException {
        response.setHeader("Content-type", "application/json; charset=UTF-8");
        try (
                OutputStream outputStream = response.getOutputStream();
                OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
            writer.write(jsonString);
            writer.flush();
        } catch (Exception e) {
            // ignore
        }

    }

}
