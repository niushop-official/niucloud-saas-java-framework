package com.niu.core.component.pay;

import cn.hutool.json.JSONObject;
import com.niu.core.component.pay.model.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
