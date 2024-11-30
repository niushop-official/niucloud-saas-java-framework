package com.niu.core.service.admin.sys.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.sys.SysPrinter;
import com.niu.core.entity.sys.SysPrinterTemplate;
import com.niu.core.enums.sys.SysPrinterTypeEnum;
import com.niu.core.mapper.sys.SysPrinterMapper;
import com.niu.core.mapper.sys.SysPrinterTemplateMapper;
import com.niu.core.service.admin.sys.ISysPrinterTemplateService;
import com.niu.core.service.admin.sys.param.SysPrinterTemplateParam;
import com.niu.core.service.admin.sys.param.SysPrinterTemplateSearchParam;
import com.niu.core.service.admin.sys.vo.SysPrinterTemplateInfoVo;
import com.niu.core.service.admin.sys.vo.SysPrinterTemplateListVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


/**
 * 小票打印模板实现
 */
@Service
@RequiredArgsConstructor
public class SysPrinterTemplateServiceImpl implements ISysPrinterTemplateService {
    private final SysPrinterMapper sysPrinterMapper;
    private final SysPrinterTemplateMapper sysPrinterTemplateMapper;

    @Override
    public PageResult<SysPrinterTemplateListVo> page(PageParam pageParam, SysPrinterTemplateSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<SysPrinterTemplate> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("site_id", RequestUtils.siteId())
                .orderByDesc("create_time");

        if (ObjectUtil.isNotEmpty(searchParam.getTemplateId())) {
            queryWrapper.eq("template_id", searchParam.getTemplateId());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getTemplateName())) {
            queryWrapper.like("template_name", searchParam.getTemplateName());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getTemplateType())) {
            queryWrapper.eq("template_type", searchParam.getTemplateType());
        }

        IPage<SysPrinterTemplate> iPage = sysPrinterTemplateMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<SysPrinterTemplateListVo> list = new LinkedList<>();
        for (SysPrinterTemplate item : iPage.getRecords()) {
            SysPrinterTemplateListVo vo = new SysPrinterTemplateListVo();
            BeanUtils.copyProperties(item, vo);
            vo.setTemplateTypeName(SysPrinterTypeEnum.getTypeName(item.getTemplateType()));
            vo.setValue(JSON.parseObject(item.getValue()));
            vo.setCreateTime(DateUtils.timestampToString(item.getCreateTime()));
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public List<SysPrinterTemplateListVo> getList(SysPrinterTemplateSearchParam searchParam) {
        QueryWrapper<SysPrinterTemplate> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("site_id", RequestUtils.siteId())
                .orderByDesc("create_time");

        if (ObjectUtil.isNotEmpty(searchParam.getTemplateId())) {
            queryWrapper.eq("template_id", searchParam.getTemplateId());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getTemplateName())) {
            queryWrapper.like("template_name", searchParam.getTemplateName());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getTemplateType())) {
            queryWrapper.eq("template_type", searchParam.getTemplateType());
        }

        List<SysPrinterTemplateListVo> voList = new LinkedList<>();
        for (SysPrinterTemplate item : sysPrinterTemplateMapper.selectList(queryWrapper)) {
            SysPrinterTemplateListVo vo = new SysPrinterTemplateListVo();
            BeanUtils.copyProperties(item, vo);
            vo.setTemplateTypeName(SysPrinterTypeEnum.getTypeName(item.getTemplateType()));
            vo.setValue(JSON.parseObject(item.getValue()));
            vo.setCreateTime(DateUtils.timestampToString(item.getCreateTime()));
            voList.add(vo);
        }
        return voList;
    }

    @Override
    public SysPrinterTemplateInfoVo info(Integer id) {
        SysPrinterTemplate model = sysPrinterTemplateMapper.selectOne(
                new QueryWrapper<SysPrinterTemplate>()
                        .eq("template_id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "小票打印模板不存在");

        SysPrinterTemplateInfoVo vo = new SysPrinterTemplateInfoVo();
        BeanUtils.copyProperties(model, vo);
        vo.setValue(JSON.parseObject(model.getValue()));
        vo.setCreateTime(DateUtils.timestampToString(model.getCreateTime()));
        return vo;
    }

    @Override
    public void add(SysPrinterTemplateParam addParam) {
        SysPrinterTemplate model = new SysPrinterTemplate();
        BeanUtils.copyProperties(addParam, model);
        model.setValue(JSON.toJSONString(addParam.getValue()));
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setSiteId(RequestUtils.siteId());

        sysPrinterTemplateMapper.insert(model);
    }

    @Override
    public void edit(Integer id, SysPrinterTemplateParam editParam) {
        SysPrinterTemplate model = sysPrinterTemplateMapper.selectOne(
                new QueryWrapper<SysPrinterTemplate>()
                        .eq("template_id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "小票打印模板不存在");
        BeanUtils.copyProperties(editParam, model);
        sysPrinterTemplateMapper.updateById(model);
    }

    @Override
    public void del(Integer id) {
        SysPrinterTemplate template = sysPrinterTemplateMapper.selectById(id);

        LambdaQueryWrapper<SysPrinter> printerLambdaQueryWrapper = new LambdaQueryWrapper<>();
        printerLambdaQueryWrapper.eq(SysPrinter::getSiteId, RequestUtils.siteId())
                .like(SysPrinter::getTemplateType, template.getTemplateType());
        List<SysPrinter> printers = sysPrinterMapper.selectList(printerLambdaQueryWrapper);

        Optional<SysPrinter> illegalPrinter = printers.stream()
                .filter(printer -> ObjectUtil.isNotEmpty(printer.getValue()))
                .filter(printer -> JSON.parseArray(printer.getValue()).stream()
                        .anyMatch(value -> JSON.parseArray(value.toString()).stream()
                                .anyMatch(o -> id.equals(JSON.parseObject(o.toString()).getInteger("template_id")))))
                .findFirst();

        if (illegalPrinter.isPresent()) {
            throw new RuntimeException("该模板已被打印机[" + illegalPrinter.get().getPrinterName() + "]使用，无法删除");
        }

        sysPrinterTemplateMapper.deleteById(id);
    }
}
