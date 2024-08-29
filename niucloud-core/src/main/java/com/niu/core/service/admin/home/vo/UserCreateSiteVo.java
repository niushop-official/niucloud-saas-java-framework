package com.niu.core.service.admin.home.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 可选择的店铺套餐
 */
@Data
public class UserCreateSiteVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**主键id */
    private Integer id;

    /**套餐id */
    private Integer groupId;

    /**用户id */
    private Integer uid;

    /**数量 */
    private Integer num;

    /**周期 */
    private Integer month;

    /**套餐集合 */
    private SiteGroupVo siteGroup;



}
