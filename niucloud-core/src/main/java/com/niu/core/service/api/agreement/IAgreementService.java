package com.niu.core.service.api.agreement;

import com.niu.core.service.api.agreement.param.AgreementInfoParam;
import com.niu.core.service.api.agreement.vo.AgreementInfoVo;

public interface IAgreementService {

    AgreementInfoVo info(AgreementInfoParam param);

}
