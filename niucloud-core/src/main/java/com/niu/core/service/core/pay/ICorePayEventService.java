package com.niu.core.service.core.pay;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.core.pay.param.PayRefundParam;
import com.niu.core.service.core.pay.param.PayRefundSearchParam;
import com.niu.core.service.core.pay.vo.PayRefundInfoVo;
import com.niu.core.service.core.pay.vo.PayRefundListVo;

/**
 * 支付记录服务接口
 */
public interface ICorePayEventService {


    /**
     * 支付记录列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<PayRefundListVo>
     */
    PageResult<PayRefundListVo> list(PageParam pageParam, PayRefundSearchParam searchParam);

    /**
     * 支付记录详情
     * @param id 主键ID
     * @return PayRefundInfoVo
     */
    PayRefundInfoVo info(Integer id);

    /**
     * 支付记录添加
     * @param addParam 添加参数
     */
    void add(PayRefundParam addParam);

    /**
     * 支付记录编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, PayRefundParam editParam);

    /**
     * 支付记录删除
     * @param id 主键ID
     */
    void del(Integer id);

}
