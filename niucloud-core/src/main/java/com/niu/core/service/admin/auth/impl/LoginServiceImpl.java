package com.niu.core.service.admin.auth.impl;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.anji.captcha.model.vo.CaptchaVO;
import com.niu.core.common.exception.AuthException;
import com.niu.core.common.utils.EnumUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.crypto.PasswordEncipher;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.enums.sys.AppTypeEnum;
import com.niu.core.service.admin.auth.IAuthService;
import com.niu.core.service.admin.auth.IConfigService;
import com.niu.core.service.admin.auth.ILoginService;
import com.niu.core.service.admin.auth.param.UserLoginParam;
import com.niu.core.service.admin.auth.vo.LoginConfigVo;
import com.niu.core.service.admin.auth.vo.LoginResultVo;
import com.niu.core.service.admin.auth.vo.LoginUserInfoVo;
import com.niu.core.service.admin.home.IAuthSiteService;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.admin.sys.ISysUserRoleService;
import com.niu.core.service.admin.sys.ISysUserService;
import com.niu.core.service.admin.sys.vo.SysUserInfoVo;
import com.niu.core.service.admin.sys.vo.SysUserRoleInfoVo;
import com.niu.core.service.core.captcha.ICoreCaptchaImgService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoginServiceImpl implements ILoginService {

    @Resource
    IConfigService configService;
    @Resource
    ISysUserService sysUserService;
    @Resource
    ISysUserRoleService sysUserRoleService;
    @Resource
    IAuthSiteService authSiteService;
    @Resource
    IAuthService authService;
    @Resource
    ISiteService siteService;
    @Resource
    ICoreCaptchaImgService coreCaptchaImgService;
    /**
     * 用户登录
     *
     * @param userLoginParam
     * @return
     */
    @Override
    public LoginResultVo login(UserLoginParam userLoginParam) {
        String appType=userLoginParam.getAppType();
        String userName=userLoginParam.getUsername();
        String passWord=userLoginParam.getPassword();

        if(!EnumUtils.isInclude(appType, AppTypeEnum.class, "getName")){
            throw new AuthException("APP_TYPE_NOT_EXIST");
        }
        Integer siteId= RequestUtils.siteId();

        LoginConfigVo loginConfigVo=configService.getLoginConfig();
        Integer isCaptcha=0;
        if(appType.equals(AppTypeEnum.ADMIN.getName())){
            isCaptcha=loginConfigVo.getIsCaptcha();
        }else if(appType.equals(AppTypeEnum.SITE.getName())){
            isCaptcha=loginConfigVo.getIsSiteCaptcha();
        }

        if(isCaptcha==1){
            //验证验证码
            CaptchaVO captchaVO=new CaptchaVO();
            captchaVO.setCaptchaVerification(userLoginParam.getCaptchaCode());
            coreCaptchaImgService.verification(captchaVO);
        }
        SysUserInfoVo userInfo=sysUserService.getUserInfoByUserName(userName);
        if(ObjectUtil.isNull(userInfo)){
            throw new AuthException("账号密码错误");
        }

        //检测密码加密是否正确
        if(!PasswordEncipher.matche(passWord, userInfo.getPassword())){
            throw new AuthException("账号密码错误");
        }
        //设置当前登录用户id
        RequestUtils.setUid(userInfo.getUid());

        Integer defaultSiteId=0;
        SysUserRoleInfoVo roleInfoVo=new SysUserRoleInfoVo();
        List<Integer> siteIds=new ArrayList<>();
        if(appType.equals(AppTypeEnum.ADMIN.getName())){
            defaultSiteId=RequestUtils.defaultSiteId();
            roleInfoVo=sysUserRoleService.getUserRole(defaultSiteId, userInfo.getUid());
            if(ObjectUtil.isNotNull(roleInfoVo)){
                if(userInfo.getStatus()<=0){
                    throw new AuthException("USER_LOCK");
                }
            }else{
                appType=AppTypeEnum.SITE.getName();
            }
        }else if(appType.equals(AppTypeEnum.SITE.getName())){
            siteIds=authSiteService.getSiteIds();
            if(ObjectUtil.isNotNull(siteIds) && siteIds.size()>0){
                defaultSiteId=siteIds.indexOf(RequestUtils.siteId())>0 || authService.isSuperAdmin()?RequestUtils.siteId():siteIds.get(0);
            }
        }else{
            throw new AuthException("APP_TYPE_NOT_EXIST");
        }

        //修改用户登录信息
        sysUserService.editUserLoginInfo(userInfo.getUid());

        SaLoginModel loginModel = SaLoginModel.create();
        loginModel.setDevice(RequestUtils.handler().getHeader("User-Agent"));
        loginModel.setExtra("userName", userInfo.getUsername());
        loginModel.setExtra("headImg", userInfo.getHeadImg());
        loginModel.setExtra("realName", userInfo.getRealName());
        // 执行登录
        StpUtil.login(userInfo.getUid(), loginModel);
        // 获取返回内容
        SaTokenInfo saTokenInfo = StpUtil.getTokenInfo();
        LoginResultVo resultVo=new LoginResultVo();

        LoginUserInfoVo userInfoVo=new LoginUserInfoVo();
        userInfoVo.setUid(userInfo.getUid());
        userInfoVo.setUsername(userInfo.getUsername());
        userInfoVo.setHeadImg(userInfo.getHeadImg());
        userInfoVo.setIsSuperAdmin(authService.isSuperAdmin());


        if(appType.equals(AppTypeEnum.ADMIN.getName()) || (appType.equals(AppTypeEnum.SITE.getName()) && defaultSiteId>0)){
            RequestUtils.setSiteId(defaultSiteId);
            SiteInfoVo siteInfoVo=siteService.info(RequestUtils.siteId());
            resultVo.setSiteInfo(siteInfoVo);
        }
        if(appType.equals(AppTypeEnum.ADMIN.getName()) && !userInfoVo.getIsSuperAdmin()){
            siteIds=authSiteService.getSiteIds();
        }

        resultVo.setToken(saTokenInfo.getTokenValue());
        resultVo.setExpiresTime(DateUtils.currTime()+saTokenInfo.getTokenTimeout());
        resultVo.setUserinfo(userInfoVo);
        resultVo.setSiteId(defaultSiteId);
        resultVo.setUserrole(roleInfoVo);
        userInfoVo.setSiteIds(siteIds);

        return resultVo;
    }

    /**
     * 用戶登出
     *
     */
    @Override
    public void logout() {
        StpUtil.logout();
    }

    /**
     * 清理token
     *
     * @param uid
     * @param appType
     * @param token
     */
    @Override
    public void clearToken(Integer uid, String appType, String token) {
        if(ObjectUtil.isNotNull(token) && ObjectUtil.isNotEmpty(token)){
            StpUtil.logoutByTokenValue(token);
        }else if(ObjectUtil.isNotNull(appType) && ObjectUtil.isNotEmpty(appType)){
            StpUtil.logout(uid, appType);
        }else{
            StpUtil.logout(uid);
        }
    }
}
