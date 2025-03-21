package com.niu.core.common.component.pay;

import cn.hutool.json.JSONObject;
import com.niu.core.common.component.pay.model.*;
import com.niu.core.common.component.pay.model.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface IPayProvider {

    /**
     * @param initObject
     * @param payModel
     * @return
     */
    public PayModelResult pay(JSONObject initObject, PayModel payModel);

    /**
     * @param initObject
     * @param request
     * @param response
     * @param asyncNotifyModel
     * @return
     */
    public AsyncNotifyModelResult asyncNotify(JSONObject initObject, HttpServletRequest request, HttpServletResponse response, AsyncNotifyModel asyncNotifyModel);

    /**
     * @param initObject
     * @param payInfoModel
     */
    public PayInfoModelResult info(JSONObject initObject, PayInfoModel payInfoModel);

}
