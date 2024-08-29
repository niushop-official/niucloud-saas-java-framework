package com.niu.core.service.api.sys.param;

import com.niu.core.service.api.base.ApiBaseParam;
import lombok.Data;

@Data
public class SysAreaAddressByLatlngParam implements ApiBaseParam {

    private String latlng;

    //是否返回周边POI列表：1.返回；0不返回(默认)
    private Integer getPoi;

}
