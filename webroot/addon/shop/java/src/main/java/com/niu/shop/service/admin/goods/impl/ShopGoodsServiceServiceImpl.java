package com.niu.shop.service.admin.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.shop.entity.goods.ShopGoodsService;
import com.niu.shop.mapper.goods.ShopGoodsServiceMapper;
import com.niu.shop.service.admin.goods.param.ShopGoodsServiceParam;
import com.niu.shop.service.admin.goods.param.ShopGoodsServiceSearchParam;
import com.niu.shop.service.admin.goods.IShopGoodsServiceService;
import com.niu.shop.service.admin.goods.vo.ShopGoodsServiceInfoVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsServiceListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import jakarta.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 商品服务实现
 */
@Service
public class ShopGoodsServiceServiceImpl implements IShopGoodsServiceService {
        
    @Resource
    ShopGoodsServiceMapper shopGoodsServiceMapper;

    /**
     * 商品服务分页列表
     *
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsServiceListVo>
     */
    @Override
    public PageResult<ShopGoodsServiceListVo> page(PageParam pageParam, ShopGoodsServiceSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<ShopGoodsService> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.orderByDesc("service_id");
        if (ObjectUtil.isNotNull(searchParam.getServiceName())) queryWrapper.like("service_name", searchParam.getServiceName());

        IPage<ShopGoodsService> iPage = shopGoodsServiceMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<ShopGoodsServiceListVo> list = new LinkedList<>();
        for (ShopGoodsService item : iPage.getRecords()) {
            ShopGoodsServiceListVo vo = new ShopGoodsServiceListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 商品服务列表
     * @param searchParam 搜索参数
     * @return List<ShopGoodsServiceListVo>
     */
    @Override
    public List<ShopGoodsServiceListVo> list(ShopGoodsServiceSearchParam searchParam) {
        QueryWrapper<ShopGoodsService> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.orderByDesc("service_id");
        if (ObjectUtil.isNotNull(searchParam.getServiceName())) queryWrapper.like("service_name", searchParam.getServiceName());

        List<ShopGoodsService> record = shopGoodsServiceMapper.selectList(queryWrapper);
        List<ShopGoodsServiceListVo> list = new LinkedList<>();
        for (ShopGoodsService item : record) {
            ShopGoodsServiceListVo vo = new ShopGoodsServiceListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }

    /**
     * 商品服务详情
     * @param id 主键
     * @return ShopGoodsServiceInfoVo
     */
    @Override
    public ShopGoodsServiceInfoVo info(Integer id) {
        ShopGoodsService model = shopGoodsServiceMapper.selectOne(
                new QueryWrapper<ShopGoodsService>()
                    .eq("service_id", id)
                    .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "服务不存在");

        ShopGoodsServiceInfoVo vo = new ShopGoodsServiceInfoVo();
        BeanUtils.copyProperties(model, vo);

        return vo;
    }

    /**
     * 商品服务添加
     * @param addParam 参数
     */
    @Override
    public void add(ShopGoodsServiceParam addParam) {
        ShopGoodsService model = new ShopGoodsService();

        model.setSiteId(RequestUtils.siteId());
        model.setServiceName(addParam.getServiceName());
        model.setImage(addParam.getImage());
        model.setDesc(addParam.getDesc());
        model.setCreateTime(System.currentTimeMillis() / 1000);

        shopGoodsServiceMapper.insert(model);
    }

    /**
     * 商品服务编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, ShopGoodsServiceParam editParam) {
        ShopGoodsService model = shopGoodsServiceMapper.selectOne(
                new QueryWrapper<ShopGoodsService>()
                        .eq("service_id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "服务不存在!");

        model.setServiceName(editParam.getServiceName());
        model.setImage(editParam.getImage());
        model.setDesc(editParam.getDesc());
        model.setUpdateTime(System.currentTimeMillis() / 1000);

        shopGoodsServiceMapper.updateById(model);
    }

    /**
     * 商品服务删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        shopGoodsServiceMapper.delete(new QueryWrapper<ShopGoodsService>().eq("service_id", id).eq("site_id", RequestUtils.siteId()));
    }

}
