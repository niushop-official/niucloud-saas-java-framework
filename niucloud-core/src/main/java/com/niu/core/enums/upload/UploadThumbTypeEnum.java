package com.niu.core.enums.upload;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 上传压缩枚举
 */

@Getter
@AllArgsConstructor
public enum UploadThumbTypeEnum {

    BIG("big", 1200, 1200),
    MID("mid", 800, 800),
    SMALL("small", 200, 200),
    ;

    private final String type;

    private final Integer width;

    private final Integer height;
}
