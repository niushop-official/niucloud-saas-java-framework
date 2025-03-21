package com.niu.core.service.admin.site.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.pay.PayRefund;
import com.niu.core.entity.pay.PayTransfer;
import com.niu.core.entity.site.SiteAccountLog;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.core.mapper.pay.PayRefundMapper;
import com.niu.core.mapper.pay.PayTransferMapper;
import com.niu.core.mapper.site.SiteAccountLogMapper;
import com.niu.core.service.admin.site.param.SiteAccountLogParam;
import com.niu.core.service.admin.site.param.SiteAccountLogSearchParam;
import com.niu.core.service.admin.site.ISiteAccountLogService;
import com.niu.core.service.admin.site.vo.SiteAccountLogInfoVo;
import com.niu.core.service.admin.site.vo.SiteAccountLogListVo;
import com.niu.core.service.admin.site.vo.SiteGroupListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;


/**
 * 站点账单记录实现
 */
@Service
public class SiteAccountLogServiceImpl implements ISiteAccountLogService {

    @Resource
    SiteAccountLogMapper siteAccountLogMapper;

    @Resource
    PayMapper payMapper;

    @Resource
    PayRefundMapper payRefundMapper;

    @Resource
    PayTransferMapper payTransferMapper;

    /**
     * 站点账单记录列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SiteAccountLogListVo>
     */
    @Override
    public PageResult<SiteAccountLogListVo> list(PageParam pageParam, SiteAccountLogSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<SiteAccountLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        //查询条件判断组装
        if (ObjectUtil.isNotEmpty(searchParam.getTradeNo())) {
            queryWrapper.like("trade_no", searchParam.getTradeNo());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getType())) {
            queryWrapper.eq("type", searchParam.getType());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) {
            String[] createTime = searchParam.getCreateTime();
            QueryMapperUtils.buildByTime(queryWrapper, "create_time", createTime);
        }
        queryWrapper.orderByDesc("create_time");
        IPage<SiteAccountLog> iPage = siteAccountLogMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<SiteAccountLogListVo> list = new LinkedList<>();
        for (SiteAccountLog item : iPage.getRecords()) {
            SiteAccountLogListVo vo = new SiteAccountLogListVo();
            BeanUtils.copyProperties(item, vo);
            Object typeModel = new Object();
            if (item.getType().equals("pay")) {
                typeModel = payMapper.selectOne(
                        new QueryWrapper<Pay>()
                                .eq("out_trade_no", item.getTradeNo())
                                .last("limit 1"));
            } else if (item.getType().equals("refund")) {
                typeModel = payRefundMapper.selectOne(
                        new QueryWrapper<PayRefund>()
                                .eq("refund_no", item.getTradeNo())
                                .last("limit 1"));
            } else {
                typeModel = payTransferMapper.selectOne(
                        new QueryWrapper<PayTransfer>()
                                .eq("transfer_no", item.getTradeNo())
                                .last("limit 1"));
            }
            vo.setPayInfo(typeModel);
            list.add(vo);
        }

        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 站点账单记录详情
     *
     * @param id 主键
     * @return SiteAccountLogInfoVo
     */
    @Override
    public SiteAccountLogInfoVo info(Integer id) {
        SiteAccountLog model = siteAccountLogMapper.selectOne(
                new QueryWrapper<SiteAccountLog>()
                        .eq("site_id", RequestUtils.siteId())
                        .eq("id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        SiteAccountLogInfoVo vo = new SiteAccountLogInfoVo();
        BeanUtils.copyProperties(model, vo);
        Object typeModel = new Object();
        if (model.getType().equals("pay")) {
            typeModel = payMapper.selectOne(
                    new QueryWrapper<Pay>()
                            .eq("out_trade_no", model.getTradeNo())
                            .last("limit 1"));
        } else if (model.getType().equals("refund")) {
            typeModel = payRefundMapper.selectOne(
                    new QueryWrapper<PayRefund>()
                            .eq("refund_no", model.getTradeNo())
                            .last("limit 1"));
        } else {
            typeModel = payTransferMapper.selectOne(
                    new QueryWrapper<PayTransfer>()
                            .eq("transfer_no", model.getTradeNo())
                            .last("limit 1"));
        }
        vo.setPayInfo(typeModel);
        return vo;
    }

    @Override
    public Map<String, Object> stat() {
        Map<String, Object> statMap = new HashMap<>();
        List<Map<String, Object>> payList = siteAccountLogMapper.selectMaps(new QueryWrapper<SiteAccountLog>()
                .select("sum(money) as pay")
                .eq("site_id", RequestUtils.siteId())
                .eq("type", "pay"));

        if(ObjectUtil.isNotEmpty(payList.get(0)))
        {
            statMap.put("pay", payList.get(0).get("pay"));
        }else{
            statMap.put("pay", 0);
        }
        List<Map<String, Object>> refundList = siteAccountLogMapper.selectMaps(new QueryWrapper<SiteAccountLog>()
                .select("sum(money)*-1 as refund")
                .eq("site_id", RequestUtils.siteId())
                .eq("type", "refund"));

        if(ObjectUtil.isNotEmpty(refundList.get(0)))
        {
            statMap.put("refund", payList.get(0).get("refund"));
        }else{
            statMap.put("refund", 0);
        }

        List<Map<String, Object>> transferList = siteAccountLogMapper.selectMaps(new QueryWrapper<SiteAccountLog>()
                .select("sum(money)*-1 as transfer")
                .eq("site_id", RequestUtils.siteId())
                .eq("type", "transfer"));

        if(ObjectUtil.isNotEmpty(transferList.get(0)))
        {
            statMap.put("transfer", payList.get(0).get("transfer"));
        }else{
            statMap.put("transfer", 0);

        }
        return statMap;
    }

}
