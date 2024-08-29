package com.niu.core.service.api.member.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.entity.member.MemberLevel;
import com.niu.core.enums.member.MemberLevelStyleEnum;
import com.niu.core.mapper.member.MemberLevelMapper;
import com.niu.core.service.api.member.IMemberLevelService;
import com.niu.core.service.api.member.param.MemberLevelParam;
import com.niu.core.service.api.member.vo.MemberLevelInfoVo;
import com.niu.core.service.api.member.vo.MemberLevelSimpleInfoVo;
import com.niu.core.service.core.member.ICoreMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        Integer index = 0;
        List<MemberLevelInfoVo> list = new LinkedList<>();
        for (MemberLevel level : memberLevelList) {
            MemberLevelInfoVo vo = new MemberLevelInfoVo();
            BeanUtils.copyProperties(level, vo);

            if (!level.getLevelBenefits().isEmpty()) {
                JSONObject benefits = JSONUtil.parseObj(level.getLevelBenefits());
                benefits = coreMemberService.getBenefitsContent(param.siteId(), benefits, "member_level");
                vo.setLevelBenefits(benefits);
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

}
