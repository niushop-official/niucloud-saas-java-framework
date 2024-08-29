package com.niu.core.service.admin.site;

import cn.hutool.json.JSONArray;
import com.niu.core.common.domain.PageResult;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.service.admin.site.param.SiteGroupAddParam;
import com.niu.core.service.admin.site.param.SiteGroupParam;
import com.niu.core.service.admin.site.param.SiteGroupSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.site.vo.SiteGroupListVo;

import java.util.List;

/**
 * 店铺分组（分组权限）服务接口
 */
public interface ISiteGroupService {


    /**
     * 店铺分组（分组权限）列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SiteGroupListVo>
     */
    PageResult<SiteGroupListVo> list(PageParam pageParam, SiteGroupSearchParam searchParam);

    /**
     * 获取所有站点分组
     * @return
     */
    List<SiteGroup> getAll();

    /**
     * 店铺分组（分组权限）详情
     * @param id 主键ID
     * @return SiteGroup
     */
    SiteGroup info(Integer id);

    /**
     * 店铺分组（分组权限）添加
     * @param addParam 添加参数
     */
    void add(SiteGroupAddParam addParam);

    /**
     * 店铺分组（分组权限）编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, SiteGroupParam editParam);

    /**
     * 店铺分组（分组权限）删除
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 检测插件是否有效
     *
     * @param jsonArray
     */
    void checkAddon(JSONArray jsonArray);

    /**
     * 查询店铺套餐以及用户创建的站点数量
     *
     * @param uid
     * @return
     */
    List<SiteGroupListVo> getUserSiteGroupAll(Integer uid);

    /**
     * 查询用户某个站点套餐的站点数量
     *
     * @param uid
     * @param groupId
     * @return
     */
    Integer getUserSiteGroupSiteNum(Integer uid, Integer groupId);

}
