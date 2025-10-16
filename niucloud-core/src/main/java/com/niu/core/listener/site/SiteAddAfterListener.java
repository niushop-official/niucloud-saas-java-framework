package com.niu.core.listener.site;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.entity.diy.DiyTheme;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.entity.sys.SysAttachment;
import com.niu.core.entity.sys.SysAttachmentCategory;
import com.niu.core.entity.sys.SysPoster;
import com.niu.core.event.site.SiteAddAfterEvent;
import com.niu.core.mapper.diy.DiyThemeMapper;
import com.niu.core.mapper.sys.SysAttachmentCategoryMapper;
import com.niu.core.mapper.sys.SysAttachmentMapper;
import com.niu.core.mapper.sys.SysPosterMapper;
import com.niu.core.service.admin.diy.IDiyService;
import com.niu.core.service.admin.diy.param.SetDiyDataParam;
import com.niu.core.service.core.diy.ICoreDiyService;
import com.niu.core.service.core.poster.ICorePosterService;
import com.niu.core.service.core.poster.param.AddPosterParam;
import com.niu.core.service.core.site.ICoreSiteService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@EventListen("core")
@Component
public class SiteAddAfterListener extends AbstractListener {

    private IDiyService diyService() {
        return SpringContext.bean(IDiyService.class);
    }

    @Resource
    private SysAttachmentCategoryMapper categoryMapper;

    @Resource
    private SysAttachmentMapper attachmentMapper;

    @Resource
    private SysPosterMapper posterMapper;

    @Resource
    private ICorePosterService corePosterService;

    @Resource
    private ICoreDiyService coreDiyService;

    @Resource
    private ICoreSiteService coreSiteService;

    @Resource
    private DiyThemeMapper diyThemeMapper;

    /**
     * 监听事件
     *
     * @param event
     */
    public void handleEvent(SiteAddAfterEvent event) {

        SiteGroup siteGroup = event.getSiteGroup();
        JSONArray app = JSONUtil.parseArray(siteGroup.getApp());
        Integer siteId = event.getSiteId();

        SysAttachmentCategory category = categoryMapper.selectOne(
                new LambdaQueryWrapper<SysAttachmentCategory>()
                        .eq(SysAttachmentCategory::getSiteId, siteId)
                        .eq(SysAttachmentCategory::getName, "默认素材")
                        .select(SysAttachmentCategory::getId)
        );

        Integer categoryId;
        if (category == null) {
            SysAttachmentCategory newCategory = new SysAttachmentCategory();
            newCategory.setSiteId(siteId);
            newCategory.setPid(0);
            newCategory.setType("image");
            newCategory.setName("默认素材");
            newCategory.setSort(0);
            categoryMapper.insert(newCategory);
            categoryId = newCategory.getId();
        } else {
            categoryId = category.getId();
        }


        JSONArray attachments = new JsonModuleLoader().getResultSet("core", "attachment.json");
        List<SysAttachment> defaultAttachments = new ArrayList<>();
        for (int i = 0; i < attachments.size(); i++) {
            JSONObject json = attachments.getJSONObject(i);
            defaultAttachments.add(new SysAttachment().builder()
                    .name(json.getStr("name"))
                    .realName(json.getStr("real_name"))
                    .path(json.getStr("path"))
                    .url(json.getStr("url"))
                    .dir(json.getStr("dir"))
                    .attSize(json.getLong("att_size"))
                    .attType(json.getStr("att_type"))
                    .storageType(json.getStr("storage_type")).build());
        }

        Set<String> configPaths = defaultAttachments.stream()
                .map(SysAttachment::getPath)
                .collect(Collectors.toSet());

        List<SysAttachment> existingAttachments = attachmentMapper.selectList(
                new LambdaQueryWrapper<SysAttachment>()
                        .eq(SysAttachment::getSiteId, siteId)
                        .in(SysAttachment::getPath, configPaths)
        );

        Set<String> existingPaths = Optional.ofNullable(existingAttachments)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(SysAttachment::getPath)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());

        long currentTime = DateUtils.currTime();
        List<SysAttachment> attachmentsToAdd = defaultAttachments.stream()
                .filter(att -> StringUtils.isNotBlank(att.getPath()))
                .filter(att -> !existingPaths.contains(att.getPath()))
                .map(att -> {
                    SysAttachment sysAttachment = new SysAttachment();
                    BeanUtil.copyProperties(att, sysAttachment);
                    sysAttachment.setSiteId(siteId);
                    sysAttachment.setCateId(categoryId);
                    sysAttachment.setCreateTime(currentTime);
                    sysAttachment.setName(currentTime + siteId + categoryId + att.getName());
                    return sysAttachment;
                })
                .collect(Collectors.toList());


        if (CollectionUtil.isNotEmpty(attachmentsToAdd)) {
            attachmentMapper.insert(attachmentsToAdd);
        }

        posterMapper.delete(new LambdaQueryWrapper<SysPoster>()
                .eq(SysPoster::getSiteId, siteId)
                .eq(SysPoster::getType, "friendspay"));
        JSONObject jsonObject = (JSONObject) corePosterService.template("", "friendspay").get(0);
        corePosterService.add(new AddPosterParam().builder()
                .siteId(siteId)
                .name(jsonObject.get("name").toString())
                .type(jsonObject.get("type").toString())
                .value(jsonObject.get("data").toString())
                .status(1)
                .isDefault(1)
                .addon("core")
                .build());

//        SetDiyDataParam setParam = new SetDiyDataParam();
//        setParam.setSiteId(event.getSiteId());
//        setParam.setType("index");
//        setParam.setKey("DIY_INDEX");
//        setParam.setIsStart(app.size() > 1 ? 1 : 0);
//        setParam.setMainApp(app);
//        diyService().setDiyData(setParam);
//
//        setParam.setType("member_index");
//        setParam.setKey("DIY_MEMBER_INDEX");
//        diyService().setDiyData(setParam);

        //创建主题色
        createThemeColor(siteId);

        coreSiteService.addSiteAddonInitRecord(siteId, "core");

    }

    private void createThemeColor(Integer siteId) {
        boolean exists = diyThemeMapper.exists(new LambdaQueryWrapper<DiyTheme>()
                .eq(DiyTheme::getSiteId, siteId)
                .eq(DiyTheme::getAddon, "app"));
        if (exists){
            return;
        }
        JSONObject addonTheme = coreDiyService.getDefaultThemeColor("app");
        String theme = addonTheme.getJSONArray("theme_color").getJSONObject(0).get("theme").toString();
        String title = addonTheme.getJSONArray("theme_color").getJSONObject(0).get("title").toString();
        diyThemeMapper.insert(new DiyTheme().builder()
                .siteId(siteId)
                .title(title)
                .type("app")
                .addon("app")
                .themeType("default")
                .defaultTheme(theme)
                .theme(theme)
                .isSelected(1)
                .createTime(DateUtils.currTime()).build());
    }

}