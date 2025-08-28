package com.niu.core.service.admin.niucloud;

import cn.hutool.json.JSONObject;
import com.niu.core.service.admin.niucloud.param.ConnectTestParam;
import com.niu.core.service.admin.niucloud.vo.AuthInfoVo;
import com.niu.core.service.admin.niucloud.vo.FrameWorkVersion;
import com.niu.core.service.admin.niucloud.vo.ModuleListVo;

import java.util.List;
import java.util.Map;

public interface ICloudBuildService {

    JSONObject build(String mode);

    JSONObject buildPreCheck();

    JSONObject getBuildTask(String mode);

    void clearBuildTask();

    JSONObject getBuildLog(String mode);

    /**
     * 获取本地地址
     * @return
     */
    Map<String, Object> getLocalCloudCompileConfig();

    /**
     * 设置 本地服务器地址
     * @param param
     */
    void setLocalCloudCompileConfig(ConnectTestParam param);

    /**
     * 连通测试
     * @param checkLocal
     * @param url
     * @return
     */
    Boolean connectTest(Boolean checkLocal, String url);
}
