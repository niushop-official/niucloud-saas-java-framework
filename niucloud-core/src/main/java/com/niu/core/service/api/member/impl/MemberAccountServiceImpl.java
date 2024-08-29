package com.niu.core.service.api.member.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.BaseException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.member.MemberAccountLog;
import com.niu.core.mapper.member.MemberAccountLogMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.api.member.IMemberAccountService;
import com.niu.core.service.api.member.param.*;
import com.niu.core.service.api.member.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class MemberAccountServiceImpl implements IMemberAccountService {

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private MemberAccountLogMapper memberAccountLogMapper;

    @Override
    public PageResult<?> pointFlow(PageParam pageParam, AccountPointFlowParam param) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<MemberAccountLog> queryWrapper = new QueryWrapper();
        queryWrapper.select("id, member_id, site_id, account_type, account_data, from_type, related_id, create_time, memo");
        queryWrapper.eq("site_id", param.siteId());
        queryWrapper.eq("member_id", param.memberId());
        if ("income".equals(param.getAccountType())) {
            queryWrapper.gt("account_data", 0);
        } else if ("disburse".equals(param.getAccountType())) {
            queryWrapper.lt("account_data", 0);
        }
        if (StrUtil.isNotEmpty(param.getAccountType())) {
            queryWrapper.eq("account_type", param.getAccountType());
        }
        if (StrUtil.isNotEmpty(param.getFromType())) {
            queryWrapper.eq("from_type", param.getFromType());
        }
        if (ObjectUtil.isNotEmpty(param.getCreateTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "create_time", param.getCreateTime());
        }
        if (StrUtil.isNotEmpty(param.getAccountDataGt())) {
            queryWrapper.gt("account_data", param.getAccountDataGt());
        }
        if (StrUtil.isNotEmpty(param.getAccountDataLt())) {
            queryWrapper.lt("account_data", param.getAccountDataLt());
        }
        queryWrapper.orderByDesc(Arrays.asList("create_time"));
        IPage<MemberAccountLog> iPage = this.memberAccountLogMapper.selectPage(new Page<>(page, limit), queryWrapper);

        List<AccountPointFlowVo> dataList = new LinkedList<>();
        if (iPage.getRecords().size() > 0) {
            Map<String, List<AccountPointFlowVo.Record>> month = new HashMap<String, List<AccountPointFlowVo.Record>>();

            for (MemberAccountLog item : iPage.getRecords()) {
                String time = DateUtil.format(new Date(item.getCreateTime() * 1000), "yyyy-MM");

                AccountPointFlowVo.Record record = new AccountPointFlowVo.Record();
                BeanUtil.copyProperties(item, record);

                if (month.get(time) == null) {
                    List<AccountPointFlowVo.Record> records = new LinkedList<>();
                    records.add(record);
                    month.put(time, records);
                } else {
                    month.get(time).add(record);
                }
            }

            if (month.size() > 0) {
                for (String key: month.keySet()) {
                    AccountPointFlowVo vo = new AccountPointFlowVo();

                    AccountPointFlowVo.Month monthInfo = new AccountPointFlowVo.Month();
                    monthInfo.setYear(key.split("-")[0]);
                    monthInfo.setMonth(key.split("-")[1]);

                    vo.setMonthInfo(monthInfo);
                    vo.setMonthData(month.get(key));

                    dataList.add(vo);
                }
            }
        }
        return PageResult.build(page, limit).setData(dataList);
    }

    /**
     * 余额流水
     *
     * @param pageParam
     * @param param
     * @return
     */
    @Override
    public PageResult<?> balanceFlow(PageParam pageParam, AccountBalanceFlowParam param) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<MemberAccountLog> queryWrapper = new QueryWrapper();
        queryWrapper.select("id, member_id, site_id, account_type, account_data, from_type, related_id, create_time, memo");
        queryWrapper.eq("site_id", param.siteId());
        queryWrapper.eq("member_id", param.memberId());
        if (StrUtil.isNotEmpty(param.getAccountType())) {
            queryWrapper.eq("account_type", param.getAccountType());
        }
        if (StrUtil.isNotEmpty(param.getFromType())) {
            queryWrapper.eq("from_type", param.getFromType());
        }
        if (ObjectUtil.isNotEmpty(param.getCreateTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "create_time", param.getCreateTime());
        }
        if (StrUtil.isNotEmpty(param.getAccountDataGt())) {
            queryWrapper.gt("account_data", param.getAccountDataGt());
        }
        if (StrUtil.isNotEmpty(param.getAccountDataLt())) {
            queryWrapper.lt("account_data", param.getAccountDataLt());
        }
        if (StrUtil.isNotEmpty(param.getKeyword())) {
            queryWrapper.eq("keyword", param.getKeyword());
        }
        queryWrapper.orderByDesc(Arrays.asList("create_time"));
        IPage<MemberAccountLog> iPage = this.memberAccountLogMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<AccountBalanceFlowVo> dataList = CollectUtils.convert(iPage.getRecords(), AccountBalanceFlowVo.class);
        return PageResult.build(page, limit).setData(dataList);
    }

    /**
     * 余额流水(新)
     *
     * @param pageParam
     * @param param
     * @return
     */
    @Override
    public PageResult<?> balanceList(PageParam pageParam, AccountBalanceListParam param) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        QueryWrapper<MemberAccountLog> queryWrapper = new QueryWrapper();
        queryWrapper.select("id, member_id, site_id, account_type, account_data, from_type, related_id, create_time, memo");
        queryWrapper.eq("site_id", param.siteId());
        queryWrapper.eq("member_id", param.memberId());
        if ("income".equals(param.getTradeType())) {
            queryWrapper.in("account_type", "balance", "money");
            queryWrapper.gt("account_data", 0);
            queryWrapper.ne("from_type", "cash_out");
        } else if ("disburse".equals(param.getTradeType())) {
            queryWrapper.in("account_type", "balance", "money");
            queryWrapper.lt("account_data", 0);
            queryWrapper.ne("from_type", "cash_out");
        } else if ("cash_out".equals(param.getTradeType())) {
            queryWrapper.eq("account_type", "money");
            queryWrapper.eq("from_type", "cash_out");
        } else {
            queryWrapper.in("account_type", "balance", "money");
        }
        queryWrapper.orderByDesc(Arrays.asList("create_time"));
        IPage<MemberAccountLog> iPage = this.memberAccountLogMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<AccountBalanceListVo> dataList = CollectUtils.convert(iPage.getRecords(), AccountBalanceListVo.class);
        return PageResult.build(page, limit).setData(dataList);
    }

    /**
     * 零钱流水
     *
     * @param pageParam
     * @param param
     * @return
     */
    @Override
    public PageResult<?> moneyFlow(PageParam pageParam, AccountMoneyFlowParam param) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        QueryWrapper<MemberAccountLog> queryWrapper = new QueryWrapper();
        queryWrapper.select("id, member_id, site_id, account_type, account_data, from_type, related_id, create_time, memo");
        queryWrapper.eq("site_id", param.siteId());
        queryWrapper.eq("member_id", param.memberId());
        if (StrUtil.isNotEmpty(param.getAccountType())) {
            queryWrapper.eq("account_type", param.getAccountType());
        }
        if (StrUtil.isNotEmpty(param.getFromType())) {
            queryWrapper.eq("from_type", param.getFromType());
        }
        if (ObjectUtil.isNotEmpty(param.getCreateTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "create_time", param.getCreateTime());
        }
        if (StrUtil.isNotEmpty(param.getAccountDataGt())) {
            queryWrapper.gt("account_data", param.getAccountDataGt());
        }
        if (StrUtil.isNotEmpty(param.getAccountDataLt())) {
            queryWrapper.lt("account_data", param.getAccountDataLt());
        }
        if (StrUtil.isNotEmpty(param.getKeyword())) {
            queryWrapper.eq("keyword", param.getKeyword());
        }
        queryWrapper.orderByDesc(Arrays.asList("create_time"));
        IPage<MemberAccountLog> iPage = this.memberAccountLogMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<AccountMoneyFlowVo> dataList = CollectUtils.convert(iPage.getRecords(), AccountMoneyFlowVo.class);
        return PageResult.build(page, limit).setData(dataList);
    }

    @Override
    public Long accountRecords(AccountRecordsParam param) {
        QueryWrapper<MemberAccountLog> queryWrapper = new QueryWrapper();
        queryWrapper.select("id");
        queryWrapper.eq("member_id", param.memberId());
        if (StrUtil.isNotEmpty(param.getAccountType())) {
            queryWrapper.eq("account_type", param.getAccountType());
        }
        if (StrUtil.isNotEmpty(param.getFromType())) {
            queryWrapper.eq("from_type", param.getFromType());
        }
        if (ObjectUtil.isNotEmpty(param.getCreateTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "create_time", param.getCreateTime());
        }
        Long count = this.memberAccountLogMapper.selectCount(queryWrapper);
        return count;
    }

    @Override
    public PageResult<?> commissionFlow(PageParam pageParam, AccountCommissionFlowParam param) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<MemberAccountLog> queryWrapper = new QueryWrapper();
        queryWrapper.select("id, member_id, site_ida, account_type, account_data, from_type, related_id, create_time, memo");
        queryWrapper.eq("member_id", param.memberId());
        queryWrapper.eq("site_id", param.siteId());
        if (StrUtil.isNotEmpty(param.getAccountType())) {
            queryWrapper.eq("account_type", param.getAccountType());
        }
        if (StrUtil.isNotEmpty(param.getFromType())) {
            queryWrapper.eq("from_type", param.getFromType());
        }
        if (ObjectUtil.isNotEmpty(param.getCreateTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "create_time", param.getCreateTime());
        }
        if (StrUtil.isNotEmpty(param.getAccountDataGt())) {
            queryWrapper.gt("account_data", param.getAccountDataGt());
        }
        if (StrUtil.isNotEmpty(param.getAccountDataLt())) {
            queryWrapper.lt("account_data", param.getAccountDataLt());
        }
        if (StrUtil.isNotEmpty(param.getKeyword())) {
            queryWrapper.eq("keyword", param.getKeyword());
        }
        queryWrapper.orderByDesc(Arrays.asList("create_time"));
        IPage<MemberAccountLog> iPage = this.memberAccountLogMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<AccountCommissionFlowVo> dataList = CollectUtils.convert(iPage.getRecords(), AccountCommissionFlowVo.class);
        return PageResult.build(page, limit).setData(dataList);
    }

    @Override
    public JSONObject getAccountSource(AccountAccountSourceParam param) {
        JSONObject jsonObject = JsonModuleLoader.build().mergeResultElement("account_change_type.json");
        JSONObject accountTypeObject = jsonObject.getJSONObject(param.getAccountType());
        if (accountTypeObject == null) {
            throw new BaseException("MEMBER_TYPE_NOT_EXIST");
        }
        return accountTypeObject;
    }

    @Override
    public AccountPointCountVo pointCount(AccountPointCountParam param) {
        AccountPointCountVo accountPointCountVo = new AccountPointCountVo();
        QueryWrapper<Member> queryWrapper = new QueryWrapper();
        queryWrapper.select("point,point_get");
        queryWrapper.eq("site_id", param.siteId());
        queryWrapper.eq("member_id", param.memberId());
        Member member = this.memberMapper.selectOne(queryWrapper);
        if (member == null) {
            throw new BaseException("会员不存在");
        }
        if (member.getPoint() != null) {
            accountPointCountVo.setPoint(member.getPoint());
        }
        if (member.getPointGet() != null) {
            accountPointCountVo.setPointGet(member.getPointGet());
        }
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("site_id", param.siteId());
        paramsMap.put("member_id", param.memberId());
        paramsMap.put("account_type", "point");
        paramsMap.put("account_data", 0);
        Double use = memberAccountLogMapper.sumAccountData(paramsMap);
        if (use != null) {
            accountPointCountVo.setUse(Math.abs(use));
        }
        return accountPointCountVo;
    }

}
