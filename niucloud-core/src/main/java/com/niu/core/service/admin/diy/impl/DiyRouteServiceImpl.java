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
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<DiyRouteListVo>
     */
    @Override
    public List<DiyRouteListVo> list(DiyRouteSearchParam searchParam) {
        JSONObject linkEnum = LinkEnum.getLink();

        List<DiyRouteListVo> routerList = new LinkedList<>();
        int sort = 0;
        for (String key : linkEnum.keySet()) {

            if (ObjectUtil.isNotEmpty(searchParam.getAddonName())) {
                String addonName = ObjectUtil.defaultIfNull(linkEnum.getByPath(key + ".addon_info.title", String.class), "");
                if (!addonName.equals(searchParam.getAddonName())) continue;
            }

            JSONArray linkArray = ObjectUtil.defaultIfNull(linkEnum.getByPath(key + ".child_list", JSONArray.class), new JSONArray());
            for (int i = 0; i < linkArray.size(); i++) {
                JSONObject link = linkArray.getJSONObject(i);

                if (ObjectUtil.isNotEmpty(searchParam.getUrl()) && !link.getStr("url").equals(searchParam.getUrl())) {
                    continue;
                }
                if (ObjectUtil.isNotEmpty(searchParam.getTitle()) && link.getStr("title").indexOf(searchParam.getTitle()) == -1) {
                    continue;
                }

                DiyRouteListVo vo = new DiyRouteListVo();
                vo.setName(link.getStr("name"));
                vo.setTitle(link.getStr("title"));
                vo.setAddonInfo(ObjectUtil.defaultIfNull(linkEnum.getByPath(key + ".addon_info", JSONObject.class), new JSONObject()));
                vo.setParent(key);
                vo.setPage(link.getStr("url"));
                vo.setIsShare(link.getInt("is_share"));
                vo.setAction(link.getStr("action"));
                vo.setSort(sort);
                routerList.add(vo);
                sort++;
            }
        }

        return routerList;
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

}
