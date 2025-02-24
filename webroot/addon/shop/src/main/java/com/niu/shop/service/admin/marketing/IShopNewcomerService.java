package com.niu.shop.service.admin.marketing;

import com.niu.core.common.domain.PageParam;
import com.niu.shop.service.admin.marketing.param.ShopNewcomerSelectPageParam;
import com.niu.shop.service.admin.marketing.param.ShopNewcomerSelectSkuParam;
import com.niu.shop.service.admin.marketing.param.ShopNewcomerSetConfigParam;
import com.niu.shop.service.admin.marketing.param.ShopNewcomerShopGoodsVo;
import com.niu.shop.service.admin.marketing.vo.ShopNewcomerSelectPageVo;
import com.niu.shop.service.admin.marketing.vo.ShopNewcomerConfigVo;
import com.niu.shop.service.admin.marketing.vo.ShopNewcomerSelectSkuVo;

import java.util.List;

/**
 * 新人专享会员参与记录服务接口
 */
public interface IShopNewcomerService {

    void setConfig(ShopNewcomerSetConfigParam param);

    ShopNewcomerConfigVo getConfig();

    ShopNewcomerSelectPageVo<ShopNewcomerShopGoodsVo> getSelectPage(PageParam pageParam, ShopNewcomerSelectPageParam param);

    List<ShopNewcomerSelectSkuVo> getSelectSku(ShopNewcomerSelectSkuParam param);
}
