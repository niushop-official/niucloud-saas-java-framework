package com.niu.core.service.admin.generator.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 代码生成器字段
 */
@Data
public class GenerateColumnVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**id*/
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /**表id*/
    private Integer tableId;

    /**字段名称*/
    private String columnName;

    /**字段描述*/
    private String columnComment;

    /**字段类型*/
    private String columnType;

    /**是否必填 0-非必填 1-必填*/
    private Integer isRequired;

    /**是否主键 0-非主键 1-主键 */
    private Integer isPk;

    /**是否为插入字段 0-非自增 1-自增*/
    private Integer isInsert;

    /**是否为更新字段 0-不是 1-是*/
    private Integer isUpdate;

    /**是否为列表字段 0-不是 1-是*/
    private Integer isLists;

    /**是否为查询字段 0-不是 1-是*/
    private Integer isQuery;

    /**是否搜索字段*/
    private Integer isSearch;

    /**查询类型*/
    private String queryType;

    /**显示类型*/
    private String viewType;

    /**字典类型*/
    private String dictType;

    /**远程下拉关联应用*/
    private String addon;

    /**远程下拉关联model*/
    private String model;

    /**远程下拉标题字段*/
    private String labelKey;

    /**远程下拉value字段*/
    private String valueKey;

    /**创建时间*/
    private Long createTime;

    /**修改时间*/
    private Long updateTime;

    /**是否为软删除字段 0-不是 1-是*/
    private Integer isDelete;

    /**是否为排序字段 0-不是 1-是*/
    private Integer isOrder;

    /**验证类型*/
    private String validateType;

    private String viewMax;

    private String viewMin;

    private String maxNumber;

    private String minNumber;

    private Integer selectType;

}