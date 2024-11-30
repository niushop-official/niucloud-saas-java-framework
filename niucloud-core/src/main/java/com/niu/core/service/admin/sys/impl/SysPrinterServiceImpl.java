package com.niu.core.service.admin.sys.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.sys.SysPrinter;
import com.niu.core.enums.sys.SysPrinterBrandEnum;
import com.niu.core.enums.sys.SysPrinterTypeEnum;
import com.niu.core.mapper.sys.SysPrinterMapper;
import com.niu.core.service.admin.sys.ISysPrinterService;
import com.niu.core.service.admin.sys.param.SysPrinterModifyStatusParam;
import com.niu.core.service.admin.sys.param.SysPrinterParam;
import com.niu.core.service.admin.sys.param.SysPrinterSearchParam;
import com.niu.core.service.admin.sys.vo.SysPrinterInfoVo;
import com.niu.core.service.admin.sys.vo.SysPrinterListVo;
import com.niu.core.service.core.sys.ICorePrinterService;
import com.niu.core.service.core.sys.param.SysPrinterAddPrinterYlyParam;
import com.niu.core.service.core.sys.param.SysPrinterPrintTicketParam;
import com.niu.core.service.core.sys.vo.SysPrinterPrintTicketVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


/**
 * 小票打印机实现
 */
@Service
@RequiredArgsConstructor
public class SysPrinterServiceImpl implements ISysPrinterService {
    private final SysPrinterMapper sysPrinterMapper;
    private final ICorePrinterService corePrinterService;

    @Override
    public PageResult<SysPrinterListVo> page(PageParam pageParam, SysPrinterSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<SysPrinter> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("site_id", RequestUtils.siteId())
                .orderByDesc("create_time");

        if (ObjectUtil.isNotEmpty(searchParam.getPrinterName())) {
            queryWrapper.like("printer_name", searchParam.getPrinterName());
        }

        IPage<SysPrinter> iPage = sysPrinterMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<SysPrinterListVo> list = new LinkedList<>();
        for (SysPrinter item : iPage.getRecords()) {
            SysPrinterListVo vo = new SysPrinterListVo();
            BeanUtils.copyProperties(item, vo);
            vo.setBrandName(SysPrinterBrandEnum.getNameByBrand(item.getBrand()));
            vo.setTrigger(JSONArray.parseArray(item.getTrigger(), String.class));
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public List<SysPrinterListVo> getList(SysPrinterSearchParam param) {
        QueryWrapper<SysPrinter> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("site_id", RequestUtils.siteId())
                .orderByDesc("create_time")
                .select("printer_id, apikey, brand, create_time, open_id, print_width, printer_code, printer_key, printer_name, site_id, status, template_type, 'trigger',  update_time, value");

        if (ObjectUtil.isNotEmpty(param.getPrinterName())) {
            queryWrapper.like("printer_name", param.getPrinterName());
        }

        List<SysPrinterListVo> list = new LinkedList<>();
        for (SysPrinter item : sysPrinterMapper.selectList(queryWrapper)) {
            SysPrinterListVo vo = new SysPrinterListVo();
            BeanUtils.copyProperties(item, vo);
            vo.setBrandName(SysPrinterBrandEnum.getNameByBrand(item.getBrand()));
            vo.setValue(JSON.parseObject(item.getValue()));
            vo.setTrigger(JSONArray.parseArray(item.getTrigger(), String.class));
            list.add(vo);
        }

        return list;
    }

    @Override
    public SysPrinterInfoVo info(Integer id) {
        SysPrinter model = sysPrinterMapper.selectOne(
                new QueryWrapper<SysPrinter>()
                        .eq("printer_id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "小票打印机不存在");

        SysPrinterInfoVo vo = new SysPrinterInfoVo();
        BeanUtils.copyProperties(model, vo);
        vo.setValue(JSON.parseObject(model.getValue()));
        return vo;
    }

    @Override
    @Transactional
    public void add(SysPrinterParam addParam) {
        SysPrinter model = new SysPrinter();
        BeanUtils.copyProperties(addParam, model);
        model.setSiteId(RequestUtils.siteId());
        model.setValue(JSON.toJSONString(addParam.getValue()));
        model.setTemplateType(addParam.getTemplateType().get(0));
        model.setTrigger(JSON.toJSONString(addParam.getTrigger()));
        model.setCreateTime(System.currentTimeMillis() / 1000);
        sysPrinterMapper.insert(model);

        if (addParam.getBrand().equals(SysPrinterBrandEnum.YI_LIAN_YUN.getBrand())) {
            SysPrinterAddPrinterYlyParam addPrinterYlyParam = new SysPrinterAddPrinterYlyParam();
            BeanUtils.copyProperties(addParam, addPrinterYlyParam);
            corePrinterService.addPrinterYly(addPrinterYlyParam);
        }
    }

    @Override
    @Transactional
    public void edit(Integer id, SysPrinterParam editParam) {
        SysPrinter model = sysPrinterMapper.selectOne(
                new QueryWrapper<SysPrinter>()
                        .eq("printer_id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "小票打印机不存在");
        BeanUtils.copyProperties(editParam, model);
        model.setValue(JSON.toJSONString(editParam.getValue()));
        model.setTemplateType(editParam.getTemplateType().get(0));
        model.setTrigger(JSON.toJSONString(editParam.getTrigger()));
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        sysPrinterMapper.updateById(model);

        if (editParam.getBrand().equals(SysPrinterBrandEnum.YI_LIAN_YUN.getBrand())) {
            SysPrinterAddPrinterYlyParam addPrinterYlyParam = new SysPrinterAddPrinterYlyParam();
            BeanUtils.copyProperties(editParam, addPrinterYlyParam);
            corePrinterService.addPrinterYly(addPrinterYlyParam);
        }

    }

    @Override
    public void modifyStatus(SysPrinterModifyStatusParam param) {
        SysPrinter model = sysPrinterMapper.selectOne(
                new QueryWrapper<SysPrinter>()
                        .eq("printer_id", param.getPrinterId())
                        .eq("site_id", RequestUtils.siteId()));
        model.setStatus(param.getStatus());
        sysPrinterMapper.updateById(model);
    }

    @Override
    public void del(Integer id) {
        SysPrinter model = sysPrinterMapper.selectOne(
                new QueryWrapper<SysPrinter>()
                        .eq("printer_id", id)
                        .eq("site_id", RequestUtils.siteId()));

        if (ObjectUtil.isEmpty(model)) {
            return;
        }

        sysPrinterMapper.deleteById(id);

        if (model.getBrand().equals(SysPrinterBrandEnum.YI_LIAN_YUN.getBrand())) {
            corePrinterService.deletePrinterYly(model);
        }
    }

    @Override
    public cn.hutool.json.JSON getType() {
        return SysPrinterTypeEnum.getType();
    }

    @Override
    public JSONObject getBrand() {
        JSONObject jsonObject = new JSONObject();
        for (SysPrinterBrandEnum value : SysPrinterBrandEnum.values()) {
            if (ObjectUtil.isEmpty(value.getBrand())) {
                continue;
            }

            jsonObject.put(value.getBrand(), value.getName());
        }
        return jsonObject;
    }

    @Override
    public String refreshToken(Integer id) {
        SysPrinter model = sysPrinterMapper.selectOne(
                new QueryWrapper<SysPrinter>()
                        .eq("printer_id", id)
                        .eq("site_id", RequestUtils.siteId()));

        if (ObjectUtil.isEmpty(model)) {
            throw new RuntimeException("打印机不存在");
        }

        return corePrinterService.refreshToken(model);
    }

    @Override
    public void testPrint(Integer id) {
        SysPrinter model = sysPrinterMapper.selectOne(
                new QueryWrapper<SysPrinter>()
                        .eq("printer_id", id)
                        .eq("site_id", RequestUtils.siteId()));

        if (ObjectUtil.isEmpty(model)) {
            throw new RuntimeException("打印机不存在");
        }

        if (model.getBrand().equals(SysPrinterBrandEnum.YI_LIAN_YUN.getBrand())) {
            testYlyPrint(model);
        }
    }

    @Override
    public SysPrinterPrintTicketVo printTicket(SysPrinterPrintTicketParam param) {
        param.setSiteId(RequestUtils.siteId());
        return corePrinterService.printTicket(param);
    }

    private void testYlyPrint(SysPrinter printer) {
        Random random = new Random();
        String originId = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + (random.nextInt(999) + 1); // 内部订单号(32位以内)
        String content = "<MN>" + 1 + "</MN>" +
                "<center>小票名称</center>" +
                repeat('.', 32) +
                "<FH2><FS><center>商城名称</center></FS></FH2>" +
                repeat('.', 32) +
                "订单时间:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "\n" +
                "订单编号:" + originId + "\n" +
                repeat('.', 32) +
                "<table>" +
                "<tr><td>商品名称</td><td></td><td>数量</td><td>金额</td></tr>" +
                "</table>" +
                repeat('.', 32) +
                "<table>" +
                "<tr><td>烤土豆(超级辣)</td><td></td><td>x3</td><td>5</td></tr>" +
                "<tr><td>烤豆干(超级辣)</td><td></td><td>x2</td><td>10</td></tr>" +
                "<tr><td>烤鸡翅(超级辣)</td><td></td><td>x3</td><td>15</td></tr>" +
                "</table>" +
                repeat('.', 32) +
                "商品总额：￥30 " + "\n" +
                "订单共8件商品，总计: ￥30 " + "\n" +
                repeat('.', 32) +
                "<FH2>买家留言：微辣，多放孜然</FH2>\n" +
                repeat('.', 32) +
                "<center>谢谢惠顾，欢迎下次光临</center>";

        corePrinterService.printIndex(printer, content, originId);
    }

    private String repeat(char ch, int count) {
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            sb.append(ch);
        }
        return sb.toString();
    }

}
