package com.niu.shop.service.admin.order.vo;

import com.niu.core.entity.sys.SysUser;
import lombok.Data;

import java.io.Serializable;

/**
 * ShopOrderBatchDelivery视图
 */
@Data
public class ShopOrderBatchDeliveryInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // id
    private Integer siteId;  // 站点id
    private Integer mainId;  // 操作人id
    private Integer status;  // 状态 进行中  已完成  已失败
    private String statusName;
    private String type;  // 操作类型
    private String typeName;
    private Integer totalNum;  // 总发货单数
    private Integer successNum;  // 成功发货单数
    private Integer failNum;  // 失败发货单数
    private String data;  // 导入文件的路径
    private String output;  // 对外输出记录
    private String failOutput;  // 失败记录
    private String failRemark;  // 失败原因
    private SysUser user;

}
