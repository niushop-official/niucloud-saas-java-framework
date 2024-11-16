package com.niu.core.service.core.pay.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.entity.pay.PayRefund;
import com.niu.core.mapper.pay.PayRefundMapper;
import com.niu.core.service.core.pay.ICorePayEventService;
import com.niu.core.service.core.pay.param.PayRefundParam;
import com.niu.core.service.core.pay.param.PayRefundSearchParam;
import com.niu.core.service.core.pay.vo.PayRefundInfoVo;
import com.niu.core.service.core.pay.vo.PayRefundListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 支付记录实现
 */
@Service
public class CorePayEventServiceImpl implements ICorePayEventService {
        
    @Resource
    PayRefundMapper payRefundMapper;

    /**
     * 支付记录列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<PayRefundListVo>
     */
    @Override
    public PageResult<PayRefundListVo> list(PageParam pageParam, PayRefundSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<PayRefund> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        IPage<PayRefund> iPage = payRefundMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<PayRefundListVo> list = new LinkedList<>();
        for (PayRefund item : iPage.getRecords()) {
            PayRefundListVo vo = new PayRefundListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 支付记录详情
     * @param id 主键
     * @return PayRefundInfoVo
     */
    @Override
    public PayRefundInfoVo info(Integer id) {
        PayRefund model = payRefundMapper.selectOne(
                new QueryWrapper<PayRefund>()
                    .eq("id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        PayRefundInfoVo vo = new PayRefundInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 支付记录添加
     * @param addParam 参数
     */
    @Override
    public void add(PayRefundParam addParam) {
        PayRefund model = new PayRefund();
        model.setSiteId(addParam.getSiteId());
        model.setRefundNo(addParam.getRefundNo());
        model.setOutTradeNo(addParam.getOutTradeNo());
        model.setType(addParam.getType());
        model.setChannel(addParam.getChannel());
        model.setMoney(addParam.getMoney());
        model.setReason(addParam.getReason());
        model.setStatus(addParam.getStatus());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setRefundTime(addParam.getRefundTime());
        model.setCloseTime(addParam.getCloseTime());
        model.setFailReason(addParam.getFailReason());
        model.setVoucher(addParam.getVoucher());
        model.setTradeType(addParam.getTradeType());
        model.setTradeId(addParam.getTradeId());
        model.setRefundType(addParam.getRefundType());
        model.setMainType(addParam.getMainType());
        model.setMainId(addParam.getMainId());
        model.setPayRefundNo(addParam.getPayRefundNo());
        payRefundMapper.insert(model);
    }

    /**
     * 支付记录编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, PayRefundParam editParam) {
        PayRefund model = payRefundMapper.selectOne(
                new QueryWrapper<PayRefund>()
                    .eq("id",  id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        model.setId(id);
        model.setSiteId(editParam.getSiteId());
        model.setRefundNo(editParam.getRefundNo());
        model.setOutTradeNo(editParam.getOutTradeNo());
        model.setType(editParam.getType());
        model.setChannel(editParam.getChannel());
        model.setMoney(editParam.getMoney());
        model.setReason(editParam.getReason());
        model.setStatus(editParam.getStatus());
        model.setRefundTime(editParam.getRefundTime());
        model.setCloseTime(editParam.getCloseTime());
        model.setFailReason(editParam.getFailReason());
        model.setVoucher(editParam.getVoucher());
        model.setTradeType(editParam.getTradeType());
        model.setTradeId(editParam.getTradeId());
        model.setRefundType(editParam.getRefundType());
        model.setMainType(editParam.getMainType());
        model.setMainId(editParam.getMainId());
        model.setPayRefundNo(editParam.getPayRefundNo());
        payRefundMapper.updateById(model);
    }

    /**
     * 支付记录删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        PayRefund model = payRefundMapper.selectOne(
                new QueryWrapper<PayRefund>()
                    .eq("id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        payRefundMapper.delete(new QueryWrapper<PayRefund>().eq("id", id));
    }

}
