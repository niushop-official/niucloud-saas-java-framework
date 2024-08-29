package com.niu.core.component.upload.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ThumbModel {

    // 文件位置
    private String filePath;

    // 类型
    private String type;

    /**
     * @return
     */
    public static ThumbModel build() {
        return new ThumbModel();
    }

    /**
     * @param filePath
     * @return
     */
    public static ThumbModel build(String filePath) {
        return build().setFilePath(filePath);
    }

    /**
     * @param type
     * @param location
     * @return
     */
    public static ThumbModel build(String type, String location) {
        return build().setType(type).setFilePath(location);
    }

}
