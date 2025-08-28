package com.niu.core.service.admin.home;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.addon.vo.AddonInfoVo;
import com.niu.core.service.admin.home.param.HomeSiteAddParam;
import com.niu.core.service.admin.home.param.SiteParam;
import com.niu.core.service.admin.home.param.SiteSearchParam;
import com.niu.core.service.admin.home.vo.SiteInfoVo;
import com.niu.core.service.admin.home.vo.SiteListVo;
import com.niu.core.service.admin.home.vo.UserCreateSiteVo;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IAuthSiteService {

    /**
     * 站点列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SiteListVo>
     */
    PageResult<SiteListVo> list(PageParam pageParam, SiteSearchParam searchParam);

    /**
     * 站点详情
     * @param id 主键ID
     * @return SiteInfoVo
     */
    SiteInfoVo info(Integer id);

    /**
     * 站点添加
     * @param addParam 添加参数
     */
    void add(SiteParam addParam);

    /**
     * 站点编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, SiteParam editParam);

    /**
     * 站点删除
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 关闭站点
     * @param siteId
     */
    void closeSite(Integer siteId);

    /**
     * 开启站点
     * @param siteId
     */
    void openSite(Integer siteId);

    /**
     *  获取站点的菜单列表
     * @param siteId
     * @param status
     * @return
     */
    Map<String, List<String>> getSiteApiList(Integer siteId, Integer status);

    /**
     * 通过条件检索 站点数量
     * @param siteSearchParam
     * @return
     */
    Integer getSiteCountByCondition(SiteSearchParam siteSearchParam);
    /**
     * 获取用户的
     * @return
     */
    List<Integer> getSiteIds();

    /**
     * 获取可选择的店铺套餐
     *
     * @return
     */
    List<UserCreateSiteVo> getSiteGroup();

    /**
     * 用户添加站点
     *
     * @param homeSiteAddParam
     */
    void createSite(HomeSiteAddParam homeSiteAddParam);

    /**
     * 获取站点分组应用列表
     * @return
     */
    List<AddonInfoVo> getSiteGroupAppList();
}
