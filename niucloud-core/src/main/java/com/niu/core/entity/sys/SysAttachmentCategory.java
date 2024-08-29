package com.niu.core.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 附件分类实体
 */
@Data
public class SysAttachmentCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    private Integer id; //

    /** 站点id */
    private Integer siteId;

    /** 父级ID */
    private Integer pid;

    /** 文件管理类型（image,video） */
    private String type;

    /** 分类名称 */
    private String name;

    /** 分类目录 */
    private String enname;

    /** 排序 */
    private Integer sort;

}