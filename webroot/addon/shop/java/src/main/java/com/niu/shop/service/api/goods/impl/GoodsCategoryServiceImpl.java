package com.niu.shop.service.api.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.TreeUtils;
import com.niu.core.common.utils.json.JacksonUtils;
import com.niu.shop.entity.goods.ShopGoodsCategory;
import com.niu.shop.mapper.goods.ShopGoodsCategoryMapper;
import com.niu.shop.service.api.goods.IGoodsCategoryService;
import com.niu.shop.service.api.goods.param.ShopGoodsCategorySearchParam;
import com.niu.shop.service.api.goods.vo.ShopGoodsCategoryListVo;
import com.niu.shop.service.core.goods.ICoreGoodsCategoryService;
import com.niu.shop.service.core.goods.vo.GoodsCategoryConfigVo;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

@Service
public class GoodsCategoryServiceImpl implements IGoodsCategoryService {

    @Resource
    ICoreGoodsCategoryService coreGoodsCategoryService;

    @Resource
    ShopGoodsCategoryMapper shopGoodsCategoryMapper;

    /**
     * 获取商品分类配置
     *
     * @return
     */
    public GoodsCategoryConfigVo getGoodsCategoryConfig() {
        return coreGoodsCategoryService.getGoodsCategoryConfig(RequestUtils.siteId());
    }

    /**
     * 获取商品分类树
     *
     * @return
     */
    public JSONArray tree() {
        QueryWrapper queryWrapper = new QueryWrapper<ShopGoodsCategory>().eq("site_id", RequestUtils.siteId());
        queryWrapper.eq("is_show", 1);
        queryWrapper.orderByDesc("sort");
        List<ShopGoodsCategory> list = shopGoodsCategoryMapper.selectList(queryWrapper);
        if (list == null) return null;

        JSONArray jsonArray = JSONUtil.parseArray(JacksonUtils.toSnakeCaseJSONString(list));
        return TreeUtils.listToTree(jsonArray, "category_id", "pid", "child_list");
    }

    /**
     * 获取商品分类列表
     *
     * @param searchParam
     * @return
     */
    public List<ShopGoodsCategoryListVo> getList(ShopGoodsCategorySearchParam searchParam) {
        QueryWrapper queryWrapper = new QueryWrapper<ShopGoodsCategory>().eq("site_id", RequestUtils.siteId());
        queryWrapper.eq("is_show", 1);
        queryWrapper.orderByDesc("sort");
        if (searchParam.getPid() != null && searchParam.getPid() > 0) queryWrapper.eq("pid", searchParam.getPid());
        return CollectUtils.convert(shopGoodsCategoryMapper.selectList(queryWrapper), ShopGoodsCategoryListVo.class);
    }
}
