package com.niu.core.service.core.pay;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.loader.pay.param.TransferNotifyParam;
import com.niu.core.entity.pay.PayTransfer;
import com.niu.core.service.core.pay.param.PayTransferParam;
import com.niu.core.service.core.pay.param.PayTransferSearchParam;
import com.niu.core.service.core.pay.vo.PayTransferInfoVo;
import com.niu.core.service.core.pay.vo.PayTransferListVo;

import java.util.Map;

/**
 * 转账服务接口
 */
public interface ICoreTransferService {

    String create(PayTransfer model);

    Object transfer(Integer siteId, String transferNo, String transferType, Map<String, Object> data);

    PayTransfer findTransferByTransferNo(Integer siteId, String transferNo);

    void transferNotify(TransferNotifyParam param);

    void cancel(Integer siteId, String transferNo);

    void check(Integer siteId, String transferNo);
}
