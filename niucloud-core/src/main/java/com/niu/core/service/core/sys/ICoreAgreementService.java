package com.niu.core.service.core.sys;

import com.niu.core.entity.sys.SysAgreement;

public interface ICoreAgreementService {

    /**
     * 获取协议
     * @param siteId
     * @param key
     * @return
     */
    SysAgreement getAgreement(Integer siteId, String key);

    /**
     * 设置协议
     * @param siteId
     * @param key
     * @param title
     * @param content
     */
    void setAgreement(Integer siteId, String key, String title, String content);
}
