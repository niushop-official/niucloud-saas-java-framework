package com.niu.core.service.core.member;

/**
 * 会员等级core服务层
 */
public interface ICoreMemberLevelService {

    /**
     * 会员检测升级
     *
     * @param siteId
     * @param memberId
     */
    void checkLevelUpgrade(Integer siteId, Integer memberId);

}
