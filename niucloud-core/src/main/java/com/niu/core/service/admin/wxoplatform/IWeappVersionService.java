package com.niu.core.service.admin.wxoplatform;

import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.entity.oplatform.WxOplatfromWeappVersion;
import com.niu.core.service.admin.wechat.param.WechatFansSearchParam;
import com.niu.core.service.admin.wechat.vo.WechatFansListVo;
import com.niu.core.service.admin.wxoplatform.param.OplatformConfigParam;
import com.niu.core.service.admin.wxoplatform.vo.SiteGroupWeappVersionVo;
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

    /**
     * 添加小程序版本
     */
    void add(Integer siteGroupId, boolean isAll);

    /**
     * 获取小程序版本提交结果
     * @param taskKey
     */
    void getVersionUploadResult(String taskKey, boolean isAll);

    /**
     * 上传成功
     * @param taskKey
     */
    void uploadSuccess(String taskKey, boolean isAll);

    /**
     * 站点小程序提交
     * @param
     */
    void weappCommit(Integer siteId, Integer siteGroupId, WxOplatfromWeappVersion version);

    /**
     * 提交审核
     * @param siteId
     * @param versionId
     * @return
     */
    void submitAudit(Integer siteId, Integer versionId);

    void siteWeappCommit();

    /**
     * 按站点套餐获取提交记录
     * @param pageParam
     * @return
     */
    PageResult<SiteGroupWeappVersionVo> getSiteGroupCommitRecord(PageParam pageParam);
}
