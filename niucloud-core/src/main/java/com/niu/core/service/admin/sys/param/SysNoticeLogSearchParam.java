package com.niu.core.service.admin.sys.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 通知记录参数
 */
@Data
public class SysNoticeLogSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String key;

    private String receiver;

    private String[] createTime;

}
