package com.niu.core.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 附件管理实体
 */
@Data
public class SysAttachment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="att_id", type= IdType.AUTO)
    private Integer attId;

    /** 站点id */
    private Integer siteId;

    /** 附件名称 */
    private String name;

    /** 原始文件名 */
    private String realName;

    /** 完整地址 */
    private String path;

    /** 附件路径 */
    private String dir;

    /** 附件大小 */
    private Long attSize;

    /** 附件类型image,video */
    private String attType;

    /** 图片上传类型 local本地  aliyun  阿里云oss  qiniu  七牛 .... */
    private String storageType;

    /** 相关分类 */
    private Integer cateId;

    /** 上传时间 */
    private Long createTime;

    private Long updateTime;

    /** 网络地址 */
    private String url;

}