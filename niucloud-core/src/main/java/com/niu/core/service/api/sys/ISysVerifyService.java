package com.niu.core.service.api.sys;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.api.sys.param.*;
import com.niu.core.service.api.sys.vo.SysVerifyDetailVo;
import com.niu.core.service.api.sys.vo.SysVerifyGetCodeVo;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ISysVerifyService {

    /**
     * 根据业务获取核销码
     *
     * @param param
     * @return
     */
    List<SysVerifyGetCodeVo> getVerifyCode(SysVerifyGetCodeParam param) ;

    /**
     * 校验当前会员是否是核销员
     *
     * @param param
     * @return
     */
    Boolean checkVerifier(SysVerifyCheckVerifierParam param);

    /**
     * 核销记录
     *
     * @param param
     * @return
     */
    PageResult records(PageParam pageParam, SysVerifyRecordsParam param);

    /**
     * 核销记录
     *
     * @param param
     * @return
     */
    PageResult records2(PageParam pageParam, SysVerifyRecordsParam param);


    /**
     * 获取核销详情
     *
     * @param param
     * @return
     */
    SysVerifyDetailVo detail(SysVerifyDetailParam param);

    /**
     * 通过code码获取核销信息
     *
     * @param param
     * @return
     */
    Map<String, Object> getInfoByCode(SysVerifyByCodeParam param);

    /**
     * 核销操作
     *
     * @param param
     * @return
     */
    Boolean verifyCode(SysVerifyCodeParam param);

}
