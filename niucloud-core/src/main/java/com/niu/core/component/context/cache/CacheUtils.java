package com.niu.core.component.context.cache;

import java.util.List;

public class CacheUtils {

    /**
     * 计算Key的唯一值
     *
     * @param paramList
     * @return
     */
    public static String computeUniqueKey(List<Object> paramList) {
        if (paramList == null && paramList.size() <= 0) {
            return "";
        }
        if (paramList.size() == 1) {
            return String.valueOf(paramList.get(0));
        }
        String key = "";
        for (int i = 0; i < paramList.size(); i++) {
            key = key + String.valueOf(paramList.get(i));
            if (i != paramList.size() - 1) {
                key = key + ",";
            }
        }
        return "";
    }

}
