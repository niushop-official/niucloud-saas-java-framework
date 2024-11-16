package com.niu.shop.service.core.pointexchange.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.niu.shop.entity.exchange.ShopPointExchange;
import com.niu.shop.mapper.exchange.ShopPointExchangeMapper;
import com.niu.shop.service.core.pointexchange.ICoreGoodsSaleNumService;
import com.niu.shop.service.core.pointexchange.param.ExchangeDataChangeParam;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CoreGoodsSaleNumServiceImpl implements ICoreGoodsSaleNumService {

    @Resource
    ShopPointExchangeMapper shopPointExchangeMapper;

    /**
     * 增加
     * @param id
     * @param data
     */
    @Override
    public void inc(Integer id, ExchangeDataChangeParam data) {
        UpdateWrapper update = new UpdateWrapper<ShopPointExchange>();
        update.eq("id", id);
        if (ObjectUtil.isNotEmpty(data.getTotalPointNum())) update.setSql("total_point_num = total_point_num + " + data.getTotalPointNum());
        if (ObjectUtil.isNotEmpty(data.getTotalPriceNum())) update.setSql("total_price_num = total_price_num + " + data.getTotalPriceNum());
        if (ObjectUtil.isNotEmpty(data.getTotalOrderNum())) update.setSql("total_order_num = total_order_num + " + data.getTotalOrderNum());
        if (ObjectUtil.isNotEmpty(data.getTotalMemberNum())) update.setSql("total_member_num = total_member_num + " + data.getTotalMemberNum());
        if (ObjectUtil.isNotEmpty(data.getTotalExchangeNum())) update.setSql("total_exchange_num = total_exchange_num + " + data.getTotalExchangeNum());
        shopPointExchangeMapper.update(null, update);
    }

    /**
     * 减少
     * @param id
     * @param data
     */
    @Override
    public void dec(Integer id, ExchangeDataChangeParam data) {
        UpdateWrapper update = new UpdateWrapper<ShopPointExchange>();
        update.eq("id", id);
        if (ObjectUtil.isNotEmpty(data.getTotalPointNum())) update.setSql("total_point_num = total_point_num - " + data.getTotalPointNum());
        if (ObjectUtil.isNotEmpty(data.getTotalPriceNum())) update.setSql("total_price_num = total_price_num - " + data.getTotalPriceNum());
        if (ObjectUtil.isNotEmpty(data.getTotalOrderNum())) update.setSql("total_order_num = total_order_num - " + data.getTotalOrderNum());
        if (ObjectUtil.isNotEmpty(data.getTotalMemberNum())) update.setSql("total_member_num = total_member_num - " + data.getTotalMemberNum());
        if (ObjectUtil.isNotEmpty(data.getTotalExchangeNum())) update.setSql("total_exchange_num = total_exchange_num - " + data.getTotalExchangeNum());
        shopPointExchangeMapper.update(null, update);
    }

    /**
     * 改变库存
     *
     * @param id
     * @param skuId
     * @param stock
     */
    @Override
    public void changeStock(Integer id, Integer skuId, Integer stock) {
        ShopPointExchange model = shopPointExchangeMapper.selectOne(new QueryWrapper<ShopPointExchange>().select("id,product_detail,stock").eq("id", id));
        if (model == null) return;

        JSONArray productDetail = JSONUtil.parseArray(model.getProductDetail());
        for (int i = 0; i < productDetail.size(); i++) {
            JSONObject item = productDetail.getJSONObject(i);
            if (item.getInt("sku_id").equals(skuId)) {
                item.set("stock", item.getInt("stock") + stock);
                productDetail.set(i, item);
            }
        }
        model.setProductDetail(productDetail.toString());
        model.setStock(model.getStock() + stock);
        shopPointExchangeMapper.updateById(model);
    }
}
