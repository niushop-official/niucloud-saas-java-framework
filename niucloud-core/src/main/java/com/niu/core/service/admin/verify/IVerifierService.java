package com.niu.core.service.admin.verify;

import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.verify.param.VerifierParam;
import com.niu.core.service.admin.verify.param.VerifierSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.verify.vo.VerifierInfoVo;
import com.niu.core.service.admin.verify.vo.VerifierListVo;

import java.util.List;

/**
 * 核销员服务接口
 */
public interface IVerifierService {


    /**
     * 核销员列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<VerifierListVo>
     */
    PageResult<VerifierListVo> list(PageParam pageParam, VerifierSearchParam searchParam);

    /**
     * 全部核销员
     * @return
     */
    List<VerifierListVo> all();

    /**
     * 核销员添加
     * @param addParam 添加参数
     */
    void add(VerifierParam addParam);

    /**
     * 核销员删除
     * @param id 主键ID
     */
    void del(Integer id);

}
