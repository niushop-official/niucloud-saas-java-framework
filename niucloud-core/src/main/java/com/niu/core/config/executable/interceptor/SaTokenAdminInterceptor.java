package com.niu.core.config.executable.interceptor;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.annotation.SaNotCheckLogin;
import com.niu.core.common.exception.AuthException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.json.HttpJson;
import com.niu.core.service.admin.auth.IAuthService;
import com.niu.core.service.admin.sys.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
            throw new AuthException("MUST_LOGIN", 401);
        }
        /** 检验当前登录是否有效 */
        Boolean islogin=StpUtil.isLogin();
        if(!islogin && isCheckLogin){
            throw new AuthException("LOGIN_EXPIRE", 401);
        }

        if(islogin){
            //设置当前登录用户id
            Integer uid = Integer.parseInt(StpUtil.getLoginId().toString());
            RequestUtils.setUid(uid);
        }

        /** 校对当前登录的用户是否拥有这个站点的管理权限 */
        authService.checkSiteAuth(request);

        /** 校验用户和当前的路由权限 */
        authService.checkRole(request);


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
