package com.niu.core.service.admin.pay.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.entity.pay.Pay;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.core.service.admin.pay.param.PayParam;
import com.niu.core.service.admin.pay.param.PaySearchParam;
import com.niu.core.service.admin.pay.IPayService;
import com.niu.core.service.admin.pay.vo.PayInfoVo;
import com.niu.core.service.admin.pay.vo.PayListVo;
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
public class PayServiceImpl implements IPayService {
        
    @Resource
    PayMapper payMapper;

    /**
     * 支付记录列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<PayListVo>
     */
    @Override
    public PageResult<PayListVo> list(PageParam pageParam, PaySearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<Pay> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        IPage<Pay> iPage = payMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<PayListVo> list = new LinkedList<>();
        for (Pay item : iPage.getRecords()) {
            PayListVo vo = new PayListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit).setData(list);
    }

    /**
     * 支付记录详情
     * @param id 主键
     * @return PayInfoVo
     */
    @Override
    public PayInfoVo info(Integer id) {
        Pay model = payMapper.selectOne(
                new QueryWrapper<Pay>()
                    .eq("id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        PayInfoVo vo = new PayInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 支付记录添加
     * @param addParam 参数
     */
    @Override
    public void add(PayParam addParam) {
        Pay model = new Pay();
        model.setSiteId(addParam.getSiteId());
        model.setMainId(addParam.getMainId());
        model.setOutTradeNo(addParam.getOutTradeNo());
        model.setTradeType(addParam.getTradeType());
        model.setTradeId(addParam.getTradeId());
        model.setTradeNo(addParam.getTradeNo());
        model.setBody(addParam.getBody());
        model.setMoney(addParam.getMoney());
        model.setVoucher(addParam.getVoucher());
        model.setStatus(addParam.getStatus());
        model.setJson(addParam.getJson());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setPayTime(addParam.getPayTime());
        model.setCancelTime(addParam.getCancelTime());
        model.setType(addParam.getType());
        model.setMchId(addParam.getMchId());
        model.setMainType(addParam.getMainType());
        model.setChannel(addParam.getChannel());
        model.setFailReason(addParam.getFailReason());
        payMapper.insert(model);
    }

    /**
     * 支付记录编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, PayParam editParam) {
        Pay model = payMapper.selectOne(
                new QueryWrapper<Pay>()
                    .eq("id",  id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        model.setId(id);
        model.setSiteId(editParam.getSiteId());
        model.setMainId(editParam.getMainId());
        model.setOutTradeNo(editParam.getOutTradeNo());
        model.setTradeType(editParam.getTradeType());
        model.setTradeId(editParam.getTradeId());
        model.setTradeNo(editParam.getTradeNo());
        model.setBody(editParam.getBody());
        model.setMoney(editParam.getMoney());
        model.setVoucher(editParam.getVoucher());
        model.setStatus(editParam.getStatus());
        model.setJson(editParam.getJson());
        model.setPayTime(editParam.getPayTime());
        model.setCancelTime(editParam.getCancelTime());
        model.setType(editParam.getType());
        model.setMchId(editParam.getMchId());
        model.setMainType(editParam.getMainType());
        model.setChannel(editParam.getChannel());
        model.setFailReason(editParam.getFailReason());
        payMapper.updateById(model);
    }

    /**
     * 支付记录删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        Pay model = payMapper.selectOne(
                new QueryWrapper<Pay>()
                    .eq("id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        payMapper.delete(new QueryWrapper<Pay>().eq("id", id));
    }

}
