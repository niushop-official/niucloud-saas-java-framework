package com.niu.core.service.admin.site.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 应用工具列表视图
 */
@Data
public class ShowAppListVo implements Serializable {


    private static final long serialVersionUID = 1L;

    private String title;
    private List<AppInfo> list;

    @Data
    public static class AppInfo{
        private static final long serialVersionUID = 1L;

        private String title;

        private String icon;

        private String url;

        private String desc;

        private String key;
    }



}
