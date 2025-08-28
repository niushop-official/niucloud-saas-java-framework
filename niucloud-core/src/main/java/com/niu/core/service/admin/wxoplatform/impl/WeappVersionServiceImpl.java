package com.niu.core.service.admin.wxoplatform.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.WechatUtils;
import com.niu.core.entity.oplatform.WxOplatfromWeappVersion;
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.entity.sys.SysConfig;
import com.niu.core.entity.weapp.WeappVersion;
import com.niu.core.enums.channel.WeappVersionStatusEnum;
import com.niu.core.enums.sys.ConfigKeyEnum;
import com.niu.core.mapper.oplatform.WxOplatfromWeappVersionMapper;
import com.niu.core.mapper.site.SiteGroupMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.mapper.sys.SysConfigMapper;
import com.niu.core.mapper.weapp.WeappVersionMapper;
import com.niu.core.service.admin.pay.vo.PayListVo;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.admin.wxoplatform.IWeappVersionService;
import com.niu.core.service.admin.wxoplatform.param.SyncSiteGroupAuthWeappParam;
import com.niu.core.service.admin.wxoplatform.param.UndoAuditParam;
import com.niu.core.service.admin.wxoplatform.vo.OplatformConfigVo;
import com.niu.core.service.admin.wxoplatform.vo.SiteGroupWeappVersionVo;
import com.niu.core.service.admin.wxoplatform.vo.WxOplatfromWeappVersionVo;
import com.niu.core.service.core.site.ICoreSiteService;
import com.niu.core.service.core.weapp.ICoreWeappCloudService;
import com.niu.core.service.core.weapp.ICoreWeappConfigService;
import com.niu.core.service.core.weapp.param.WeappUploadParam;
import com.niu.core.service.core.weapp.vo.WeappConfigVo;
import com.niu.core.service.core.wxoplatform.ICoreOplatformConfigService;
import com.niu.core.service.core.wxoplatform.ICoreOplatformService;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.open.api.WxOpenComponentService;
import me.chanjar.weixin.open.api.WxOpenMaService;
import me.chanjar.weixin.open.bean.WxOpenMaCodeTemplate;
import me.chanjar.weixin.open.bean.message.WxOpenMaSubmitAuditMessage;
import me.chanjar.weixin.open.bean.result.WxOpenMaGetCodePrivacyInfoResult;
import me.chanjar.weixin.open.bean.result.WxOpenMaSubmitAuditResult;
import me.chanjar.weixin.open.bean.result.WxOpenResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class WeappVersionServiceImpl implements IWeappVersionService {

    private final WxOplatfromWeappVersionMapper wxOplatfromWeappVersionMapper;
    private final SiteGroupMapper siteGroupMapper;
    private final ICoreOplatformConfigService coreOplatformConfigService;
    private final ICoreWeappCloudService coreWeappCloudService;
    private IWeappVersionService weappVersionService;
    private final SiteMapper siteMapper;
    private final WeappVersionMapper weappVersionMapper;
    private final ICoreWeappConfigService coreWeappConfigService;
    private final SysConfigMapper sysConfigMapper;
    private final ICoreSiteService coreSiteService;
    private ScheduledExecutorService scheduler;


    @Autowired
    public void setWeappVersionService(@Lazy IWeappVersionService weappVersionService) {
        this.weappVersionService = weappVersionService;
    }

    /**
     * 获取最后一次提交记录
     *
     * @return
     */
    @Override
    public WxOplatfromWeappVersionVo getLastCommitRecord() {
        QueryWrapper<WxOplatfromWeappVersion> queryWrapper=new QueryWrapper<>();
        queryWrapper.orderByDesc("id")
                .last("limit 1");
        WxOplatfromWeappVersion wxOplatfromWeappVersion=wxOplatfromWeappVersionMapper.selectOne(queryWrapper);
        if (wxOplatfromWeappVersion==null) return null;
        WxOplatfromWeappVersionVo wxOplatfromWeappVersionVo=new WxOplatfromWeappVersionVo();
        BeanUtils.copyProperties(wxOplatfromWeappVersion, wxOplatfromWeappVersionVo);
        return wxOplatfromWeappVersionVo;
    }

    /**
     * 小程序版本列表分页
     *
     * @param pageParam
     * @return
     */
    @Override
    public PageResult<WxOplatfromWeappVersionVo> list(PageParam pageParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<WxOplatfromWeappVersion> queryWrapper=new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        MPJQueryWrapper<WxOplatfromWeappVersion> wxOplatfromMPJQueryWrapper = new MPJQueryWrapper<>();
        wxOplatfromMPJQueryWrapper.select("wowv.id, wowv.site_group_id, wowv.template_id, wowv.user_version, wowv.user_desc, wowv.task_key, wowv.status, wowv.fail_reason, wowv.version_no, wowv.create_time, wowv.update_time, nsg.group_name as site_group_name")
                .setAlias("wowv")
                .leftJoin("?_site_group nsg ON nsg.group_id = wowv.site_group_id".replace("?_", GlobalConfig.tablePrefix));
        wxOplatfromMPJQueryWrapper.orderByDesc("wowv.id");

        IPage<WxOplatfromWeappVersionVo> iPage = wxOplatfromWeappVersionMapper.selectJoinPage(new Page<>(page, limit), WxOplatfromWeappVersionVo.class, wxOplatfromMPJQueryWrapper);
        return PageResult.build(page, limit, iPage.getTotal()).setData(iPage.getRecords());
    }

    /**
     * 添加小程序版本
     */
    @Override
    public void add(Integer siteGroupId, boolean isAll) {
        List<SiteGroup> siteGroupList = siteGroupMapper.selectList(new QueryWrapper<SiteGroup>().gt("group_id", 0).orderByAsc("create_time"));
        if (ObjectUtil.isEmpty(siteGroupList)) throw new CommonException("请先添加站点套餐");

        SiteGroup siteGroup = siteGroupId == null || siteGroupId == 0 ? siteGroupList.get(0) : siteGroupMapper.selectById(siteGroupId);

        WxOplatfromWeappVersion uploading = wxOplatfromWeappVersionMapper.selectOne(new QueryWrapper<WxOplatfromWeappVersion>()
            .eq("site_group_id", siteGroup.getGroupId())
            .eq("status", 0)
            .last("limit 1")
        );
        if (uploading != null) throw new CommonException("小程序有正在上传的版本，请等待上一版本上传完毕后再进行操作");

        WxOplatfromWeappVersion lastVersion = wxOplatfromWeappVersionMapper.selectOne(new QueryWrapper<WxOplatfromWeappVersion>()
            .select("version_no")
            .eq("site_group_id", siteGroup.getGroupId())
            .orderByDesc("id")
            .last("limit 1")
        );

        OplatformConfigVo config = coreOplatformConfigService.getWxOplatformConfig();

        List<String> addon = CollUtil.newArrayList();
        if (!siteGroup.getApp().isEmpty()) CollUtil.addAll(addon, JSONUtil.parseArray(siteGroup.getApp()));
        if (!siteGroup.getAddon().isEmpty()) CollUtil.addAll(addon, JSONUtil.parseArray(siteGroup.getAddon()));

        WeappUploadParam weappUploadParam = new WeappUploadParam();
        weappUploadParam.setAppId(config.getDevelopAppId());
        weappUploadParam.setBaseUrl(RequestUtils.getDomain(true));
        weappUploadParam.setSiteId(0);
        weappUploadParam.setVersion("1." + siteGroup.getGroupId() + "." + (lastVersion == null ? 1 : lastVersion.getVersionNo() + 1));
        weappUploadParam.setUploadPrivateKey(config.getDevelopUploadPrivateKey());
        weappUploadParam.setAddon(addon);
        String taskKey = coreWeappCloudService.uploadWeapp(weappUploadParam);

        WxOplatfromWeappVersion model = new WxOplatfromWeappVersion();
        model.setSiteGroupId(siteGroup.getGroupId());
        model.setUserVersion(weappUploadParam.getVersion());
        model.setVersionNo(lastVersion == null ? 1 : lastVersion.getVersionNo() + 1);
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setTaskKey(taskKey);
        wxOplatfromWeappVersionMapper.insert(model);

        weappVersionService.getVersionUploadResult(taskKey, isAll);
    }

    /**
     * 获取小程序版本提交结果
     * @param taskKey
     */
    @Async
    public void getVersionUploadResult(String taskKey, boolean isAll) {
        if (scheduler == null || scheduler.isShutdown() || scheduler.isTerminated()) {
            scheduler = Executors.newScheduledThreadPool(1);
        }
        scheduler.schedule(() -> checkVersionUploadResult(taskKey, isAll), 10, TimeUnit.SECONDS);
    }

    private void checkVersionUploadResult(String taskKey, boolean isAll) {
        JSONObject log = coreWeappCloudService.getWeappCompileLog(taskKey);
        System.out.println("checkVersionUploadResult ++++++++++ " + log.toString());

        if (log != null) {
            JSONArray data = ObjectUtil.defaultIfNull(log.getByPath("data.0", JSONArray.class), new JSONArray());
            if (data.size() > 0) {
                JSONObject last = data.getJSONObject(data.size() - 1);
                if (last.getInt("code", -1).equals(0)) {
                    WxOplatfromWeappVersion model = new WxOplatfromWeappVersion();
                    model.setStatus(WeappVersionStatusEnum.APPLET_UPLOAD_FAIL.getStatus());
                    model.setFailReason(last.getStr("msg", ""));
                    model.setUpdateTime(System.currentTimeMillis() / 1000);
                    wxOplatfromWeappVersionMapper.update(model, new QueryWrapper<WxOplatfromWeappVersion>().eq("task_key", taskKey));
                    scheduler.shutdown();
                    weappVersionService.uploadSuccess(taskKey, isAll);
                    return;
                }
                if (last.getInt("percent", 0).equals(100)) {
                    WxOplatfromWeappVersion model = new WxOplatfromWeappVersion();
                    model.setStatus(WeappVersionStatusEnum.APPLET_UPLOAD_SUCCESS.getStatus());
                    model.setUpdateTime(System.currentTimeMillis() / 1000);
                    wxOplatfromWeappVersionMapper.update(model, new QueryWrapper<WxOplatfromWeappVersion>().eq("task_key", taskKey));
                    scheduler.shutdown();
                    weappVersionService.uploadSuccess(taskKey, isAll);
                    return;
                }
                scheduler.schedule(() -> checkVersionUploadResult(taskKey, isAll), 10, TimeUnit.SECONDS);
            }
        } else {
            scheduler.shutdown();
        }
    }

    /**
     * 小程序模板上传成功
     * @param taskKey
     */
    @Async
    public void uploadSuccess(String taskKey, boolean isAll) {
        WxOplatfromWeappVersion version = wxOplatfromWeappVersionMapper.selectOne(new QueryWrapper<WxOplatfromWeappVersion>().eq("task_key", taskKey));

        WxOpenComponentService wxOpenService = WechatUtils.WxOpen().getWxOpenComponentService();

        try {
            List<WxOpenMaCodeTemplate> draftList = wxOpenService.getTemplateDraftList();
            if (ObjectUtil.isNotEmpty(draftList)) {
                for (WxOpenMaCodeTemplate item : draftList) {
                    if (item.getUserVersion().equals(version.getUserVersion())) {
                        // 添加模板
                        wxOpenService.addToTemplate(item.getDraftId());

                        List<WxOpenMaCodeTemplate> templateList = wxOpenService.getTemplateList();
                        for (WxOpenMaCodeTemplate template : templateList) {
                            if (template.getUserVersion().equals(version.getUserVersion())) {
                                version.setTemplateId(template.getTemplateId().toString());
                                wxOplatfromWeappVersionMapper.updateById(version);

                                // 删除之前的模板
                                WxOplatfromWeappVersion prev = wxOplatfromWeappVersionMapper.selectOne(new QueryWrapper<WxOplatfromWeappVersion>()
                                    .select("template_id")
                                    .eq("site_group_id", version.getSiteGroupId())
                                    .lt("id", version.getId())
                                    .orderByDesc("id")
                                    .last("limit 1")
                                );
                                if (prev!= null) {
                                    wxOpenService.deleteTemplate(Long.valueOf(prev.getTemplateId()));
                                }
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("小程序模板上传成功获取模板id异常");
            e.printStackTrace();
        }

        if (isAll) {
            SiteGroup siteGroup = siteGroupMapper.selectOne(new QueryWrapper<SiteGroup>()
                .select("group_id")
                .gt("group_id", version.getSiteGroupId())
                .orderByDesc("group_id")
                .last("limit 1")
            );
            if (siteGroup != null) {
                weappVersionService.add(siteGroup.getGroupId(), true);
            }
        }
    }

    @Async
    public void weappCommit(Integer siteId, Integer siteGroupId, WxOplatfromWeappVersion version) {
        if (version == null) {
            version = wxOplatfromWeappVersionMapper.selectOne(new QueryWrapper<WxOplatfromWeappVersion>()
                .eq("site_group_id", siteGroupId)
                .ne("template_id", "")
                .orderByDesc("id")
                .last("limit 1"));
            if (version == null) throw new CommonException("平台尚未上传小程序到模板库");
        }

        WeappVersion uploading = weappVersionMapper.selectOne(new QueryWrapper<WeappVersion>().select("site_id").eq("site_id", siteId).eq("status", WeappVersionStatusEnum.APPLET_AUDITING.getStatus()).last("limit 1"));
        if (uploading!= null) throw new CommonException("小程序有正在上传的版本，请等待上一版本上传完毕后再进行操作");

        WeappConfigVo weappCofig = coreWeappConfigService.getWeappConfig(siteId);

        try {
            JSONObject extJson = new JSONObject();
            extJson.set("extAppid", weappCofig.getAppId());
            extJson.set("entryPagePath", "app/pages/index/index");
            extJson.set("directCommit", true);
            extJson.putByPath("ext.site_id", siteId);

            WxOpenResult commitResult = WechatUtils.WxOpen().getWxOpenComponentService()
                .getWxMaServiceByAppid(weappCofig.getAppId())
                .codeCommit(Long.valueOf(version.getTemplateId()), version.getUserVersion(), version.getUserDesc(), extJson);

            if (!commitResult.getErrcode().equals("0")) throw new CommonException(commitResult.getErrmsg());

            WeappVersion model = new WeappVersion();
            model.setSiteId(siteId);
            model.setVersion(version.getUserVersion());
            model.setVersionNo(version.getVersionNo());
            model.setDesc(version.getUserDesc());
            model.setStatus(WeappVersionStatusEnum.APPLET_AUDITING.getStatus());
            model.setCreateTime(System.currentTimeMillis() / 1000);
            model.setFromType("open_platform");
            weappVersionMapper.insert(model);

            // 提交审核
            weappVersionService.submitAudit(siteId, model.getId());
        } catch (Exception e) {
            System.out.println("小程序提交代码异常");
            e.printStackTrace();
            throw new CommonException(e.getMessage());
        }
    }

    /**
     *
     * @param siteId
     * @param versionId
     */
    @Async
    public void submitAudit(Integer siteId, Integer versionId) {
        WeappVersion version = weappVersionMapper.selectById(versionId);

        try {
            WxOpenMaService wxOpenMaService = (WxOpenMaService) WechatUtils.miniapp(siteId);

            WxOpenMaGetCodePrivacyInfoResult privacyInfo = wxOpenMaService.getCodePrivacyInfo();
            if (!privacyInfo.getErrcode().equals("0")) {
                version.setStatus(WeappVersionStatusEnum.APPLET_AUDIT_FAIL.getStatus());
                version.setFailReason(privacyInfo.getErrmsg());
                version.setUpdateTime(System.currentTimeMillis() / 1000);
                weappVersionMapper.updateById(version);
                return;
            }

            WxOpenMaSubmitAuditMessage submitAuditMessage = new WxOpenMaSubmitAuditMessage();
            WxOpenMaSubmitAuditResult submitResult = wxOpenMaService.submitAudit(submitAuditMessage);

            version.setStatus(submitResult.getErrcode().equals("0") ? WeappVersionStatusEnum.APPLET_AUDITING.getStatus() :  WeappVersionStatusEnum.APPLET_AUDIT_FAIL.getStatus());
            version.setFailReason(submitResult.getErrmsg());
            version.setUpdateTime(System.currentTimeMillis() / 1000);
            version.setAuditid(ObjectUtil.isNotNull(submitResult.getAuditId()) ? submitResult.getAuditId().toString() : "");

            weappVersionMapper.updateById(version);

            if (scheduler != null && !scheduler.isShutdown()) {
                scheduler.shutdown();
            }
        } catch (WxErrorException e) {
            // 如果检测任务结束未结束
            if (e.getError().getErrorCode() == 61039) {
                if (scheduler == null || scheduler.isShutdown() || scheduler.isTerminated()) {
                    scheduler = Executors.newScheduledThreadPool(1);
                }
                scheduler.schedule(() -> submitAudit(siteId, versionId), 30, TimeUnit.SECONDS);
            } else {
                version.setStatus(WeappVersionStatusEnum.APPLET_AUDIT_FAIL.getStatus());
                version.setFailReason(e.getError().getErrorMsg());
                version.setUpdateTime(System.currentTimeMillis() / 1000);
                weappVersionMapper.updateById(version);

                if (scheduler != null && !scheduler.isShutdown()) {
                    scheduler.shutdown();
                }

                System.out.println("小程序提交审核异常");
                e.printStackTrace();
                throw new CommonException(e.getMessage());
            }
        }
    }

    /**
     * 站点小程序代码提交
     */
    @Override
    public void siteWeappCommit() {
        SiteInfoVo site = coreSiteService.getSiteCache(RequestUtils.siteId());
        this.weappCommit(site.getSiteId(), site.getGroupId(), null);
    }

    /**
     * 按站点套餐获取提交记录
     * @param pageParam
     * @return
     */
    @Override
    public PageResult<SiteGroupWeappVersionVo> getSiteGroupCommitRecord(PageParam pageParam) {
        Integer page  = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        IPage<SiteGroup> iPage = siteGroupMapper.selectPage(new Page<>(page, limit), new QueryWrapper<SiteGroup>().select("group_id,group_name"));
        List<SiteGroupWeappVersionVo> list = new LinkedList<>();

        for (SiteGroup item : iPage.getRecords()) {
            SiteGroupWeappVersionVo vo = new SiteGroupWeappVersionVo();
            BeanUtils.copyProperties(item, vo);

            WxOplatfromWeappVersion lastVersion = wxOplatfromWeappVersionMapper.selectOne(new QueryWrapper<WxOplatfromWeappVersion>().eq("site_group_id", item.getGroupId()).orderByDesc("id").last("limit 1"));
            if (lastVersion != null) {
                WxOplatfromWeappVersionVo versionVo = new WxOplatfromWeappVersionVo();
                BeanUtils.copyProperties(lastVersion, versionVo);
                vo.setCommitRecord(versionVo);
            }
            list.add(vo);
        }

        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 撤销审核
     * */
    @Override
    public void undoAudit(UndoAuditParam param) {
        WeappVersion version = weappVersionMapper.selectOne(new QueryWrapper<WeappVersion>().eq("id", param.getId()).eq("site_id", RequestUtils.siteId()));
        Assert.notNull(version, "未获取到小程序版本提交记录");
        if (!version.getStatus().equals(WeappVersionStatusEnum.APPLET_AUDITING.getStatus())) throw new CommonException("只有审核中的才可以撤回");

        WeappConfigVo weappCofig = coreWeappConfigService.getWeappConfig(RequestUtils.siteId());

        try {
            WxOpenResult commitResult = WechatUtils.WxOpen().getWxOpenComponentService()
                .getWxMaServiceByAppid(weappCofig.getAppId()).undoCodeAudit();
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }

        version.setStatus(WeappVersionStatusEnum.APPLET_AUDIT_UNDO.getStatus());
        version.setUpdateTime(System.currentTimeMillis() / 1000);
        weappVersionMapper.updateById(version);
    }

    @Override
    public void syncSiteGroupAuthWeapp(SyncSiteGroupAuthWeappParam param) {
        WxOplatfromWeappVersion version = wxOplatfromWeappVersionMapper.selectOne(new QueryWrapper<WxOplatfromWeappVersion>()
            .eq("site_group_id", param.getSiteGroupId())
            .ne("template_id", "")
            .orderByDesc("id")
            .last("limit 1")
        );
        Assert.notNull(version, "平台尚未上传小程序到模板库");

        List<Integer> siteIds = siteMapper.selectList(new QueryWrapper<Site>().select("site_id").eq("group_id", param.getSiteGroupId())).stream().map(Site::getSiteId).toList();
        if (ObjectUtil.isNotEmpty(siteIds)) {
            List<SysConfig> authSite = sysConfigMapper.selectList(new QueryWrapper<SysConfig>().in("site_id", siteIds).eq("config_key", ConfigKeyEnum.WEAPP_AUTHORIZATION_INFO.getName()));
            if (ObjectUtil.isNotEmpty(authSite)) {
                authSite.forEach(item -> {
                    weappCommit(item.getSiteId(), param.getSiteGroupId(), version);
                });
            }
        }
    }
}
