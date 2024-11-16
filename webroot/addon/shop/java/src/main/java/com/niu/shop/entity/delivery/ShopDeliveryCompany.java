package com.niu.shop.entity.delivery;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 【请填写功能名称】实体
 */
@Data
public class ShopDeliveryCompany implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="company_id", type= IdType.AUTO)
    private Integer companyId; // 
    private Integer siteId; // 站点id
    private String companyName; // 物流公司名称
    private String logo; // 物流公司logo
    private String url; // 物流公司网站
    private String expressNo; // 物流公司编号(用于物流跟踪)
    private String expressNoElectronicSheet; // 物流公司编号(用于电子面单)
    private Integer electronicSheetSwitch; // 是否支持电子面单（0：不支持，1：支持）
    private String printStyle; // 电子面单打印模板样式，json字符串
    private String expType; // 物流公司业务类型，json字符串
    private Long createTime; // 
    private Long updateTime; // 

}