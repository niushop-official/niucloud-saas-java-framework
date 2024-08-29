package com.niu.core.service.admin.member;

import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.member.param.MemberSignParam;
import com.niu.core.service.admin.member.param.MemberSignSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.member.param.SignConfigParam;
import com.niu.core.service.admin.member.vo.MemberSignInfoVo;
import com.niu.core.service.admin.member.vo.MemberSignListVo;
import com.niu.core.service.admin.member.vo.SignConfigVo;
import org.springframework.validation.annotation.Validated;

/**
 * 会员签到服务接口
 */
public interface IMemberSignService {

    /**
     * 查询签到记录
     * @param pageParam
     * @param searchParam
     * @return
     */
    PageResult<MemberSignListVo> pages(PageParam pageParam, MemberSignSearchParam searchParam);

    /**
     * 获取签到设置
     * @return
     */
    SignConfigVo getSignConfig();

    /**
     * 设置签到设置
     * @return
     */
    void setSignConfig(SignConfigParam configParam);
}
