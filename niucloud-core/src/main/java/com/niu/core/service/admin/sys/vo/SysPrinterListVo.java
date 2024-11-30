package com.niu.core.service.admin.sys.vo;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 小票打印机列表
 */
@Data
public class SysPrinterListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 开发者密钥
     */
    private String apikey;

    /**
     * 设备品牌（易联云，365，飞鹅）
     */
    private String brand;


    /**
     * 开发者id
     */
    private String openId;

    /**
     * 纸张宽度
     */
    private String printWidth;

    /**
     * 打印机编号
     */
    private String printerCode;

    /**
     *
     */
    private Integer printerId;

    /**
     * 打印机秘钥
     */
    private String printerKey;

    /**
     * 打印机名称
     */
    private String printerName;

    /**
     * 站点id
     */
    private Integer siteId;

    /**
     * 状态（0，关闭，1：开启）
     */
    private Integer status;

    /**
     * 小票打印模板类型，多个逗号隔开
     */
    private String templateType;

    /**
     * 触发打印时机
     */
    private List<String> trigger;


    /**
     * 打印模板数据，json格式
     */
    private JSONObject value;

    private String brandName;

}
