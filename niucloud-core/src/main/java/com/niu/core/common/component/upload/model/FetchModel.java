package com.niu.core.common.component.upload.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FetchModel {

    // 要上传的文件地址
    private String url;

    // 上传文件的相对路径
    private String uploadFilePath;

    // 上传文件的名称
    private String uploadFileName;


}
