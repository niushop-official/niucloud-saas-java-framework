package com.niu.shop.service.admin.delivery.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.shop.entity.delivery.ShopDeliveryCompany;
import com.niu.shop.mapper.delivery.ShopDeliveryCompanyMapper;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryCompanyParam;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryCompanySearchParam;
import com.niu.shop.service.admin.delivery.IShopDeliveryCompanyService;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryCompanyInfoVo;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryCompanyListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import jakarta.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 物流公司实现
 */
@Service
public class ShopDeliveryCompanyServiceImpl extends ServiceImpl<ShopDeliveryCompanyMapper, ShopDeliveryCompany> implements IShopDeliveryCompanyService {
        
    @Resource
    ShopDeliveryCompanyMapper shopDeliveryCompanyMapper;

    /**
     * 物流公司分页列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopDeliveryCompanyListVo>
     */
    @Override
    public PageResult<ShopDeliveryCompanyListVo> page(PageParam pageParam, ShopDeliveryCompanySearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<ShopDeliveryCompany> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(searchParam.getCompanyName())) queryWrapper.like("company_name", searchParam.getCompanyName());
        if (ObjectUtil.isNotEmpty(searchParam.getElectronicSheetSwitch())) queryWrapper.eq("electronic_sheet_switch", searchParam.getElectronicSheetSwitch());
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.orderByDesc("company_id");

        IPage<ShopDeliveryCompany> iPage = shopDeliveryCompanyMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<ShopDeliveryCompanyListVo> list = new LinkedList<>();
        for (ShopDeliveryCompany item : iPage.getRecords()) {
            ShopDeliveryCompanyListVo vo = new ShopDeliveryCompanyListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 物流公司列表
     * @param searchParam 搜索参数
     * @return PageResult<ShopDeliveryCompanyListVo>
     */
    @Override
    public List<ShopDeliveryCompanyListVo> list(ShopDeliveryCompanySearchParam searchParam) {
        QueryWrapper<ShopDeliveryCompany> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(searchParam.getCompanyName())) queryWrapper.like("company_name", searchParam.getCompanyName());
        if (ObjectUtil.isNotEmpty(searchParam.getElectronicSheetSwitch())) queryWrapper.eq("electronic_sheet_switch", searchParam.getElectronicSheetSwitch());
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.orderByDesc("company_id");

        List<ShopDeliveryCompany> record = shopDeliveryCompanyMapper.selectList(queryWrapper);
        List<ShopDeliveryCompanyListVo> list = new LinkedList<>();
        for (ShopDeliveryCompany item : record) {
            ShopDeliveryCompanyListVo vo = new ShopDeliveryCompanyListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }

    /**
     * 物流公司详情
     * @param id 主键
     * @return ShopDeliveryCompanyInfoVo
     */
    @Override
    public ShopDeliveryCompanyInfoVo info(Integer id) {
        ShopDeliveryCompany model = shopDeliveryCompanyMapper.selectOne(
                new QueryWrapper<ShopDeliveryCompany>()
                    .eq("company_id", id)
                    .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "物流公司不存在");

        ShopDeliveryCompanyInfoVo vo = new ShopDeliveryCompanyInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 物流公司添加
     * @param addParam 参数
     */
    @Override
    public void add(ShopDeliveryCompanyParam addParam) {
        ShopDeliveryCompany model = new ShopDeliveryCompany();
        BeanUtils.copyProperties(addParam, model);
        model.setSiteId(RequestUtils.siteId());
        model.setExpType(addParam.getExpType().toString());
        model.setPrintStyle(addParam.getPrintStyle().toString());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        shopDeliveryCompanyMapper.insert(model);
    }

    /**
     * 物流公司编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, ShopDeliveryCompanyParam editParam) {
        ShopDeliveryCompany model = shopDeliveryCompanyMapper.selectOne(
                new QueryWrapper<ShopDeliveryCompany>()
                        .eq("company_id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "物流公司不存在");

        BeanUtils.copyProperties(editParam, model);
        model.setExpType(editParam.getExpType().toString());
        model.setPrintStyle(editParam.getPrintStyle().toString());
        model.setUpdateTime(System.currentTimeMillis() / 1000);

        shopDeliveryCompanyMapper.updateById(model);
    }

    /**
     * 物流公司删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        shopDeliveryCompanyMapper.delete(new QueryWrapper<ShopDeliveryCompany>().eq("company_id", id).eq("site_id", RequestUtils.siteId()));
    }

    /**
     * 批量添加物流公司
     *
     * @param list
     */
    @Override
    public void insertAll(List<ShopDeliveryCompany> list) {
        super.saveOrUpdateBatch(list);
    }

}
