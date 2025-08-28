package com.niu.core.service.api.diy.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.diy.DiyPage;
import com.niu.core.entity.diy.DiyRoute;
import com.niu.core.enums.diy.PagesEnum;
import com.niu.core.enums.diy.TemplateEnum;
import com.niu.core.mapper.diy.DiyPageMapper;
import com.niu.core.mapper.diy.DiyRouteMapper;
import com.niu.core.service.admin.diy.param.TemplateParam;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.api.diy.IDiyService;
import com.niu.core.service.api.diy.param.DiyInfoParam;
import com.niu.core.service.api.diy.param.DiyShareParam;
import com.niu.core.service.api.diy.param.DiyTabbarListParam;
import com.niu.core.service.api.diy.param.DiyTabbarParam;
import com.niu.core.service.api.diy.vo.DiyInfoVo;
import com.niu.core.service.api.diy.vo.DiyShareVo;
import com.niu.core.service.api.diy.vo.DiyTabbarInfoVo;
import com.niu.core.service.api.diy.vo.DiyTabbarVo;
import com.niu.core.service.api.sys.ISysConfigService;
import com.niu.core.service.core.diy.ICoreDiyConfigService;
import com.niu.core.service.core.diy.vo.BottomConfigVo;
import com.niu.core.service.core.diy.vo.StartUpPageConfigVo;
import com.niu.core.service.core.site.ICoreSiteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class DiyServiceImpl implements IDiyService {

    @Autowired
    private DiyPageMapper diyPageMapper;

    @Autowired
    private DiyRouteMapper diyRouteMapper;

    @Resource
    private ICoreDiyConfigService coreDiyConfigService;

    @Resource
    private ICoreSiteService coreSiteService;

    @Override
    public Object info(DiyInfoParam param) {
        Integer id = ObjectUtil.defaultIfNull(param.getId(), 0);
        String name = ObjectUtil.defaultIfNull(param.getName(), "");

        log.info("id: {}, name: {}", id, name);

        StartUpPageConfigVo startUpPage = null;
        JSONObject template = null;

        if (!StrUtil.isEmpty(name)) {
            // 查询启动页
            startUpPage = coreDiyConfigService.getStartUpPageConfig(param.siteId(), name);
            log.info("startUpPage: {}", JSONUtil.toJsonStr(startUpPage));

            TemplateParam templateParam = new TemplateParam();
            String[] key = { name };
            templateParam.setKey(key);
            template = TemplateEnum.getTemplate(templateParam).getJSONObject(name);
        }

        if (id == 0 && startUpPage != null && template != null && !startUpPage.getPage().equals(template.getStr("page"))) {
            log.info("enter if, template: {}", template.toString());
            return startUpPage;
        } else {
            QueryWrapper<DiyPage> queryWrapper = new QueryWrapper();
            queryWrapper.eq("site_id", param.siteId());
            log.info("site_id: {}",param.siteId() );

            DiyPage info = null;

            log.info("id: {}, name: {}", id, name);
            if (id > 0) {
                queryWrapper.eq("id", id);
                info = diyPageMapper.selectOne(queryWrapper);
            } else if (!name.isEmpty()) {
                queryWrapper.eq("name", name);
                queryWrapper.eq("is_default", 1);
                queryWrapper.orderByDesc("create_time");
                queryWrapper.last("limit 1");
                info = diyPageMapper.selectOne(queryWrapper);
            }

            log.info("info: {}", JSONUtil.toJsonStr(info));

            if (info == null) {
                if (!name.isEmpty()) {
                    JSONObject pageData = getFirstPageData(name, "");
                    if (pageData != null) {
                        info = new DiyPage();
                        info.setId(param.siteId());
                        info.setTitle(pageData.getStr("title"));
                        info.setName(pageData.getStr("type"));
                        info.setType(pageData.getStr("type"));
                        info.setTemplate(pageData.getStr("template"));
                        info.setMode(pageData.getStr("mode"));
                        info.setValue(pageData.getJSONObject("data").toString());
                        info.setIsDefault(1);
                    }
                }
            }

            log.info("info: {}", JSONUtil.toJsonStr(info));
            if (info == null)  return null;

            DiyInfoVo vo = new DiyInfoVo();
            BeanUtils.copyProperties(info, vo);
            log.info("vo: {}", JSONUtil.toJsonStr(vo));
            return vo;
        }
    }

    /**
     * 获取默认页面数据
     * @param type
     * @param addon
     * @return
     */
    public JSONObject getFirstPageData(String type, String addon) {
        JSONObject pages = PagesEnum.getPagesByAddon(type, addon);
        if (pages == null || pages.keySet().size() == 0) return null;

        String template = pages.keySet().iterator().next();
        JSONObject data = pages.getJSONObject(template);
        data.set("type", type);
        data.set("template", template);

        return data;
    }


    @Override
    public DiyTabbarVo tabbar(DiyTabbarParam param) {
        DiyTabbarVo diyTabbarVo = null;
        return diyTabbarVo;
    }

    @Override
    public List<BottomConfigVo> tabbarList(DiyTabbarListParam param) {
        List<BottomConfigVo> list = coreDiyConfigService.getBottomList();
        SiteInfoVo site = coreSiteService.getSiteCache(param.siteId());

        List<BottomConfigVo> tabbarList = new LinkedList<>();
        for (BottomConfigVo item : list) {
            if (item.getKey().equals("app") && list.size() > 1 && site.getApps().size() == 1) continue;
            BottomConfigVo config = coreDiyConfigService.getBottomConfig(param.siteId(), item.getKey());
            tabbarList.add(config);
        }

        return tabbarList;
    }

    @Override
    public JSONObject share(DiyShareParam param) {
        QueryWrapper<DiyRoute> queryWrapper = new QueryWrapper();
        queryWrapper.select("id,title,name,page,share,is_share");
        queryWrapper.eq("page", param.getRoute());
        queryWrapper.eq("site_id", param.siteId());

        DiyRoute diyRoute = diyRouteMapper.selectOne(queryWrapper);
        if (diyRoute == null || diyRoute.getShare().isEmpty()) return new JSONObject();

        JSONObject share = JSONUtil.parseObj(diyRoute.getShare());
        share.set("route", diyRoute.getPage());
        share.set("query", "");

        JSONObject query = new JSONObject();

        if (param.getParams() != null && !param.getParams().isEmpty()) {
            query = JSONUtil.parseObj(param.getParams());
        }

        if (param.memberId() != null && param.memberId() > 0) {
            query.set("mid", param.memberId());
        }

        String queryStr = "";
        if (query.keySet().size() > 0) {
            for (String key: query.keySet()) {
                queryStr += queryStr.isEmpty() ? key + "=" + query.getStr(key) : "&" + key + "=" + query.getStr(key);
            }
        }
        share.set("url", share.getStr("route") + (queryStr.isEmpty() ? "" : "?" + queryStr));

        return share;
    }

}
