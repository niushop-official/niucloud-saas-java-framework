package com.niu.core.service.api.sys.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.QrcodeUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.object.CollectBuildUtils;
import com.niu.core.common.utils.object.ObjectGroupUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.verify.Verifier;
import com.niu.core.entity.verify.Verify;
import com.niu.core.enums.sys.VerifyTypeEnum;
import com.niu.core.event.sys.VerifyCreateEventDefiner;
import com.niu.core.event.sys.VerifyEventDefiner;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.verify.VerifierMapper;
import com.niu.core.mapper.verify.VerifyMapper;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import com.niu.core.service.api.sys.ISysVerifyService;
import com.niu.core.service.api.sys.param.*;
import com.niu.core.service.api.sys.vo.SysVerifyDetailVo;
import com.niu.core.service.api.sys.vo.SysVerifyGetCodeVo;
import com.niu.core.service.api.sys.vo.SysVerifyRecordsVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class SysVerifyServiceImpl implements ISysVerifyService {

    @Autowired
    private VerifyMapper verifyMapper;

    @Autowired
    private VerifierMapper verifierMapper;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private Cached cached;

    private static final String CACHE_VERIFY = "verify";

    @Override
    public List<SysVerifyGetCodeVo> getVerifyCode(SysVerifyGetCodeParam param) {
        // 生成核销码(对应业务调用)
        List<String> verifyCodeList = this.createVerifyCode(param.siteId(), param.memberId(), param.getType(), param.getData());
        List<SysVerifyGetCodeVo> sysVerifyGetCodeVoList = new ArrayList<>();
        for (String verifyCode : verifyCodeList) {
            SysVerifyGetCodeVo sysVerifyGetCodeVo = new SysVerifyGetCodeVo();
            sysVerifyGetCodeVo.setCode(verifyCode);
            sysVerifyGetCodeVo.setQrcode(QrcodeUtils.qrcode(param.siteId(), "h5", verifyCode, "", null));
            sysVerifyGetCodeVoList.add(sysVerifyGetCodeVo);
        }
        return sysVerifyGetCodeVoList;
    }

    /**
     * 生成二维码
     *
     * @param siteId
     * @param verifyCode
     * @return
     */
    private String generateQrcode(Integer siteId, String verifyCode) {

        return "";
    }

    /**
     * 生成核销码
     *
     * @param siteId
     * @param memberId
     * @param type
     * @param data
     * @return
     */
    private List<String> createVerifyCode(Integer siteId, Integer memberId, String type, Map<String, Object> data) {
        if (ObjectUtil.isEmpty(VerifyTypeEnum.getTypeByName(type))) {
            // 核销类型错误
            throw new RuntimeException("VERIFY_TYPE_ERROR");
        }
        // 2、通过事件获取核销创建，取第一条
        VerifyCreateEventDefiner.VerifyCreateEvent verifyCreateEvent = new VerifyCreateEventDefiner.VerifyCreateEvent();
        verifyCreateEvent.setSiteId(siteId);
        verifyCreateEvent.setType(type);
        verifyCreateEvent.setMemberId(memberId);
        verifyCreateEvent.setData(data);
        List<VerifyCreateEventDefiner.VerifyCreateEventResult> verifyCreateJsonObjectList = EventAndSubscribeOfPublisher.publishAndCallback(verifyCreateEvent);
        VerifyCreateEventDefiner.VerifyCreateEventResult verifyCreateEventResult = null;
        if (verifyCreateJsonObjectList != null && verifyCreateJsonObjectList.size() > 0) {
            verifyCreateEventResult = verifyCreateJsonObjectList.get(0);
        }
        // 3、 生成核销码的数据组装
        int count = 1;
        Long expireTime = null;
        Map<String, Object> verifyCodeDataMap = new HashMap<>();
        if (verifyCreateEventResult != null) {
            count = verifyCreateEventResult.getCount();
            expireTime = verifyCreateEventResult.getExpireTime();
            verifyCodeDataMap.put("site_id", siteId);
            verifyCodeDataMap.put("type", type);
            verifyCodeDataMap.put("type_name", VerifyTypeEnum.getTypeByName(type));
            verifyCodeDataMap.put("data", data);
            verifyCodeDataMap.put("value", verifyCreateEventResult.getData());
            verifyCodeDataMap.put("body", verifyCreateEventResult.getBody());
            verifyCodeDataMap.put("relate_tag", verifyCreateEventResult.getRelateTag());
        } else {
            verifyCodeDataMap.put("site_id", siteId);
            verifyCodeDataMap.put("type", type);
            verifyCodeDataMap.put("type_name", VerifyTypeEnum.getTypeByName(type));
            verifyCodeDataMap.put("data", data);
        }
        // 4、生成核销码，并且放入缓存
        List<String> verifyCodeList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String salt = IdUtil.randomUUID();
            String dataString = JSONUtil.toJsonStr(data);
            String verifyCode = DigestUtil.md5Hex(salt + dataString);
            this.createCode(verifyCode, verifyCodeDataMap, expireTime);
            verifyCodeList.add(verifyCode);
        }
        return verifyCodeList;
    }

    /**
     * 设置核销码数据缓存
     *
     * @param verifyCode
     * @param valueMap
     * @param timeoutSeconds
     */
    private void createCode(String verifyCode, Map<String, Object> valueMap, Long timeoutSeconds) {
        if (timeoutSeconds != null && timeoutSeconds > 0) {
            cached.tag(CACHE_VERIFY).put(verifyCode, valueMap, timeoutSeconds);
        } else {
            cached.tag(CACHE_VERIFY).put(verifyCode, valueMap);
        }
    }

    /**
     * 清除核销
     *
     * @param verifyCode
     */
    private void clearCode(String verifyCode) {
        cached.tag(CACHE_VERIFY).remove(verifyCode);
    }

    /**
     * 获取核销码数据缓存
     *
     * @param verifyCode
     * @return array
     */
    private Map<String, Object> getCodeData(String verifyCode) {
        Map<String, Object> codeMap = (Map<String, Object>) cached.tag(CACHE_VERIFY).get(verifyCode);
        if (codeMap == null) {
            // 核销码已过期
            throw new RuntimeException("当前核销码已核销或已失效");
        }
        return codeMap;
    }

    /**
     * 校验是否是核销员
     *
     * @param param
     * @return
     */
    @Override
    public Boolean checkVerifier(SysVerifyCheckVerifierParam param) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select("id");
        queryWrapper.eq("member_id", param.memberId());
        queryWrapper.eq("site_id", param.siteId());
        List<Verifier> verifierList = verifierMapper.selectList(queryWrapper);
        if (verifierList != null && verifierList.size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * 获取核销员核销记录
     *
     * @param param
     * @return
     */
    @Override
    public PageResult records(PageParam pageParam, SysVerifyRecordsParam param) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("site_id", param.siteId());
        queryWrapper.eq("verifier_member_id", param.memberId());

        if (StrUtil.isNotEmpty(param.getCode())) {
            queryWrapper.eq("code", param.getCode());
        }
        if (StrUtil.isNotEmpty(param.getType())) {
            queryWrapper.eq("type", param.getType());
        }
        if (StrUtil.isNotEmpty(param.getCreateTime())) {
            queryWrapper.eq("create_time", param.getCreateTime());
        }
        if (StrUtil.isNotEmpty(param.getRelateTag())) {
            queryWrapper.eq("relate_tag", param.getRelateTag());
        }
        if (StrUtil.isNotEmpty(param.getKeyword())) {
            queryWrapper.eq("keyword", param.getKeyword());
        }
        queryWrapper.orderByDesc(Arrays.asList("create_time"));

        IPage<Verify> iPage = this.verifyMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<SysVerifyRecordsVo> dataList = CollectUtils.convert(iPage.getRecords(), SysVerifyRecordsVo.class);

        // 以下代码块=====>用以填充SysVerifyRecordsVo类对象的：nickname, mobile, headimg属性
        // 根据集合ID主键索引查询到对象列表
        QueryWrapper withQueryWrapper = new QueryWrapper();
        withQueryWrapper.select("member_id, nickname, mobile, headimg");
        List<Integer> memberIdList = CollectBuildUtils.getKeyValueList(dataList, "verifierMemberId");
        withQueryWrapper.in("member_id", memberIdList);
        List<Member> memberList = memberMapper.selectList(withQueryWrapper);
        // 先将数据分组，然后放入结果集
        Map<Integer, Member> memberMap = ObjectGroupUtils.group(memberList, "memberId");
        dataList.stream().forEach(bean -> {
            Member member = memberMap.get(bean.getVerifierMemberId());
            if (member != null) {
                MemberBriefInfoVo membervo = new MemberBriefInfoVo();
                BeanUtils.copyProperties(member, membervo);
                bean.setMember(membervo);
            }
        });
        return PageResult.build(page, limit, iPage.getTotal()).setData(dataList);
    }

    @Override
    public PageResult records2(PageParam pageParam, SysVerifyRecordsParam param) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        MPJQueryWrapper<Verify> mpjQueryWrapper = new MPJQueryWrapper<>();
        mpjQueryWrapper.select("t.*");
        mpjQueryWrapper.setAlias("t");
        mpjQueryWrapper.innerJoin("?_site_group nsg ON ns.group_id = nsg.group_id");
        mpjQueryWrapper.eq("site_id", param.siteId());
        mpjQueryWrapper.eq("verifier_member_id", param.memberId());
        IPage<SysVerifyRecordsVo> iPage = memberMapper.selectJoinPage(new Page<>(page, limit), SysVerifyRecordsVo.class, mpjQueryWrapper);
        return PageResult.build(page, limit, iPage.getTotal()).setData(iPage.getRecords());
    }

    @Override
    public SysVerifyDetailVo detail(SysVerifyDetailParam param) {
        QueryWrapper<Verify> verifyQueryWrapper = new QueryWrapper();
        verifyQueryWrapper.eq("site_id", param.siteId());
        verifyQueryWrapper.eq("verifier_member_id", param.memberId());
        verifyQueryWrapper.eq("code", param.getCode());
        Verify verify = this.verifyMapper.selectOne(verifyQueryWrapper);
        SysVerifyDetailVo sysVerifyDetailVo = new SysVerifyDetailVo();
        if (verify != null) {
            BeanUtils.copyProperties(verify, sysVerifyDetailVo);
            QueryWrapper<Member> memberQueryWrapper = new QueryWrapper();
            memberQueryWrapper.select("member_id, nickname, mobile, headimg");
            memberQueryWrapper.eq("member_id", param.memberId());
            Member member = this.memberMapper.selectOne(memberQueryWrapper);
            if (member != null) {
                MemberBriefInfoVo membervo = new MemberBriefInfoVo();
                BeanUtils.copyProperties(member, membervo);
                sysVerifyDetailVo.setMember(membervo);
            }
        }
        return sysVerifyDetailVo;
    }

    /**
     * 获取核销码信息
     *
     * @param param
     * @return
     */
    @Override
    public Map<String, Object> getInfoByCode(SysVerifyByCodeParam param) {
        // param.getSiteId();
        Map<String, Object> codeMap = this.getCodeData(param.getCode());
        if (codeMap.get("site_id") == null || !codeMap.get("site_id").equals(param.siteId())) {
            // 核销码已过期
            throw new RuntimeException("当前核销码已核销或已失效");
        }
        codeMap.put("value", JSONUtil.parseObj(codeMap.get("value")));
        return codeMap;
    }

    /**
     * 核销(核销api调用)
     *
     * @param param
     * @return
     */
    @Override
    public Boolean verifyCode(SysVerifyCodeParam param) {
        // 1、获取核销码数据
        Map<String, Object> verifyMap = this.getCodeData(param.getCode());
        // 检测站点数据
        if (verifyMap.get("site_id") == null || !verifyMap.get("site_id").equals(param.siteId())) {
            // 核销码已过期
            throw new RuntimeException("当前核销码已核销或已失效");
        }
        // 检测核销员身份
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select("id");
        queryWrapper.eq("member_id", param.memberId());
        queryWrapper.eq("site_id", param.siteId());
        Verifier verifier = verifierMapper.selectOne(queryWrapper);
        if (verifier == null) {
            throw new RuntimeException("核销员不存在");
        }
        // 核销操作
        Map<String, Object> verifyDataMap = new HashMap<>();
        verifyDataMap.put("site_id", verifyMap.get("site_id"));
        verifyDataMap.put("code", param.getCode());
        verifyDataMap.put("body", verifyMap.get("body"));
        verifyDataMap.put("data", verifyMap.get("data"));
        verifyDataMap.put("value", verifyMap.get("value"));
        verifyDataMap.put("type", verifyMap.get("type"));
        verifyDataMap.put("relate_tag", verifyMap.get("relate_tag"));
        verifyDataMap.put("create_time", DateUtils.currTime());
        verifyDataMap.put("verifier_member_id", param.memberId());
        // 核销
        VerifyEventDefiner.VerifyEvent verifyEvent = new VerifyEventDefiner.VerifyEvent();
        verifyEvent.setSiteId(param.siteId());
        verifyEvent.build(verifyDataMap);
        EventAndSubscribeOfPublisher.publishAndCallback(verifyEvent);

        // 添加核销记录
        Verify model = new Verify();
        BeanUtils.copyProperties(verifyEvent, model);
        model.setData(JSONUtil.parseObj(verifyEvent.getData()).toString());
        verifyMapper.insert(model);

        // 核销码失效
        this.clearCode(param.getCode());
        return true;
    }

}
