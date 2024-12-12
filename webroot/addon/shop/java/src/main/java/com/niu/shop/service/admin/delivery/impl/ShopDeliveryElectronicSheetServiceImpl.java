package com.niu.shop.service.admin.delivery.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import com.niu.shop.consts.ShopDeliveryElectronicSheetConst;
import com.niu.shop.entity.delivery.ShopDeliveryCompany;
import com.niu.shop.entity.delivery.ShopDeliveryElectronicSheet;
import com.niu.shop.enums.delivery.ElectronicSheetPayTypeEnum;
import com.niu.shop.mapper.delivery.ShopDeliveryCompanyMapper;
import com.niu.shop.mapper.delivery.ShopDeliveryElectronicSheetMapper;
import com.niu.shop.service.admin.delivery.IShopDeliveryElectronicSheetService;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryElectronicSheetListsParam;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryElectronicSheetPagesParam;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryElectronicSheetParam;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryElectronicSheetInfoVo;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryElectronicSheetListVo;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryElectronicSheetPageVo;
import com.niu.shop.service.core.delivery.ICoreElectronicSheetService;
import com.niu.shop.service.core.delivery.param.ShopDeliveryPrintElectronicSheetParam;
import com.niu.shop.service.core.delivery.vo.ElectronicSheetConfigValueVo;
import com.niu.shop.service.core.delivery.vo.ElectronicSheetPrintVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 电子面单实现
 */
@Service
public class ShopDeliveryElectronicSheetServiceImpl implements IShopDeliveryElectronicSheetService {

    @Resource
    ShopDeliveryElectronicSheetMapper shopDeliveryElectronicSheetMapper;

    @Resource
    ICoreConfigService coreConfigService;

    @Resource
    ShopDeliveryCompanyMapper shopDeliveryCompanyMapper;

    @Resource
    ICoreElectronicSheetService coreElectronicSheetService;

    @Override
    public List<ShopDeliveryElectronicSheetListVo> lists(PageParam pageParam, ShopDeliveryElectronicSheetListsParam listsParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        LambdaQueryWrapper<ShopDeliveryElectronicSheet> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotEmpty(listsParam.getTemplateName())) {
            queryWrapper.like(ShopDeliveryElectronicSheet::getTemplateName, listsParam.getTemplateName());
        }

        if (ObjectUtil.isNotEmpty(listsParam.getExpressCompanyId())) {
            queryWrapper.eq(ShopDeliveryElectronicSheet::getExpressCompanyId, listsParam.getExpressCompanyId());
        }

        if (ObjectUtil.isNotEmpty(listsParam.getIsNotice())) {
            queryWrapper.eq(ShopDeliveryElectronicSheet::getIsNotice, listsParam.getIsNotice());
        }

        if (ObjectUtil.isNotEmpty(listsParam.getStatus())) {
            queryWrapper.eq(ShopDeliveryElectronicSheet::getStatus, listsParam.getStatus());
        }

        queryWrapper.orderByDesc(ShopDeliveryElectronicSheet::getId);

        return shopDeliveryElectronicSheetMapper.selectPage(new Page<>(page, limit), queryWrapper)
                .getRecords()
                .stream()
                .map(item -> BeanUtil.toBean(item, ShopDeliveryElectronicSheetListVo.class))
                .collect(Collectors.toList());
    }

    /**
     * 电子面单列表
     *
     * @param pageParam  分页参数
     * @param pagesParam 搜索参数
     * @return PageResult<ShopDeliveryElectronicSheetListVo>
     */
    @Override
    public PageResult<ShopDeliveryElectronicSheetPageVo> pages(PageParam pageParam, ShopDeliveryElectronicSheetPagesParam pagesParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        LambdaQueryWrapper<ShopDeliveryElectronicSheet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShopDeliveryElectronicSheet::getSiteId, RequestUtils.siteId())
                .orderByDesc(ShopDeliveryElectronicSheet::getIsDefault)
                .orderByDesc(ShopDeliveryElectronicSheet::getId);

        if (pagesParam.getExpressCompanyId() != null && pagesParam.getExpressCompanyId() > 0) {
            queryWrapper.eq(ShopDeliveryElectronicSheet::getExpressCompanyId, pagesParam.getExpressCompanyId());
        }

        if (ObjectUtil.isNotEmpty(pagesParam.getTemplateName())) {
            queryWrapper.like(ShopDeliveryElectronicSheet::getTemplateName, pagesParam.getTemplateName());
        }

        IPage<ShopDeliveryElectronicSheet> iPage = shopDeliveryElectronicSheetMapper.selectPage(new Page<>(page, limit), queryWrapper);

        Set<Integer> expressCompanyIds = iPage.getRecords().stream().map(ShopDeliveryElectronicSheet::getExpressCompanyId).collect(Collectors.toSet());
        final Map<Integer, ShopDeliveryCompany> companyMap = new HashMap<>();
        if (ObjectUtil.isNotEmpty(expressCompanyIds)) {
            LambdaQueryWrapper<ShopDeliveryCompany> companyQueryWrapper = new LambdaQueryWrapper<>();
            companyQueryWrapper.eq(ShopDeliveryCompany::getSiteId, RequestUtils.siteId());
            companyQueryWrapper.in(ShopDeliveryCompany::getCompanyId, expressCompanyIds);
            companyMap.putAll(shopDeliveryCompanyMapper.selectList(companyQueryWrapper)
                    .stream().collect(Collectors.toMap(ShopDeliveryCompany::getCompanyId, e -> e)));
        }

        List<ShopDeliveryElectronicSheetPageVo> list = iPage.getRecords().stream().map(item -> {
            ShopDeliveryElectronicSheetPageVo vo = BeanUtil.toBean(item, ShopDeliveryElectronicSheetPageVo.class);
            ShopDeliveryCompany company = companyMap.getOrDefault(item.getExpressCompanyId(), new ShopDeliveryCompany());
            vo.setCompany(new ShopDeliveryElectronicSheetPageVo.Company(company.getCompanyName(), company.getExpressNoElectronicSheet()));
            vo.setPayTypeName(ElectronicSheetPayTypeEnum.getPayTypeByType(item.getPayType()));
            return vo;
        }).collect(Collectors.toList());
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 电子面单详情
     *
     * @param id 主键
     * @return ShopDeliveryElectronicSheetInfoVo
     */
    @Override
    public ShopDeliveryElectronicSheetInfoVo info(Integer id) {
        ShopDeliveryElectronicSheet model = shopDeliveryElectronicSheetMapper.selectOne(
                new QueryWrapper<ShopDeliveryElectronicSheet>()
                        .eq("id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        ShopDeliveryElectronicSheetInfoVo vo = new ShopDeliveryElectronicSheetInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 电子面单添加
     *
     * @param addParam 参数
     */
    @Override
    public void add(ShopDeliveryElectronicSheetParam addParam) {
        ShopDeliveryElectronicSheet model = new ShopDeliveryElectronicSheet();
        model.setSiteId(RequestUtils.siteId());
        model.setTemplateName(addParam.getTemplateName());
        model.setExpressCompanyId(addParam.getExpressCompanyId());
        model.setCustomerName(addParam.getCustomerName());
        model.setCustomerPwd(addParam.getCustomerPwd());
        model.setSendSite(addParam.getSendSite());
        model.setSendStaff(addParam.getSendStaff());
        model.setMonthCode(addParam.getMonthCode());
        model.setPayType(addParam.getPayType());
        model.setIsNotice(addParam.getIsNotice());
        model.setStatus(addParam.getStatus());
        model.setExpType(addParam.getExpType());
        model.setPrintStyle(addParam.getPrintStyle());
        model.setIsDefault(addParam.getIsDefault());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        shopDeliveryElectronicSheetMapper.insert(model);

        if (addParam.getIsDefault() == 1) {
            setDefault(model.getId());
        }
    }

    /**
     * 电子面单编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, ShopDeliveryElectronicSheetParam editParam) {
        ShopDeliveryElectronicSheet model = shopDeliveryElectronicSheetMapper.selectOne(
                new QueryWrapper<ShopDeliveryElectronicSheet>()
                        .eq("id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        model.setId(id);

        model.setSiteId(editParam.getSiteId());
        model.setTemplateName(editParam.getTemplateName());
        model.setExpressCompanyId(editParam.getExpressCompanyId());
        model.setCustomerName(editParam.getCustomerName());
        model.setCustomerPwd(editParam.getCustomerPwd());
        model.setSendSite(editParam.getSendSite());
        model.setSendStaff(editParam.getSendStaff());
        model.setMonthCode(editParam.getMonthCode());
        model.setPayType(editParam.getPayType());
        model.setIsNotice(editParam.getIsNotice());
        model.setStatus(editParam.getStatus());
        model.setExpType(editParam.getExpType());
        model.setPrintStyle(editParam.getPrintStyle());
        model.setIsDefault(editParam.getIsDefault());
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        shopDeliveryElectronicSheetMapper.updateById(model);
    }

    /**
     * 电子面单删除
     *
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        ShopDeliveryElectronicSheet model = shopDeliveryElectronicSheetMapper.selectOne(
                new QueryWrapper<ShopDeliveryElectronicSheet>()
                        .eq("id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        shopDeliveryElectronicSheetMapper.delete(new QueryWrapper<ShopDeliveryElectronicSheet>().eq("id", id));
    }

    @Override
    public Map<Integer, String> getPayType() {
        Map<Integer, String> map = new LinkedHashMap<>();
        for (int i = 0; i < ElectronicSheetPayTypeEnum.values().length; i++) {
            map.put(i + 1, ElectronicSheetPayTypeEnum.values()[i].getPayType());
        }
        return map;
    }

    @Override
    @Transactional
    public void setDefault(Integer id) {
        LambdaQueryWrapper<ShopDeliveryElectronicSheet> selectOneQueryWrapper = new LambdaQueryWrapper<>();
        selectOneQueryWrapper.eq(ShopDeliveryElectronicSheet::getId, id);
        selectOneQueryWrapper.eq(ShopDeliveryElectronicSheet::getSiteId, RequestUtils.siteId());

        ShopDeliveryElectronicSheet shopDeliveryElectronicSheet;
        if (id == null || (shopDeliveryElectronicSheet = shopDeliveryElectronicSheetMapper.selectOne(selectOneQueryWrapper)) == null) {
            return;
        }

        LambdaQueryWrapper<ShopDeliveryElectronicSheet> resetQueryWrapper = new LambdaQueryWrapper<>();
        resetQueryWrapper.eq(ShopDeliveryElectronicSheet::getSiteId, RequestUtils.siteId());

        ShopDeliveryElectronicSheet resetElectronicSheet = new ShopDeliveryElectronicSheet();
        resetElectronicSheet.setIsDefault(0);
        resetElectronicSheet.setUpdateTime(System.currentTimeMillis() / 1000);

        shopDeliveryElectronicSheetMapper.update(resetElectronicSheet, resetQueryWrapper);

        shopDeliveryElectronicSheet.setIsDefault(1);
        shopDeliveryElectronicSheet.setUpdateTime(System.currentTimeMillis() / 1000);
        shopDeliveryElectronicSheetMapper.updateById(shopDeliveryElectronicSheet);
    }

    @Override
    public ElectronicSheetConfigValueVo getElectronicSheetConfig() {
        CoreSysConfigVo config = coreConfigService.getConfig(RequestUtils.siteId(), ShopDeliveryElectronicSheetConst.ELECTRONIC_SHEET_CONFIG_KEY);
        return ObjectUtil.hasEmpty(config, config.getValue()) ? ElectronicSheetConfigValueVo.newInstance() :
                JSON.parseObject(config.getValue(), ElectronicSheetConfigValueVo.class);
    }

    @Override
    public void setElectronicSheetConfig(Object config) {
        coreConfigService.setConfig(RequestUtils.siteId(), ShopDeliveryElectronicSheetConst.ELECTRONIC_SHEET_CONFIG_KEY, JSONUtil.parseObj(config));
    }

    @Override
    public List<ElectronicSheetPrintVo> printElectronicSheet(ShopDeliveryPrintElectronicSheetParam param) {
        param.setSiteId(RequestUtils.siteId());
        return coreElectronicSheetService.printElectronicSheet(param);
    }

}
