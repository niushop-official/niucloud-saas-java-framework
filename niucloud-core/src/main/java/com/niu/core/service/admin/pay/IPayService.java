package com.niu.core.service.admin.pay;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.pay.param.GetFriendspayInfoByTradeParam;
import com.niu.core.service.admin.pay.param.PayParam;
import com.niu.core.service.admin.pay.param.PaySearchParam;
import com.niu.core.service.admin.pay.vo.FriendsPayInfoByTradeVo;
import com.niu.core.service.admin.pay.vo.PayInfoVo;
import com.niu.core.service.admin.pay.vo.PayListVo;
import com.niu.core.service.core.pay.vo.PayTypeVo;

import java.util.List;

/**
 * 支付记录服务接口
 */
public interface IPayService {

    FriendsPayInfoByTradeVo getFriendspayInfoByTrade(GetFriendspayInfoByTradeParam param);

    List<PayTypeVo> getPayTypeList();

    Object pay(com.niu.core.common.loader.pay.param.PayParam param);

}
