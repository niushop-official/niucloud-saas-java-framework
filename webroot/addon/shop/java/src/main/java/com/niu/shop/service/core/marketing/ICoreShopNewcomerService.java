package com.niu.shop.service.core.marketing;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niu.shop.entity.newconsumer.ShopNewcomerMemberRecords;
import com.niu.shop.service.core.marketing.vo.CoreShopNewcomerConfigVo;

public interface ICoreShopNewcomerService extends IService<ShopNewcomerMemberRecords> {
    CoreShopNewcomerConfigVo getConfig(Integer siteId);

    boolean checkIfNewcomer(Integer site, Integer memberId);

    void afterSave();
}
