package com.niu.core.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 小票打印机实体
 */
@Data
public class SysPrinter implements Serializable {

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
     * 创建时间
     */
    private Long createTime;

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
    @TableId(value = "printer_id", type = IdType.AUTO)
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
    @TableField("`trigger`")
    private String trigger;

    /**
     * 修改时间
     */
    private Long updateTime;

    /**
     * 打印模板数据，json格式
     */
    private String value;

}