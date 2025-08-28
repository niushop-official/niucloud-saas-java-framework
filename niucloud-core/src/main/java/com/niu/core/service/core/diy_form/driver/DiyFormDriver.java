package com.niu.core.service.core.diy_form.driver;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.RequestUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DiyFormDriver {

    public Object convert(String fieldValue, String scene) {
        if(ObjectUtil.isEmpty(fieldValue))  return new JSONObject();
        switch (scene) {
            case "FormRadio" -> {
                return JSONUtil.parseArray(fieldValue);
            }
            case "FormCheckbox" -> {
                return JSONUtil.parseArray(fieldValue);
            }
            case "FormDate" -> {return fieldValue;}
            case "FormDateScope" -> {
                return JSONUtil.parseObj(fieldValue);

            }
            case "FormTimeScope" -> {
               return JSONUtil.parseObj(fieldValue);
            }
            case "FormImage" -> {
                return JSONUtil.parseArray(fieldValue);
            }
            default -> {return fieldValue;}
        }
    }

    public Object render(String fieldValue, String scene) {
        if(ObjectUtil.isEmpty(fieldValue))  return "";

        String result = "";
        switch (scene) {
            case "FormRadio" -> {
                JSONArray fieldJson1 = JSONUtil.parseArray(fieldValue);
                if (fieldJson1.size() > 0 && fieldJson1.getJSONObject(0).containsKey("text")) {
                    result = fieldJson1.getJSONObject(0).getStr("text");
                } else {
                    result = "";
                }
            }
            case "FormCheckbox" -> {
                JSONArray fieldJson2 = JSONUtil.parseArray(fieldValue);
                List<String> resArray = new ArrayList<>();
                for (int i = 0; i < fieldJson2.size(); i++) {
                    if (fieldJson2.getJSONObject(i).containsKey("text")) {
                        resArray.add(fieldJson2.getJSONObject(i).getStr("text"));
                    }
                }
                result = String.join(",", resArray);
            }
            case "FormDate" -> result = fieldValue;
            case "FormDateScope" -> {
                JSONObject fieldJsonDate = JSONUtil.parseObj(fieldValue);
                result = fieldJsonDate.getJSONObject("start").getStr("date") + "-" + fieldJsonDate.getJSONObject("end").getStr("date");
            }
            case "FormTimeScope" -> {
                JSONObject fieldJsonTime = JSONUtil.parseObj(fieldValue);
                result = fieldJsonTime.getJSONObject("start").getStr("date") + "-" + fieldJsonTime.getJSONObject("end").getStr("date");
            }
            case "FormImage" -> {
                JSONArray imgArray = JSONUtil.parseArray(fieldValue);
                List<String> resImgArray = new ArrayList<>();
                for (int i = 0; i < imgArray.size(); i++) {
                    if (imgArray.getStr(i).startsWith("http")) {
                        resImgArray.add(imgArray.getStr(i));
                    } else {
                        resImgArray.add(RequestUtils.getRequestDomain() + "/" + imgArray.getStr(i));
                    }
                }
                result = String.join(",", resImgArray);
            }
            default -> result = fieldValue;
        }
        return result;
    }
}
