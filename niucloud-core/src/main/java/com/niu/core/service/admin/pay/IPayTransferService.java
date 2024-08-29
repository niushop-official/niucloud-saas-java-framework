package com.niu.core.service.admin.pay;

import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.pay.param.PayTransferParam;
import com.niu.core.service.admin.pay.param.PayTransferSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.pay.vo.PayTransferInfoVo;
import com.niu.core.service.admin.pay.vo.PayTransferListVo;

/**
 * 转账服务接口
 */
public interface IPayTransferService {


    /**
     * 转账列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<PayTransferListVo>
     */
    PageResult<PayTransferListVo> list(PageParam pageParam, PayTransferSearchParam searchParam);

    /**
     * 转账详情
     * @param id 主键ID
     * @return PayTransferInfoVo
     */
    PayTransferInfoVo info(Integer id);

    /**
     * 转账添加
     * @param addParam 添加参数
     */
    void add(PayTransferParam addParam);

    /**
     * 转账编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, PayTransferParam editParam);

    /**
     * 转账删除
     * @param id 主键ID
     */
    void del(Integer id);

}
