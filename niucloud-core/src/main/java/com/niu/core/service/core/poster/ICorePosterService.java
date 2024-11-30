package com.niu.core.service.core.poster;

import cn.hutool.json.JSONArray;
import com.niu.core.service.core.poster.param.AddPosterParam;
import com.niu.core.service.core.poster.param.GetPosterParam;

public interface ICorePosterService {

    /**
     * 获取海报
     *
     * @param param
     */
    String get(GetPosterParam param);

    /**
     * @param addon
     * @param type
     * @return
     */
    JSONArray template(String addon, String type);

    void add(AddPosterParam param);
}
