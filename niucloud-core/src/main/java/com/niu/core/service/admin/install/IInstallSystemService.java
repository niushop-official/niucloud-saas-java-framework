package com.niu.core.service.admin.install;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.home.param.HomeSiteAddParam;
import com.niu.core.service.admin.home.param.SiteParam;
import com.niu.core.service.admin.home.param.SiteSearchParam;
import com.niu.core.service.admin.home.vo.SiteInfoVo;
import com.niu.core.service.admin.home.vo.SiteListVo;
import com.niu.core.service.admin.home.vo.UserCreateSiteVo;
import com.niu.core.service.admin.install.vo.InstallMenuVo;

import java.util.List;
import java.util.Map;

public interface IInstallSystemService {


    /**
     * 安装
     *
     */
    void install();

    /**
     * 菜单安装
     *
     */
    void installMenu();

    /**
     * 加载菜单
     *
     * @param appType
     * @return
     */
    List<InstallMenuVo> loadMenu(String appType);


}
