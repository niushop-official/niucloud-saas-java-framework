package com.niu.core.service.admin.auth.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.component.context.cache.Cached;
import com.niu.core.common.exception.AuthException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.TreeUtils;
import com.niu.core.common.utils.crypto.PasswordEncipher;
import com.niu.core.common.utils.json.JacksonUtils;
import com.niu.core.entity.sys.SysMenu;
import com.niu.core.entity.sys.SysUser;
import com.niu.core.entity.sys.SysUserRole;
import com.niu.core.enums.site.SiteStatusEnum;
import com.niu.core.enums.sys.AppTypeEnum;
import com.niu.core.mapper.sys.SysUserRoleMapper;
import com.niu.core.service.admin.auth.IAuthService;
import com.niu.core.service.admin.auth.param.EditAuthUserParam;
import com.niu.core.service.admin.auth.vo.AuthUserInfoVo;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.admin.sys.ISysMenuService;
import com.niu.core.service.admin.sys.ISysRoleService;
import com.niu.core.service.admin.sys.ISysUserRoleService;
import com.niu.core.service.admin.sys.ISysUserService;
import com.niu.core.service.admin.sys.param.SysUserParam;
import com.niu.core.service.admin.sys.vo.SysUserDetailVo;
import com.niu.core.service.admin.sys.vo.SysUserRoleInfoVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthServiceImpl implements IAuthService {

    @Resource
    ISiteService siteService;

    @Resource
    ISysUserRoleService sysUserRoleService;

    @Resource
    ISysMenuService sysMenuService;

    @Resource
    SysUserRoleMapper sysUserRoleMapper;

    @Resource
    ISysRoleService sysRoleService;

    @Resource
    ISysUserService sysUserService;

    @Resource
    Cached cached;

    /**
     * 校验用户和传入站点是否存在从属关系
     *
     * @param request
     */
    @Override
    public void checkSiteAuth(HttpServletRequest request) {

        Integer uid = RequestUtils.uid();

        //设置当前操作站点id
        Integer siteId = RequestUtils.adminSiteId();
        RequestUtils.setSiteId(siteId);

        //缓存站点信息数据
        SiteInfoVo siteinfo = siteService.info(siteId);
        //站点不存在 抛出异常
        if (siteinfo == null) {
            throw new AuthException("SITE_NOT_EXIST", 401);
        }
        //没有当前站点的信息
        if (!isSuperAdmin() && ObjectUtil.isNotNull(uid) && uid > 0) {
            SysUserRoleInfoVo sysUserRoleInfoVo = sysUserRoleService.getUserRole(siteId, uid);
            if (sysUserRoleInfoVo == null) {
                throw new AuthException("NO_SITE_PERMISSION", 401);
            }
        }
        RequestUtils.setAppType(siteinfo.getAppType());
    }

    /**
     * 是否是超级管理员
     *
     * @return
     */
    @Override
    public boolean isSuperAdmin() {
        Integer siteId = RequestUtils.defaultSiteId();
        Integer uid = RequestUtils.uid();
        Integer superAdminUid = (Integer) cached.tag("adminAuth").get("superAdminUid");
        if (ObjectUtil.isNotNull(superAdminUid) && ObjectUtil.isNotEmpty(superAdminUid) && superAdminUid > 0) {
            return superAdminUid == uid;
        } else {
            SysUserRole sysUserRole = sysUserRoleMapper.selectOne(new QueryWrapper<SysUserRole>().eq("site_id", siteId).eq("is_admin", 1).last(" limit 1"));
            cached.tag("adminAuth").put("superAdminUid", sysUserRole.getUid());
            return sysUserRole.getUid() == uid;
        }
    }

    /**
     * 校验权限
     *
     * @param request
     */
    @Override
    public void checkRole(HttpServletRequest request) {
        //当前访问的路由地址
        String rule = RequestUtils.getReqeustURI();
        String method = RequestUtils.getRequestMethod();

        //缓存站点信息数据
        SiteInfoVo siteinfo = siteService.info(RequestUtils.siteId());
        if (!method.equals("get")) {
            if (siteinfo.getStatus() == SiteStatusEnum.EXPIRE.getCode()) {
                throw new AuthException("SITE_EXPIRE_NOT_ALLOW", 401);
            }
            if (siteinfo.getStatus() == SiteStatusEnum.EXPIRE.getCode()) {
                throw new AuthException("SITE_CLOSE_NOT_ALLOW", 401);
            }
        }

        Map<String, List<String>> allMenuList = sysMenuService.getAllApiList(RequestUtils.appType(), 100);
        List<String> menulist = allMenuList.get(method);
        Integer is_exists = menulist.indexOf(rule);

        //判断当前访问的接口是否收到权限的限制
        if (is_exists < 0) {
            Map<String, List<String>> otherMenuList = sysMenuService.getAllApiList(RequestUtils.appType() == AppTypeEnum.ADMIN.getName() ? AppTypeEnum.SITE.getName() : AppTypeEnum.ADMIN.getName(), 100);
            List<String> methodMenuList = otherMenuList.get(method);
            Integer is_method_exists = methodMenuList.indexOf(rule);
            if (is_method_exists > 0) {
                throw new AuthException("NO_PERMISSION", 401);
            }
        }
        Map<String, List<String>> roleMenuList = this.getAuthApiList();
        /*if(roleMenuList.get(method).size()<=0 || roleMenuList.get(method).indexOf(rule)<=0){
            throw new AuthException("NO_PERMISSION");
        }*/

    }

    /**
     * 当前授权用户接口权限
     *
     * @return
     */
    @Override
    public Map<String, List<String>> getAuthApiList() {
        Integer isAdmin = 0;
        Integer siteId = RequestUtils.siteId();
        Integer uid = RequestUtils.uid();
        String appType = RequestUtils.appType();
        Map<String, List<String>> authApi = new HashMap<>();
        SysUserRoleInfoVo sysUserRoleInfoVo = new SysUserRoleInfoVo();
        if (isSuperAdmin()) {
            isAdmin = 1;
        } else {
            sysUserRoleInfoVo = sysUserRoleService.getUserRole(siteId, uid);
            if (ObjectUtil.isNull(sysUserRoleInfoVo)) {
                return authApi;
            }
            isAdmin = sysUserRoleInfoVo.getIsAdmin();//是否是超级管理员
        }
        if (isAdmin > 0) {
            authApi = siteService.getSiteApiList(siteId, 1);
        } else {
            List<String> roleIdList = JSONUtil.toList(JSONUtil.parseArray(sysUserRoleInfoVo.getRoleIds()), String.class);
            List<String> menuKeyList = sysRoleService.getMenuIdsByRoleIds(siteId, roleIdList);
            authApi = sysMenuService.getApiListByMenuKeys(appType, menuKeyList);
        }
        return authApi;

    }

    /**
     * 当前授权用户的菜单权限
     *
     * @param addon
     * @return
     */
    @Override
    public JSONArray getAuthMenuTreeList(String addon) {
        Integer isAdmin = 0;
        Integer siteId = RequestUtils.siteId();
        Integer uid = RequestUtils.uid();
        String appType = RequestUtils.appType();
        Map<String, List<String>> authApi = new HashMap<>();
        SysUserRoleInfoVo sysUserRoleInfoVo = new SysUserRoleInfoVo();
        List<SysMenu> menuList = new ArrayList<>();
        if (isSuperAdmin()) {
            isAdmin = 1;
        } else {
            sysUserRoleInfoVo = sysUserRoleService.getUserRole(siteId, uid);
            if (ObjectUtil.isNull(sysUserRoleInfoVo)) {
                return new JSONArray();
            }
            isAdmin = sysUserRoleInfoVo.getIsAdmin();//是否是超级管理员
        }
        if (isAdmin > 0) {
            menuList = sysMenuService.getMenuListByCondition(appType, siteId, 1, 0, new ArrayList<>(), addon);
        } else {
            List<String> roleIdList = JSONUtil.toList(JSONUtil.parseArray(sysUserRoleInfoVo.getRoleIds()), String.class);
            List<String> menuKeyList = sysRoleService.getMenuIdsByRoleIds(siteId, roleIdList);
            menuList = sysMenuService.getMenuListByCondition(appType, siteId, 100, 0, menuKeyList, addon);
        }

        JSONArray jsonArray = JSONUtil.parseArray(JacksonUtils.toSnakeCaseJSONString(menuList));
        return TreeUtils.listToTree(jsonArray, "menu_key", "parent_key", "children");
    }

    /**
     * 获取授权用户信息
     *
     * @return
     */
    @Override
    public AuthUserInfoVo getAuthUserInfo() {
        Integer uid = RequestUtils.uid();
        SysUserDetailVo userInfo = sysUserService.info(uid);
        if (userInfo == null) return null;

        AuthUserInfoVo vo = new AuthUserInfoVo();
        BeanUtil.copyProperties(userInfo, vo);
        return vo;
//        MPJQueryWrapper<SysUserRole> userRoleMPJQueryWrapper = new MPJQueryWrapper<>();
//        userRoleMPJQueryWrapper.select("sur.id, su.username, su.head_img, su.real_name, su.last_ip, su.last_time, su.login_count, sur.uid, sur.site_id, sur.role_ids, sur.create_time, sur.is_admin, sur.status")
//                .setAlias("sur")
//                .leftJoin("?_sys_user su ON sur.uid = su.uid".replace("?_", GlobalConfig.tablePrefix));
//        userRoleMPJQueryWrapper.eq("sur.uid", uid);
//        userRoleMPJQueryWrapper.eq("sur.site_id", siteId);
//        AuthUserInfoVo authUserInfoVo = sysUserRoleMapper.selectJoinOne(AuthUserInfoVo.class, userRoleMPJQueryWrapper);
//        if(ObjectUtil.isNotNull(authUserInfoVo))
//        {
//            authUserInfoVo.setStatusName("");
//        }
//
//        return authUserInfoVo;
    }

    /**
     * 更新用户
     *
     * @param editAuthUserParam
     */
    @Override
    public void editAuth(EditAuthUserParam editAuthUserParam) {
        if (ObjectUtil.isNotNull(editAuthUserParam.getPassword()) && ObjectUtil.isNotEmpty(editAuthUserParam.getPassword())) {
            SysUser sysUser = sysUserService.find(RequestUtils.uid());
            if (!PasswordEncipher.matche(editAuthUserParam.getOriginalPassword(), sysUser.getPassword())) {
                throw new AuthException("OLD_PASSWORD_ERROR");
            }
        }
        SysUserParam sysUserParam = new SysUserParam();
        sysUserParam.setHeadImg(editAuthUserParam.getHeadImg());
        sysUserParam.setRealName(editAuthUserParam.getRealName());
        sysUserParam.setPassword(editAuthUserParam.getPassword());
        sysUserService.edit(RequestUtils.uid(), sysUserParam);
    }

}
