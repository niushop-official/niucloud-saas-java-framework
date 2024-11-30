package com.niu.shop.service.core.marketing;

import com.niu.shop.service.core.marketing.vo.ShopNewcomerConfigVo;

public interface ICoreShopNewcomerService {
    ShopNewcomerConfigVo getConfig();

    void checkIfNewcomer(Integer site, Integer memberId);

    void afterSave(Integer siteId);
}
