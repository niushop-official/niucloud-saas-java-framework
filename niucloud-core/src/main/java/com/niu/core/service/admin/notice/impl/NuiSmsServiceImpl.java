package com.niu.core.service.admin.notice.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.util.StringUtil;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.exception.ApiException;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.utils.NiucloudUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.json.JacksonUtils;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.sys.NiuSmsTemplate;
import com.niu.core.entity.sys.SysNotice;
import com.niu.core.enums.notice.*;
import com.niu.core.enums.notice.vo.NoticeEnumListVo;
import com.niu.core.enums.sys.SmsTypeEnum;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.mapper.sys.NiuSmsTemplateMapper;
import com.niu.core.mapper.sys.SysNoticeMapper;
import com.niu.core.service.admin.notice.INuiSmsService;
import com.niu.core.service.admin.notice.param.*;
import com.niu.core.service.admin.notice.vo.TemplateListVo;
import com.niu.core.service.api.login.param.SendMobileCodeParam;
import com.niu.core.service.core.notice.ICoreNoticeService;
import com.niu.core.service.core.notice.vo.AddonNoticeListVo;
import com.niu.core.service.core.notice.vo.NoticeInfoVo;
import com.niu.core.service.core.site.ICoreSiteService;
import com.niu.core.service.core.sys.ICoreConfigService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.InputStream;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static cn.dev33.satoken.SaManager.config;

@Log4j2
@Service
public class NuiSmsServiceImpl implements INuiSmsService {
    private final String NIUYUN = "niuyun";

    private final String SEND_CODE_URL = "niusms/send";

    private final String SEND_CAPTCHA_URL = "niusms/captcha";

    private final String ACCOUNT_REGISTER_URL = "niusms/account/register";

    private final String LOGIN_ACCOUNT_URL = "niusms/account/login/%s";

    private final String ACCOUNT_INFO_URL = "niusms/account/info/%s";

    private final String RESET_PASSWORD_URL = "niusms/account/reset/password/%s";

    private final String ORDER_LIST_URL = "niusms/order/list/%s";

    private final String ACCOUNT_SEND_LIST_URL = "niusms/account/send_list/%s";

    private final String ACCOUNT_EDIT_URL = "niusms/account/edit/%s";

    private final String SIGN_LIST_URL = "niusms/sign/%s/list";

    private final String SIGN_ADD_URL = "niusms/sign/%s/add";

    private final String PACKAGE_LIST_URL = "niusms/packages";

    private final String ORDER_CALCULATE_URL = "niusms/order/calculate/%s";

    private final String ORDER_CREATE_URL = "niusms/order/create/%s";

    private final String ORDER_PAY_URL = "niusms/pay/info/%s";

    private final String ORDER_INFO_URL = "niusms/order/info/%s/%s";

    private final String ORDER_STATUS_URL = "niusms/order/status/%s/%s";

    private final String TEMPLATE_LIST_URL = "niusms/template/%s/list";

    private final String TEMPLATE_ADD_URL = "niusms/template/%s/add";

    private final String SIGN_DEL = "https://api-shss.zthysms.com/sms/v1/sign/delete";

    private final String TEMPLATE_DELETE = "https://api-shss.zthysms.com/sms/v2/template/delete";

    private final String TEMPLATE_INFO_URL = "/niusms/template/%s/info";


    @Resource
    private ICoreConfigService coreConfigService;

    @Resource
    private ICoreNoticeService coreNoticeService;

    @Resource
    private SysNoticeMapper sysNoticeMapper;

    @Resource
    private ICoreSiteService coreSiteService;

    @Resource
    private NiuSmsTemplateMapper niuSmsTemplateMapper;

    @Override
    public Map<String, Object> getConfig() {
        Map<String, Object> result = new HashMap<>();
        JSONObject config = getConfig(true);
        if (config == null) {
            result.put("is_login", 0);
            result.put("is_enable", 0);
            result.put("username", "");
            return result;
        }

        boolean isNiuSmsPresent = config.containsKey(NIUYUN) && config.get(NIUYUN) != null;
        result.put("is_login", isNiuSmsPresent ? 1 : 0);
        if (isNiuSmsPresent) {
            JSONObject niuyun = config.getJSONObject(NIUYUN);
            result.put("username", niuyun.getOrDefault("username", ""));
        } else {
            result.put("username", "");
        }
        boolean isEnable = NIUYUN.equals(config.getOrDefault("default", ""));
        result.put("is_enable", isEnable ? 1 : 0);
        return result;
    }

    @Override
    public Map<String, Object> signCreateConfig() {
        Map<String, Object> result = new HashMap<>();
        NoticeTypeEnum.getSignSource();
        result.put("sign_source_list", NoticeTypeEnum.getSignSource());
        result.put("sign_type_list", NoticeTypeEnum.getSignType());
        result.put("sign_default_list", NoticeTypeEnum.getSignDefault());
        return result;
    }

    @Override
    public JSONObject captcha() {
        try {
            JSONObject jsonObject = NiucloudUtils.Niucloud.get(SEND_CAPTCHA_URL, new HashMap<>());
            JSONObject result = jsonObject.getJSONObject("data");
            //删除null值 防止序列化报错
            JacksonUtils.removeNull(result);
            return result;
        } catch (Exception e) {
            log.error("获取验证码失败异常信息:{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public JSONObject sendMobileCode(SendMobileCodeParam param) {
        Map<String, String> body = new HashMap<>();
        body.put("mobile", param.getMobile());
        body.put("captcha_key", param.getCaptchaKey());
        body.put("captcha_code", param.getCaptchaCode());
        try {
            JSONObject jsonObject = NiucloudUtils.Niucloud.post(SEND_CODE_URL, body);
            JSONObject result = jsonObject.getJSONObject("data");
            //删除null值 防止序列化报错
            JacksonUtils.removeNull(result);
            return result;
        } catch (Exception e) {
            log.error("发送验证码失败异常信息:{}", e.getMessage());
            throw new AdminException("发送验证码失败");
        }
    }

    @Override
    public JSONObject registerAccount(RegisterAccountParam param) {
        if (ObjectUtil.isNotEmpty(param.getImgUrl())) {
            param.setImgUrl(Paths.get(RequestUtils.getReqeustURI(), param.getImgUrl()).toString());
        }
        JSONObject result = null;
        try {
            result = NiucloudUtils.Niucloud.post(ACCOUNT_REGISTER_URL, param);
            //删除null值 防止序列化报错
            JacksonUtils.removeNull(result);
        } catch (Exception e) {
            log.error("注册账号失败异常信息:{}", e.getMessage());
            throw new AdminException("注册账号失败");
        }
        return result;
    }

    @Override
    public JSONObject loginAccount(RegisterAccountParam param) {
        String url = String.format(LOGIN_ACCOUNT_URL, param.getUsername());
        Map<String, String> body = new HashMap<>();
        body.put("username", param.getUsername());
        body.put("password", param.getPassword());
        try {
            JSONObject jsonObject = NiucloudUtils.Niucloud.post(url, body);
            JSONObject result = jsonObject.getJSONObject("data");
            if (result == null) {
                throw new AdminException("登录失败");
            }
            param.setSignature(" ");
            param.setDefaultVal(" ");
            setConfig(param);
            //删除null值 防止序列化报错
            JacksonUtils.removeNull(result);
            return result;
        } catch (Exception e) {
            log.error("登录账号失败异常信息:{}", e.getMessage());
            throw new AdminException(e.getMessage());
        }
    }

    @Override
    public JSONObject resetPassword(RegisterAccountParam param) {
        JSONObject result = new JSONObject();
        // 获取用户信息
        JSONObject data = accountInfo(param.getUsername());

        // 拆分手机号并验证
        String mobiles = String.valueOf(data.getOrDefault("mobiles", ""));
        List<String> mobileList = Arrays.asList(mobiles.split(","));
        if (!mobileList.contains(param.getMobile())) {
            throw new AdminException("手机号错误");
        }

        // 重置密码
        String newPassword = null;
        try {
            String resetPasswordUrl = String.format(RESET_PASSWORD_URL, param.getUsername());
            Map<String, String> resetPasswordBody = new HashMap<>();
            resetPasswordBody.put("mobile", param.getMobile());
            resetPasswordBody.put("code", param.getCode());
            resetPasswordBody.put("key", param.getKey());
            JSONObject resetPasswordJson = NiucloudUtils.Niucloud.put(resetPasswordUrl, resetPasswordBody);
            JSONObject resetPasswordDataJson = resetPasswordJson.getJSONObject("data");
            newPassword = resetPasswordDataJson.getStr("newPassword");
        } catch (Exception e) {
            log.error("重置密码失败异常信息:{}", e.getMessage());
            throw new AdminException("重置密码失败");
        }

        //修改配置
        RegisterAccountParam registerAccountParam = new RegisterAccountParam();
        registerAccountParam.setUsername(param.getUsername());
        registerAccountParam.setPassword(param.getPassword());
        setConfig(registerAccountParam);

        result.put("password", newPassword);
        return result;
    }

    @Override
    public JSONObject accountInfo(String username) {
        String infoUrl = String.format(ACCOUNT_INFO_URL, username);
        try {
            JSONObject jsonObject = NiucloudUtils.Niucloud.get(infoUrl, new HashMap<>());
            JSONObject result = jsonObject.getJSONObject("data");
            // 获取配置
            JSONObject nyConfig = getConfig(false);
            if (result != null && nyConfig != null && nyConfig.containsKey("username")) {
                if (nyConfig.getStr("username").equals(result.getStr("username"))) {
                    result.set("signature", nyConfig.getOrDefault("signature", "").toString().trim());
                }
            }
            JacksonUtils.removeNull(result);
            return result;
        } catch (JSONException e) {
            log.error("获取用户信息失败异常信息:{}", e.getMessage());
            throw new AdminException("获取用户信息失败");
        }
    }

    @Override
    public Map<String, Object> templateCreateConfig() {
        Map<String, Object> result = new HashMap<>();
        result.put("template_params_type_list", TemplateParamsTypeEnum.getApiParamsType());
        result.put("template_type_list", TemplateTypeEnum.getMap());
        result.put("template_status_list", TemplateStatusEnum.getMap());
        return result;
    }

    @Override
    public List<TemplateListVo> getTemplateList(String smsType, String username) {
        JSONObject config = getConfig(false);
        Integer siteId = RequestUtils.siteId();
        if (ObjectUtil.isEmpty(config) || !config.getOrDefault("username", "").equals(username)) {
            throw new ApiException("牛云短信账号异常,请重新登录账号");
        }
        List<SysNotice> list = sysNoticeMapper.selectList(new QueryWrapper<SysNotice>().eq("site_id", siteId));
        Map<String, SysNotice> map = new HashMap<>();
        if (!CollectionUtils.isEmpty(list)) {
            map = list.stream().collect(Collectors.toMap(SysNotice::getKey, item -> item));
        }
        List<TemplateListVo> notice = new ArrayList<>();
        for (Map.Entry<String, NoticeEnumListVo> noticeMap : NoticeEnum.getNiuyunNotice().entrySet()) {
            TemplateListVo noticeInfoVo = new TemplateListVo();
            BeanUtil.copyProperties(noticeMap.getValue(), noticeInfoVo);
            if (map.containsKey(noticeMap.getKey())) {
                BeanUtil.copyProperties(map.get(noticeMap.getKey()), noticeInfoVo);
            }
            //针对短信，微信公众号，小程序配置
            if (ObjectUtil.isNotEmpty(noticeMap.getValue().getSupport_type_map())) {
                for (Map.Entry<String, JSONObject> supportTypeMap : noticeMap.getValue().getSupport_type_map().entrySet()) {
                    if (supportTypeMap.getKey().equals("sms")) {
                        noticeInfoVo.setSms(supportTypeMap.getValue());
                    }
                    if (supportTypeMap.getKey().equals("wechat")) {
                        noticeInfoVo.setWechat(supportTypeMap.getValue());
                    }
                    if (supportTypeMap.getKey().equals("weapp")) {
                        noticeInfoVo.setWeapp(supportTypeMap.getValue());
                    }
                }
            }

            notice.add(noticeInfoVo);
        }
        List<NiuSmsTemplate> niuSmsTemplates = niuSmsTemplateMapper.selectList(new QueryWrapper<NiuSmsTemplate>()
                .eq("sms_type", smsType)
                .eq("username", username)
                .eq("site_id", siteId));
        Map<String, NiuSmsTemplate> templateMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(niuSmsTemplates)){
            templateMap = niuSmsTemplates.stream().collect(Collectors.toMap(NiuSmsTemplate::getTemplateKey, item -> item));
        }
        List<Addon> addonList = coreSiteService.getSiteAddons(siteId);
        Addon sys = new Addon();
        sys.setKey("system");
        addonList.add(0, sys);
        List<TemplateListVo> result = new ArrayList<>();
        for (Addon addon : addonList) {
            for (TemplateListVo noticeInfoVo : notice) {
                if (addon.getKey().equals(noticeInfoVo.getAddon())) {
                    if ("system".equals(noticeInfoVo.getAddon())) {
                        noticeInfoVo.setAddon("系统");
                    }
                    JSONObject auditInfo = new JSONObject();
                    auditInfo.set("audit_msg", templateMap.containsKey(noticeInfoVo.getKey()) ? templateMap.get(noticeInfoVo.getKey()).getAuditMsg() : "");
                    auditInfo.set("audit_status", templateMap.containsKey(noticeInfoVo.getKey()) ? templateMap.get(noticeInfoVo.getKey()).getAuditStatus() : TemplateAuditStatus.TEMPLATE_NOT_REPORT.getCode());
                    auditInfo.set("audit_status_name", TemplateAuditStatus.getByCode(auditInfo.getInt("audit_status")).getDescription());
                    List<String> paramsJson = new ArrayList<>();
                    if (templateMap.containsKey(noticeInfoVo.getKey())){
                        String paramJson = templateMap.get(noticeInfoVo.getKey()).getParamJson();
                        if (ObjectUtil.isNotEmpty(paramJson)){
                            JSONObject jsonObject = JSONUtil.parseObj(paramJson);
                            paramsJson.addAll(jsonObject.keySet());
                        }
                    }
                    Collections.sort(paramsJson);

                    List<String> variable = new ArrayList<>();
                    if (ObjectUtil.isNotEmpty(noticeInfoVo.getVariable())) {
                        variable.addAll(noticeInfoVo.getVariable().keySet());
                    }
                    Collections.sort(variable);

                    String errorStatus = "";
                    // 比较两个键列表
                    if (templateMap.containsKey(noticeInfoVo.getKey()) && !variable.equals(paramsJson)) {
                        if (paramsJson.isEmpty()) {
                            errorStatus = String.valueOf(TemplateAuditStatus.TEMPLATE_NEED_PULL.getCode());
                        } else {
                            errorStatus = auditInfo.getInt("audit_status") == TemplateAuditStatus.TEMPLATE_PASS.getCode()
                                    ? String.valueOf(TemplateAuditStatus.TEMPLATE_STATUS_AGAIN_REPORT.getCode())
                                    : String.valueOf(TemplateAuditStatus.TEMPLATE_NEED_EDIT.getCode());
                        }
                    }
                    auditInfo.set("error_status", errorStatus);
                    if (StringUtil.isNotEmpty(errorStatus)){
                        auditInfo.set("error_status_name", TemplateAuditStatus.getByCode(Integer.parseInt(errorStatus)).getDescription());
                    }else {
                        auditInfo.set("error_status_name", "");
                    }
                    noticeInfoVo.setAuditInfo(auditInfo);
                    if (templateMap.containsKey(noticeInfoVo.getKey())){
                        NiuSmsTemplate niuSmsTemplate = templateMap.get(noticeInfoVo.getKey());
                        if (StringUtils.isNotEmpty(niuSmsTemplate.getTemplateType())){
                            noticeInfoVo.setTemplateTypeName(TemplateTypeEnum.fromCode(Integer.parseInt(niuSmsTemplate.getTemplateType())).getDescription());
                        }else {
                            noticeInfoVo.setTemplateTypeName("");
                        }
                        noticeInfoVo.setTemplateId(Integer.parseInt(niuSmsTemplate.getTemplateId()));
                    }
                    result.add(noticeInfoVo);
                }
            }
        }
        return result;
    }

    @Override
    public NiuSmsTemplate checkTemplateAudit(String templateKey, String templateId) {
        return null;
    }

    @Override
    public JSONObject orderList(PageParam pageParam, String username, OrderListParam param) {
        String orderListUrl = String.format(ORDER_LIST_URL, username);
        JSONObject result = null;
        try {
            Map<String, Object> orderListParam = new HashMap<>();
            orderListParam.put("out_trade_no", param.getOutTradeNo());
            orderListParam.put("order_status", param.getStatus());
            orderListParam.put("create_time_start", param.getCreateTimeStart());
            orderListParam.put("create_time_end", param.getCreateTimeEnd());
            orderListParam.put("limit", pageParam.getLimit());
            orderListParam.put("page", pageParam.getPage());
            JSONObject jsonObject = NiucloudUtils.Niucloud.get(orderListUrl, orderListParam);
            result = jsonObject.getJSONObject("data");
            //删除null值 防止序列化报错
            JacksonUtils.removeNull(result);
        } catch (Exception e) {
            log.error("获取订单列表失败异常信息:{}", e.getMessage());
            throw new AdminException("获取订单列表失败");
        }
        return result;
    }

    @Override
    public JSONObject accountSendList(PageParam pageParam, String username, SendListParam param) {
        String accountSendUrl = String.format(ACCOUNT_SEND_LIST_URL, username);
        Map<String, Object> accountSendParam = new HashMap<>();
        accountSendParam.put("mobile", param.getMobile());
        accountSendParam.put("content", param.getContent());
        accountSendParam.put("smsStatus", param.getSmsStatus());
        accountSendParam.put("limit", pageParam.getLimit());
        accountSendParam.put("page", pageParam.getPage());
        try {
            JSONObject jsonObject = NiucloudUtils.Niucloud.get(accountSendUrl, accountSendParam);
            JSONObject result = jsonObject.getJSONObject("data");
            //删除null值 防止序列化报错
            JacksonUtils.removeNull(result);
            return result;
        } catch (Exception e) {
            log.error("获取发送列表失败异常信息:{}", e.getMessage());
            throw new AdminException("获取发送列表失败");
        }
    }

    @Override
    public void enable(Integer isEnable) {
        RegisterAccountParam registerAccountParam = new RegisterAccountParam();
        if (isEnable == 1) {
            JSONObject config = getConfig(true);
            if (ObjectUtil.isEmpty(config) ||
                    !config.containsKey(NIUYUN) ||
                    (config.getJSONObject(NIUYUN)).get("username") == null ||
                    (config.getJSONObject(NIUYUN)).get("username").toString().isEmpty() ||
                    (config.getJSONObject(NIUYUN)).get("password") == null ||
                    (config.getJSONObject(NIUYUN)).get("password").toString().isEmpty() ||
                    (config.getJSONObject(NIUYUN)).get("signature") == null ||
                    (config.getJSONObject(NIUYUN)).get("signature").toString().isEmpty()) {
                throw new AdminException("需登录账号并配置签名后才能启用牛云短信");
            }
            registerAccountParam.setDefaultVal(NIUYUN);
            setConfig(registerAccountParam);
        } else {
            registerAccountParam.setDefaultVal(" ");
            setConfig(registerAccountParam);
        }
    }

    @Override
    public JSONObject editAccount(String username, EditAccountParam param) {
        String editAccountUrl = String.format(ACCOUNT_EDIT_URL, username);
        Map<String, Object> editAccountBody = new HashMap<>();
        editAccountBody.put("new_mobile", param.getNewMobile());
        editAccountBody.put("mobile", param.getMobile());
        editAccountBody.put("code", param.getCode());
        editAccountBody.put("key", param.getKey());
        editAccountBody.put("signature", param.getSignature());
        try {
            JSONObject jsonObject = NiucloudUtils.Niucloud.put(editAccountUrl, editAccountBody);
            RegisterAccountParam registerAccountParam = new RegisterAccountParam();
            registerAccountParam.setSignature(param.getSignature());
            setConfig(registerAccountParam);
            JacksonUtils.removeNull(jsonObject);
            return jsonObject;
        } catch (Exception e) {
            log.error("修改账号信息失败异常信息:{}", e.getMessage());
            throw new AdminException("修改账号信息失败");
        }
    }

    @Override
    public Map<String, Object> getSignList(PageParam pageParam, String username) {
        Map<String, Object> result = new HashMap<>();
        String signListUrl = String.format(SIGN_LIST_URL, username);
        Map<String, Object> signListParam = new HashMap<>();
        signListParam.put("limit", pageParam.getLimit());
        signListParam.put("page", pageParam.getPage());
        try {
            JSONObject jsonObject = NiucloudUtils.Niucloud.get(signListUrl, signListParam);
            JSONObject data = jsonObject.getJSONObject("data");
            JSONObject page = data.getJSONObject("page");
            result.put("total", page.getInt("total"));
            result.put("per_page", page.getInt("size"));
            result.put("current_page", page.getInt("currentPage"));
            result.put("last_page", page.getInt("totalPage"));
            JSONObject config = getConfig(true);
            List<JSONObject> dataResult = new ArrayList<>();
            for (Object o : data.getJSONArray("signatures")) {
                JSONObject dataMap = new JSONObject(o);
                BeanUtils.copyProperties(o, dataMap);
                dataMap.put("auditResultName", SignAuditStatusEnum.fromCode(((JSONObject) o).getInt("auditResult")).getDescription());
                dataMap.put("createTime", DateUtils.timestampToString(((JSONObject) o).getLong("createTime") / 1000));
                dataMap.put("is_default", config.getOrDefault("signature", "").equals(dataMap.getOrDefault("sign", "")) ? 1 : 0);
                dataResult.add(dataMap);
            }
            result.put("data", dataResult);
            return result;
        } catch (Exception e) {
            log.error("获取签名列表失败异常信息:{}", e.getMessage());
            throw new AdminException("获取签名列表失败");
        }
    }

    @Override
    public List<Object> signDelete(String username, SignDeleteParam param) {
        JSONObject config = getConfig(false);
        param.setPassword(config.getStr("password"));
        try {
            List<Object> failList = delSign(username, param);

            String configSignature = config.getStr("signature");
            List<String> signatures = param.getSignatures();

            if (signatures != null && !failList.isEmpty() &&
                    signatures.contains(configSignature) &&
                    !failList.contains(configSignature)) {
                // 如果满足条件，则清空账户的签名
                EditAccountParam editAccountParam = new EditAccountParam();
                editAccountParam.setSignature("");
                editAccount(username, editAccountParam);
            }
            return failList;
        } catch (Exception e) {
            log.error("删除签名失败异常信息:{}", e.getMessage());
            throw new AdminException("删除签名失败异常");
        }
    }

    @Override
    public void signCreate(String username, RegisterAccountParam param) {
        if (ObjectUtil.isNotEmpty(param.getImgUrl())) {
            param.setImgUrl(Paths.get(RequestUtils.getReqeustURI(), param.getImgUrl()).toString());
        }
        String signCreateUrl = String.format(SIGN_ADD_URL, username);
        try {
            JSONObject jsonObject = NiucloudUtils.Niucloud.post(signCreateUrl, param);
            JSONObject data = jsonObject.getJSONObject("data");
            if (data.containsKey("failList") && !data.getJSONArray("failList").isEmpty()) {
                JSONArray failList = data.getJSONArray("failList");
                throw new ApiException(failList.getJSONObject(0).getStr("msg"));
            }
        } catch (ApiException e) {
            log.error("创建签名失败异常信息:{}", e.getMessage());
            throw new AdminException("创建签名失败");
        }
    }

    @Override
    public JSONObject getSmsPackageList(SmsPackageParam param) {
        Map<String, Object> pageListParam = new HashMap<>();
        pageListParam.put("package_name", param.getPackageName());
        pageListParam.put("sms_num", param.getSmsNum());
        pageListParam.put("price_start", param.getPriceStart());
        pageListParam.put("price_end", param.getPriceEnd());
        pageListParam.put("original_price_start", param.getOriginalPriceStart());
        pageListParam.put("original_price_end", param.getOriginalPriceEnd());
        pageListParam.put("time_start", param.getTimeStart());
        pageListParam.put("time_end", param.getTimeEnd());
        pageListParam.put("page", param.getPage());
        pageListParam.put("limit", param.getLimit());
        try {
            JSONObject jsonObject = NiucloudUtils.Niucloud.get(PACKAGE_LIST_URL, pageListParam);
            JSONObject result = jsonObject.getJSONObject("data");
            //删除null值 防止序列化报错
            JacksonUtils.removeNull(result);
            return result;
        } catch (Exception e) {
            log.error("获取套餐列表失败异常信息:{}", e.getMessage());
            throw new AdminException("获取套餐列表失败");
        }
    }

    @Override
    public JSONObject orderCalculate(String username, OrderCalculateParam param) {
        String orderCalculateUrl = String.format(ORDER_CALCULATE_URL, username);
        Map<String, Object> orderCalculateBody = new HashMap<>();
        orderCalculateBody.put("package_id", param.getPackageId());
        try {
            JSONObject jsonObject = NiucloudUtils.Niucloud.post(orderCalculateUrl, orderCalculateBody);
            JSONObject result = jsonObject.getJSONObject("data");
            //删除null值 防止序列化报错
            JacksonUtils.removeNull(result);
            return result;
        } catch (Exception e) {
            log.error("计算订单失败异常信息:{}", e.getMessage());
            throw new AdminException("计算订单失败");
        }
    }

    @Override
    public JSONObject createOrder(String username, OrderCalculateParam param) {
        String orderCreateUrl = String.format(ORDER_CREATE_URL, username);
        Map<String, Object> orderCreateBody = new HashMap<>();
        orderCreateBody.put("package_id", param.getPackageId());
        try {
            JSONObject jsonObject = NiucloudUtils.Niucloud.post(orderCreateUrl, orderCreateBody);
            JSONObject result = jsonObject.getJSONObject("data");
            //删除null值 防止序列化报错
            JacksonUtils.removeNull(result);
            return result;
        } catch (Exception e) {
            log.error("创建订单失败异常信息:{}", e.getMessage());
            throw new AdminException("创建订单失败");
        }
    }

    @Override
    public JSONObject getPayInfo(String username, String outTradeNo) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String protocol = request.isSecure() ? "https" : "http";
        String host = request.getServerName();
        int port = request.getServerPort();
        if (port != 80 && port != 443) {
            host += ":" + port;
        }
        String returnUrl = String.format("%s://%s/site/setting/sms/pay", protocol, host);
        String payInfoUrl = String.format(ORDER_PAY_URL, username);
        Map<String, Object> payInfoBody = new HashMap<>();
        payInfoBody.put("notify_url", payInfoUrl);
        payInfoBody.put("return_url", returnUrl);
        payInfoBody.put("out_trade_no", outTradeNo);
        try {
            JSONObject jsonObject = NiucloudUtils.Niucloud.post(payInfoUrl, payInfoBody);
            JSONObject result = jsonObject.getJSONObject("data");
            //删除null值 防止序列化报错
            JacksonUtils.removeNull(result);
            return result;
        } catch (Exception e) {
            log.error("获取支付信息失败异常信息:{}", e.getMessage());
            throw new AdminException("获取支付信息失败");
        }
    }

    @Override
    public JSONObject getOrderInfo(String username, String outTradeNo) {
        String orderInfoUrl = String.format(ORDER_INFO_URL, username, outTradeNo);
        try {
            JSONObject jsonObject = NiucloudUtils.Niucloud.get(orderInfoUrl, new HashMap<>());
            JSONObject result = jsonObject.getJSONObject("data");
            //删除null值 防止序列化报错
            JacksonUtils.removeNull(result);
            return result;
        } catch (Exception e) {
            log.error("获取订单信息失败异常信息:{}", e.getMessage());
            throw new AdminException("获取订单信息失败");
        }
    }

    @Override
    public JSONObject getOrderStatus(String username, String outTradeNo) {
        String orderStatusUrl = String.format(ORDER_STATUS_URL, username, outTradeNo);
        try {
            JSONObject jsonObject = NiucloudUtils.Niucloud.get(orderStatusUrl, new HashMap<>());
            JSONObject result = jsonObject.getJSONObject("data");
            //删除null值 防止序列化报错
            JacksonUtils.removeNull(result);
            return result;
        } catch (Exception e) {
            log.error("获取订单状态失败异常信息:{}", e.getMessage());
            throw new AdminException("获取订单状态失败");
        }
    }

    @Override
    public Map<String, Object> templateSync(String username, String smsType) {
        List<TemplateListVo> templateList = getTemplateList(smsType, username);
        Map<String, List<Object>> repeatNameMap = new HashMap<>();
        int isRepeat = 0;

        // 检查重复名称
        for (TemplateListVo item : templateList) {
            String name = item.getName();
            String addon = item.getAddon();

            repeatNameMap.computeIfAbsent(name, k -> new ArrayList<>()).add(addon);

            if (repeatNameMap.get(name).size() > 1) {
                isRepeat = 1;
            }
        }

        if (isRepeat == 1) {
            // 移除不重复的条目
            repeatNameMap.entrySet().removeIf(entry -> entry.getValue().size() == 1);
            return Map.of("repeat_list", repeatNameMap);
        } else {
            repeatNameMap = new HashMap<>();
        }

        // 执行同步操作
        execSync(smsType, username, templateList, 1);
        return Map.of("repeat_list", repeatNameMap);
    }

    @Override
    public JSONObject templateCreate(String username, String smsType, TemplateCreateParam param) {
        String templateKey = param.getTemplateKey();
        List<TemplateListVo> templateList = getTemplateList(smsType, username);

        // 查找模版信息，如果不存在则抛出异常
        TemplateListVo templateInfo = templateList.stream()
                .filter(item -> item.getKey().equals(templateKey))
                .findFirst()
                .orElseThrow(() -> new AdminException("当前模版未配置短信内容"));

        // 检查模版是否配置了短信内容，如果未配置则抛出异常
        if (ObjectUtil.isEmpty(templateInfo.getSms()) || ObjectUtil.isEmpty(templateInfo.getSms().get("content"))) {
            throw new AdminException("当前模版未配置短信内容");
        }

        // 检查模版是否已经审核通过，如果已通过则抛出异常
        List<NiuSmsTemplate> modelInfo = niuSmsTemplateMapper.selectList(new QueryWrapper<NiuSmsTemplate>()
                .eq("template_key", templateKey)
                .eq("site_id", RequestUtils.siteId())
                .eq("username", username));
        if (!CollectionUtils.isEmpty(modelInfo) && TemplateAuditStatus.TEMPLATE_PASS.getCode().toString().equals(modelInfo.get(0).getAuditStatus())) {
            throw new AdminException("审核通过的模版不允许修改");
        }

        JSONObject config = getConfig(false);
        String templateCreateUrl = String.format(TEMPLATE_ADD_URL, username);
        Map<String, Object> templateCreateBody = new HashMap<>();
        templateCreateBody.put("temName", templateInfo.getName());
        templateCreateBody.put("temType", param.getTemplateType());
        templateCreateBody.put("temContent", templateInfo.getSms().get("content"));
        templateCreateBody.put("paramJson", param.getParamsJson());
        templateCreateBody.put("extend", Map.of("template_key", templateKey));
        templateCreateBody.put("signature", config.getStr("signature"));
        templateCreateBody.put("temId", param.getTemplateId());

        JSONObject result;
        try {
            JSONObject jsonObject = NiucloudUtils.Niucloud.post(templateCreateUrl, templateCreateBody);
            result = jsonObject.getJSONObject("data");
            JacksonUtils.removeNull(result); // 删除null值，防止序列化报错
        } catch (Exception e) {
            log.error("创建模版失败异常信息:{}，", e.getMessage());
            throw new AdminException("创建模版失败");
        }

        // 获取模板ID，如果不存在则设为0
        Integer temId = result.containsKey("temId") ? result.getInt("temId") : 0;

        if (CollectionUtils.isEmpty(modelInfo)) {
            NiuSmsTemplate niuSmsTemplate = new NiuSmsTemplate();
            niuSmsTemplate.setSiteId(RequestUtils.siteId());
            niuSmsTemplate.setSmsType(smsType);
            niuSmsTemplate.setUsername(username);
            niuSmsTemplate.setTemplateKey(templateKey);
            niuSmsTemplate.setAuditStatus(TemplateAuditStatus.TEMPLATE_WAIT.getCode().toString());
            niuSmsTemplate.setTemplateId(temId.toString());
            niuSmsTemplate.setReportInfo(JSONUtil.toJsonStr(result));
            niuSmsTemplate.setCreateTime(System.currentTimeMillis() / 1000);
            niuSmsTemplate.setUpdateTime(System.currentTimeMillis() / 1000);
            niuSmsTemplateMapper.insert(niuSmsTemplate);
        } else {
            NiuSmsTemplate niuSmsTemplate = modelInfo.get(0);
            niuSmsTemplate.setAuditStatus(TemplateAuditStatus.TEMPLATE_WAIT.getCode().toString());
            niuSmsTemplate.setTemplateId(temId.toString());
            niuSmsTemplate.setReportInfo(JSONUtil.toJsonStr(result));
            niuSmsTemplate.setUpdateTime(System.currentTimeMillis() / 1000);
            niuSmsTemplateMapper.updateById(niuSmsTemplate);
        }

        return result != null ? result : new JSONObject();
    }

    @Override
    public void templateDelete(String username, String templateId) {
        JSONObject config = getConfig(false);
        Map<String, Object> deleteBody = new HashMap<>();
        long time = DateUtil.currentSeconds();
        deleteBody.put("tKey", time);
        deleteBody.put("password", DigestUtil.md5Hex(DigestUtil.md5Hex(config.getStr("password")) + time));
        deleteBody.put("username", username);
        deleteBody.put("temId", templateId);
        try {
            sendHttp(TEMPLATE_DELETE, deleteBody);
            niuSmsTemplateMapper.delete(new QueryWrapper<NiuSmsTemplate>().eq("template_id", templateId));
        } catch (Exception e) {
            log.error("删除模版失败异常信息:{}，", e.getMessage());
            throw new AdminException("删除模版失败");
        }
    }

    @Override
    public NiuSmsTemplate templateInfo(String username, String smsType, String templateKey) {
        NiuSmsTemplate niuSmsTemplate = niuSmsTemplateMapper.selectOne(new LambdaQueryWrapper<NiuSmsTemplate>()
                .eq(NiuSmsTemplate::getSiteId, RequestUtils.siteId())
                .eq(NiuSmsTemplate::getSmsType, smsType)
                .eq(NiuSmsTemplate::getUsername, username)
                .eq(NiuSmsTemplate::getTemplateKey, templateKey));
        if (ObjectUtil.isEmpty(niuSmsTemplate)){
            throw new AdminException("短信模版暂未报备");
        }
        String orderCreateUrl = String.format(TEMPLATE_INFO_URL, username);
        Map<String, Object> templateInfoParam = new HashMap<>();
        templateInfoParam.put("tem_id", niuSmsTemplate.getTemplateId());
        try {
            JSONObject jsonObject = NiucloudUtils.Niucloud.get(orderCreateUrl, templateInfoParam);
            JSONObject result = jsonObject.getJSONObject("data");
            //删除null值 防止序列化报错
            JacksonUtils.removeNull(result);
            String auditStatus = result.containsKey("auditResult") && ObjectUtil.isNotEmpty(result.getJSONObject("auditResult"))
                    ? result.getStr("auditResult") : niuSmsTemplate.getAuditStatus();
            niuSmsTemplate.setAuditStatus(auditStatus);
            niuSmsTemplateMapper.updateById(niuSmsTemplate);
            return niuSmsTemplate;
        } catch (Exception e) {
            log.error("获取模版信息失败异常信息:{}", e.getMessage());
            throw new AdminException("获取模版信息失败");
        }
    }

    public JSONObject sendHttp(String url, Map<String, Object> body){
        HttpResponse response = HttpRequest.post(url).body(JSONUtil.toJsonStr(body)).execute();
        if (!response.isOk()) {
            throw new AdminException("HTTP请求失败，状态码: " + response.getStatus());
        }

        JSONObject resJson = JSONUtil.parseObj(response.body());
        if (resJson.getInt("code") != 200) {
            throw new AdminException(resJson.getStr("msg"));
        }
        return resJson;
    }


    private void execSync(String smsType, String username, List<TemplateListVo> templateList, Integer page) {
        Map<String, TemplateListVo> nameTemplateMap = templateList.stream().collect(Collectors.toMap(TemplateListVo::getName, i -> i));
        Integer siteId = RequestUtils.siteId();
        String templateListUrl = String.format(TEMPLATE_LIST_URL, username);
        Map<String, Object> templateListUrlParam = new HashMap<>();
        templateListUrlParam.put("page", page);
        templateListUrlParam.put("size", 100);
        JSONObject jsonObject = NiucloudUtils.Niucloud.get(templateListUrl, templateListUrlParam);
        JSONObject data = jsonObject.getJSONObject("data");
        JSONObject pageObj = data.getJSONObject("page");
        Integer total = pageObj.getInt("total");
        Integer pageVal = pageObj.getInt("currentPage");
        List<Map<String, Object>> templates =(List<Map<String, Object>>) data.get("templates");
        List<NiuSmsTemplate> batchInsert = new ArrayList<>();

        for (Map<String, Object> template : templates) {

            String temId = String.valueOf(template.get("temId"));
            String temName = String.valueOf(template.get("temName"));

            // 检查模板是否在项目中配置
            if (!nameTemplateMap.containsKey(temName)) {
                continue;
            }

            // 获取template_key
            String templateKey;
            if (template.containsKey("extend") && ObjectUtil.isNotEmpty(template.get("extend")) && !"null".equals(String.valueOf(template.get("extend")))) {
                Map<String, Object> extend = (Map<String, Object>) template.get("extend");
                templateKey = String.valueOf(extend.getOrDefault("template_key", ""));
            } else {
                TemplateListVo templateListVo = nameTemplateMap.get(temName);
                templateKey = templateListVo.getKey();
            }

            // 查询数据库
            List<NiuSmsTemplate> modelInfo = niuSmsTemplateMapper.selectList(new QueryWrapper<NiuSmsTemplate>()
                    .eq("sms_type", smsType)
                    .eq("username", username)
                    .eq("site_id", siteId)
                    .eq("template_key", templateKey));

            NiuSmsTemplate niuSmsTemplate = new NiuSmsTemplate();
            niuSmsTemplate.setSiteId(siteId);
            niuSmsTemplate.setSmsType(smsType);
            niuSmsTemplate.setUsername(username);
            niuSmsTemplate.setTemplateKey(templateKey);
            niuSmsTemplate.setTemplateContent(String.valueOf(template.get("temContent")));
            niuSmsTemplate.setParamJson(String.valueOf(template.get("paramJson")));
            niuSmsTemplate.setTemplateType(String.valueOf(template.get("temType")));
            niuSmsTemplate.setAuditStatus(String.valueOf(template.get("auditResult")));
            niuSmsTemplate.setAuditMsg(String.valueOf(template.get("auditMsg")));
            niuSmsTemplate.setTemplateId(temId);
            niuSmsTemplate.setReportInfo(String.valueOf(template));
            niuSmsTemplate.setCreateTime(((Number) template.get("createTime")).longValue() / 1000);
            niuSmsTemplate.setUpdateTime(System.currentTimeMillis() / 1000);

            if (CollectionUtils.isEmpty(modelInfo)) {
                batchInsert.add(niuSmsTemplate);
            } else {
                niuSmsTemplate.setId(modelInfo.get(0).getId());
                niuSmsTemplateMapper.updateById(niuSmsTemplate);
            }
        }
        // 批量插入
        if (!batchInsert.isEmpty()) {
            niuSmsTemplateMapper.insert(batchInsert);
        }

        // 递归处理下一页
        if (total > 100 * pageVal) {
            execSync(smsType, username, templateList, pageVal + 1);
        }
    }

    private List<Object> delSign(String username, SignDeleteParam param) {
        long time = DateUtil.currentSeconds();
        Map<String, Object> body = new HashMap<>();
        body.put("tKey", time);
        body.put("password", DigestUtil.md5Hex(DigestUtil.md5Hex(param.getPassword()) + time));
        body.put("username", username);
        body.put("remark", "");
        body.put("signatureList", param.getSignatures());
        JSONObject resJson = sendHttp(SIGN_DEL, body);
        return resJson.get("failList", List.class);
    }

    private JSONObject getConfig(boolean isAll) {
        JSONObject config = coreConfigService.getConfigValue(RequestUtils.siteId(), "SMS");
        if (isAll) {
            return config;
        }
        return config.getJSONObject(NIUYUN);
    }


    public void setConfig(RegisterAccountParam param) {
        JSONObject config = getConfig(true);
        config.put("default", ObjectUtil.isNotEmpty(param.getDefaultVal()) ? param.getDefaultVal() : config.getOrDefault("default", ""));
        JSONObject niuSmsConfig = config.getJSONObject(NIUYUN);
        Map<String, Object> newNiuSmsConfig = new HashMap<>();
        newNiuSmsConfig.put("username", ObjectUtil.isNotEmpty(param.getUsername()) ? param.getUsername() : niuSmsConfig != null ? niuSmsConfig.getOrDefault("username", "") : "");
        newNiuSmsConfig.put("password", ObjectUtil.isNotEmpty(param.getPassword()) ? param.getPassword() : niuSmsConfig != null ? niuSmsConfig.getOrDefault("password", "") : "");
        newNiuSmsConfig.put("signature", ObjectUtil.isNotEmpty(param.getSignature()) ? param.getSignature() : niuSmsConfig != null ? niuSmsConfig.getOrDefault("signature", "") : "");
        config.put(NIUYUN, newNiuSmsConfig);
        coreConfigService.setConfig(RequestUtils.siteId(), "SMS", config);
    }
}
