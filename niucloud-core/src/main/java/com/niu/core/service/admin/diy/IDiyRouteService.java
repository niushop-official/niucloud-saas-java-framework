package com.niu.core.service.admin.diy;

import cn.hutool.json.JSONObject;
import com.niu.core.service.admin.diy.param.DiyRouteSearchParam;
import com.niu.core.service.admin.diy.param.DiyRouteShareParam;
import com.niu.core.service.admin.diy.vo.DiyRouteInfoVo;
import com.niu.core.service.admin.diy.vo.DiyRouteListVo;
import com.niu.core.common.domain.PageParam;

import java.util.List;

/**
 * 自定义路由服务接口类
 */
public interface IDiyRouteService {

    /**
     * 自定义路由列表
     *
     * @param searchParam 搜索参数
     * @return PageResult<DiyRouteListedVo>
     */
    List<DiyRouteListVo> list(DiyRouteSearchParam searchParam);

    /**
     * 自定义路由详情
     * @return DiyRouteInfoVo
     */
    DiyRouteInfoVo getInfoByName(String name);

    /**
     * 修改页面分享内容
     * @param editParam
     */
    void modifyShare(DiyRouteShareParam editParam);

    JSONObject getApps();
}
