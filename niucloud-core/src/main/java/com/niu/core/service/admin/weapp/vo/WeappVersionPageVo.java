package com.niu.core.service.admin.weapp.vo;

import com.niu.core.common.domain.PageResult;
import com.niu.core.service.core.weapp.vo.WeappVersionVo;
import lombok.Data;

import java.util.ArrayList;

@Data
public class WeappVersionPageVo extends PageResult<WeappVersionListVo> {

    WeappVersionVo versionInfo;

    public Object getVersionInfo() {
        return versionInfo == null ? new ArrayList<>() : versionInfo;
    }
}
