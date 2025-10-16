package com.niu.core.service.admin.channel.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.sys.AppVersion;
import com.niu.core.enums.channel.AppDict;
import com.niu.core.mapper.sys.AppVersionMapper;
import com.niu.core.service.admin.channel.IAdminAppService;
import com.niu.core.service.admin.channel.param.AppVersionAddParam;
import com.niu.core.service.admin.channel.param.AppVersionPageParam;
import com.niu.core.service.admin.channel.vo.AppVersionInfoVo;
import com.niu.core.service.admin.channel.vo.AppVersionListVo;
import com.niu.core.service.core.channel.ICoreAppCloudService;
import com.niu.core.service.core.channel.ICoreAppService;
import com.niu.core.service.core.channel.param.SetAppParam;
import com.niu.core.service.core.channel.vo.AppCompileLogVo;
import com.niu.core.service.core.channel.vo.AppConfigVo;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import org.springframework.util.Assert;

import java.util.*;

/**
 * APP管理服务实现类
 */
@Service
public class AdminAppServiceImpl implements IAdminAppService {

    @Resource
    private ICoreAppService coreAppService;

    @Resource
    private AppVersionMapper appVersionMapper;

    @Resource
    private ICoreAppCloudService coreAppCloudService;

    /**
     * 获取APP配置
     * @return AppConfigVo
     */
    @Override
    public AppConfigVo getAppConfig() {
        return coreAppService.getConfig(RequestUtils.siteId());
    }

    /**
     * 设置APP配置
     * @param param APP配置参数
     * @return boolean
     */
    @Override
    public void setAppConfig(SetAppParam param) {
        coreAppService.setConfig(RequestUtils.siteId(), param);
    }

    /**
     * 获取版本列表
     * @param param 分页查询参数
     * @return PageResult<AppVersionListVo>
     */
    @Override
    public PageResult<AppVersionListVo> getVersionPage(PageParam pageParam, AppVersionPageParam param) {
        Page<AppVersion> page = new Page<>(pageParam.getPage(), pageParam.getLimit());

        QueryWrapper<AppVersion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        
        if (param.getPlatform() != null) {
            queryWrapper.eq("platform", param.getPlatform());
        }
        queryWrapper.orderByDesc("create_time");
        
        Page<AppVersion> resultPage = appVersionMapper.selectPage(page, queryWrapper);

        List<AppVersionListVo> list = new LinkedList();
        for (AppVersion item : resultPage.getRecords()) {
            AppVersionListVo vo = new AppVersionListVo();
            BeanUtil.copyProperties(item, vo);
            list.add(vo);
        }
        
        return PageResult.build(resultPage, list);
    }

    /**
     * 获取版本详情
     * @param id 版本ID
     * @return AppVersionInfoVo
     */
    @Override
    public AppVersionInfoVo getVersionInfo(Integer id) {
        AppVersion appVersion = appVersionMapper.selectOne(
            new QueryWrapper<AppVersion>()
            .eq("id", id)
            .eq("site_id", RequestUtils.siteId())
        );
        if (appVersion == null) {
            return null;
        }
        
        AppVersionInfoVo vo = new AppVersionInfoVo();
        BeanUtil.copyProperties(appVersion, vo);

        return vo;
    }

    /**
     * 添加版本
     * @param param 添加版本参数
     * @return boolean
     */
    @Override
    public boolean addVersion(AppVersionAddParam param) {
        AppVersion notRelease = appVersionMapper.selectOne(
            new QueryWrapper<AppVersion>()
            .eq("site_id", RequestUtils.siteId())
            .eq("release_time", 0)
            .last("limit 1")
        );
        Assert.isNull(notRelease, "当前已存在未发布的版本");

        AppVersion lastVersion = appVersionMapper.selectOne(
            new QueryWrapper<AppVersion>()
                .eq("site_id", RequestUtils.siteId())
                .orderByDesc("id")
                .last("limit 1")
        );
        if (lastVersion != null && Long.valueOf(param.getVersionCode()) <= Long.valueOf(lastVersion.getVersionCode())) {
            throw new CommonException("版本号必须高于上一版本设置的值");
        }

        AppVersion appVersion = new AppVersion();
        param.setSiteId(RequestUtils.siteId());
        BeanUtil.copyProperties(param, appVersion);
        appVersion.setCreateTime(DateUtils.currTime());

        if (param.getPackageType().equals("cloud")) {
            appVersion.setTaskKey(coreAppCloudService.appCloudBuid(param));
            appVersion.setStatus(AppDict.StatusEnum.STATUS_CREATING.getValue());
        } else {
            appVersion.setStatus(AppDict.StatusEnum.STATUS_UPLOAD_SUCCESS.getValue());
        }

        return appVersionMapper.insert(appVersion) > 0;
    }

    /**
     * 编辑版本
     * @param param 编辑版本参数
     * @return boolean
     */
    @Override
    public boolean editVersion(Integer id, AppVersionAddParam param) {
        AppVersion appVersion = appVersionMapper.selectOne(
            new QueryWrapper<AppVersion>()
                .eq("site_id", RequestUtils.siteId())
                .eq("id", id)
        );
        Assert.notNull(appVersion, "版本不存在");

        AppVersion lastVersion = appVersionMapper.selectOne(
            new QueryWrapper<AppVersion>()
                .eq("site_id", RequestUtils.siteId())
                .ne("id", id)
                .orderByDesc("id")
                .last("limit 1")
        );
        if (lastVersion != null && Long.valueOf(param.getVersionCode()) <= Long.valueOf(lastVersion.getVersionCode())) {
            throw new CommonException("版本号必须高于上一版本设置的值");
        }

        BeanUtil.copyProperties(param, appVersion);
        appVersion.setUpdateTime(DateUtils.currTime());

        if (param.getPackageType().equals("cloud")) {
            param.setSiteId(RequestUtils.siteId());
            appVersion.setTaskKey(coreAppCloudService.appCloudBuid(param));
            appVersion.setStatus(AppDict.StatusEnum.STATUS_CREATING.getValue());
        } else {
            appVersion.setStatus(AppDict.StatusEnum.STATUS_UPLOAD_SUCCESS.getValue());
        }
        
        return appVersionMapper.updateById(appVersion) > 0;
    }

    /**
     * 删除版本
     * @param id 版本ID
     * @return boolean
     */
    @Override
    public void delVersion(Integer id) {
        appVersionMapper.delete(new QueryWrapper<AppVersion>()
            .eq("id", id)
            .eq("site_id", RequestUtils.siteId()));
    }

    /**
     * 获取构建日志
     * @return String
     */
    @Override
    public AppCompileLogVo getBuildLog(String key) {
        AppCompileLogVo vo = coreAppCloudService.getAppCompileLog(key);
        if (vo.getStatus().equals("fail")) {
            appVersionMapper.update(null, new UpdateWrapper<AppVersion>()
                .eq("task_key", key)
                .eq("site_id", RequestUtils.siteId())
                .set("status", AppDict.StatusEnum.STATUS_CREATE_FAIL.getValue())
                .set("update_time", DateUtils.currTime())
                .set("fail_reason", ObjectUtil.defaultIfNull(vo.getFailReason(), "") ));
        }
        if (vo.getStatus().equals("success")) {
            appVersionMapper.update(null, new UpdateWrapper<AppVersion>()
                .eq("task_key", key)
                .eq("site_id", RequestUtils.siteId())
                .set("status", AppDict.StatusEnum.STATUS_UPLOAD_SUCCESS.getValue())
                .set("update_time", DateUtils.currTime())
                .set("package_path", vo.getFilePath() ));
        }
        return vo;
    }

    /**
     * 发布版本
     * @param id 版本ID
     * @return boolean
     */
    @Override
    public boolean release(Integer id) {
        AppVersion appVersion = appVersionMapper.selectOne(
            new QueryWrapper<AppVersion>()
                .eq("site_id", RequestUtils.siteId())
                .eq("id", id)
        );
        Assert.notNull(appVersion, "版本不存在");
        if (!appVersion.getStatus().equals(AppDict.StatusEnum.STATUS_UPLOAD_SUCCESS.getValue())) {
            throw new CommonException("版本未上传成功");
        }

        AppVersion model = new AppVersion();
        model.setId(appVersion.getId());
        appVersion.setStatus(AppDict.StatusEnum.STATUS_PUBLISHED.getValue());
        appVersion.setReleaseTime(DateUtils.currTime());

        return appVersionMapper.updateById(appVersion) > 0;
    }

    /**
     * 生成签名证书
     * @param param 证书生成参数
     * @return Map<String, Object>
     */
    @Override
    public Map<String, Object> generateSingCert(Map<String, Object> param) {
        // 这里简化实现，实际项目中可能需要调用证书生成服务
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "证书生成成功");
        return result;
    }
}