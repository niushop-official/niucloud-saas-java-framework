package com.niu.core.service.admin.sys;

import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.sys.param.SysAgreementParam;
import com.niu.core.service.admin.sys.param.SysAgreementSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.sys.vo.SysAgreementInfoVo;
import com.niu.core.service.admin.sys.vo.SysAgreementListVo;

import java.util.List;

/**
 * 协议服务接口
 */
public interface ISysAgreementService {


    /**
     * 协议列表
     *
     * @return List<SysAgreementListVo>
     */
    List<SysAgreementListVo> list();

    SysAgreementInfoVo getAgreement(String key);

    void setAgreement(String key, String title, String content);
}
