package com.niu.core.service.admin.channel;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.channel.param.AppVersionAddParam;
import com.niu.core.service.admin.channel.param.AppVersionEditParam;
import com.niu.core.service.admin.channel.param.AppVersionPageParam;
import com.niu.core.service.admin.channel.vo.AppVersionInfoVo;
import com.niu.core.service.admin.channel.vo.AppVersionListVo;
import com.niu.core.service.core.channel.param.SetAppParam;
import com.niu.core.service.core.channel.vo.AppCompileLogVo;
import com.niu.core.service.core.channel.vo.AppConfigVo;

import java.util.Map;

/**
 * APP管理服务接口
 */
public interface IAdminAppService {
    
    /**
     * 获取APP配置
     * @return AppConfigVo
     */
    AppConfigVo getAppConfig();
    
    /**
     * 设置APP配置
     * @param param APP配置参数
     * @return boolean
     */
    void setAppConfig(SetAppParam param);
    
    /**
     * 获取版本列表
     * @param param 分页查询参数
     * @return PageResult<AppVersionListVo>
     */
    PageResult<AppVersionListVo> getVersionPage(PageParam pageParam, AppVersionPageParam param);
    
    /**
     * 获取版本详情
     * @param id 版本ID
     * @return AppVersionInfoVo
     */
    AppVersionInfoVo getVersionInfo(Integer id);
    
    /**
     * 添加版本
     * @param param 版本添加参数
     * @return boolean
     */
    boolean addVersion(AppVersionAddParam param);
    
    /**
     * 编辑版本
     * @param id
     * @param param 版本编辑参数
     * @return boolean
     */
    boolean editVersion(Integer id, AppVersionAddParam param);
    
    /**
     * 删除版本
     * @param id 版本ID
     * @return boolean
     */
    void delVersion(Integer id);
    
    /**
     * 发布版本
     * @param id 版本ID
     * @return boolean
     */
    boolean release(Integer id);

    /**
     * 获取构建日志
     * @return Map<String, Object>
     */
    AppCompileLogVo getBuildLog(String key);
    
    /**
     * 生成签名证书
     * @param param 证书生成参数
     * @return Map<String, Object>
     */
    Map<String, Object> generateSingCert(Map<String, Object> param);
}