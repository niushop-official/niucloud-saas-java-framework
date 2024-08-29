package com.niu.core.service.api.sys;

import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.Result;
import com.niu.core.entity.sys.SysArea;
import com.niu.core.service.api.sys.param.SysAreaAddressByLatlngParam;
import com.niu.core.service.api.sys.vo.SysAreaLevelVo;
import com.niu.core.service.api.sys.vo.SysAreaListVo;

import java.util.List;

public interface ISysAreaService {

    /**
     * @param id
     * @return
     */
    SysArea getAreaById(Integer id);

    /**
     * 根据PID返回区域列表
     *
     * @param pid
     * @return
     */
    List<SysAreaListVo> getListByPid(Integer pid);

    /**
     * 根据级别返回城市
     *
     * @param level
     * @return
     */
    List<SysAreaLevelVo> getAreaTree(Integer level);

    /**
     * @param code
     * @return
     */
    JSONObject getAreaByAreaCode(Integer code);

    /**
     * @param param
     * @return
     */
    JSONObject getAddressByLatlng(SysAreaAddressByLatlngParam param);


}
