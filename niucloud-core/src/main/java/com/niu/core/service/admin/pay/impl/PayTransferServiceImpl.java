package com.niu.core.service.admin.pay.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.entity.pay.PayTransfer;
import com.niu.core.mapper.pay.PayTransferMapper;
import com.niu.core.service.admin.pay.param.PayTransferParam;
import com.niu.core.service.admin.pay.param.PayTransferSearchParam;
import com.niu.core.service.admin.pay.IPayTransferService;
import com.niu.core.service.admin.pay.vo.PayTransferInfoVo;
import com.niu.core.service.admin.pay.vo.PayTransferListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 转账实现
 */
@Service
public class PayTransferServiceImpl implements IPayTransferService {
        
    @Resource
    PayTransferMapper payTransferMapper;

    /**
     * 转账列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<PayTransferListVo>
     */
    @Override
    public PageResult<PayTransferListVo> list(PageParam pageParam, PayTransferSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<PayTransfer> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        IPage<PayTransfer> iPage = payTransferMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<PayTransferListVo> list = new LinkedList<>();
        for (PayTransfer item : iPage.getRecords()) {
            PayTransferListVo vo = new PayTransferListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 转账详情
     * @param id 主键
     * @return PayTransferInfoVo
     */
    @Override
    public PayTransferInfoVo info(Integer id) {
        PayTransfer model = payTransferMapper.selectOne(
                new QueryWrapper<PayTransfer>()
                    .eq("id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        PayTransferInfoVo vo = new PayTransferInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 转账添加
     * @param addParam 参数
     */
    @Override
    public void add(PayTransferParam addParam) {
        PayTransfer model = new PayTransfer();
        model.setSiteId(addParam.getSiteId());
        model.setTradeType(addParam.getTradeType());
        model.setTransferNo(addParam.getTransferNo());
        model.setMainId(addParam.getMainId());
        model.setMainType(addParam.getMainType());
        model.setTransferType(addParam.getTransferType());
        model.setTransferRealname(addParam.getTransferRealname());
        model.setTransferMobile(addParam.getTransferMobile());
        model.setTransferBank(addParam.getTransferBank());
        model.setTransferAccount(addParam.getTransferAccount());
        model.setTransferVoucher(addParam.getTransferVoucher());
        model.setTransferRemark(addParam.getTransferRemark());
        model.setTransferFailReason(addParam.getTransferFailReason());
        model.setTransferStatus(addParam.getTransferStatus());
        model.setMoney(addParam.getMoney());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setTransferTime(addParam.getTransferTime());
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        model.setOpenid(addParam.getOpenid());
        model.setRemark(addParam.getRemark());
        model.setBatchId(addParam.getBatchId());
        payTransferMapper.insert(model);
    }

    /**
     * 转账编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, PayTransferParam editParam) {
        PayTransfer model = payTransferMapper.selectOne(
                new QueryWrapper<PayTransfer>()
                    .eq("id",  id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        model.setId(id);
        model.setSiteId(editParam.getSiteId());
        model.setTradeType(editParam.getTradeType());
        model.setTransferNo(editParam.getTransferNo());
        model.setMainId(editParam.getMainId());
        model.setMainType(editParam.getMainType());
        model.setTransferType(editParam.getTransferType());
        model.setTransferRealname(editParam.getTransferRealname());
        model.setTransferMobile(editParam.getTransferMobile());
        model.setTransferBank(editParam.getTransferBank());
        model.setTransferAccount(editParam.getTransferAccount());
        model.setTransferVoucher(editParam.getTransferVoucher());
        model.setTransferRemark(editParam.getTransferRemark());
        model.setTransferFailReason(editParam.getTransferFailReason());
        model.setTransferStatus(editParam.getTransferStatus());
        model.setMoney(editParam.getMoney());
        model.setTransferTime(editParam.getTransferTime());
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        model.setOpenid(editParam.getOpenid());
        model.setRemark(editParam.getRemark());
        model.setBatchId(editParam.getBatchId());
        payTransferMapper.updateById(model);
    }

    /**
     * 转账删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        PayTransfer model = payTransferMapper.selectOne(
                new QueryWrapper<PayTransfer>()
                    .eq("id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        payTransferMapper.delete(new QueryWrapper<PayTransfer>().eq("id", id));
    }

}
