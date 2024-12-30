package com.niu.core.service.core.member.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.member.MemberLevel;
import com.niu.core.mapper.member.MemberLevelMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.core.member.ICoreMemberLevelService;
import com.niu.core.service.core.member.ICoreMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class CoreMemberLevelServiceImpl implements ICoreMemberLevelService {

    @Resource
    MemberMapper memberMapper;

    @Resource
    MemberLevelMapper memberLevelMapper;

    @Resource
    ICoreMemberService coreMemberService;

    /**
     * 会员检测升级
     *
     * @param siteId
     * @param memberId
     */
    public void checkLevelUpgrade(Integer siteId, Integer memberId) {
        try {
            Member member = memberMapper.selectOne(new QueryWrapper<Member>().select("member_id,growth,member_level").eq("member_id", memberId).eq("site_id", siteId));
            if (member == null) return;

            // 查询达到的等级
            QueryWrapper upgradeQuery = new QueryWrapper<MemberLevel>();
            upgradeQuery.eq("site_id", siteId);
            upgradeQuery.le("growth", member.getGrowth());
            if (member.getMemberLevel() > 0) {
                MemberLevel memberLevel = memberLevelMapper.selectOne(new QueryWrapper<MemberLevel>().select("growth").eq("level_id", member.getMemberLevel()));
                if (memberLevel != null) {
                    upgradeQuery.gt("growth", memberLevel.getGrowth());
                }
            }
            upgradeQuery.orderByAsc("growth");
            List<MemberLevel> upgrade = memberLevelMapper.selectList(upgradeQuery);

            if (ObjectUtil.isNotEmpty(upgrade)) {
                Map<String, Object> vars = new HashMap<String, Object>();
                vars.put("from_type", "level_upgrade");
                vars.put("memo", "会员升级奖励");

                for (MemberLevel level : upgrade) {
                    if (ObjectUtil.isNotEmpty(level.getLevelGifts()) && JSONUtil.isJson(level.getLevelGifts())) {
                        coreMemberService.memberGiftGrant(siteId, memberId, JSONUtil.parseObj(level.getLevelGifts()), vars);
                    }
                }

                MemberLevel end = upgrade.get(upgrade.size() - 1);
                member.setMemberLevel(end.getLevelId());
                memberMapper.updateById(member);
            }
        } catch (Exception e) {
            log.info("会员检测升级异常");
            e.printStackTrace();
        }
    }

}
