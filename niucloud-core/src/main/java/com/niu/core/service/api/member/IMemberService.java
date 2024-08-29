package com.niu.core.service.api.member;

import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.api.member.param.*;
import com.niu.core.service.api.member.vo.*;

import java.util.List;

public interface IMemberService {

    /**
     * 会员信息
     *
     * @param param
     * @return
     */
    MemberInfoVo info(MemberInfoParam param);

    /**
     * 会员中心
     *
     * @param param
     * @return
     */
    MemberCenterVo center(MemberCenterParam param);

    /**
     * 修改会员
     *
     * @param param
     * @return
     */
    int modify(MemberModifyParam param);

    /**
     * 编辑会员
     *
     * @param param
     * @return
     */
    int edit(MemberEditParam param);

    /**
     * 绑定手机号
     *
     * @param param
     * @return
     */
    int mobile(MemberMobileParam param);

    /**
     * 会员日志
     *
     * @param param
     * @return
     */
    int log(MemberLogParam param);

    /**
     * 获取会员码
     *
     * @param param
     * @return
     */
    MemberQcodeVo qrcode(MemberQcodeParam param);

}
