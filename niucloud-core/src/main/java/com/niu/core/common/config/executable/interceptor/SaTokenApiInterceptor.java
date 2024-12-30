package com.niu.core.common.config.executable.interceptor;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.exception.AuthException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.enums.sys.AppTypeEnum;
import com.niu.core.service.api.login.IAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * https://gitee.com/dromara/sa-token/tree/dev/sa-token-demo/sa-token-demo-jwt
 */
@Component
public class SaTokenApiInterceptor extends SaInterceptor {

    @Value("${sa-token.timeout}")
    private long tokenTimeout;

    @Resource
    IAuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        /** 设置请求类型 */
        RequestUtils.setAppType(AppTypeEnum.API.getName());

        // 是否忽略认证
        if (PermissionAuthorizer.getInstance().existAnnotation(request, SaIgnore.class)) return true;

        /** 检测当前路由是否需要登录 */
        Boolean isCheckLogin=PermissionAuthorizer.getInstance().existAnnotation(request, SaCheckLogin.class);

        /** 验证是否登录 */
        String token= RequestUtils.adminToken();
        if((ObjectUtil.isEmpty(token) || ObjectUtil.isNull(token)) && isCheckLogin){
                throw new AuthException("MUST_LOGIN", 401);
        }

        /** 检验当前登录是否有效 */
        Boolean islogin=StpUtil.isLogin();
        if(!islogin && isCheckLogin){
            throw new AuthException("LOGIN_EXPIRE", 401);
        }

        /** 设置当前登录会员id */
        if(islogin){
            Integer memberId=Integer.parseInt(StpUtil.getLoginId().toString());
            RequestUtils.setMemberId(memberId);
        }

        /** 检测站点 */
        authService.checkSite();

        /** 校验会员和站点 */
        authService.checkSiteAuth();

        /** 校验渠道 */
        authService.checkChannel();

        return true;
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                @Nullable Exception ex) throws Exception {
        // 续约长期有效期<避免redis中大量无效的token>
        StpUtil.renewTimeout(tokenTimeout);
    }

}
