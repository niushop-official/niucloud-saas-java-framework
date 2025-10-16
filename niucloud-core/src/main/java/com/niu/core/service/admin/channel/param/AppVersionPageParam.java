package com.niu.core.service.admin.channel.param;

import com.niu.core.common.domain.PageParam;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * APP版本分页查询参数
 */
@Data
public class AppVersionPageParam {
    
    /**
     * 平台类型
     */
    private Integer platform;

}