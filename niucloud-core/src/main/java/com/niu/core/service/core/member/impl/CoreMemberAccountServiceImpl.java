package com.niu.core.service.core.member.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.task.ThreadPoolManager;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.member.MemberAccountLog;
import com.niu.core.enums.member.MemberAccountChangeTypeEnum;
import com.niu.core.event.member.MemberAccountEvent;
import com.niu.core.mapper.member.MemberAccountLogMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.member.ICoreMemberAccountService;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class CoreMemberAccountServiceImpl implements ICoreMemberAccountService {

    @Resource
    MemberAccountLogMapper memberAccountLogMapper;

    @Resource
    MemberMapper memberMapper;

    private static final ReentrantLock lock = new ReentrantLock();

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addLog(Integer siteId, Integer memberId, String accountType, Double data, String fromType, String memo, String relatedId) {
        lock.lock();

        try {
            QueryWrapper memberQuery = new QueryWrapper<Member>().eq("site_id", siteId).eq("member_id", memberId);
            Member member = memberMapper.selectOne(memberQuery);
            Assert.notNull(member, "会员不存在");

            BigDecimal accountData = new BigDecimal(data);
            BeanWrapper memberWrapper = new BeanWrapperImpl(member);
            BigDecimal originalData = new BigDecimal(Double.valueOf(memberWrapper.getPropertyValue(StringUtils.toCamelCase(accountType)).toString()));

            BigDecimal accountNewData = originalData.add(accountData);
            if (accountNewData.signum() < 0) throw new CommonException("账户余额不足");

            // 添加账户变更记录
            MemberAccountLog model = new MemberAccountLog();
            model.setSiteId(siteId);
            model.setMemberId(memberId);
            model.setAccountType(accountType);
            model.setAccountData(accountData);
            model.setAccountSum(accountNewData);
            model.setFromType(fromType);
            model.setMemo(memo);
            model.setRelatedId(relatedId);
            model.setCreateTime(System.currentTimeMillis() / 1000);
            memberAccountLogMapper.insert(model);

            // 修改会员账户
            Member memberModel = new Member();
            memberModel.setMemberId(memberId);
            BeanWrapper updateMemberWrapper = new BeanWrapperImpl(memberModel);
            updateMemberWrapper.setPropertyValue(StringUtils.toCamelCase(accountType), accountNewData);
            if (accountData.signum() > 0) {
                JSONObject changeTypeEnum = ObjectUtil.defaultIfNull(MemberAccountChangeTypeEnum.getType(accountType).getJSONObject(fromType), new JSONObject());
                Integer isChangeGet = changeTypeEnum.getInt("is_change_get", 1);
                if (isChangeGet == 1) {
                    BigDecimal originalGetData = new BigDecimal(Double.valueOf(memberWrapper.getPropertyValue(StringUtils.toCamelCase(accountType + "_get")).toString()));
                    updateMemberWrapper.setPropertyValue(StringUtils.toCamelCase(accountType + "_get"), originalGetData.add(accountData));
                }
            }
            memberMapper.updateById(memberModel);

            // 账户变更事件
            MemberAccountEvent accountEvent = new MemberAccountEvent();
            accountEvent.setSiteId(siteId);
            accountEvent.addAppSign("core");
            accountEvent.setName("MemberAccountEvent");
            accountEvent.setAccountType(accountType);
            accountEvent.setAccountData(accountData);
            accountEvent.setAccountNewData(accountNewData);
            accountEvent.setMemberId(memberId);
            EventAndSubscribeOfPublisher.publishAll(accountEvent);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        } finally {
            lock.unlock();
        }
    }
}
