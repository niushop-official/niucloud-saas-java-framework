package com.niu.core.common.utils.json;

import com.alibaba.fastjson2.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HttpJson {

    /**
     * @param response
     * @param o
     * @throws IOException
     */
    public static void json(HttpServletResponse response, Object o) throws IOException {
        String json = JSONObject.toJSONString(o);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write(json);
    }

}
