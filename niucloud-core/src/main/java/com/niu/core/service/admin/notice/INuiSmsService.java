package com.niu.core.service.admin.notice;

import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.notice.param.*;
import com.niu.core.service.admin.notice.vo.TemplateListVo;
import com.niu.core.service.api.login.param.SendMobileCodeParam;
import com.niu.core.service.core.notice.vo.AddonNoticeListVo;
import com.niu.core.service.core.notice.vo.NoticeInfoVo;

import java.util.List;
import java.util.Map;

public interface INuiSmsService {


    Map<String, Object> getConfig();

    /**
     * 获取创建签名初始化的配置信息
     * @return
     */
    Map<String, Object> signCreateConfig();

    /**
     * 获取图形验证码
     * @return
     */
    JSONObject captcha();

    /**
     * 手机号发送验证码
     * @param param
     * @return
     */
    JSONObject sendMobileCode(SendMobileCodeParam param);

    /**
     * 注册牛之云子账号
     * @param param
     * @return
     */
    JSONObject registerAccount(RegisterAccountParam param);

    /**
     * 登录牛之云子账号
     * @param param
     * @return
     */
    JSONObject loginAccount(RegisterAccountParam param);

    /**
     * 重置密码
     * @param param
     * @return
     */
    JSONObject resetPassword(RegisterAccountParam param);

    /**
     * 获取子账号信息
     * @param username
     * @return
     */
    JSONObject accountInfo(String username);

    /**
     * 创建模版需要的配置
     * @return
     */
    Map<String, Object> templateCreateConfig();

    /**
     * 模版列表
     * @param smsType
     * @param username
     * @return
     */
    List<TemplateListVo> getTemplateList(String smsType, String username);

    /**
     * 充值订单列表
     * @param pageParam
     * @param username
     * @param param
     * @return
     */
    JSONObject orderList(PageParam pageParam, String username, OrderListParam param);

    /**
     * 获取短信发送列表
     * @param pageParam
     * @param username
     * @param param
     * @return
     */
    JSONObject accountSendList(PageParam pageParam, String username, SendListParam param);

    /**
     * 启用牛云短信
     * @param isEnable
     */
    void enable(Integer isEnable);

    /**
     * 修改子账户信息
     * @param username
     * @param param
     * @return
     */
    JSONObject editAccount(String username, EditAccountParam param);

    /**
     * 获取签名列表
     * @param pageParam
     * @param username
     * @return
     */
    Map<String, Object> getSignList(PageParam pageParam, String username);

    /**
     * 删除签名
     * @param username
     * @param param
     * @return
     */
    List<Object> signDelete(String username, SignDeleteParam param);

    /**
     * 创建签名
     * @param username
     * @param param
     */
    void signCreate(String username, RegisterAccountParam param);

    /**
     * 获取短信套餐列表
     * @param param
     * @return
     */
    JSONObject getSmsPackageList(SmsPackageParam param);

    /**
     * 订单计算
     * @param username
     * @param param
     * @return
     */
    JSONObject orderCalculate(String username, OrderCalculateParam param);

    /**
     * 创建订单
     * @param username
     * @param param
     * @return
     */
    JSONObject createOrder(String username, OrderCalculateParam param);

    /**
     * 获取订单支付信息
     * @param username
     * @param outTradeNo
     * @return
     */
    JSONObject getPayInfo(String username, String outTradeNo);

    /**
     * 订单详情
     * @param username
     * @param outTradeNo
     * @return
     */
    JSONObject getOrderInfo(String username, String outTradeNo);

    /**
     * 订单状态
     * @param username
     * @param outTradeNo
     * @return
     */
    JSONObject getOrderStatus(String username, String outTradeNo);

    /**
     * 模版同步
     * @param username
     * @return
     */
    Map<String, Object> templateSync(String username, String smsType);

    /**
     * 模板创建
     * @param username
     * @param smsType
     * @param param
     * @return
     */
    JSONObject templateCreate(String username, String smsType, TemplateCreateParam param);

    /**
     * 模版删除
     * @param username
     * @param templateId
     */
    void templateDelete(String username, String templateId);
}
