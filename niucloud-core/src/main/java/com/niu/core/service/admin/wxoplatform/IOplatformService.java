package com.niu.core.service.admin.wxoplatform;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.member.param.MemberSearchParam;
import com.niu.core.service.admin.member.vo.MemberListVo;
import com.niu.core.service.admin.sys.param.SysLoginConfigParam;
import com.niu.core.service.admin.wxoplatform.param.AuthorizationParam;
import com.niu.core.service.admin.wxoplatform.param.IOplatformAuthRecordParam;
import com.niu.core.service.admin.wxoplatform.vo.OplatformRecordVo;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;

/**
 *
 */
public interface IOplatformService {

    /**
     * 查询微信需要的静态信息
     *
     * @return
     */
    String createPreAuthorizationUrl();

    /**
     * 授权
     *
     * @param param
     */
    void authorization(AuthorizationParam param);

    /**
     * 清理授权
     * @param appid
     */
    void clearAuthorization(String appid);

    PageResult<OplatformRecordVo> getAuthRecord(PageParam pageParam, IOplatformAuthRecordParam searchParam);
}
