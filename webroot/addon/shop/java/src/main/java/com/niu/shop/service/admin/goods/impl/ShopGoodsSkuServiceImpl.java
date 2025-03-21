package com.niu.shop.service.admin.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.shop.entity.goods.ShopGoodsService;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.entity.goods.ShopGoodsSpec;
import com.niu.shop.mapper.goods.ShopGoodsSkuMapper;
import com.niu.shop.mapper.goods.ShopGoodsSpecMapper;
import com.niu.shop.service.admin.goods.param.ShopGoodsSkuParam;
import com.niu.shop.service.admin.goods.param.ShopGoodsSkuSearchParam;
import com.niu.shop.service.admin.goods.IShopGoodsSkuService;
import com.niu.shop.service.admin.goods.vo.ShopGoodsServiceListVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsSkuInfoVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsSkuListVo;
import com.niu.shop.service.api.goods.vo.ShopGoodsSkuVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * 商品规格实现
 */
@Service
public class ShopGoodsSkuServiceImpl extends ServiceImpl<ShopGoodsSkuMapper, ShopGoodsSku>  implements IShopGoodsSkuService {
        
    @Resource
    ShopGoodsSkuMapper shopGoodsSkuMapper;

    @Override
    public void insertAll(List<ShopGoodsSku> list) {
        super.saveOrUpdateBatch(list);
    }

    @Override
    public List<ShopGoodsSkuVo> list(ShopGoodsSkuSearchParam searchParam) {
        QueryWrapper<ShopGoodsSku> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.eq("goods_id", searchParam.getGoodsId());

        List<ShopGoodsSku> record = shopGoodsSkuMapper.selectList(queryWrapper);
        List<ShopGoodsSkuVo> list = new LinkedList<>();
        for (ShopGoodsSku item : record) {
            ShopGoodsSkuVo vo = new ShopGoodsSkuVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }

}
