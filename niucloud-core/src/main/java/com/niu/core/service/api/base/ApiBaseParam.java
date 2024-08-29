package com.niu.core.service.api.base;

import com.niu.core.common.utils.RequestUtils;

public interface ApiBaseParam {

    /**
     * @return
     */
    default Integer siteId() {
        Integer siteId = RequestUtils.siteId();
        if (siteId == null) {
            // 测试时使用，非测试去掉
//            siteId = 0;
        }
        return siteId;
    }

    /**
     * @return
     */
    default Integer memberId() {
        Integer memberId = RequestUtils.memberId();
        if (memberId == null) {
            // 测试时使用，非测试去掉
//            memberId = 15;
        }
        return memberId;
    }

}
