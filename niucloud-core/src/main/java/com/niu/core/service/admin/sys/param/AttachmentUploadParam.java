package com.niu.core.service.admin.sys.param;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 地址参数
*/
@Data
public class AttachmentUploadParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private MultipartFile file;

    private Integer siteId;

    private String storageType = "";

    /** 相册分组id  */
    private Integer cateId = 0;

    /** 是否上传到相册  */
    private Integer isAttachment = 1;

    /** 附件类型  */
    private String attType = "";

    private String documentType = "";

    /** 上传文件路径 */
    private String dir = "";

    private String newFilename = "";
}
