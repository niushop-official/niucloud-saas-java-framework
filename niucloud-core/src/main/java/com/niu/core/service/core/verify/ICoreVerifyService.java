package com.niu.core.service.core.verify;

import java.util.Map;

public interface ICoreVerifyService {

    /**
     * 核销员核销
     * @param siteId
     * @param memberId
     * @param code
     */
    void verifyCode(Integer siteId, Integer memberId, String code);

    /**
     * 获取核销信息
     * @param siteId
     * @param memberId
     * @param code
     * @return
     */
    Map<String, Object> getInfoByCode(Integer siteId, Integer memberId, String code);

    /**
     * 管理端核销
     * @param siteId
     * @param code
     */
    void adminVerify(Integer siteId, String code);

    /**
     * 管理端获取核销信息
     * @param siteId
     * @param code
     * @return
     */
    Map<String, Object> adminGetInfoByCode(Integer siteId, String code);
}
