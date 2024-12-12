package com.niu.shop.service.core.shop.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.service.admin.diy.IDiyService;
import com.niu.core.service.admin.diy.param.DiyPageParam;
import com.niu.core.service.admin.diy.param.SetDiyDataParam;
import com.niu.core.service.core.site.ICoreSiteService;
import com.niu.shop.entity.delivery.ShopDeliveryCompany;
import com.niu.shop.service.admin.delivery.IShopDeliveryCompanyService;
import com.niu.shop.service.core.shop.ICoreAddonService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

@Service
public class CoreAddonServiceImpl implements ICoreAddonService {

    @Resource
    IDiyService diyService;

    @Resource
    ICoreSiteService coreSiteService;

    @Resource
    IShopDeliveryCompanyService shopDeliveryCompanyService;

    /**
     * 插件初始化
     *
     * @param
     */
    @Override
    public void addonInit(Site site, SiteGroup siteGroup) {
        if (coreSiteService.siteAddonIsInit(site.getSiteId(), "shop")) return;

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

        // 添加积分商城模板
        JSONObject template = diyService.getFirstPageData("DIY_SHOP_POINT_INDEX", "shop");
        if (template != null) {
            DiyPageParam pointPage = new DiyPageParam();
            pointPage.setPageTitle(template.getStr("title"));
            pointPage.setTitle(template.getStr("title"));
            pointPage.setName("DIY_SHOP_POINT_INDEX");
            pointPage.setType("DIY_SHOP_POINT_INDEX");
            pointPage.setTemplate(template.getStr("template"));
            pointPage.setMode(template.getStr("mode"));
            pointPage.setValue(template.getJSONObject("data").toString());
            pointPage.setIsDefault(1);
            pointPage.setIsChange(0);
            diyService.add(pointPage);
        }

        // 添加物流公司
        List<ShopDeliveryCompany> companyList = new LinkedList<>();
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        JSONArray expressCompany = jsonModuleLoader.getResultSet("shop", "express_company.json");
        for (int i = 0; i < expressCompany.size(); i++) {
            JSONObject company = expressCompany.getJSONObject(i);
            ShopDeliveryCompany companyVo = JSONUtil.toBean(company, ShopDeliveryCompany.class);
            companyVo.setSiteId(site.getSiteId());
            companyVo.setPrintStyle(company.getJSONArray("print_style").toString());
            companyVo.setExpType(company.getJSONArray("exp_type").toString());
            companyList.add(companyVo);
        }
        shopDeliveryCompanyService.insertAll(companyList);

        coreSiteService.addSiteAddonInitRecord(site.getSiteId(), "shop");
    }
}
