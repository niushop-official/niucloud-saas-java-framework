package com.niu.core.service.api.member.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.member.MemberLevel;
import com.niu.core.enums.member.MemberLevelStyleEnum;
import com.niu.core.mapper.member.MemberLevelMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.api.member.IMemberLevelService;
import com.niu.core.service.api.member.param.MemberLevelParam;
import com.niu.core.service.api.member.vo.MemberLevelInfoVo;
import com.niu.core.service.api.member.vo.MemberLevelSimpleInfoVo;
import com.niu.core.service.core.member.ICoreMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class MemberLevelServiceImpl implements IMemberLevelService {

    @Autowired
    private MemberLevelMapper memberLevelMapper;

    @Autowired
    private ICoreMemberService coreMemberService;

    @Resource
    private MemberMapper memberMapper;

    @Override
    public MemberLevel findById(Integer id) {
        return memberLevelMapper.selectById(id);
    }

    @Override
    public List<MemberLevelSimpleInfoVo> findByIds(Set<Integer> idSet) {
        QueryWrapper<MemberLevel> queryWrapper = new QueryWrapper();
        queryWrapper.select("level_id,level_name");
        queryWrapper.in("id", idSet);
        List<MemberLevel> memberLevelList = memberLevelMapper.selectList(queryWrapper);
        return CollectUtils.convert(memberLevelList, MemberLevelSimpleInfoVo.class);
    }

    @Override
    public List<MemberLevelInfoVo> list(MemberLevelParam param) {
        QueryWrapper<MemberLevel> queryWrapper = new QueryWrapper();
        queryWrapper.select("level_id,site_id,level_name,growth,remark,level_benefits,level_gifts");
        queryWrapper.eq("site_id", param.siteId());
        List<MemberLevel> memberLevelList = memberLevelMapper.selectList(queryWrapper);

        if (memberLevelList == null) return null;

        JSONObject levelStyle = MemberLevelStyleEnum.getStyle();

        JSONObject filling = new JSONObject();
        filling.put("benefits_one", new JSONObject().set("title", "专属客服").set("desc", "专业服务").set("icon", "/static/resource/images/member/benefits/benefits_kefu.png"));
        filling.put("benefits_two", new JSONObject().set("title", "专属徽章").set("desc", "专属徽章").set("icon", "/static/resource/images/member/benefits/benefits_badge.png"));
        filling.put("benefits_four", new JSONObject().set("title", "经验累计").set("desc", "经验累计").set("icon", "/static/resource/images/member/benefits/benefits_experience.png"));
        filling.put("benefits_three", new JSONObject().set("title", "尊享客服").set("desc", "尊享客服").set("icon", "/static/resource/images/member/benefits/benefits_badge.png"));

        Integer index = 0;
        List<MemberLevelInfoVo> list = new LinkedList<>();
        for (MemberLevel level : memberLevelList) {
            MemberLevelInfoVo vo = new MemberLevelInfoVo();
            BeanUtils.copyProperties(level, vo);

            if (ObjectUtil.isNotEmpty(level.getLevelBenefits())) {
                JSONObject benefits = JSONUtil.parseObj(level.getLevelBenefits());
                benefits = coreMemberService.getBenefitsContent(param.siteId(), benefits, "member_level");
                vo.setLevelBenefits(benefits);
            } else {
                vo.setLevelBenefits(filling);
            }

            if (vo.getLevelBenefits().size() < 4) {
                for(String key : filling.keySet()) {
                    if (vo.getLevelBenefits().size() == 4) break;
                    vo.getLevelBenefits().set(key, filling.get(key));
                }
            }

            if (!level.getLevelGifts().isEmpty()) {
                JSONObject gifts = JSONUtil.parseObj(level.getLevelGifts());
                gifts = coreMemberService.getGiftContent(param.siteId(), gifts, "member_level");
                vo.setLevelGifts(gifts);
            }
            Integer levelKey = index % 7 + 1;
            vo.setLevelBg("/static/resource/images/member/level/bg_" + levelKey + ".png");
            vo.setMemberBg("/static/resource/images/member/level/member_" + levelKey + ".png");
            vo.setLevelIcon("/static/resource/images/member/level/level_icon" + levelKey + ".png");
            vo.setLevelTag("/static/resource/images/member/level/level_" + levelKey + ".png");
            vo.setLevelStyle(levelStyle.getJSONObject("level_" + levelKey));

            list.add(vo);
            index++;
        }

        return list;
    }

    /**
     * 获取会员的会员等级
     *
     * @param memberId
     * @return
     */
    public MemberLevelInfoVo findMemberLevel(Integer memberId) {
        MPJQueryWrapper<Member> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("m")
                .leftJoin("?_member_level ml ON ml.level_id = m.member_level".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("ml.*");
        queryWrapper.eq("m.member_id", memberId);
        queryWrapper.ne("m.member_level", 0);
        MemberLevel level = memberMapper.selectJoinOne(MemberLevel.class, queryWrapper);
        if (level == null) return null;

        MemberLevelInfoVo vo = new MemberLevelInfoVo();
        BeanUtils.copyProperties(level, vo);

        if (ObjectUtil.isNotEmpty(level.getLevelBenefits()) && JSONUtil.isJson(level.getLevelBenefits())) vo.setLevelBenefits(JSONUtil.parseObj(level.getLevelBenefits()));
        if (ObjectUtil.isNotEmpty(level.getLevelGifts()) && JSONUtil.isJson(level.getLevelGifts())) vo.setLevelGifts(JSONUtil.parseObj(level.getLevelGifts()));

        return vo;
    }
}
