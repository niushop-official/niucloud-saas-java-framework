package com.niu.core.service.admin.sys.vo;
import lombok.Data;

import java.io.Serializable;

/**
 * SysAttachment视图
 */
@Data
public class SysAttachmentInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer attId;  // 
    private Integer siteId;  // 站点id
    private String name;  // 附件名称
    private String realName;  // 原始文件名
    private String path;  // 完整地址
    private String dir;  // 附件路径
    private String attSize;  // 附件大小
    private String attType;  // 附件类型image,video
    private String storageType;  // 图片上传类型 local本地  aliyun  阿里云oss  qiniu  七牛 ....
    private Integer cateId;  // 相关分类
    private String url;  // 网络地址

}
