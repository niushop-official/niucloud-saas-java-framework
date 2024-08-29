package com.niu.core.service.api.member;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.api.member.param.*;
import com.niu.core.service.api.member.vo.*;

import java.util.List;

public interface IMemberSignService {

    /**
     * 会员签到记录
     *
     * @param param
     * @return
     */
    PageResult<MemberSignRecordVo> signRecord(PageParam pageParam, MemberSignRecordParam param);


    /**
     * 签到详情
     *
     * @param param
     * @return
     */
    MemberSignDetailsVo signDetails(MemberSignDetailsParam param);

    /**
     * 会员签到
     *
     * @param param
     * @return
     */
    MemberSignOperateVo signOperate(MemberSignOperateParam param);

    /**
     * 获取月签到数据
     *
     * @param param
     * @return
     */
    MemberSignMonthRecordVo signMonthRecord(MemberSignMonthRecordParam param);

    /**
     * 获取日签到奖励
     *
     * @param param
     * @return
     */
    List<MemberSignDayRecordVo> signDayRecord(MemberSignDayRecordParam param);

    /**
     * 获取签到设置
     *
     * @param param
     * @return
     */
    MemberSignConfigVo signConfig(MemberSignConfigParam param);

}
