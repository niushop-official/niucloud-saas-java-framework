package com.niu.core.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class NiuSmsTemplate {
    /**
     * 主键ID
     */
    @TableId(value="id", type= IdType.AUTO)
    public Integer id;

    /**
     * 站点ID
     */
    public Integer siteId;

    /**
     * 短信服务商类型 niuyun-牛云 aliyun-阿里云 tencent-腾讯
     */
    public String smsType;

    /**
     * 子账号名称
     */
    public String username;

    /**
     * 模板key
     */
    public String templateKey;

    /**
     * 模板id
     */
    public String templateId;

    /**
     * 模板类型
     */
    public String templateType;

    /**
     * 模板内容
     */
    public String templateContent;

    /**
     * 参数变量
     */
    public String paramJson;

    /**
     * 上下架状态
     */
    public String status;

    /**
     * 报备、审核状态
     */
    public String auditStatus;

    /**
     * 审核结果/拒绝原因
     */
    public String auditMsg;

    /**
     * 报备、审核信息
     */
    public String reportInfo;

    /**
     * 创建时间
     */
    public Long createTime;

    /**
     * 修改时间
     */
    public Long updateTime;

}
