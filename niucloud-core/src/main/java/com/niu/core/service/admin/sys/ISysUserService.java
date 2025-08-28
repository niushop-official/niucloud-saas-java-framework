package com.niu.core.service.admin.sys;

import com.niu.core.common.domain.PageResult;
import com.niu.core.entity.sys.SysUser;
import com.niu.core.service.admin.site.param.SiteUserParam;
import com.niu.core.service.admin.site.vo.SiteUserVo;
import com.niu.core.service.admin.sys.param.SysUserCreateSiteLimitAddParam;
import com.niu.core.service.admin.sys.param.SysUserCreateSiteLimitEditParam;
import com.niu.core.service.admin.sys.param.SysUserParam;
import com.niu.core.service.admin.sys.param.SysUserSearchParam;
import com.niu.core.service.admin.sys.vo.SysUserCreateSiteLimitVo;
import com.niu.core.service.admin.sys.vo.SysUserDetailVo;
import com.niu.core.service.admin.sys.vo.SysUserInfoVo;
import com.niu.core.service.admin.sys.vo.SysUserListVo;
import com.niu.core.common.domain.PageParam;

import java.util.List;

/**
 * 后台管理员服务接口类
 */
public interface ISysUserService {

    /**
     * 后台管理员列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysUserListedVo>
     */
    PageResult<SysUserListVo> list(PageParam pageParam, SysUserSearchParam searchParam);

    /**
     * 后台管理员详情
     *
     * @param id 主键ID
     * @return SysUserInfoVo
     */
    SysUserDetailVo info(Integer id);

    /**
     * 后台管理员添加
     *
     * @param addParam 参数
     */
    Integer add(SysUserParam addParam);

    /**
     * 后台管理员编辑
     *
     * @param editParam 参数
     */
    void edit(Integer uid, SysUserParam editParam);

    /**
     * 后台管理员删除
     *
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 通过账号获取账号信息
     * @param userName
     * @return
     */
    SysUserInfoVo getUserInfoByUserName(String userName);

    /**
     * 修改用户登录信息
     * @param uid
     */
    void editUserLoginInfo(Integer uid);

    /**
     * 添加对应站点用户（添加站点，同时添加站点用户,用于添加站点以及站点添加站点用户）
     * @param siteUserParam
     * @param siteId
     */
    Integer addSiteUser(SiteUserParam siteUserParam, Integer siteId);

    /**
     * 检测用户账号是否存在
     * @param userName
     * @return
     */
    boolean checkUserName(String userName);

    /**
     * 获取所有用户数据
     * @return
     */
    List<SysUser> getUserAll();

    /**
     * 获取用户站点创建限制
     *
     * @param uid
     * @return
     */
    List<SysUserCreateSiteLimitVo> getUserCreateSiteLimit(Integer uid);

    /**
     * 获取用户站点创建限制
     *
     * @param id
     * @return
     */
    SysUserCreateSiteLimitVo getUserCreateSiteLimitInfo(Integer id);

    /**
     * 添加用户站点创建限制
     *
     * @param userCreateSiteLimitAddParam
     */
    void addUserCreateSiteLimit(SysUserCreateSiteLimitAddParam userCreateSiteLimitAddParam);

    /**
     * 编辑用户站点创建限制
     *
     * @param userCreateSiteLimitEditParam
     */
    void editUserCreateSiteLimit(SysUserCreateSiteLimitEditParam userCreateSiteLimitEditParam);

    /**
     * 删除用户站点创建限制
     *
     * @param id
     */
    void delUserCreateSiteLimit(Integer id);

    /**
     * 通过用户id获取用户信息
     *
     * @param uid
     * @return
     */
    SysUser find(Integer uid);

    /**
     * 获取用户下拉框
     * @param siteId
     * @return
     */
    List<SysUserInfoVo> getUserSelect(String siteId);
}
