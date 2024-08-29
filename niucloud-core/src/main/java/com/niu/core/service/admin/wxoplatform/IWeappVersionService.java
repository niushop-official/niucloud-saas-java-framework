package com.niu.core.service.admin.wxoplatform;

import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.wechat.param.WechatFansSearchParam;
import com.niu.core.service.admin.wechat.vo.WechatFansListVo;
import com.niu.core.service.admin.wxoplatform.param.OplatformConfigParam;
import com.niu.core.service.admin.wxoplatform.vo.WxOplatfromWeappVersionVo;
import com.niu.core.service.core.wxoplatform.vo.CoreOplatformStaticConfigVo;

/**
 * 小程序版本管理服务层
 */
public interface IWeappVersionService {

    /**
     * 获取最后一次提交记录
     *
     * @return
     */
    WxOplatfromWeappVersionVo getLastCommitRecord();

    /**
     * 小程序版本列表分页
     *
     * @param pageParam
     * @return
     */
    PageResult<WxOplatfromWeappVersionVo> list(PageParam pageParam);
}
