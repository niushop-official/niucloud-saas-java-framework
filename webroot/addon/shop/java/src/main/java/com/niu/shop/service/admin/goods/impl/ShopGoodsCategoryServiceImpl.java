package com.niu.shop.service.admin.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.TreeUtils;
import com.niu.core.common.utils.json.JacksonUtils;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.goods.ShopGoodsCategory;
import com.niu.shop.mapper.goods.ShopGoodsCategoryMapper;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.service.admin.goods.IGoodsCategoryService;
import com.niu.shop.service.admin.goods.param.EditGoodsCategorySortParam;
import com.niu.shop.service.admin.goods.param.ShopGoodsCategoryParam;
import com.niu.shop.service.admin.goods.param.ShopGoodsCategorySearchParam;
import com.niu.shop.service.admin.goods.vo.ShopGoodsCategoryInfoVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsCategoryListVo;
import com.niu.shop.service.core.goods.ICoreGoodsCategoryService;
import com.niu.shop.service.core.goods.vo.GoodsCategoryConfigVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * 商品分类实现
 */
@Service
public class ShopGoodsCategoryServiceImpl implements IGoodsCategoryService {
        
    @Resource
    ShopGoodsCategoryMapper shopGoodsCategoryMapper;

    @Resource
    ShopGoodsMapper shopGoodsMapper;

    @Resource
    ICoreGoodsCategoryService coreGoodsCategoryService;

    /**
     * 商品分类列表
     *
     */
    @Override
    public List<ShopGoodsCategoryListVo> list(ShopGoodsCategorySearchParam searchParam) {
        QueryWrapper<ShopGoodsCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        if (ObjectUtil.isNotEmpty(searchParam.getCategoryName())) queryWrapper.like("category_name", searchParam.getCategoryName());
        if (ObjectUtil.isNotEmpty(searchParam.getLevel())) queryWrapper.eq("level", searchParam.getLevel());
        queryWrapper.orderByDesc(Arrays.asList("sort", "category_id"));

        List<ShopGoodsCategory> records = shopGoodsCategoryMapper.selectList(queryWrapper);
        List<ShopGoodsCategoryListVo> list = new LinkedList<>();
        for (ShopGoodsCategory item : records) {
            ShopGoodsCategoryListVo vo = new ShopGoodsCategoryListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }

    /**
     * 获取商品分类树
     *
     * @return
     */
    public JSONArray tree() {
        List<ShopGoodsCategory> list = shopGoodsCategoryMapper.selectList(new QueryWrapper<ShopGoodsCategory>().eq("site_id", RequestUtils.siteId()));
        if (list == null) return null;

        JSONArray jsonArray = JSONUtil.parseArray(JacksonUtils.toSnakeCaseJSONString(list));
        return TreeUtils.listToTree(jsonArray, "category_id", "pid", "child_list");
    }

    /**
     * 获取商品分类树
     *
     * @return
     */
    public JSONArray tree(ShopGoodsCategorySearchParam searchParam) {
        QueryWrapper queryWrapper = new QueryWrapper<ShopGoodsCategory>().eq("site_id", RequestUtils.siteId());
        if (ObjectUtil.isNotNull(searchParam.getIsShow())) queryWrapper.eq("is_show", searchParam.getIsShow());
        List<ShopGoodsCategory> list = shopGoodsCategoryMapper.selectList(queryWrapper);
        if (list == null) return null;

        JSONArray jsonArray = JSONUtil.parseArray(JacksonUtils.toSnakeCaseJSONString(list));
        return TreeUtils.listToTree(jsonArray, "category_id", "pid", "child_list");
    }


    /**
     * 商品分类详情
     * @param id 主键
     * @return ShopGoodsCategoryInfoVo
     */
    @Override
    public ShopGoodsCategoryInfoVo info(Integer id) {
        ShopGoodsCategory model = shopGoodsCategoryMapper.selectOne(
                new QueryWrapper<ShopGoodsCategory>()
                    .eq("category_id", id)
                    .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "分类不存在");

        ShopGoodsCategoryInfoVo vo = new ShopGoodsCategoryInfoVo();
        BeanUtils.copyProperties(model, vo);

        Long childCount = shopGoodsCategoryMapper.selectCount(new QueryWrapper<ShopGoodsCategory>()
                .eq("pid", id));
        vo.setChildCount(childCount == null ? 0 : childCount.intValue());

        return vo;
    }

    /**
     * 商品分类添加
     * @param addParam 参数
     */
    @Override
    public void add(ShopGoodsCategoryParam addParam) {
        QueryWrapper checkQuery = new QueryWrapper<ShopGoodsCategory>()
                .eq("category_name", addParam.getCategoryName())
                .eq("site_id", RequestUtils.siteId());
        if (addParam.getPid() > 0) {
            checkQuery.eq("pid", addParam.getPid());
        } else {
            checkQuery.eq("level", 1);
        }
        ShopGoodsCategory category = shopGoodsCategoryMapper.selectOne(checkQuery);
        Assert.isNull(category, "分类已存在");

        ShopGoodsCategory model = new ShopGoodsCategory();
        BeanUtils.copyProperties(addParam, model);
        model.setSiteId(RequestUtils.siteId());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        if (addParam.getPid() > 0) {
            ShopGoodsCategory parentCategory = shopGoodsCategoryMapper.selectOne(new QueryWrapper<ShopGoodsCategory>()
                    .eq("category_id", addParam.getPid()));
            Assert.notNull(parentCategory, "所选父级分类不存在");
            model.setCategoryFullName(parentCategory.getCategoryName() + "/" + addParam.getCategoryName());
            model.setLevel(2);
        } else {
            model.setLevel(1);
            model.setCategoryFullName(addParam.getCategoryName());
        }

        shopGoodsCategoryMapper.insert(model);
    }

    /**
     * 商品分类编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, ShopGoodsCategoryParam editParam) {
        QueryWrapper checkQuery = new QueryWrapper<ShopGoodsCategory>()
                .eq("category_name", editParam.getCategoryName())
                .eq("site_id", RequestUtils.siteId())
                .ne("category_id", id);
        if (editParam.getPid() > 0) {
            checkQuery.eq("pid", editParam.getPid());
        } else {
            checkQuery.eq("level", 1);
        }
        ShopGoodsCategory category = shopGoodsCategoryMapper.selectOne(checkQuery);
        Assert.isNull(category, "分类已存在");

        ShopGoodsCategory model = shopGoodsCategoryMapper.selectOne(new QueryWrapper<ShopGoodsCategory>()
                .eq("category_id", id)
                .eq("site_id", RequestUtils.siteId()));
        Assert.notNull(model, "分类不存在");

        if (model.getLevel().equals(1) && editParam.getPid() > 0) {
            Long childCount = shopGoodsCategoryMapper.selectCount(new QueryWrapper<ShopGoodsCategory>()
                    .eq("pid", id));
            if (childCount > 0) throw new CommonException("当前分类存在下级，不可修改该分类上级");
        }

        BeanUtils.copyProperties(editParam, model);
        model.setSiteId(RequestUtils.siteId());
        model.setUpdateTime(System.currentTimeMillis() / 1000);

        if (editParam.getPid() > 0) {
            ShopGoodsCategory parentCategory = shopGoodsCategoryMapper.selectOne(new QueryWrapper<ShopGoodsCategory>()
                    .eq("category_id", editParam.getPid()));
            Assert.notNull(parentCategory, "所选父级分类不存在");
            model.setCategoryFullName(parentCategory.getCategoryName() + "/" + editParam.getCategoryName());
            model.setLevel(2);
        } else {
            model.setLevel(1);
            model.setCategoryFullName(editParam.getCategoryName());
        }

        shopGoodsCategoryMapper.updateById(model);
    }

    /**
     * 修改分类排序
     *
     * @param editParam
     */
    public void editGoodsCategorySort(EditGoodsCategorySortParam editParam) {
        for (int i = 0; i < editParam.getCategorySortArray().size(); i++) {
            EditGoodsCategorySortParam.Item item = editParam.getCategorySortArray().get(i);
            ShopGoodsCategory model = new ShopGoodsCategory();
            model.setSort(item.getSort());
            shopGoodsCategoryMapper.update(model, new QueryWrapper<ShopGoodsCategory>()
                    .eq("site_id", RequestUtils.siteId())
                    .eq("category_id", item.getCategoryId()));
        }
    }

    /**
     * 商品分类删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        Long count = shopGoodsMapper.selectCount(new QueryWrapper<ShopGoods>()
                .eq("site_id", RequestUtils.siteId())
                .like("goods_category", "\"" + id + "\""));
        if (count > 0) throw new CommonException("当前分类下存在商品，不可删除");

        shopGoodsCategoryMapper.delete(new QueryWrapper<ShopGoodsCategory>().eq("category_id", id).or().eq("pid", id));
    }


    /**
     * 获取商品分类配置
     *
     * @return GoodsCategoryConfigVo
     */
    public GoodsCategoryConfigVo getGoodsCategoryConfig() {
        return coreGoodsCategoryService.getGoodsCategoryConfig(RequestUtils.siteId());
    }

    /**
     * 设置商品分类配置
     *
     * @param data
     */
    public void setGoodsCategoryConfig(JSONObject data) {
        coreGoodsCategoryService.setGoodsCategoryConfig(RequestUtils.siteId(), data);
    }
}
