package com.niu.core.service.api.sys;

import com.niu.core.service.api.sys.param.FetchImageParam;
import com.niu.core.service.api.sys.vo.FetchImageVo;

public interface IFetchService {

    FetchImageVo image(FetchImageParam param);
}
