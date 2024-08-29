package com.niu.core.service.core.channel;

import cn.hutool.json.JSONObject;
import com.niu.core.service.core.channel.param.SetPcParam;
import com.niu.core.service.core.channel.vo.PcConfigVo;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;

public interface ICorePcService {

    /**
     * 获取pc端配置
     *
     * @param siteId
     * @return
     */
    PcConfigVo getPc(Integer siteId);

    /**
     * 设置pc端配置
     * @param param
     */
    void setPc(SetPcParam param);

}
