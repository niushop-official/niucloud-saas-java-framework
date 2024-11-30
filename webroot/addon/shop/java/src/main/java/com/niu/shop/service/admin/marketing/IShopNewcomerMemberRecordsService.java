package com.niu.shop.service.admin.marketing;

import com.niu.shop.service.admin.marketing.param.ShopNewcomerSetConfigParam;
import com.niu.shop.service.admin.marketing.vo.ShopNewComerConfigVo;

/**
 * 新人专享会员参与记录服务接口
 */
public interface IShopNewcomerMemberRecordsService {

    void setConfig(ShopNewcomerSetConfigParam param);

    ShopNewComerConfigVo getConfig();
}
