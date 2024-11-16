package com.niu.shop.service.admin.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.shop.entity.goods.ShopGoodsBrand;
import com.niu.shop.entity.goods.ShopGoodsLabel;
import com.niu.shop.mapper.goods.ShopGoodsLabelMapper;
import com.niu.shop.service.admin.goods.param.EditGoodsLabelSortParam;
import com.niu.shop.service.admin.goods.param.ShopGoodsLabelParam;
import com.niu.shop.service.admin.goods.param.ShopGoodsLabelSearchParam;
import com.niu.shop.service.admin.goods.IShopGoodsLabelService;
import com.niu.shop.service.admin.goods.vo.ShopGoodsLabelInfoVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsLabelListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 商品标签实现
 */
@Service
public class ShopGoodsLabelServiceImpl implements IShopGoodsLabelService {
        
    @Resource
    ShopGoodsLabelMapper shopGoodsLabelMapper;

    /**
     * 商品标签列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsLabelListVo>
     */
    @Override
    public PageResult<ShopGoodsLabelListVo> page(PageParam pageParam, ShopGoodsLabelSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<ShopGoodsLabel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        if (ObjectUtil.isNotEmpty(searchParam.getLabelName())) queryWrapper.like("label_name", searchParam.getLabelName());
        queryWrapper.orderByDesc("label_id");

        IPage<ShopGoodsLabel> iPage = shopGoodsLabelMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<ShopGoodsLabelListVo> list = new LinkedList<>();
        for (ShopGoodsLabel item : iPage.getRecords()) {
            ShopGoodsLabelListVo vo = new ShopGoodsLabelListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 商品标签列表
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsLabelListVo>
     */
    @Override
    public List<ShopGoodsLabelListVo> list(ShopGoodsLabelSearchParam searchParam) {
        QueryWrapper<ShopGoodsLabel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        if (ObjectUtil.isNotEmpty(searchParam.getLabelName())) queryWrapper.like("label_name", searchParam.getLabelName());
        queryWrapper.orderByDesc("label_id");

        List<ShopGoodsLabel> record = shopGoodsLabelMapper.selectList(queryWrapper);
        List<ShopGoodsLabelListVo> list = new LinkedList<>();
        for (ShopGoodsLabel item : record) {
            ShopGoodsLabelListVo vo = new ShopGoodsLabelListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }

    /**
     * 商品标签详情
     * @param id 主键
     * @return ShopGoodsLabelInfoVo
     */
    @Override
    public ShopGoodsLabelInfoVo info(Integer id) {
        ShopGoodsLabel model = shopGoodsLabelMapper.selectOne(
                new QueryWrapper<ShopGoodsLabel>()
                    .eq("label_id", id)
                    .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "标签不存在");

        ShopGoodsLabelInfoVo vo = new ShopGoodsLabelInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 商品标签添加
     * @param addParam 参数
     */
    @Override
    public void add(ShopGoodsLabelParam addParam) {
        Long count = shopGoodsLabelMapper.selectCount(new QueryWrapper<ShopGoodsLabel>()
                .eq("label_name", addParam.getLabelName())
                .eq("site_id", RequestUtils.siteId()));
        if (count > 0) throw new CommonException("标签已存在");

        ShopGoodsLabel model = new ShopGoodsLabel();

        model.setSiteId(RequestUtils.siteId());
        model.setLabelName(addParam.getLabelName());
        model.setMemo(addParam.getMemo());
        model.setSort(ObjectUtil.isNotEmpty(addParam.getSort()) ? Integer.parseInt(addParam.getSort()) : 0);
        model.setCreateTime(System.currentTimeMillis() / 1000);

        shopGoodsLabelMapper.insert(model);
    }

    /**
     * 商品标签编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, ShopGoodsLabelParam editParam) {
        Long count = shopGoodsLabelMapper.selectCount(new QueryWrapper<ShopGoodsLabel>()
                .eq("label_name", editParam.getLabelName())
                .ne("label_id", id)
                .eq("site_id", RequestUtils.siteId()));
        if (count > 0) throw new CommonException("标签已存在");

        ShopGoodsLabel model = shopGoodsLabelMapper.selectOne(
                new QueryWrapper<ShopGoodsLabel>()
                        .eq("label_id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "标签不存在");

        model.setLabelName(editParam.getLabelName());
        model.setMemo(editParam.getMemo());
        model.setSort(ObjectUtil.isNotEmpty(editParam.getSort()) ? Integer.parseInt(editParam.getSort()) : 0);
        model.setUpdateTime(System.currentTimeMillis() / 1000);

        shopGoodsLabelMapper.updateById(model);
    }

    /**
     * 商品标签删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        shopGoodsLabelMapper.delete(new QueryWrapper<ShopGoodsLabel>().eq("label_id", id).eq("site_id", RequestUtils.siteId()));
    }

    /**
     * 商品标签排序编辑
     *
     * @param editParam 参数
     */
    @Override
    public void modifySort(EditGoodsLabelSortParam editParam) {
        ShopGoodsLabel model = shopGoodsLabelMapper.selectOne(
                new QueryWrapper<ShopGoodsLabel>()
                        .eq("label_id",  editParam.getLabelId())
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "标签不存在!");

        model.setSort(editParam.getSort());
        model.setUpdateTime(System.currentTimeMillis() / 1000);

        shopGoodsLabelMapper.updateById(model);
    }
}
