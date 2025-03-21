package com.niu.core.service.admin.pay.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.pay.PayRefund;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.core.mapper.pay.PayRefundMapper;
import com.niu.core.service.admin.pay.param.PayRefundSearchParam;
import com.niu.core.service.admin.pay.IPayRefundService;
import com.niu.core.service.core.pay.param.PayRefundTransferParam;
import com.niu.core.service.admin.pay.vo.PayRefundInfoVo;
import com.niu.core.service.admin.pay.vo.PayRefundListVo;
import com.niu.core.service.core.pay.ICoreRefundService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import jakarta.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 退款记录实现
 */
@Service
public class PayRefundServiceImpl implements IPayRefundService {
        
    @Resource
    PayRefundMapper payRefundMapper;

    @Resource
    ICoreRefundService coreRefundService;

    /**
     * 退款记录列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<PayRefundListVo>
     */
    @Override
    public PageResult<PayRefundListVo> list(PageParam pageParam, PayRefundSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<PayRefund> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        if (ObjectUtil.isNotEmpty(searchParam.getRefundNo())) queryWrapper.eq("refund_no", searchParam.getRefundNo());
        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) queryWrapper.eq("status", searchParam.getStatus());
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) QueryMapperUtils.buildByTime(queryWrapper, "create_time", searchParam.getCreateTime());
        queryWrapper.orderByDesc("id");

        JSONObject payTypeEnum = PayTypeEnum.getType();

        IPage<PayRefund> iPage = payRefundMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<PayRefundListVo> list = new LinkedList<>();
        for (PayRefund item : iPage.getRecords()) {
            PayRefundListVo vo = new PayRefundListVo();
            BeanUtils.copyProperties(item, vo);
            vo.setTypeName(payTypeEnum.getByPath(vo.getType() + ".name", String.class));
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 退款记录详情
     * @param refundNo 主键
     * @return PayRefundInfoVo
     */
    @Override
    public PayRefundInfoVo info(String refundNo) {
        PayRefund model = payRefundMapper.selectOne(
                new QueryWrapper<PayRefund>()
                    .eq("refund_no", refundNo)
                    .eq("site_id", RequestUtils.siteId())
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        PayRefundInfoVo vo = new PayRefundInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 退款转账
     *
     * @param param
     */
    public void transfer(PayRefundTransferParam param) {
        param.setSiteId(RequestUtils.siteId());
        coreRefundService.refund(param);
    }
}
