package com.niu.core.common.component.upload.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DeleteModel {

    // 文件
    private String filePath;

    /**
     * @return
     */
    public static DeleteModel build() {
        return new DeleteModel();
    }

    /**
     * @param filePath
     * @return
     */
    public static DeleteModel build(String filePath) {
        return build().setFilePath(filePath);
    }

}
