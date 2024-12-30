package com.niu.shop.service.admin.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.shop.entity.goods.ShopGoodsLabel;
import com.niu.shop.entity.goods.ShopGoodsLabelGroup;
import com.niu.shop.mapper.goods.ShopGoodsLabelGroupMapper;
import com.niu.shop.mapper.goods.ShopGoodsLabelMapper;
import com.niu.shop.service.admin.goods.ILabelGroupService;
import com.niu.shop.service.admin.goods.param.LabelGroupModifySortParam;
import com.niu.shop.service.admin.goods.param.LabelGroupParam;
import com.niu.shop.service.admin.goods.param.LabelGroupSearchParam;
import com.niu.shop.service.admin.goods.vo.LabelGroupInfoVo;
import com.niu.shop.service.admin.goods.vo.LabelGroupListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * 商品标签分组实现
 */
@Service
public class LabelGroupServiceImpl implements ILabelGroupService {
        
    @Resource
    ShopGoodsLabelGroupMapper shopGoodsLabelGroupMapper;

    @Resource
    ShopGoodsLabelMapper shopGoodsLabelMapper;

    @Override
    public PageResult<LabelGroupListVo> page(PageParam pageParam, LabelGroupSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<ShopGoodsLabelGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        if (ObjectUtil.isNotEmpty(searchParam.getGroupName())) queryWrapper.like("group_name", searchParam.getGroupName());
        if (ObjectUtil.isNotEmpty(searchParam.getSort())) {
            queryWrapper.orderBy(false, searchParam.getSort().equals("asc"), searchParam.getOrder());
        } else {
            queryWrapper.orderByDesc("group_id");
        }

        IPage<ShopGoodsLabelGroup> iPage = shopGoodsLabelGroupMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<LabelGroupListVo> list = new LinkedList<>();
        for (ShopGoodsLabelGroup item : iPage.getRecords()) {
            LabelGroupListVo vo = new LabelGroupListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public LabelGroupInfoVo info(Integer id) {
        ShopGoodsLabelGroup model = shopGoodsLabelGroupMapper.selectOne(
        new QueryWrapper<ShopGoodsLabelGroup>()
            .eq("group_id", id)
            .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "商品标签分组不存在");

        LabelGroupInfoVo vo = new LabelGroupInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    @Override
    public void add(LabelGroupParam addParam) {
        ShopGoodsLabelGroup group = shopGoodsLabelGroupMapper.selectOne(
                new QueryWrapper<ShopGoodsLabelGroup>()
                        .eq("group_name", addParam.getGroupName())
                        .eq("site_id", RequestUtils.siteId()));

        Assert.isNull(group, "标签分组已存在，请检查");

        ShopGoodsLabelGroup model = new ShopGoodsLabelGroup();
        model.setGroupName(addParam.getGroupName());
        model.setSort(addParam.getSort());
        model.setSiteId(RequestUtils.siteId());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        shopGoodsLabelGroupMapper.insert(model);
    }

    @Override
    public void edit(Integer id, LabelGroupParam editParam) {
        ShopGoodsLabelGroup model = shopGoodsLabelGroupMapper.selectOne(
                new QueryWrapper<ShopGoodsLabelGroup>()
                        .eq("group_id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "商品标签分组不存在");
        model.setGroupName(editParam.getGroupName());
        model.setSort(editParam.getSort());
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        shopGoodsLabelGroupMapper.updateById(model);
    }

    @Override
    public void del(Integer id) {
        Long count = shopGoodsLabelMapper.selectCount(new QueryWrapper<ShopGoodsLabel>().eq("group_id", id));
        if (count > 0) throw new CommonException("该标签分组正在使用中，无法删除");

        shopGoodsLabelGroupMapper.delete(new QueryWrapper<ShopGoodsLabelGroup>().eq("group_id", id).eq("site_id", RequestUtils.siteId()));
    }

    @Override
    public List<LabelGroupListVo> list(LabelGroupSearchParam searchParam) {
        QueryWrapper<ShopGoodsLabelGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        if (ObjectUtil.isNotEmpty(searchParam.getGroupName())) queryWrapper.like("group_name", searchParam.getGroupName());

        List<ShopGoodsLabelGroup> records = shopGoodsLabelGroupMapper.selectList(queryWrapper);
        List<LabelGroupListVo> list = new LinkedList<>();
        for (ShopGoodsLabelGroup item : records) {
            LabelGroupListVo vo = new LabelGroupListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }

    @Override
    public void modifySort(LabelGroupModifySortParam param) {
        ShopGoodsLabelGroup model = shopGoodsLabelGroupMapper.selectOne(
                new QueryWrapper<ShopGoodsLabelGroup>()
                        .eq("group_id", param.getGroupId())
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "商品标签分组不存在");
        model.setSort(param.getSort());
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        shopGoodsLabelGroupMapper.updateById(model);
    }

}
