package com.niu.core.common.config.executable.interceptor;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.exception.AuthException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.admin.auth.IAuthService;
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
public class SaTokenAdminInterceptor extends SaInterceptor {

    @Value("${sa-token.timeout}")
    private long tokenTimeout;

    @Resource
    IAuthService authService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        /** 检测当前路由是否需要登录 */
        Boolean isCheckLogin=PermissionAuthorizer.getInstance().existAnnotation(request, SaCheckLogin.class);

        /** 验证是否登录 */
        String token= RequestUtils.adminToken();
        if((ObjectUtil.isEmpty(token) || ObjectUtil.isNull(token)) && isCheckLogin){
            throw new AuthException("请登录", 401);
        }
        /** 检验当前登录是否有效 */
        Boolean islogin=StpUtil.isLogin();
        if(!islogin && isCheckLogin){
            throw new AuthException("登录过期,请重新登录", 401);
        }

        if(islogin){
            //设置当前登录用户id
            String[] loginId = ObjectUtil.defaultIfEmpty(StpUtil.getLoginId().toString(), "").split("-");
            if (loginId[1] == null) throw new AuthException("登录过期,请重新登录", 401);

            Integer uid = Integer.parseInt(loginId[1]);
            RequestUtils.setUid(uid);
        }

        if (!RequestUtils.getReqeustURI().contains("/adminapi/home")) {
            /** 校对当前登录的用户是否拥有这个站点的管理权限 */
            authService.checkSiteAuth(request);

            /** 校验用户和当前的路由权限 */
            authService.checkRole(request);
        }

        /*** 检测是否是演示环境 */
        authService.checkIsDemo(request);

        /** 添加用户操作日志 */
        authService.addUserLog(request);
       // Integer userId = (Integer)StpUtil.getLoginId();

        //adminapi/xxxx

/*        // 免登录接口
        List<String> notLoginUri = Arrays.asList(AdminConfig.notLoginUri);
        if (notLoginUri.contains(auths)) {
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }

        if (!StpKit.admin.isLogin()) {
            // 用户未登录
            HttpJson.json(response, SaResult.error("没有登录"));
            return false;
        }
        SaTokenInfo saTokenInfo = StpKit.admin.getTokenInfo();
        System.out.println(saTokenInfo);

        System.out.println("role : " + StpUtil.getSession().get("role"));*/
        // 权限校验
        // StpUtil.checkPermission("user-view");
        // StpUtil.checkRole("role");
        // SaRouter.match("/user/**", r -> StpUtil.checkRole("admin"));

        // 当前用户的角色列表
/*        System.out.println(StpUtil.getRoleList());
        // 当前用户的权限列表
        System.out.println(StpUtil.getPermissionList());*/
        System.out.println("后台登录验证");
        return true;
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                @Nullable Exception ex) throws Exception {
        // 续约长期有效期<避免redis中大量无效的token>
        StpUtil.renewTimeout(tokenTimeout);
    }

}
