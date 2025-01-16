package com.niu.shop.service.admin.order.vo;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.io.Serializable;

/**
 * ShopOrderBatchDelivery列表视图
 */
@Data
public class ShopOrderBatchDeliveryListVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;  // id
    private Integer siteId;  // 站点id
    private Integer mainId;  // 操作人id
    private Integer status;  // 状态 进行中  已完成  已失败
    private String statusName;  // 状态 进行中  已完成  已失败
    private String type;  // 操作类型
    private String typeName;
    private Integer totalNum;  // 总发货单数
    private Integer successNum;  // 成功发货单数
    private Integer failNum;  // 失败发货单数
    private JSONObject data;  // 导入文件的路径
    private String output;  // 对外输出记录
    private String failOutput;  // 失败记录
    private String failRemark;  // 失败原因
    private String createTime; // 创建时间
    private String updateTime; // 操作时间
    private SysUser user;

    @Data
    public static class SysUser {
        private Integer uid;
        private String username;
        private String headImg;
        private String realName;
    }
}
