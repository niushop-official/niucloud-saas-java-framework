package com.niu.core.service.admin.verify;

import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.verify.param.VerifyParam;
import com.niu.core.service.admin.verify.param.VerifySearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.verify.vo.VerifyInfoVo;
import com.niu.core.service.admin.verify.vo.VerifyListVo;

/**
 * 核销记录服务接口
 */
public interface IVerifyService {


    /**
     * 核销记录列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<VerifyListVo>
     */
    PageResult<VerifyListVo> list(PageParam pageParam, VerifySearchParam searchParam);

    /**
     * 核销记录详情
     * @return VerifyInfoVo
     */
    VerifyInfoVo detail(String code);
}
