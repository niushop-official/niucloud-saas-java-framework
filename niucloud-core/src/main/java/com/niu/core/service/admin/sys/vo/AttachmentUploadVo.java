package com.niu.core.service.admin.sys.vo;
import lombok.Data;

import java.io.Serializable;

/**
 * SysAttachment视图
 */
@Data
public class AttachmentUploadVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer attId;  // 
    private String url;  // 网络地址

}
