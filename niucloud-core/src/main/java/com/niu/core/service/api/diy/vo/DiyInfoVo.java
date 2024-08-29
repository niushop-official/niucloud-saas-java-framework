package com.niu.core.service.api.diy.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

@Data
public class DiyInfoVo {

    /** 站点id */
    private Integer siteId;

    /** 页面标题（用于前台展示） */
    private String title;

    /** 页面标识 */
    private String name;

    /** 页面模板 */
    private String type;

    /** 模板名称 */
    private String template;

    /** 页面展示模式，diy：自定义，fixed：固定 */
    private String mode;

    /** 页面数据，json格式 */
    private String value;

    /** 是否默认页面，1：是，0：否 */
    private Integer isDefault;

    /** 分享内容 */
    private String share;

    /** 访问量 */
    private Integer visitCount;
}
