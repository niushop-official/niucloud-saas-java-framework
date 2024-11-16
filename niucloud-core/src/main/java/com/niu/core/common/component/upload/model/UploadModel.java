package com.niu.core.common.component.upload.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
public class UploadModel {

    // 要上传的文件
    private MultipartFile uploadFile;

    // 上传的类型
    private String uploadType;

    // 上传文件的相对路径
    private String uploadFilePath;

    // 上传文件的名称
    private String uploadFileName;

    // 扩展的数据
    private Map<String, Object> extendData = new HashMap<>();

    /**
     * @param key
     * @param value
     * @return
     */
    public UploadModel addExtendData(String key, Object value) {
        extendData.put(key, value);
        return this;
    }

    /**
     * @param key
     * @param <T>
     * @return
     */
    public <T> T getExtendData(String key) {
        return (T) extendData.get(key);
    }

    /**
     * @return
     */
    public static UploadModel build() {
        return new UploadModel();
    }

    /**
     * @param uploadFile
     * @param uploadType
     * @return
     */
    public static UploadModel build(MultipartFile uploadFile, String uploadType) {
        return build().setUploadFile(uploadFile).setUploadType(uploadType);
    }

}
