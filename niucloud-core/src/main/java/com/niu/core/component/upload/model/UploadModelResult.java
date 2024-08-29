package com.niu.core.component.upload.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UploadModelResult {

    private boolean result;

    // 原始文件
    private String originalFilename;

    // 文件大小
    private Long size;

    // 访问地址
    private String accessUrl;

    // 上传的方式
    private String uploadMethod;

    public UploadModelResult() {
        this.result = true;
    }

}
