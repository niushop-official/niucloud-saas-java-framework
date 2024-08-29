package com.niu.core.enums.member;

import cn.hutool.json.JSONArray;
import com.niu.core.common.utils.json.JsonModuleLoader;


public enum TestEnum {
    ;
    public static JSONArray getAccountType(){
        return JsonModuleLoader.load("loader/account_type.json");
    }
}
