package com.niu.core.service.admin.diy.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.enums.diy.LinkEnum;
import com.niu.core.service.admin.diy.param.DiyRouteSearchParam;
import com.niu.core.service.admin.diy.IDiyRouteService;
import com.niu.core.service.admin.diy.param.DiyRouteShareParam;
import com.niu.core.service.admin.diy.vo.DiyRouteInfoVo;
import com.niu.core.service.admin.diy.vo.DiyRouteListVo;
import com.niu.core.common.domain.PageParam;
import com.niu.core.entity.diy.DiyRoute;
import com.niu.core.mapper.diy.DiyRouteMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 自定义路由实现类
 */
@Service
public class DiyRouteServiceImpl implements IDiyRouteService {
        
    @Resource
    DiyRouteMapper diyRouteMapper;

    /**
     * 自定义路由列表
     *
     * @param searchParam 搜索参数
     * @return PageResult<DiyRouteListVo>
     */
    @Override
    public List<DiyRouteListVo> list(DiyRouteSearchParam searchParam) {
        JSONObject linkEnum = LinkEnum.getLink();
        List<DiyRouteListVo> routerList = new LinkedList<>();
        int sort = 0;

        for (String key : linkEnum.keySet()) {
            JSONObject parentItem = linkEnum.getJSONObject(key);
            JSONObject addonInfo = parentItem.getJSONObject("addon_info");
            JSONArray childArray = ObjectUtil.defaultIfNull(parentItem.getJSONArray("child_list"), new JSONArray());

            sort = processChildItems(childArray, key, addonInfo, searchParam, routerList, sort);
        }

        return routerList;
    }

    /**
     * 递归处理子项
     */
    private int processChildItems(JSONArray childArray, String parentKey, JSONObject addonInfo,
                                  DiyRouteSearchParam searchParam, List<DiyRouteListVo> routerList, int sort) {
        for (int i = 0; i < childArray.size(); i++) {
            JSONObject item = childArray.getJSONObject(i);

            JSONArray nestedChildren = ObjectUtil.defaultIfNull(item.getJSONArray("child_list"), new JSONArray());
            if (nestedChildren.size() > 0) {
                sort = processChildItems(nestedChildren, parentKey, addonInfo, searchParam, routerList, sort);
                continue;
            }

            if (!passesFilter(item, addonInfo, searchParam)) {
                continue;
            }

            if (ObjectUtil.isEmpty(item.getStr("url"))) {
                continue;
            }

            DiyRouteListVo vo = createDiyRouteVo(parentKey, item, addonInfo, sort);
            routerList.add(vo);
            sort++;
        }
        return sort;
    }

    /**
     * 检查是否通过过滤条件
     */
    private boolean passesFilter(JSONObject link, JSONObject addonInfo, DiyRouteSearchParam searchParam) {
        // URL过滤
        if (ObjectUtil.isNotEmpty(searchParam.getUrl()) && !searchParam.getUrl().equals(link.getStr("url"))) {
            return false;
        }

        // 标题过滤
        if (ObjectUtil.isNotEmpty(searchParam.getTitle()) && !link.getStr("title").contains(searchParam.getTitle())) {
            return false;
        }

        // 插件名称过滤
        if (ObjectUtil.isNotEmpty(searchParam.getAddonName()) && !searchParam.getAddonName().equals(addonInfo.getStr("key"))) {
            return false;
        }

        return true;
    }

    /**
     * 创建Vo对象
     */
    private DiyRouteListVo createDiyRouteVo(String parentKey, JSONObject link, JSONObject addonInfo, int sort) {
        DiyRouteListVo vo = new DiyRouteListVo();
        vo.setName(link.getStr("name"));
        vo.setTitle(link.getStr("title"));
        vo.setAddonInfo(ObjectUtil.defaultIfNull(addonInfo, new JSONObject()));
        vo.setParent(parentKey);
        vo.setPage(link.getStr("url"));
        vo.setIsShare(link.getInt("is_share"));
        vo.setAction(link.getStr("action"));
        vo.setSort(sort);
        return vo;
    }

    /**
     * 自定义路由详情
     * @return DiyRoute
     */
    @Override
    public DiyRouteInfoVo getInfoByName(String name) {
        DiyRoute model = diyRouteMapper.selectOne(
                new QueryWrapper<DiyRoute>()
                    .eq("name", name)
                    .eq("site_id", RequestUtils.siteId())
                    .last("limit 1"));
        if (model == null) return null;

        DiyRouteInfoVo vo = new DiyRouteInfoVo();
        BeanUtils.copyProperties(model, vo);

        return vo;
    }

    /**
     * 自定义路由新增
     * @param editParam 参数
     */
    @Override
    public void modifyShare(DiyRouteShareParam editParam) {
        QueryWrapper queryWrapper = new QueryWrapper<DiyRoute>()
                .eq("name", editParam.getName())
                .eq("site_id", RequestUtils.siteId())
                .last("limit 1");
        DiyRoute model = diyRouteMapper.selectOne(queryWrapper);

        if (model != null) {
            BeanUtils.copyProperties(editParam, model);
            diyRouteMapper.update(model, queryWrapper);
        } else {
            DiyRoute insertModel = new DiyRoute();
            BeanUtils.copyProperties(editParam, insertModel);
            insertModel.setSiteId(RequestUtils.siteId());
            diyRouteMapper.insert(insertModel);
        }
    }

    @Override
    public JSONObject getApps() {
        return LinkEnum.getAddonLink();
    }

}
