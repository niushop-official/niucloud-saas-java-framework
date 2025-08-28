package com.niu.core.enums.notice;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum TemplateParamsTypeEnum {
    VALID_CODE("valid_code", "验证码", "4-6位纯数字", "/^\\d{4,6}$/", 4, 6),
    MOBILE_NUMBER("mobile_number", "手机号", "1-15位纯数字", "/^\\d{1,15}$/", 1, 15),
    OTHER_NUMBER("other_number", "其他号码", "1-32位字母+数字组合", "/^[a-zA-Z0-9]{1,32}$/", 1, 32),
    AMOUNT("amount", "金额", "支持数字或数字的中文 （壹贰叁肆伍陆柒捌玖拾佰仟万亿 圆元整角分厘毫）", "/^(?:\\d+|(?:[零壹贰叁肆伍陆柒捌玖拾佰仟万亿圆角分厘毫]+|圆|元|整)+)$/u", 0, 0),
    DATE("date", "日期", "符合时间的表达方式 也支持中文：2019年9月3日16时24分35秒", "", 0, 0),
    CHINESE("chinese", "中文", "1-32中文，支持中文圆括号()", "/^[\\p{Han}()（）]{1,32}$/u", 1, 32),
    OTHERS("others", "其他", "1-35个中文数字字母组合，支持中文符号和空格", "/^[\\p{Han}\\p{N}\\p{L}\\p{P}\\p{S}\\s]{1,35}$/u", 1, 35);

    private final String type;
    private final String name;
    private final String desc;
    private final String rule;
    private final int min;
    private final int max;

    public static List<Map<String, Object>> getApiParamsType() {
        List<Map<String, Object>> paramsTypes = new ArrayList<>();

        for (TemplateParamsTypeEnum templateParamsType : TemplateParamsTypeEnum.values()) {
            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put("name", templateParamsType.getName());
            paramsMap.put("type", templateParamsType.getType());
            paramsMap.put("desc", templateParamsType.getDesc());
            paramsMap.put("rule", templateParamsType.getRule());
            if (templateParamsType.getMin() != 0) {
                paramsMap.put("min", templateParamsType.getMin());
            }
            if (templateParamsType.getMax() != 0) {
                paramsMap.put("max", templateParamsType.getMax());
            }
            paramsTypes.add(paramsMap);
        }

        return paramsTypes;
    }
}
