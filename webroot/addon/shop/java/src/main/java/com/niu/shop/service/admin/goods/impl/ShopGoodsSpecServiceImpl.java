package com.niu.shop.service.admin.goods.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.shop.entity.goods.ShopGoodsSpec;
import com.niu.shop.mapper.goods.ShopGoodsSpecMapper;
import com.niu.shop.service.admin.goods.param.ShopGoodsSpecParam;
import com.niu.shop.service.admin.goods.param.ShopGoodsSpecSearchParam;
import com.niu.shop.service.admin.goods.IShopGoodsSpecService;
import com.niu.shop.service.admin.goods.vo.ShopGoodsSpecInfoVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsSpecListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 商品规格项/值实现
 */
@Service
public class ShopGoodsSpecServiceImpl extends ServiceImpl<ShopGoodsSpecMapper, ShopGoodsSpec> implements IShopGoodsSpecService {
        
    @Resource
    ShopGoodsSpecMapper shopGoodsSpecMapper;

    @Override
    public void insertAll(List<ShopGoodsSpec> list) {
        super.saveOrUpdateBatch(list);
    }
}
