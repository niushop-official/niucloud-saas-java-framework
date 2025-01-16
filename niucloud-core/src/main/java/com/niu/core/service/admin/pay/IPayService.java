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


    /**
     * 支付记录列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<PayListVo>
     */
    PageResult<PayListVo> list(PageParam pageParam, PaySearchParam searchParam);

    /**
     * 支付记录详情
     *
     * @param id 主键ID
     * @return PayInfoVo
     */
    PayInfoVo info(Integer id);

    /**
     * 支付记录添加
     *
     * @param addParam 添加参数
     */
    void add(PayParam addParam);

    /**
     * 支付记录编辑
     *
     * @param id        主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, PayParam editParam);

    /**
     * 支付记录删除
     *
     * @param id 主键ID
     */
    void del(Integer id);

    FriendsPayInfoByTradeVo getFriendspayInfoByTrade(GetFriendspayInfoByTradeParam param);

    List<PayTypeVo> getPayTypeList();

    Object pay(com.niu.core.common.loader.pay.param.PayParam param);

}
