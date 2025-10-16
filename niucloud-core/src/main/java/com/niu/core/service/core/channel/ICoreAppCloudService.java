package com.niu.core.service.core.channel;

import com.niu.core.service.admin.channel.param.AppVersionAddParam;
import com.niu.core.service.core.channel.param.GenerateSignCertParam;
import com.niu.core.service.core.channel.vo.AppCompileLogVo;

public interface ICoreAppCloudService {

    /**
     * 上传APP
     * @param param 应用版本添加参数
     * @return 应用云版本ID
     */
    String appCloudBuid(AppVersionAddParam param);

    /**
     * 获取构建日志
     * @param key 构建日志键值
     * @return AppCompileLogVo
     */
    AppCompileLogVo getAppCompileLog(String key);

    /**
     * 生成证书
     * @param param 生成证书参数
     * @return 单证书内容
     */
    String generateSignCert(GenerateSignCertParam param);
}
