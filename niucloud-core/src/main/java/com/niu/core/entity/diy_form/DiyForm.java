package com.niu.core.entity.diy_form;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.io.Serializable;
import lombok.Data;

/**
* 万能表单表实体
*/
@Data
public class DiyForm implements Serializable {

private static final long serialVersionUID = 1L;

    /** 表单id */
    @TableId(value="form_id", type= IdType.AUTO)
    private Integer formId;

    /** 站点id */
    private Integer siteId;

    /** 表单名称（用于后台展示） */
    private String pageTitle;

    /** 表单名称（用于前台展示） */
    private String title;

    /** 表单类型 */
    private String type;

    /** 状态（0，关闭，1：开启） */
    private Integer status;

    /** 模板名称 */
    private String template;

    /** 表单数据，json格式，包含展示组件 */
    private String value;

    /** 所属插件标识 */
    private String addon;

    /** 分享内容 */
    private String share;

    /** 表单填写总数量 */
    private Integer writeNum;

    /** 备注说明 */
    private String remark;

    /** 创建时间 */
    private Long createTime;

    /** 更新时间 */
    private Long updateTime;

}