package com.niu.core.service.admin.sys;

import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.sys.param.SysRoleParam;
import com.niu.core.service.admin.sys.param.SysRoleSearchParam;
import com.niu.core.service.admin.sys.vo.SysRoleInfoVo;
import com.niu.core.service.admin.sys.vo.SysRoleListVo;
import com.niu.core.common.domain.PageParam;

import java.util.List;

/**
 * 角色服务接口类
 */
public interface ISysRoleService {

    /**
     * 角色列表
     *
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysRoleListedVo>
     */
    PageResult<SysRoleListVo> list(PageParam pageParam, SysRoleSearchParam searchParam);

    /**
     * 角色详情
     *
     * @param id 主键ID
     * @return SysRoleInfoVo
     */
    SysRoleInfoVo info(Integer id);

    /**
     * 角色添加
     *
     * @param addParam 参数
     */
    void add(SysRoleParam addParam);

    /**
     * 角色编辑
     *
     * @param editParam 参数
     */
    void edit(Integer roleId, SysRoleParam editParam);

    /**
     * 角色删除
     *
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 通过权限组id获取菜单
     * @param siteId
     * @param roleIds
     * @return
     */
    List<String> getMenuIdsByRoleIds(Integer siteId, List<String> roleIds);

    /**
     * 获取站点下所有的权限角色
     * @return
     */
    List<SysRoleListVo> getAllRole();

}
