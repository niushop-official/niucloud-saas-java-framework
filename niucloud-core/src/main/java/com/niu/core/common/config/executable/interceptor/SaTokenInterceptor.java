package com.niu.core.common.config.executable.interceptor;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import com.niu.core.common.exception.AuthException;
import com.niu.core.common.utils.RequestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * https://gitee.com/dromara/sa-token/tree/dev/sa-token-demo/sa-token-demo-jwt
 */
@Component
public class SaTokenInterceptor extends SaInterceptor {

    @Value("${sa-token.timeout}")
    private long tokenTimeout;

    @Resource
    SaTokenAdminInterceptor saTokenAdminInterceptor;

    @Resource
    SaTokenApiInterceptor saTokenApiInterceptor;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        /** 获取当前请求的域名 */
        RequestUtils.setRequestDomain(request.getServerName());

        /** 获取当前请求的类型 */
        RequestUtils.setRequestSecure(request.getScheme());

        /** 设置当前请求的Ip地址 */
        RequestUtils.setRequestIpAddr(request);

        /** 未授权验证，还没有校验token的有效性 */
        if (PermissionAuthorizer.getInstance().existAnnotation(request, SaIgnore.class)) {
            return true;
        }
        /** 执行授权，验证token操作，通过返回true，不通过返回false */
        try{
            boolean checkToken = super.preHandle(request, response, handler);
            if(!checkToken){
                throw new AuthException("请登录", 401);
            }
        }catch (Exception e){
            throw new AuthException("请登录", 401);
        }


        /** 授权之后，进一步鉴权，则当前用户是否具备访问权限 */
        String route = request.getRequestURI();
        if (route.contains("/adminapi/")) {
            return saTokenAdminInterceptor.preHandle(request, response, handler);
        } else if (route.contains("/api/")) {
            return saTokenApiInterceptor.preHandle(request, response, handler);
        } else {
            return true;
        }
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                @Nullable Exception ex) throws Exception {
        // 续约长期有效期<避免redis中大量无效的token>
        StpUtil.renewTimeout(tokenTimeout);
    }

}
