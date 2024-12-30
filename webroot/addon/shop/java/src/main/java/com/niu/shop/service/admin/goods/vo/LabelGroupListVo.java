package com.niu.shop.service.admin.goods.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import java.io.Serializable;
import lombok.Data;
/**
* 商品标签分组表列表
*/
@Data
public class LabelGroupListVo implements Serializable {

private static final long serialVersionUID = 1L;

        /** 分组ID */
        private Integer groupId;

        /** 站点id */
        private Integer siteId;

        /** 分组名称 */
        private String groupName;

        /** 排序 */
        private Integer sort;




}
