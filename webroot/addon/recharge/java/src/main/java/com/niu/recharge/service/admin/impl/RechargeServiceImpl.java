package com.niu.recharge.service.admin.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.recharge.entity.Recharge;
import com.niu.recharge.enums.PackageGiftEnum;
import com.niu.recharge.event.RechargeGiftContentEvent;
import com.niu.recharge.mapper.RechargeMapper;
import com.niu.recharge.service.admin.IRechargeService;
import com.niu.recharge.service.admin.param.RechargeEditSortParam;
import com.niu.recharge.service.admin.param.RechargeModifyStatusParam;
import com.niu.recharge.service.admin.param.RechargePageParam;
import com.niu.recharge.service.admin.param.RechargeParam;
import com.niu.recharge.service.admin.vo.RechargeInitVo;
import com.niu.recharge.service.admin.vo.RechargeListVo;
import com.niu.recharge.service.core.ICoreRechargeConfigService;
import com.niu.recharge.service.core.param.RechargeSetConfigParam;
import com.niu.recharge.service.core.vo.RechargeConfigVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RechargeServiceImpl implements IRechargeService {
    private final RechargeMapper rechargeMapper;
    private final ICoreRechargeConfigService coreRechargeConfigService;

    @Override
    public void setRechargeConfig(RechargeSetConfigParam param) {
        coreRechargeConfigService.setRechargeConfig(RequestUtils.siteId(), param);
    }

    @Override
    public RechargeConfigVo getRechargeConfig() {
        return coreRechargeConfigService.getRechargeConfig(RequestUtils.siteId());
    }

    @Override
    public PageResult<RechargeListVo> getPage(PageParam pageParam, RechargePageParam param) {
        QueryWrapper<Recharge> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId())
                .orderByDesc("create_time");

        if (ObjectUtil.isNotEmpty(param.getRechargeName())) {
            queryWrapper.like("recharge_name", param.getRechargeName());
        }

        if (ObjectUtil.isNotEmpty(param.getCreateTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "create_time", param.getCreateTime());
        }

        Page<Recharge> iPage = rechargeMapper.selectPage(new Page<>(pageParam.getPage(), pageParam.getLimit()), queryWrapper);
        if (ObjectUtil.isEmpty(iPage.getRecords())) {
            return PageResult.build(iPage, Collections.emptyList());
        }

        IPage<RechargeListVo> voPage = iPage.convert(recharge -> {
            RechargeListVo vo = new RechargeListVo();
            BeanUtils.copyProperties(recharge, vo);
            vo.setCreateTime(DateUtils.timestampToString(recharge.getCreateTime()));

            JSONObject gift = JSONUtil.parseObj(recharge.getGiftJson());
            for (Map.Entry<String, Object> entry : gift.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                RechargeGiftContentEvent event = new RechargeGiftContentEvent();
                event.setKey(key);
                event.setValue(JSONUtil.parseObj(value).get("value"));
                vo.setGiftContent(EventAndSubscribeOfPublisher.publishAndCallback(event));
            }

            return vo;
        });

        return PageResult.build(voPage);
    }

    @Override
    public void add(RechargeParam param) {
        QueryWrapper<Recharge> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId())
                .eq("face_value", param.getFaceValue());
        long count = rechargeMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new RuntimeException("充值金额不能重复");
        }

        param.setPoint(0);
        param.setGrowth(0);
        int growth = param.getPoint();
        int point = param.getPoint();
        JSONObject giftJson = JSONUtil.parseObj(param.getGiftJson());
        if (ObjectUtil.isNotEmpty(giftJson)) {
            if (giftJson.containsKey("growth")) {
                Integer value = giftJson.getJSONObject("growth").getInt("value");
                if (ObjectUtil.isNotEmpty(value)) {
                    growth = value;
                    giftJson.remove("growth");
                }
            }

            if (giftJson.containsKey("point")) {
                Integer value = giftJson.getJSONObject("point").getInt("value");
                if (ObjectUtil.isNotEmpty(point)) {
                    point = value;
                    giftJson.remove("point");
                }
            }
        }

        Recharge recharge = new Recharge();
        BeanUtils.copyProperties(param, recharge);
        recharge.setSiteId(RequestUtils.siteId());
        recharge.setPoint(point);
        recharge.setGrowth(growth);
        recharge.setCreateTime(System.currentTimeMillis() / 1000);
        recharge.setGiftJson(giftJson.toString());

        rechargeMapper.insert(recharge);
    }

    @Override
    public void edit(Integer id, RechargeParam param) {
        Recharge recharge = rechargeMapper.selectById(id);
        if (ObjectUtil.isEmpty(recharge)) {
            throw new RuntimeException("充值套餐不存在");
        }

        QueryWrapper<Recharge> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId())
                .ne("recharge_id", id)
                .eq("face_value", param.getFaceValue());
        Long count = rechargeMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new RuntimeException("充值金额不能重复");
        }

        param.setPoint(0);
        param.setGrowth(0);
        int growth = param.getPoint();
        JSONObject giftJson = JSONUtil.parseObj(param.getGiftJson());
        Integer value = giftJson.getJSONObject("growth").getInt("value");
        if (ObjectUtil.isNotEmpty(value)) {
            growth = value;
            giftJson.remove("growth");
        }

        int point = param.getPoint();
        value = giftJson.getJSONObject("point").getInt("value");
        if (ObjectUtil.isNotEmpty(point)) {
            point = value;
            giftJson.remove("point");
        }

        BeanUtils.copyProperties(param, recharge);
        recharge.setGrowth(growth);
        recharge.setPoint(point);
        recharge.setGiftJson(giftJson.toString());
        recharge.setUpdateTime(System.currentTimeMillis() / 1000);
        rechargeMapper.updateById(recharge);
    }

    @Override
    public RechargeInitVo getInit(Integer rechargeId) {
        if (rechargeId == null) {
            return new RechargeInitVo();
        }

        Recharge recharge = rechargeMapper.selectById(rechargeId);
        RechargeInitVo vo = new RechargeInitVo();
        BeanUtils.copyProperties(recharge, vo);
        vo.setCreateTime(DateUtils.timestampToString(recharge.getCreateTime()));
        vo.setGiftJson(JSONUtil.parseObj(recharge.getGiftJson()));
        vo.setStatusName(ObjectUtil.equal(recharge.getStatus(), 1) ? "开启" : "关闭");
        if (ObjectUtil.isNotEmpty(vo)) {
            if (ObjectUtil.isNotEmpty(vo.getPoint())) {
                vo.getGiftJson().set("point", Map.of("value", vo.getPoint()));
            }
            if (ObjectUtil.isNotEmpty(vo.getGrowth())) {
                vo.getGiftJson().set("growth", Map.of("value", vo.getGrowth()));
            }
        }

        return vo;
    }

    @Override
    public void del(Integer id) {
        rechargeMapper.deleteById(id);
    }

    @Override
    public void modifyStatus(RechargeModifyStatusParam param) {
        Recharge recharge = rechargeMapper.selectById(param.getRechargeId());
        if (ObjectUtil.isEmpty(recharge)) {
            throw new RuntimeException("充值套餐不存在");
        }

        recharge.setStatus(param.getStatus());
        rechargeMapper.updateById(recharge);
    }

    @Override
    public void editSort(RechargeEditSortParam param) {
        Recharge recharge = rechargeMapper.selectById(param.getRechargeId());
        if (ObjectUtil.isEmpty(recharge)) {
            throw new RuntimeException("充值套餐不存在");
        }
        recharge.setSort(param.getSort());
        rechargeMapper.updateById(recharge);
    }

    @Override
    public JSONArray getPackageGiftDict() {
        return PackageGiftEnum.getPackageGiftEnum();
    }

}
