package com.niu.core.service.admin.wechat.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * WechatReply列表视图
 */
@Data
public class WechatReplyListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 
    private String name;  // 规则名称
    private Integer siteId;  // 站点id
    private String keyword;  // 关键词
    private String replyType;  // 回复类型 subscribe-关注回复 keyword-关键字回复 default-默认回复
    private String matchingType;  // 匹配方式：full 全匹配；like-模糊匹配
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String content;
    private Integer sort;  // 排序
    private String createTime; // 创建时间
    private String updateTime; // 更新时间
    private String replyMethod;  // 回复方式 all 全部 rand随机

}
