package com.niu.core.service.api.member.impl;

import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.WechatUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.diy_form.DiyFormRecords;
import com.niu.core.entity.member.Member;
import com.niu.core.mapper.diy_form.DiyFormRecordsMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.admin.member.vo.MemberConfigVo;
import com.niu.core.service.api.login.IRegisterService;
import com.niu.core.service.api.member.IMemberLevelService;
import com.niu.core.service.api.member.IMemberService;
import com.niu.core.service.api.member.param.*;
import com.niu.core.service.api.member.vo.MemberCenterVo;
import com.niu.core.service.api.member.vo.MemberInfoVo;
import com.niu.core.service.api.member.vo.MemberQcodeVo;
import com.niu.core.service.core.member.ICoreMemberConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@Slf4j
public class MemberServiceImpl implements IMemberService {

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private IMemberLevelService memberLevelService;

    @Autowired
    private IRegisterService registerService;

    @Resource
    private ICoreMemberConfigService coreMemberConfigService;

    @Resource
    private DiyFormRecordsMapper diyFormRecordsMapper;

    @Override
    public MemberInfoVo info(MemberInfoParam param) {
        MPJQueryWrapper<Member> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("m")
            .leftJoin("?_member_level ml ON ml.level_id = m.member_level".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("member_id, m.site_id, username, member_no, mobile, register_channel, nickname, headimg, member_level, member_label, login_ip, login_type, login_time, m.create_time, last_visit_time, last_consum_time, sex, m.status, birthday, point, balance, m.growth, is_member, member_time, is_del, province_id, city_id, district_id, address, location, money, money_get, wx_openid, weapp_openid, commission, commission_get, commission_cash_outing, ml.level_name as member_level_name");
        queryWrapper.eq("member_id", param.memberId());
        // SAAS域控制（交给SQL引擎处理）
        queryWrapper.eq("m.site_id", param.siteId());
        MemberInfoVo memberInfoVo = memberMapper.selectJoinOne(MemberInfoVo.class, queryWrapper);
        if (ObjectUtil.isNotEmpty(memberInfoVo)){
            MemberConfigVo memberConfig = coreMemberConfigService.getMemberConfig(memberInfoVo.getSiteId());
            memberInfoVo.setFormId(memberConfig.getFormId() == null ? "" : memberConfig.getFormId().toString());
            memberInfoVo.setFormRecordId("");
            if (StringUtils.isNotEmpty(memberInfoVo.getFormId())){
                DiyFormRecords diyFormRecords = diyFormRecordsMapper.selectOne(new LambdaQueryWrapper<DiyFormRecords>()
                        .eq(DiyFormRecords::getFormId, memberConfig.getFormId())
                        .eq(DiyFormRecords::getMemberId, memberInfoVo.getMemberId())
                        .orderByDesc(DiyFormRecords::getCreateTime));
                if (ObjectUtil.isNotEmpty(diyFormRecords)){
                    memberInfoVo.setFormRecordId(diyFormRecords.getRecordId().toString());
                }
            }
        }
        return memberInfoVo;
    }

    @Override
    public MemberCenterVo center(MemberCenterParam param) {
        QueryWrapper<Member> queryWrapper = new QueryWrapper();
        queryWrapper.select("member_id, site_id, username, member_no, mobile, register_channel, nickname, headimg, member_level, member_label, login_ip, login_type, login_time, create_time, last_visit_time, last_consum_time, sex, status, birthday, point, balance, growth, is_member, member_time, is_del, province_id, city_id, district_id, address, location, money, money_get, commission, commission_get, commission_cash_outing");
        queryWrapper.eq("member_id", param.memberId());
        queryWrapper.eq("site_id", param.siteId());
        Member member = memberMapper.selectOne(queryWrapper);
        if (member == null) {
            return null;
        }

        MemberCenterVo memberCenterVo = new MemberCenterVo();
        BeanUtils.copyProperties(member, memberCenterVo);
        return memberCenterVo;
    }

    @Override
    public int modify(MemberModifyParam param) {
        Member oldMember = this.memberMapper.selectById(param.memberId());
        Assert.notNull(oldMember, "会员不存在");

        Member newMember = new Member();
        newMember.setMemberId(oldMember.getMemberId());

        switch (param.getField()) {
            case "nickname":
                newMember.setNickname(String.valueOf(param.getValue()));
                break;
            case "headimg":
                newMember.setHeadimg(String.valueOf(param.getValue()));
                break;
            case "member_label":
                newMember.setMemberLabel(String.valueOf(param.getValue()));
                break;
            case "member_level":
                newMember.setMemberLevel(Integer.parseInt(String.valueOf(param.getValue())));
                break;
            case "birthday":
                newMember.setBirthday(String.valueOf(param.getValue()));
                break;
            case "sex":
                newMember.setSex(Integer.parseInt(String.valueOf(param.getValue())));
                break;
            default:
                throw new CommonException("不允许修改的信息");
        }
        return this.memberMapper.updateById(newMember);
    }

    @Override
    public int edit(MemberEditParam param) {
        Member oldMember = this.memberMapper.selectById(param.memberId());
        Assert.notNull(oldMember, "会员不存在");

        Member newMember = new Member();
        newMember.setMemberId(oldMember.getMemberId());
        if (ObjectUtil.isNotEmpty(param.getData().getNickname())) newMember.setNickname(param.getData().getNickname());
        if (ObjectUtil.isNotEmpty(param.getData().getHeadimg())) newMember.setHeadimg(param.getData().getHeadimg());
        if (ObjectUtil.isNotEmpty(param.getData().getBirthday())) newMember.setBirthday(param.getData().getBirthday());
        if (ObjectUtil.isNotEmpty(param.getData().getSex())) newMember.setSex(param.getData().getSex());
        if (ObjectUtil.isNotEmpty(param.getData().getLastVisitTime())) newMember.setLastVisitTime(param.getData().getLastVisitTime());
        return this.memberMapper.updateById(newMember);
    }

    @Override
    public int mobile(MemberMobileParam param) {
        Member oldMember = this.memberMapper.selectById(param.memberId());
        Assert.notNull(oldMember, "会员不存在");

        if (ObjectUtil.isNotEmpty(param.getMobile())) {
            registerService.checkMobileCode(param.getMobile(), param.getMobileKey(), param.getMobileCode());
        } else if (ObjectUtil.isNotEmpty(param.getMobileCode())) {
            try {
                WxMaPhoneNumberInfo phoneInfo = WechatUtils.miniapp(RequestUtils.siteId()).getUserService().getPhoneNoInfo(param.getMobileCode());
                param.setMobile(phoneInfo.getPurePhoneNumber());
            } catch (Exception e) {
                throw new CommonException(e.getMessage());
            }
        } else {
            throw new CommonException("手机号不存在");
        }

        Member mobileExist = memberMapper.selectOne(new QueryWrapper<Member>().select("member_id").eq("mobile", param.getMobile()).eq("site_id", RequestUtils.siteId()));
        if (ObjectUtil.isNotNull(mobileExist)) throw new CommonException("当前手机号已绑定账号");

        Member newMember = new Member();
        newMember.setMemberId(param.memberId());
        newMember.setMobile(param.getMobile());

        return this.memberMapper.updateById(newMember);
    }

    @Override
    public int log(MemberLogParam param) {
        Member updateMember = new Member();
        updateMember.setMemberId(param.memberId());
        updateMember.setLastVisitTime(DateUtils.currTime());
        this.memberMapper.updateById(updateMember);
        return 1;
    }

    @Override
    public MemberQcodeVo qrcode(MemberQcodeParam param) {


        return null;
    }

}
