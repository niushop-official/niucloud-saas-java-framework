package com.niu.core.service.admin.site;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.site.param.SiteGroupSearchParam;
import com.niu.core.service.admin.site.param.SiteUserParam;
import com.niu.core.service.admin.site.param.SiteUserSearchParam;
import com.niu.core.service.admin.site.vo.SiteGroupListVo;
import com.niu.core.service.admin.site.vo.SiteUserVo;

public interface ISiteUserService {


    /**
     * 用户管理员列表
     * @param pageParam
     * @param searchParam
     * @return
     */
    PageResult<SiteUserVo> list(PageParam pageParam, SiteUserSearchParam searchParam);

    /**
     * 添加站点用户信息
     * @param siteUserParam
     */
    void add(SiteUserParam siteUserParam);

    /**
     * 查询用户信息
     * @param uid
     * @return
     */
    SiteUserVo getInfo(Integer uid);

    /**
     * 编辑用户信息
     * @param uid
     * @param siteUserParam
     */
    void edit(Integer uid, SiteUserParam siteUserParam);

    /**
     * 锁定用户
     * @param uid
     */
    void lock(Integer uid);

    /**
     * 解锁用户
     * @param uid
     */
    void unlock(Integer uid);

}
