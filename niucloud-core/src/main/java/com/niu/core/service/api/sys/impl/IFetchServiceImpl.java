package com.niu.core.service.api.sys.impl;

import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.api.sys.IFetchService;
import com.niu.core.service.api.sys.param.FetchImageParam;
import com.niu.core.service.api.sys.vo.FetchImageVo;
import com.niu.core.service.core.upload.ICoreFetchService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class IFetchServiceImpl implements IFetchService {

    @Resource
    ICoreFetchService coreFetchService;

    @Override
    public FetchImageVo image(FetchImageParam param) {
        FetchImageVo vo = new FetchImageVo();
        String url = coreFetchService.image(param.getUrl(), RequestUtils.siteId());
        vo.setUrl(url);
        return vo;
    }
}
