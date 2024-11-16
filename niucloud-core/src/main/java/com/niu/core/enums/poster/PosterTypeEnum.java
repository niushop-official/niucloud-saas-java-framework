package com.niu.core.enums.poster;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.json.JsonModuleLoader;

import java.util.stream.Collectors;

public class PosterTypeEnum {

    public static JSONArray getType(){
        return JsonModuleLoader.build().mergeResultSet("poster/type.json");
    }

    public static JSONObject getType(String type){
        JSONArray posterType = getType();
        if (posterType.size() > 0) {
            for (int i = 0; i < posterType.size(); i++) {
                JSONObject item = posterType.getJSONObject(i);
                if (item.getStr("type", "").equals(type)) return item;
            }
        }
        return null;
    }
}
