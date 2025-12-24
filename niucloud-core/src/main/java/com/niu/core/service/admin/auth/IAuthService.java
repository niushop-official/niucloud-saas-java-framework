package com.niu.core.service.admin.auth;

import cn.hutool.json.JSONArray;
import com.niu.core.service.admin.auth.param.EditAuthUserParam;
import com.niu.core.service.admin.auth.vo.AuthUserInfoVo;

import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 用户权限服务
 */
public interface IAuthService {

    /**
     * 校验用户和传入站点是否存在从属关系
     */
    void checkSiteAuth(HttpServletRequest request);


    /**
     * 是否是超级管理员
     * @return
     */
    boolean isSuperAdmin();

    /**
     * 校验权限
     * @param request
     */
    void checkRole(HttpServletRequest request);

    /**
     * 检测是否是演示环境
     */
    void checkIsDemo(HttpServletRequest request);

    /**
     * 校验授权
     */
    void checkAuthInfo(HttpServletRequest request);

    /**
     * 当前授权用户接口权限
     * @return
     */
    Map<String, List<String>> getAuthApiList();

    /**
     * 当前授权用户的菜单权限
     * @param addon
     * @return
     */
    JSONArray getAuthMenuTreeList(String addon);

    JSONArray getAuthMenuTreeList(Integer isTree, String addon);

    /**
     * 获取授权用户信息
     * @return
     */
    AuthUserInfoVo getAuthUserInfo();

    /**
     * 更新用户
     *
     * @param editAuthUserParam
     */
    void editAuth(EditAuthUserParam editAuthUserParam);

    /**
     * 获取是否允许切换站点
     * @return
     */
    Map<String, Integer> getIsAllowChangeSite();

    /**
     * 添加用户操作日志
     * @param request
     */
    void addUserLog(HttpServletRequest request);

    /**
     * 设置是否允许切换站点
     * @param
     */
    void setIsAllowChangeSite(Map<String, Integer> param);
}
