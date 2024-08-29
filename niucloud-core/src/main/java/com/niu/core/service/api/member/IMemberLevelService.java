package com.niu.core.service.api.member;

import com.niu.core.entity.member.MemberLevel;
import com.niu.core.service.api.member.param.MemberLevelParam;
import com.niu.core.service.api.member.vo.MemberLevelInfoVo;
import com.niu.core.service.api.member.vo.MemberLevelSimpleInfoVo;

import java.util.List;
import java.util.Set;

/**
 * 会员等级
 */
public interface IMemberLevelService {

    /**
     * @param memberLevel
     * @return
     */
    MemberLevel findById(Integer memberLevel);

    /**
     * @param idSet
     * @return
     */
    public List<MemberLevelSimpleInfoVo> findByIds(Set<Integer> idSet);

    /**
     * @param param
     * @return
     */
    List<MemberLevelInfoVo> list(MemberLevelParam param);

}
