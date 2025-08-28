package com.niu.shop.service.core.shop.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.entity.diy.DiyTheme;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.entity.sys.SysAttachment;
import com.niu.core.entity.sys.SysAttachmentCategory;
import com.niu.core.mapper.diy.DiyThemeMapper;
import com.niu.core.mapper.sys.SysAttachmentCategoryMapper;
import com.niu.core.mapper.sys.SysAttachmentMapper;
import com.niu.core.service.admin.diy.IDiyService;
import com.niu.core.service.admin.diy.param.DiyPageParam;
import com.niu.core.service.admin.diy.param.SetDiyDataParam;
import com.niu.core.service.core.diy.ICoreDiyService;
import com.niu.core.service.core.poster.ICorePosterService;
import com.niu.core.service.core.poster.param.AddPosterParam;
import com.niu.core.service.core.site.impl.CoreSiteServiceImpl;
import com.niu.shop.entity.delivery.ShopDeliveryCompany;
import com.niu.shop.entity.delivery.ShopDeliveryElectronicSheet;
import com.niu.shop.entity.goods.ShopGoodsLabel;
import com.niu.shop.entity.goods.ShopGoodsLabelGroup;
import com.niu.shop.mapper.delivery.ShopDeliveryElectronicSheetMapper;
import com.niu.shop.mapper.goods.ShopGoodsLabelGroupMapper;
import com.niu.shop.mapper.goods.ShopGoodsLabelMapper;
import com.niu.shop.service.admin.delivery.IShopDeliveryCompanyService;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryCompanySearchParam;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryCompanyListVo;
import com.niu.shop.service.core.shop.ICoreAddonService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CoreAddonServiceImpl implements ICoreAddonService {

    @Resource
    IDiyService diyService;

    @Resource
    IShopDeliveryCompanyService shopDeliveryCompanyService;

    @Resource
    CoreSiteServiceImpl coreSiteService;

    @Resource
    private ICorePosterService corePosterService;

    @Resource
    private ShopDeliveryElectronicSheetMapper shopDeliveryElectronicSheetMapper;

    @Resource
    private ShopGoodsLabelGroupMapper labelGroupMapper;

    @Resource
    private ShopGoodsLabelMapper labelMapper;

    @Resource
    private SysAttachmentCategoryMapper sysAttachmentCategoryMapper;

    @Resource
    private SysAttachmentMapper sysAttachmentMapper;

    @Resource
    private ICoreDiyService coreDiyService;

    @Resource
    private DiyThemeMapper diyThemeMapper;


    /**
     * 插件初始化
     *
     * @param
     */
    @Override
    public void addonInit(Site site, SiteGroup siteGroup) {
        if (coreSiteService.siteAddonIsInit(site.getSiteId(), "shop")) return;

        Integer siteId = site.getSiteId();

        // 创建默认商品海报
        createDefaultPosters(siteId);

        // 创建积分商城微页面
        createShopDiyPage(site, siteGroup);

        // 创建物流公司
        createExpressCompanies(siteId);

        // 创建电子面单
        createElectronicSheets(siteId);

        // 创建商品标签
        createProductLabels(siteId);

        // 创建商城素材
        createShopAttachments(siteId);
        
        //创建主题色
        createThemeColor(siteId);

        coreSiteService.addSiteAddonInitRecord(site.getSiteId(), "shop");
    }

    private void createThemeColor(Integer siteId) {
        boolean exists = diyThemeMapper.exists(new LambdaQueryWrapper<DiyTheme>()
                .eq(DiyTheme::getSiteId, siteId)
                .eq(DiyTheme::getAddon, "shop"));
        if (exists){
            return;
        }
        JSONObject addonTheme = coreDiyService.getDefaultThemeColor("shop");
        String theme = addonTheme.getJSONArray("theme_color").getJSONObject(0).get("theme").toString();
        String title = addonTheme.getJSONArray("theme_color").getJSONObject(0).get("title").toString();
        diyThemeMapper.insert(new DiyTheme().builder()
                .siteId(siteId)
                .title(title)
                .type("app")
                .addon("shop")
                .themeType("default")
                .defaultTheme(theme)
                .theme(theme)
                .isSelected(1)
                .createTime(DateUtils.currTime()).build());
    }

    private void createDefaultPosters(Integer siteId) {
        // 普通商品海报
        JSONObject goodsTemplate = (JSONObject) corePosterService.template("shop", "shop_goods").get(0);
        corePosterService.add(new AddPosterParam().builder()
                .siteId(siteId)
                .name(goodsTemplate.get("name").toString())
                .type(goodsTemplate.get("type").toString())
                .value(goodsTemplate.get("data").toString())
                .status(1)
                .isDefault(1)
                .addon("shop")
                .build());

        // 积分商品海报
        JSONObject pointTemplate = (JSONObject) corePosterService.template("shop", "shop_point_goods").get(0);
        corePosterService.add(new AddPosterParam().builder()
                .siteId(siteId)
                .name(pointTemplate.get("name").toString())
                .type(pointTemplate.get("type").toString())
                .value(pointTemplate.get("data").toString())
                .status(1)
                .isDefault(1)
                .addon("shop")
                .build());
    }

    private void createShopDiyPage(Site site, SiteGroup siteGroup) {
        JSONArray app = JSONUtil.parseArray(site.getApp());

        // 设置首页模板
        SetDiyDataParam setParam = new SetDiyDataParam();
        setParam.setSiteId(site.getSiteId());
        setParam.setType("index");
        setParam.setKey("DIY_INDEX");
        setParam.setIsStart(app.size() == 1 ? 1 : 0);
        setParam.setMainApp(app);
        setParam.setAddon("shop");
        diyService.setDiyData(setParam);

        // 设置个人中心模板
        setParam.setType("member_index");
        setParam.setKey("DIY_MEMBER_INDEX");
        diyService.setDiyData(setParam);

        // 添加积分商城微页面
        String addonFlag = "DIY_SHOP_POINT_INDEX";
        Map<String, Object> template = diyService.getFirstPageData(addonFlag, "shop");
        diyService.add(new DiyPageParam().builder()
                .pageTitle(template.get("title").toString())
                .title(template.get("title").toString())
                .name(addonFlag)
                .type(addonFlag)
                .template(template.get("template").toString())
                .mode(template.get("mode").toString())
                .value(JSONUtil.toJsonStr(template.get("data")))
                .isDefault(1)
                .isChange(0)
                .siteId(site.getSiteId())
                .build());
    }

    private void createExpressCompanies(Integer siteId) {
        List<ShopDeliveryCompany> companyList = new ArrayList<>();
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        JSONArray expressCompany = jsonModuleLoader.getResultSet("shop", "express_company.json");
        for (int i = 0; i < expressCompany.size(); i++) {
            JSONObject company = expressCompany.getJSONObject(i);
            ShopDeliveryCompany companyVo = JSONUtil.toBean(company, ShopDeliveryCompany.class);
            companyVo.setSiteId(siteId);
            companyVo.setCreateTime(DateUtils.currTime());
            companyVo.setPrintStyle(company.getJSONArray("print_style").toString());
            companyVo.setExpType(company.getJSONArray("exp_type").toString());
            companyList.add(companyVo);
        }
        shopDeliveryCompanyService.insertAll(companyList);

    }

    private void createElectronicSheets(Integer siteId) {
        ShopDeliveryCompanySearchParam param = new ShopDeliveryCompanySearchParam();
        param.setSiteId(siteId);
        param.setExpressNoElectronicSheet(Arrays.asList("EMS", "YZPY", "SF"));
        param.setElectronicSheetSwitch("1");
        List<ShopDeliveryCompanyListVo> list = shopDeliveryCompanyService.list(param);
        if (list.isEmpty()) {
            return;
        }
        List<ShopDeliveryElectronicSheet> electronicSheets = list.stream().map(item -> {
            Map printStyle = new HashMap();
            if (!item.getPrintStyle().isEmpty()) {
                printStyle = JSONUtil.toList(item.getPrintStyle(), Map.class).get(0);
            }
            Map expType = new HashMap();
            if (!item.getExpType().isEmpty()) {
                expType = JSONUtil.toList(item.getExpType(), Map.class).get(0);
            }
            ShopDeliveryElectronicSheet sheet = new ShopDeliveryElectronicSheet();
            sheet.setSiteId(siteId);
            sheet.setTemplateName(item.getCompanyName() + " " + printStyle.getOrDefault("template_name", ""));
            sheet.setExpressCompanyId(item.getCompanyId());
            sheet.setCustomerName("电子面单账号");
            sheet.setCustomerPwd("电子面单密码");
            sheet.setSendSite("");
            sheet.setSendStaff("");
            sheet.setMonthCode("");
            sheet.setPayType(1);
            sheet.setIsNotice(0);
            sheet.setStatus(1);
            sheet.setExpType(Integer.parseInt(expType.getOrDefault("value", 0).toString()));
            sheet.setPrintStyle(printStyle.get("template_size").toString());
            sheet.setIsDefault(0);
            sheet.setCreateTime(DateUtils.currTime());
            return sheet;
        }).toList();
        shopDeliveryElectronicSheetMapper.insert(electronicSheets);
    }

    private void createProductLabels(Integer siteId) {
        // 创建标签分组
        ShopGoodsLabelGroup labelGroup = new ShopGoodsLabelGroup();
        labelGroup.setSiteId(siteId);
        labelGroup.setGroupName("默认");
        labelGroup.setCreateTime(DateUtils.currTime());
        labelGroupMapper.insert(labelGroup);

        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        JSONArray labels = jsonModuleLoader.getResultSet("shop", "shop_label.json");
        List<ShopGoodsLabel> addLabels = new ArrayList<>();
        for (int i = 0; i < labels.size(); i++) {
            JSONObject jsonObject = labels.getJSONObject(i);
            ShopGoodsLabel label = JSONUtil.toBean(jsonObject, ShopGoodsLabel.class);
            label.setSiteId(siteId);
            label.setGroupId(labelGroup.getGroupId());
            label.setCreateTime(DateUtils.currTime());
            label.setStyleType(jsonObject.getStr("style_type"));
            label.setColorJson(jsonObject.get("color_json").toString());
            label.setSort(jsonObject.getInt("sort"));
            label.setStatus(jsonObject.getInt("status"));
            label.setIcon(jsonObject.getStr("icon"));
            addLabels.add(label);
        }

        labelMapper.insert(addLabels);
    }

    private void createShopAttachments(Integer siteId) {
        // 创建素材分类
        SysAttachmentCategory category = sysAttachmentCategoryMapper.selectOne(new LambdaQueryWrapper<SysAttachmentCategory>()
                .eq(SysAttachmentCategory::getSiteId, siteId)
                .eq(SysAttachmentCategory::getName, "商城素材"));

        Integer categoryId;
        if (category == null) {
            category = new SysAttachmentCategory();
            category.setSiteId(siteId);
            category.setPid(0);
            category.setType("image");
            category.setName("商城素材");
            category.setSort(1);
            sysAttachmentCategoryMapper.insert(category);
            categoryId = category.getId();
        } else {
            categoryId = category.getId();
        }
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        JSONArray attachments = jsonModuleLoader.getResultSet("shop", "attachment.json");
        List<SysAttachment> addAttachments = new ArrayList<>();
        for (int i = 0; i < attachments.size(); i++) {
            JSONObject jsonObject = attachments.getJSONObject(i);
            SysAttachment attachment = JSONUtil.toBean(jsonObject, SysAttachment.class);
            attachment.setSiteId(siteId);
            attachment.setCateId(categoryId);
            attachment.setCreateTime(DateUtils.currTime());
            attachment.setName(DateUtils.currTime() + siteId + categoryId + attachment.getName());
            addAttachments.add(attachment);
        }

        // 过滤已存在素材
        List<String> paths = addAttachments.stream()
                .map(SysAttachment::getPath)
                .collect(Collectors.toList());

        List<SysAttachment> existing = sysAttachmentMapper.selectList(
                new LambdaQueryWrapper<SysAttachment>()
                        .eq(SysAttachment::getSiteId, siteId)
                        .in(SysAttachment::getPath, paths)
        );

        Set<String> existingPaths = existing.stream()
                .map(SysAttachment::getPath)
                .collect(Collectors.toSet());

        List<SysAttachment> toInsert = addAttachments.stream()
                .filter(a -> !existingPaths.contains(a.getPath()))
                .collect(Collectors.toList());

        if (!toInsert.isEmpty()) {
            sysAttachmentMapper.insert(toInsert);
        }
    }

}
