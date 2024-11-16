package com.niu.core.service.admin.sys;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageResult;
import com.niu.core.entity.sys.SysArea;
import com.niu.core.service.admin.sys.param.SysAreaSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.sys.vo.SysAreaByCodeVo;
import com.niu.core.service.admin.sys.vo.SysAreaListVo;

import java.util.List;
import java.util.Map;

/**
 * 地址服务接口
 */
public interface ISysAreaService {

    List<SysArea> getListByPid(Integer pid);

    JSONArray getAreaTree(Integer level);

    Map<String, SysAreaByCodeVo> getAreaByAreaCode(Integer id);

    Integer getAreaId(String name, Integer level);

    String getAreaName(Integer id);

    /**
     * 地址列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysAreaListVo>
     */
    PageResult<SysAreaListVo> list(PageParam pageParam, SysAreaSearchParam searchParam);

    /**
     * 逆地址解析
     * @param location
     */
    JSONObject getAddressInfo(String location);

    /**
     * 地址解析
     * @param address
     * @return
     */
    JSONObject getAddress(String address);
}
