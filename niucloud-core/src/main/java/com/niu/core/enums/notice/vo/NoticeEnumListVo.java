package com.niu.core.enums.notice.vo;
import cn.hutool.json.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * SysNotice视图
 */
@Data
public class NoticeEnumListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String addon;
    private String key;
    private Integer receiver_type;
    private String name;
    private String title;
    private Boolean async;
    private JSONObject variable;
    private List<String> support_type;
    Map<String, JSONObject> support_type_map;

}
