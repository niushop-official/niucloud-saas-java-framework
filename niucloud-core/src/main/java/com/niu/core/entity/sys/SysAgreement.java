package com.niu.core.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 协议实体
 */
@Data
public class SysAgreement implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /** 站点id */
    private Integer siteId;

    /** 协议关键字 */
    private String agreementKey;

    /** 协议标题 */
    private String title;

    /** 协议内容 */
    private String content;

    /** 添加时间 */
    private Long createTime;

    /** 修改时间 */
    private Long updateTime;

}