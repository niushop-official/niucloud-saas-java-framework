package com.niu.core.service.core.niucloud;

import com.niu.core.service.core.niucloud.param.SetAuthorizeParam;
import com.niu.core.service.core.niucloud.vo.NiucloudConfigVo;

public interface ICoreNiucloudConfigService {

    NiucloudConfigVo getNiucloudConfig();

    void setNiucloudConfig(SetAuthorizeParam param);
}
