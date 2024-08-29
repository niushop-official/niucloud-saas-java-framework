package com.niu.core.service.admin.niucloud;

import cn.hutool.json.JSONObject;
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
}
