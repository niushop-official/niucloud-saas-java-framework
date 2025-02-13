package com.niu.core.service.admin.member.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.crypto.PasswordEncipher;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.member.MemberLabel;
import com.niu.core.entity.member.MemberLevel;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.enums.common.SexEnum;
import com.niu.core.enums.member.MemberRegisterChannelEnum;
import com.niu.core.enums.member.MemberRegisterTypeEnum;
import com.niu.core.enums.member.StatusEnum;
import com.niu.core.mapper.member.MemberLabelMapper;
import com.niu.core.mapper.member.MemberLevelMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.admin.member.IMemberService;
import com.niu.core.service.admin.member.param.MemberAddParam;
import com.niu.core.service.admin.member.param.MemberModifyParam;
import com.niu.core.service.admin.member.param.MemberParam;
import com.niu.core.service.admin.member.param.MemberSearchParam;
import com.niu.core.service.admin.member.vo.MemberAllListVo;
import com.niu.core.service.admin.member.vo.MemberInfoVo;
import com.niu.core.service.admin.member.vo.MemberLabelAllListVo;
import com.niu.core.service.admin.member.vo.MemberListVo;
import com.niu.core.service.core.member.ICoreMemberService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 会员实现
 */
@Service
public class MemberServiceImpl implements IMemberService {

    @Resource
    MemberMapper memberMapper;

    @Resource
    MemberLabelMapper memberLabelMapper;

    @Resource
    MemberLevelMapper memberLevelMapper;

    @Resource
    ICoreMemberService iCoreMemberService;

    /**
     * 会员列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<MemberListVo>
     */
    @Override
    public PageResult<MemberListVo> list(PageParam pageParam, MemberSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        Integer siteId = RequestUtils.siteId();
        MPJQueryWrapper<Member> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("m").leftJoin("?_member_level ml ON ml.level_id = m.member_level".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("m.*, ml.level_name as member_level_name");
        queryWrapper.eq("m.site_id", siteId);
        queryWrapper.orderByDesc("member_id");

        // 查询条件
        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) {
            queryWrapper.and(i -> i.like("member_no", searchParam.getKeyword()).or()
                    .like("username", searchParam.getKeyword()).or()
                    .like("nickname", searchParam.getKeyword()).or()
                    .like("mobile", searchParam.getKeyword()));
        }

        if (ObjectUtil.isNotNull(searchParam.getIsDel()) && ObjectUtil.isNotEmpty(searchParam.getKeyword())) {
            queryWrapper.eq("is_del", searchParam.getIsDel());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getMemberLevel()) && searchParam.getMemberLevel() != 0) {
            queryWrapper.eq("member_level", searchParam.getMemberLevel());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getRegisterChannel())) {
            queryWrapper.eq("register_channel", searchParam.getRegisterChannel());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getMemberLabel()) && searchParam.getMemberLabel() != 0) {
            queryWrapper.like("member_label", searchParam.getMemberLabel());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getRegisterType())) {
            queryWrapper.eq("register_type", searchParam.getRegisterType());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "m.create_time", searchParam.getCreateTime());
        }

        IPage<Member> iPage = null;
        List<Member> memberList = new ArrayList<>();
        if (page > 0 && limit > 0) {
            iPage = memberMapper.selectPage(new Page<>(page, limit), queryWrapper);
            memberList = iPage.getRecords();
        } else {
            memberList = memberMapper.selectList(queryWrapper);
        }

        Map<Integer, MemberLevel> levelMap = Collections.emptyMap();
        Set<Integer> levelIds = CollStreamUtil.toSet(memberList, Member::getMemberLevel);
        if (ObjectUtil.isNotEmpty(levelIds)) {
            levelMap = memberLevelMapper.selectBatchIds(levelIds).stream().collect(Collectors.toMap(MemberLevel::getLevelId, e -> e));
        }
        
        List<MemberListVo> list = new LinkedList<>();
        for (Member item : memberList) {
            MemberListVo vo = new MemberListVo();
            BeanUtils.copyProperties(item, vo);
            vo.setSexName(SexEnum.getNameBySex(item.getSex()));
            vo.setStatusName(StatusEnum.getNameByStatus(item.getStatus()));
            vo.setRegisterChannelName(ChannelEnum.getNameByCode(item.getRegisterChannel()));
            vo.setMemberLevelName(levelMap.getOrDefault(item.getMemberLevel(), new MemberLevel()).getLevelName());

            if (!item.getMemberLabel().isEmpty()) {
                JSONArray memberLabelArrays = JSONUtil.parseArray(vo.getMemberLabel());
                if (memberLabelArrays != null && memberLabelArrays.size() > 0) {
                    List<MemberLabelAllListVo> memberLabelArray = new LinkedList<>();
                    List<MemberLabel> labelList = memberLabelMapper.selectList(new QueryWrapper<MemberLabel>().select("label_name").in("label_id", memberLabelArrays));
                    for (MemberLabel labelItem : labelList) {
                        MemberLabelAllListVo labelVo = new MemberLabelAllListVo();
                        BeanUtils.copyProperties(labelItem, labelVo);
                        memberLabelArray.add(labelVo);
                    }
                    vo.setMemberLabelArray(memberLabelArray);
                }
            }
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage == null ? list.size() : iPage.getTotal()).setData(list);
    }

    /**
     * 会员详情
     *
     * @param id 主键
     * @return MemberInfoVo
     */
    @Override
    public MemberInfoVo info(Integer id) {
        Integer siteId = RequestUtils.siteId();

        Member model = memberMapper.selectOne(
                new MPJQueryWrapper<Member>()
                        .setAlias("m")
                        .leftJoin("?_member_level ml ON ml.level_id = m.member_level".replace("?_", GlobalConfig.tablePrefix))
                        .select("m.*, ml.level_name as member_level_name")
                        .eq("member_id", id)
                        .eq("m.site_id", siteId)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        MemberInfoVo vo = new MemberInfoVo();
        BeanUtils.copyProperties(model, vo);

        if (StrUtil.isNotEmpty(model.getMemberLabel()) && !Arrays.asList(model.getMemberLabel()).isEmpty()) {
            JSONArray memberLabelArrays = JSONUtil.parseArray(vo.getMemberLabel());
            if (memberLabelArrays != null && memberLabelArrays.size() > 0) {
                List<MemberLabelAllListVo> memberLabelArray = new LinkedList<>();
                List<MemberLabel> labelList = memberLabelMapper.selectList(new QueryWrapper<MemberLabel>().select("label_name").in("label_id", memberLabelArrays));
                for (MemberLabel item : labelList) {
                    MemberLabelAllListVo labelVo = new MemberLabelAllListVo();
                    BeanUtils.copyProperties(item, labelVo);
                    memberLabelArray.add(labelVo);
                }
                vo.setMemberLabelArray(memberLabelArray);
            }
        }
        return vo;
    }


    /**
     * 会员添加
     *
     * @param addParam 参数
     */
    @Override
    public void add(MemberAddParam addParam) {
        Integer siteId = RequestUtils.siteId();

        addParam.setUsername(addParam.getMobile());

        Member mobileIsExist = memberMapper.selectOne(new QueryWrapper<Member>()
                .select("member_id")
                .eq("site_id", siteId)
                .and(i -> i.eq("mobile", addParam.getMobile()).or().eq("username", addParam.getMobile()))
                .last("limit 1"));
        Assert.isNull(mobileIsExist, "手机号已存在");

        if (addParam.getNickname().length() == 0 & addParam.getMobile().length() > 0) {
            addParam.setNickname(addParam.getMobile().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
        }

        if (addParam.getMemberNo().isEmpty()) {
            addParam.setMemberNo(iCoreMemberService.createMemberNo(siteId));
        } else {
            Member memberNoIsExist = memberMapper.selectOne(new QueryWrapper<Member>()
                    .select("member_id")
                    .eq("site_id", siteId)
                    .eq("member_no", addParam.getMemberNo())
                    .last("limit 1"));
            Assert.isNull(memberNoIsExist, "会员编码已存在");
        }

        Member model = new Member();
        model.setSiteId(siteId);
        model.setMobile(addParam.getMobile());
        model.setMemberNo(addParam.getMemberNo());
        model.setUsername(addParam.getUsername());
        model.setNickname(addParam.getNickname());
        model.setHeadimg(addParam.getHeadimg());
        model.setPassword(PasswordEncipher.encode(addParam.getPassword()));
        model.setRegisterType(MemberRegisterTypeEnum.MANUAL.getType());
        model.setRegisterChannel(MemberRegisterChannelEnum.MANUAL.getType());
        model.setCreateTime(System.currentTimeMillis() / 1000);

        memberMapper.insert(model);
    }

    /**
     * 会员编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, MemberParam editParam) {
        Integer siteId = RequestUtils.siteId();

        UpdateWrapper<Member> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("site_id", siteId)
                .eq("member_id", id);

        Member model = new Member();
        model.setNickname(editParam.getNickname());
        model.setHeadimg(editParam.getHeadimg());
        model.setPassword(editParam.getPassword());
        model.setSex(editParam.getSex());
        model.setBirthday(editParam.getBirthday());

        memberMapper.update(model, updateWrapper);
    }

    /**
     * 修改会员
     *
     * @param editParam
     */
    public void modify(MemberModifyParam editParam) {
        Integer siteId = RequestUtils.siteId();

        Member model = new Member();

        switch (editParam.getField()) {
            case "nickname":
                model.setNickname(String.valueOf(editParam.getValue()));
                break;
            case "headimg":
                model.setHeadimg(String.valueOf(editParam.getValue()));
                break;
            case "member_label":
                model.setMemberLabel(String.valueOf(editParam.getValue()));
                break;
            case "member_level":
                model.setMemberLevel(Integer.parseInt(String.valueOf(editParam.getValue())));
                break;
            case "birthday":
                model.setBirthday(String.valueOf(editParam.getValue()));
                break;
            case "sex":
                model.setSex(Integer.parseInt(String.valueOf(editParam.getValue())));
                break;
        }

        UpdateWrapper<Member> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("site_id", siteId)
                .eq("member_id", editParam.getMemberId());
        memberMapper.update(model, updateWrapper);
    }

    /**
     * 会员删除
     *
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        Integer siteId = RequestUtils.siteId();

        QueryWrapper<Member> queryWrapper = new QueryWrapper<Member>()
                .eq("site_id", siteId)
                .eq("member_id", id)
                .last("limit 1");

        memberMapper.delete(queryWrapper);
    }

    /**
     * 查询全部会员
     *
     * @param searchParam
     * @return
     */
    @Override
    public List<MemberAllListVo> all(MemberSearchParam searchParam) {
        Integer siteId = RequestUtils.siteId();

        MPJQueryWrapper<Member> queryWrapper = new MPJQueryWrapper<Member>();
        queryWrapper.select("member_id,headimg,nickname").eq("site_id", siteId);
        if (ObjectUtil.isNotEmpty(searchParam.getKeyword()))
            queryWrapper.like("member_no|username|nickname|mobile", searchParam.getKeyword());

        List<Member> members = memberMapper.selectList(queryWrapper); // 调用 selectList 方法

        List<MemberAllListVo> list = new LinkedList<>();
        for (Member item : members) {
            MemberAllListVo vo = new MemberAllListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }

    /**
     * 设置会员状态
     *
     * @param status
     * @param param
     */
    @Override
    public void setStatus(Integer status, MemberParam param) {
        Integer siteId = RequestUtils.siteId();

        UpdateWrapper<Member> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("member_id", param.getMemberIds());
        updateWrapper.eq("site_id", siteId);

        Member updateMember = new Member();
        updateMember.setStatus(status);
        memberMapper.update(updateMember, updateWrapper);
    }

    /**
     * 获取会员码
     *
     * @return
     */
    public String getMemberNo() {
        return iCoreMemberService.createMemberNo(RequestUtils.siteId());
    }

    /**
     * 获取会员礼包内容
     *
     * @param param
     * @return
     */
    public JSONObject getMemberGiftsContent(JSONObject param) {
        return iCoreMemberService.getGiftContent(RequestUtils.siteId(), param.getJSONObject("gifts"), "admin");
    }

    /**
     * 获取权益内容
     *
     * @param param
     * @return
     */
    public JSONObject getMemberBenefitsContent(JSONObject param) {
        return iCoreMemberService.getBenefitsContent(RequestUtils.siteId(), param.getJSONObject("benefits"), "admin");
    }
}
