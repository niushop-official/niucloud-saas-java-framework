package com.niu.shop.service.admin.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.shop.entity.goods.ShopGoodsAttr;
import com.niu.shop.mapper.goods.ShopGoodsAttrMapper;
import com.niu.shop.service.admin.goods.param.*;
import com.niu.shop.service.admin.goods.IShopGoodsAttrService;
import com.niu.shop.service.admin.goods.vo.ShopGoodsAttrInfoVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsAttrListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import jakarta.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * 商品参数实现
 */
@Service
public class ShopGoodsAttrServiceImpl implements IShopGoodsAttrService {
        
    @Resource
    ShopGoodsAttrMapper shopGoodsAttrMapper;

    /**
     * 商品参数列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsAttrListVo>
     */
    @Override
    public PageResult<ShopGoodsAttrListVo> page(PageParam pageParam, ShopGoodsAttrSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<ShopGoodsAttr> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        if (ObjectUtil.isNotEmpty(searchParam.getAttrName())) queryWrapper.like("attr_name", searchParam.getAttrName());
        if(ObjectUtil.isNotEmpty(searchParam.getOrder()))
        {
            if(searchParam.getSort().equals("asc"))
            {
                queryWrapper.orderByAsc(searchParam.getOrder());
            }
            else
            {
                queryWrapper.orderByDesc(searchParam.getOrder());
            }
        }else{
            queryWrapper.orderByDesc("attr_id");
        }

        IPage<ShopGoodsAttr> iPage = shopGoodsAttrMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<ShopGoodsAttrListVo> list = new LinkedList<>();
        for (ShopGoodsAttr item : iPage.getRecords()) {
            ShopGoodsAttrListVo vo = new ShopGoodsAttrListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 商品参数列表
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsAttrListVo>
     */
    @Override
    public List<ShopGoodsAttrListVo> list(ShopGoodsAttrSearchParam searchParam) {
        QueryWrapper<ShopGoodsAttr> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        if(ObjectUtil.isNotNull(searchParam.getAttrIdArr()) && searchParam.getAttrIdArr().size() > 0){
            queryWrapper.in("attr_id", searchParam.getAttrIdArr());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getAttrName())) queryWrapper.like("attr_name", searchParam.getAttrName());
        if(ObjectUtil.isNotEmpty(searchParam.getOrder()))
        {
            if(searchParam.getSort().equals("asc"))
            {
                queryWrapper.orderByAsc(searchParam.getOrder());
            }
            else
            {
                queryWrapper.orderByDesc(searchParam.getOrder());
            }
        }else{
            queryWrapper.orderByDesc("attr_id");
        }

        List<ShopGoodsAttr> record = shopGoodsAttrMapper.selectList(queryWrapper);
        List<ShopGoodsAttrListVo> list = new LinkedList<>();
        for (ShopGoodsAttr item : record) {
            ShopGoodsAttrListVo vo = new ShopGoodsAttrListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }

    /**
     * 商品参数详情
     *
     * @param id 主键
     * @return ShopGoodsAttrInfoVo
     */
    @Override
    public ShopGoodsAttrInfoVo info(Integer id) {
        ShopGoodsAttr model = shopGoodsAttrMapper.selectOne(
                new QueryWrapper<ShopGoodsAttr>()
                    .eq("attr_id", id)
                    .eq("site_id", RequestUtils.siteId()));

        if (model == null) return null;

        ShopGoodsAttrInfoVo vo = new ShopGoodsAttrInfoVo();
        BeanUtils.copyProperties(model, vo);

        return vo;
    }

    /**
     * 商品参数添加
     * @param addParam 参数
     */
    @Override
    public void add(ShopGoodsAttrParam addParam) {
        ShopGoodsAttr model = new ShopGoodsAttr();
        model.setSiteId(RequestUtils.siteId());
        model.setAttrName(addParam.getAttrName());
        model.setSort(addParam.getSort());
        shopGoodsAttrMapper.insert(model);
    }

    /**
     * 商品参数编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, ShopGoodsAttrParam editParam) {
        ShopGoodsAttr model = shopGoodsAttrMapper.selectOne(
                new QueryWrapper<ShopGoodsAttr>()
                    .eq("attr_id",  id)
                    .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "商品参数不存在!");

        model.setAttrName(editParam.getAttrName());
        model.setSort(editParam.getSort());

        shopGoodsAttrMapper.updateById(model);
    }

    /**
     * 商品参数删除
     *
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        shopGoodsAttrMapper.delete(new QueryWrapper<ShopGoodsAttr>().eq("attr_id", id).eq("site_id", RequestUtils.siteId()));
    }

    /**
     * 修改商品参数排序号
     *
     * @param editParam
     */
    @Override
    public void modifySort(EditGoodsAttrSortParam editParam) {
        ShopGoodsAttr model = shopGoodsAttrMapper.selectOne(
                new QueryWrapper<ShopGoodsAttr>()
                        .eq("attr_id",  editParam.getAttrId())
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "商品参数不存在!");

        model.setSort(editParam.getSort());

        shopGoodsAttrMapper.updateById(model);
    }

    /**
     * 修改商品参数名称
     *
     * @param editParam
     */
    @Override
    public void modifyAttrName(EditGoodsAttrNameParam editParam) {
        ShopGoodsAttr model = shopGoodsAttrMapper.selectOne(
                new QueryWrapper<ShopGoodsAttr>()
                        .eq("attr_id",  editParam.getAttrId())
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "商品参数不存在!");

        model.setAttrName(editParam.getAttrName());

        shopGoodsAttrMapper.updateById(model);
    }

    /**
     * 修改商品参数值
     *
     * @param editParam
     */
    @Override
    public void modifyAttrValueFormat(EditGoodsAttrValueParam editParam) {
        ShopGoodsAttr model = shopGoodsAttrMapper.selectOne(
                new QueryWrapper<ShopGoodsAttr>()
                        .eq("attr_id",  editParam.getAttrId())
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "商品参数不存在!");

        model.setAttrValueFormat(editParam.getAttrValueFormat());

        shopGoodsAttrMapper.updateById(model);
    }
}
