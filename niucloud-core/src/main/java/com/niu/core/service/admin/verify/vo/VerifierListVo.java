package com.niu.core.service.admin.verify.vo;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.sys.VerifyTypeEnum;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Verifier列表视图
 */
@Data
public class VerifierListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 
    private Integer siteId;  // 站点id
    private Integer memberId;  // 会员id
    private Integer uid;  // 用户id
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 添加时间
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String verifyType;  // 核销类型
    private MemberBriefInfoVo member; // 会员信息
    private List<JSONObject> verifyTypeArray = new LinkedList<>();  // 核销类型

    public List<JSONObject> getVerifyTypeArray() {
        JSONArray verifyType = JSONUtil.parseArray(this.verifyType);
        List<JSONObject> verifyTypeArray = new LinkedList<>();

        JSONObject verifyTypeEnum = VerifyTypeEnum.getType();
        for (String key: verifyTypeEnum.keySet()) {
            if (verifyType.indexOf(key) != -1) {
                verifyTypeArray.add(new JSONObject().set("verify_type", key).set("verify_type_name", verifyTypeEnum.getByPath(key + ".name").toString()));
            }
        }

        return verifyTypeArray;
    }
}
