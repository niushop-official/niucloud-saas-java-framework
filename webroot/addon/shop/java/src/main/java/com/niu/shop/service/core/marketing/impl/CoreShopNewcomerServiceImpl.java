package com.niu.shop.service.core.marketing.impl;

import com.niu.shop.service.core.marketing.ICoreShopNewcomerService;
import com.niu.shop.service.core.marketing.vo.ShopNewcomerConfigVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoreShopNewcomerServiceImpl implements ICoreShopNewcomerService {

    @Override
    public ShopNewcomerConfigVo getConfig() {
        return null;
    }

    @Override
    public void checkIfNewcomer(Integer site, Integer memberId) {

    }

    @Override
    public void afterSave(Integer siteId) {

    }

}
