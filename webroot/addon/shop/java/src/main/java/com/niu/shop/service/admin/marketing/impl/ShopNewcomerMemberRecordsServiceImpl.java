package com.niu.shop.service.admin.marketing.impl;

import com.niu.shop.mapper.newconsumer.ShopNewcomerMemberRecordsMapper;
import com.niu.shop.service.admin.marketing.IShopNewcomerMemberRecordsService;
import com.niu.shop.service.admin.marketing.param.ShopNewcomerSetConfigParam;
import com.niu.shop.service.admin.marketing.vo.ShopNewComerConfigVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


/**
 * 新人专享会员参与记录实现
 */
@Service
@RequiredArgsConstructor
public class ShopNewcomerMemberRecordsServiceImpl implements IShopNewcomerMemberRecordsService {
    private final ShopNewcomerMemberRecordsMapper shopNewcomerMemberRecordsMapper;

    @Override
    public void setConfig(ShopNewcomerSetConfigParam param) {

    }

    @Override
    public ShopNewComerConfigVo getConfig() {
        return null;
    }
    
}
