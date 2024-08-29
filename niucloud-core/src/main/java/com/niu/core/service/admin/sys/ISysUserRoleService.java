package com.niu.core.service.admin.sys;

import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.sys.param.SysUserRoleParam;
import com.niu.core.service.admin.sys.param.SysUserRoleSearchParam;
import com.niu.core.service.admin.sys.vo.SysUserRoleInfoVo;
import com.niu.core.service.admin.sys.vo.SysUserRoleListVo;
import com.niu.core.common.domain.PageParam;

/**
 * 用户权限服务接口类
 */
public interface ISysUserRoleService{

    /**
     * 用户权限列表
     *
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysUserRoleListedVo>
     */
    PageResult<SysUserRoleListVo> list(PageParam pageParam, SysUserRoleSearchParam searchParam);

    /**
     * 用户权限详情
     *
     * @param id 主键ID
     * @return SysUserRoleInfoVo
     */
    SysUserRoleInfoVo info(Integer id);

    /**
     * 用户权限添加
     *
     * @param addParam 参数
     */
    void add(SysUserRoleParam addParam);

    /**
     * 用户权限编辑
     *
     * @param editParam 参数
     */
    void edit(SysUserRoleParam editParam);

    /**
     * 用户权限删除
     *
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 用户权限信息  获取用户对应站点权限
     * @param site_id
     * @param uid
     * @return
     */
    SysUserRoleInfoVo getUserRole(Integer site_id, Integer uid);

}
