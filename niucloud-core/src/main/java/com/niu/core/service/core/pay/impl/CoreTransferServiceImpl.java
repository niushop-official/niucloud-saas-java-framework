package com.niu.core.service.core.pay.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.loader.pay.param.TransferNotifyParam;
import com.niu.core.common.loader.pay.param.TransferParam;
import com.niu.core.entity.pay.PayTransfer;
import com.niu.core.enums.pay.TransferStatusEnum;
import com.niu.core.enums.pay.TransferTypeEnum;
import com.niu.core.event.pay.PayCloseEvent;
import com.niu.core.event.pay.TransferSuccessEvent;
import com.niu.core.mapper.pay.PayTransferMapper;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.core.service.core.pay.ICoreTransferService;
import com.niu.core.service.core.pay.param.PayTransferParam;
import com.niu.core.service.core.pay.param.PayTransferSearchParam;
import com.niu.core.service.core.pay.vo.PayTransferInfoVo;
import com.niu.core.service.core.pay.vo.PayTransferListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * 转账实现
 */
@Service
public class CoreTransferServiceImpl implements ICoreTransferService {
        
    @Resource
    PayTransferMapper payTransferMapper;

    @Resource
    ICorePayService corePayService;

    /**
     * 创建转账单据
     * @param model
     * @return
     */
    public String create(PayTransfer model) {
        model.setTransferNo(createTransferNo());
        model.setTransferStatus(TransferStatusEnum.WAIT.getStatus());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        payTransferMapper.insert(model);
        return model.getTransferNo();
    }

    /**
     * 创建交易号
     * @return
     */
    private String createTransferNo() {
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        long id = snowflake.nextId();
        return DateUtil.format(DateUtil.date(System.currentTimeMillis()), "yyyyMMdd") + id;
    }

    /**
     * 通过转账单号查询转账
     * @param siteId
     * @param transferNo
     * @return
     */
    public PayTransfer findTransferByTransferNo(Integer siteId, String transferNo) {
        return payTransferMapper.selectOne(new QueryWrapper<PayTransfer>()
                .eq("site_id", siteId)
                .eq("transfer_no", transferNo));
    }

    /**
     *
     * @param siteId
     * @param transferNo
     * @param transferType
     * @param data
     */
    @Transactional
    public void transfer(Integer siteId, String transferNo, String transferType, Map<String, Object> data) {
        PayTransfer transfer = findTransferByTransferNo(siteId, transferNo);
        if (transfer == null) throw new CommonException("无效的转账单据");
        if (!transfer.getTransferStatus().equals(TransferStatusEnum.WAIT.getStatus()) && !transfer.getTransferStatus().equals(TransferStatusEnum.FAIL.getStatus())) {
            throw new CommonException("当前转账未处于待转账状态");
        }

        Map transferTypeEnum = TransferTypeEnum.getMap().get(transferType);
        if (transferTypeEnum == null) throw new CommonException("不支持的转账方式");

        if ((Boolean) transferTypeEnum.get("is_online")) {
            TransferParam param = new TransferParam();
            param.setTransfer(transfer);
            param.setNotifyUrl(corePayService.buildNotifyUrl(siteId, "transfer", transferType, "transfer"));
            corePayService.driver(siteId, "transfer", transferType).transfer(param);
            return;
        }
        this.success(transfer);
    }

    @Transactional
    public void transferNotify(TransferNotifyParam param) {
        PayTransfer transfer = findTransferByTransferNo(param.getSiteId(), param.getTransferNo());
        if (transfer == null) throw new CommonException("无效的转账单据");
        if (!transfer.getTransferStatus().equals(TransferStatusEnum.WAIT.getStatus()) && !transfer.getTransferStatus().equals(TransferStatusEnum.FAIL.getStatus())) {
            throw new CommonException("当前转账未处于待转账状态");
        }

        if (param.getTransferStatus().getStatus().equals(TransferStatusEnum.SUCCESS.getStatus())) {
            this.success(transfer);
        }

        if (param.getTransferStatus().getStatus().equals(TransferStatusEnum.FAIL.getStatus())) {
            transfer.setTransferFailReason(param.getFailReason());
            this.fail(transfer);
        }
    }

    /**
     * 转账成功
     *
     * @param transfer
     */
    private void success(PayTransfer transfer) {
        transfer.setTransferStatus(TransferStatusEnum.SUCCESS.getStatus());
        transfer.setTransferTime(System.currentTimeMillis() / 1000);
        payTransferMapper.updateById(transfer);

        TransferSuccessEvent event = new TransferSuccessEvent();
        event.setTransfer(transfer);
        event.setSiteId(transfer.getSiteId());
        event.addAppSign("core");
        event.setName("TransferSuccessEvent");
        EventAndSubscribeOfPublisher.publishAll(event);
    }

    /**
     * 转账失败
     *
     * @param transfer
     */
    private void fail(PayTransfer transfer) {
        transfer.setTransferStatus(TransferStatusEnum.FAIL.getStatus());
        payTransferMapper.updateById(transfer);
    }
}
