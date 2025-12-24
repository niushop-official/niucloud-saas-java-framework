package com.niu.shop.service.admin.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.shop.entity.goods.ShopGoodsBrand;
import com.niu.shop.mapper.goods.ShopGoodsBrandMapper;
import com.niu.shop.service.admin.goods.param.EditGoodsBrandSortParam;
import com.niu.shop.service.admin.goods.param.ShopGoodsBrandParam;
import com.niu.shop.service.admin.goods.param.ShopGoodsBrandSearchParam;
import com.niu.shop.service.admin.goods.IShopGoodsBrandService;
import com.niu.shop.service.admin.goods.vo.ShopGoodsBrandInfoVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsBrandListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import jakarta.annotation.Resource;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * 商品品牌实现
 */
@Service
public class ShopGoodsBrandServiceImpl implements IShopGoodsBrandService {
        
    @Resource
    ShopGoodsBrandMapper shopGoodsBrandMapper;

    /**
     * 商品品牌分页列表
     *
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsBrandListVo>
     */
    @Override
    public PageResult<ShopGoodsBrandListVo> page(PageParam pageParam, ShopGoodsBrandSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<ShopGoodsBrand> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        if (ObjectUtil.isNotEmpty(searchParam.getBrandName())) queryWrapper.like("brand_name", searchParam.getBrandName());
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
            queryWrapper.orderByDesc("brand_id");
        }


        IPage<ShopGoodsBrand> iPage = shopGoodsBrandMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<ShopGoodsBrandListVo> list = new LinkedList<>();
        for (ShopGoodsBrand item : iPage.getRecords()) {
            ShopGoodsBrandListVo vo = new ShopGoodsBrandListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 商品品牌列表
     *
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsBrandListVo>
     */
    @Override
    public List<ShopGoodsBrandListVo> list(ShopGoodsBrandSearchParam searchParam) {
        QueryWrapper<ShopGoodsBrand> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        if (ObjectUtil.isNotEmpty(searchParam.getBrandName())) queryWrapper.like("brand_name", searchParam.getBrandName());
        queryWrapper.orderByDesc("create_time");

        List<ShopGoodsBrand> record = shopGoodsBrandMapper.selectList(queryWrapper);
        List<ShopGoodsBrandListVo> list = new LinkedList<>();
        for (ShopGoodsBrand item : record) {
            ShopGoodsBrandListVo vo = new ShopGoodsBrandListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }

    /**
     * 商品品牌详情
     *
     * @param id 主键
     * @return ShopGoodsBrandInfoVo
     */
    @Override
    public ShopGoodsBrandInfoVo info(Integer id) {
        ShopGoodsBrand model = shopGoodsBrandMapper.selectOne(
                new QueryWrapper<ShopGoodsBrand>()
                    .eq("brand_id", id)
                    .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "品牌不存在");

        ShopGoodsBrandInfoVo vo = new ShopGoodsBrandInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 商品品牌添加
     *
     * @param addParam 参数
     */
    @Override
    public void add(ShopGoodsBrandParam addParam) {
        Long count = shopGoodsBrandMapper.selectCount(new QueryWrapper<ShopGoodsBrand>()
                .eq("brand_name", addParam.getBrandName())
                .eq("site_id", RequestUtils.siteId()));
        if (count > 0) throw new CommonException("品牌已存在");

        ShopGoodsBrand model = new ShopGoodsBrand();

        model.setSiteId(RequestUtils.siteId());
        model.setBrandName(addParam.getBrandName());
        model.setLogo(addParam.getLogo());
        model.setDesc(addParam.getDesc());
        model.setSort(ObjectUtil.isNotEmpty(addParam.getSort()) ? Integer.parseInt(addParam.getSort()) : 0);
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setColorJson(JSONUtil.toJsonStr(addParam.getColorJson()));
        shopGoodsBrandMapper.insert(model);
    }

    /**
     * 商品品牌编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, ShopGoodsBrandParam editParam) {
        Long count = shopGoodsBrandMapper.selectCount(new QueryWrapper<ShopGoodsBrand>()
                .eq("brand_name", editParam.getBrandName())
                .ne("brand_id", id)
                .eq("site_id", RequestUtils.siteId()));
        if (count > 0) throw new CommonException("品牌已存在");

        ShopGoodsBrand model = shopGoodsBrandMapper.selectOne(
                new QueryWrapper<ShopGoodsBrand>()
                    .eq("brand_id",  id)
                    .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "品牌不存在!");

        model.setBrandName(editParam.getBrandName());
        model.setLogo(editParam.getLogo());
        model.setDesc(editParam.getDesc());
        model.setSort(ObjectUtil.isNotEmpty(editParam.getSort()) ? Integer.parseInt(editParam.getSort()) : 0);
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        model.setColorJson(JSONUtil.toJsonStr(editParam.getColorJson()));

        shopGoodsBrandMapper.updateById(model);
    }

    /**
     * 商品品牌删除
     *
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        shopGoodsBrandMapper.delete(new QueryWrapper<ShopGoodsBrand>().eq("brand_id", id).eq("site_id", RequestUtils.siteId()));
    }

    /**
     * 商品品牌编辑
     *
     * @param editParam 参数
     */
    @Override
    public void modifySort(EditGoodsBrandSortParam editParam) {
        ShopGoodsBrand model = shopGoodsBrandMapper.selectOne(
                new QueryWrapper<ShopGoodsBrand>()
                        .eq("brand_id",  editParam.getBrandId())
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "品牌不存在!");

        model.setSort(editParam.getSort());
        model.setUpdateTime(System.currentTimeMillis() / 1000);

        shopGoodsBrandMapper.updateById(model);
    }
}
