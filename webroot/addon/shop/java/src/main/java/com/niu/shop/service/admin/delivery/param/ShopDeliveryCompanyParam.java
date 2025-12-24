package com.niu.shop.service.admin.delivery.param;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.Data;
import java.io.Serializable;
import jakarta.validation.constraints.*;

/**
 * 【请填写功能名称】参数
*/
@Data
public class ShopDeliveryCompanyParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "company_name参数缺失")
    @NotEmpty(message = "物流公司名称不能为空")
    private String companyName;

    private String logo = "";

    private String url = "";

    private String expressNo = "";

    private String expressNoElectronicSheet = "";

    private Integer electronicSheetSwitch = 0;

    private JSONArray printStyle = new JSONArray();

    private JSONArray expType = new JSONArray();

    private String kd100ExpressNo = ""; // 快递100:物流公司编号(用于物流跟踪)

    private String kd100ExpressNoElectronicSheet = ""; // 快递100:物流公司编号(用于电子面单)

}
