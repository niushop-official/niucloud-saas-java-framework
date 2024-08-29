package com.niu.core.common.utils.date;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtils {

    /**
     * 上传文件时的时间格式化
     *
     * @return
     */
    public static String getUploadFormat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMM/dd");
        return simpleDateFormat.format(new Date());
    }

}
