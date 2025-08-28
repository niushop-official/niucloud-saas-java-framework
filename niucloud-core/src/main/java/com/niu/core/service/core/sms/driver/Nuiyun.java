package com.niu.core.service.core.sms.driver;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.util.StringUtil;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.loader.notice.NoticeDataVo;
import com.niu.core.common.loader.sms.BaseSms;
import com.niu.core.common.loader.sms.SendResultVo;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.sys.NiuSmsTemplate;
import com.niu.core.enums.notice.TemplateParamsTypeEnum;
import com.niu.core.enums.sys.SmsStatusEnum;
import com.niu.core.mapper.sys.NiuSmsTemplateMapper;
import com.niu.core.service.core.notice.vo.NoticeInfoVo;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class Nuiyun extends BaseSms {

    private String username;
    private String password;
    private String signature;

    private NiuSmsTemplateMapper niuSmsTemplateMapper() {
        return SpringContext.bean(NiuSmsTemplateMapper.class);
    }

    // 发送URL常量
    private static final String SEND_URL = "https://api-shss.zthysms.com/v2/sendSmsTp";


    public Nuiyun(JSONObject config) throws Exception {
        super(config);
        username = config.getStr("username");
        password = config.getStr("password");
        signature = config.getStr("signature");

        log.info("牛运短信 init " + JSONUtil.toJsonStr(config));
    }

    @Override
    public SendResultVo send(NoticeInfoVo notice, NoticeDataVo noticeData) {
        log.info("牛运短信 发送开始 {}" , noticeData.getVars().toString());

        // 检查签名配置
        if (!StringUtils.hasText(signature)) {
            throw new CommonException("签名未配置");
        }

        NiuSmsTemplate templateInfo = niuSmsTemplateMapper().selectOne(new LambdaQueryWrapper<NiuSmsTemplate>()
                .eq(NiuSmsTemplate::getTemplateKey, notice.getKey())
                .eq(NiuSmsTemplate::getSiteId, RequestUtils.siteId())
                .eq(NiuSmsTemplate::getUsername, username));
        log.info("牛云短信模板：{}" , templateInfo.toString());

        // 检查模板是否存在
        if (ObjectUtil.isEmpty(templateInfo)) {
            throw new CommonException("模版未报备");
        }

        // 检查审核状态
        String auditStatus = templateInfo.getAuditStatus();
        if (StringUtil.isEmpty(auditStatus) || !"2".equals(auditStatus)) {
            throw new CommonException("模版审核未通过");
        }

        // 格式化参数
        Map<String, Object> formattedData = formatParams(noticeData.getVars(), templateInfo);
        // 构建请求参数
        Map<String, Object> params = new HashMap<>();

        // 构建records数组
        List<Map<String, Object>> records = new ArrayList<>();
        Map<String, Object> record = new HashMap<>();
        record.put("mobile", noticeData.getMobile());
        record.put("tpContent", formattedData);
        records.add(record);

        params.put("records", records);
        params.put("tpId", templateInfo.getTemplateId());
        params.put("username", username);

        long tKey = System.currentTimeMillis() / 1000;
        params.put("tKey", tKey);

        // 计算密码：md5(md5(password) + tKey)
        String md5Password = DigestUtils.md5Hex(password);
        String finalPassword = DigestUtils.md5Hex(md5Password + tKey);
        params.put("password", finalPassword);

        params.put("signature", signature);

        log.info("牛云短信发送参数 params {}" , JSONUtil.toJsonStr(params));
        SendResultVo sendResult = new SendResultVo();
        try {
            // 发送HTTP请求
            String response = HttpRequest.post(SEND_URL)
                    .body(JSONUtil.toJsonStr(params))
                    .execute()
                    .body();

            log.info("牛云短信返回参数 res  {}", response);

            JSONObject result = JSONUtil.parseObj(response);
            Integer code = result.getInt("code");

            if (code != 200) {
                sendResult.setStatus(SmsStatusEnum.FAIL);
                sendResult.setFailReason(result.getStr("msg"));
            } else {
                sendResult.setStatus(SmsStatusEnum.SUCCESS);
            }
        } catch (Exception e) {
            log.error("短信发送失败", e);
            sendResult.setStatus(SmsStatusEnum.FAIL);
            sendResult.setFailReason(e.getMessage());
        }
        return sendResult;
    }


    /**
     * 格式化参数
     */
    private Map<String, Object> formatParams(Map<String, Object> data, NiuSmsTemplate templateInfo) {
        String paramJson = templateInfo.getParamJson();
        JSONObject paramsJson = JSONUtil.parseObj(paramJson);
        List<Map<String, Object>> paramsTypeArr = TemplateParamsTypeEnum.getApiParamsType();

        // 将参数类型数组转换为map，方便查找
        Map<String, Map<String, Object>> typeMap = new HashMap<>();
        for (Map<String, Object> type : paramsTypeArr) {
            typeMap.put(type.get("type").toString(), type);
        }

        Map<String, Object> result = new HashMap<>();

        for (String param : paramsJson.keySet()) {
            String validateType = paramsJson.getStr(param);
            Object valueObj = data.get(param);

            if (valueObj == null) {
                log.error("牛运短信 参数缺失 param:{}" , param);
                throw new CommonException("NY:参数异常，无法发送");
            }

            String value = valueObj.toString();

            // 获取类型配置
            Map<String, Object> typeConfig = typeMap.get(validateType);
            String pattern = typeConfig != null ? (String) typeConfig.get("rule") : "";
            int max = typeConfig != null ? Integer.parseInt(typeConfig.get("max").toString()) : 35;
            int min = typeConfig != null ? Integer.parseInt(typeConfig.get("min").toString()) : 1;

            // 对中文和其他类型特殊处理
            if (StringUtils.hasText(pattern) &&
                    (TemplateParamsTypeEnum.CHINESE.getType().equals(validateType) ||
                            TemplateParamsTypeEnum.OTHERS.getType().equals(validateType))) {

                value = value.replace(" ", "").replace(".", "");
                value = value.replaceAll(pattern, "");

                // 检查长度
                int length = value.length();
                if (length >= min && length <= max) {
                    // 长度合法，保留过滤后的字符串
                } else if (length > max) {
                    // 长度非法，截取到最大长度
                    value = value.substring(0, max);
                } else {
                    // 长度不足，设置为空
                    value = "";
                }
            }

            if (!StringUtils.hasText(value)) {
                log.error("牛运短信 参数异常，无法发送 param:{}" , param);
                throw new CommonException("NY:参数异常，无法发送");
            }

            result.put(param, value);
        }

        return result;
    }


}
