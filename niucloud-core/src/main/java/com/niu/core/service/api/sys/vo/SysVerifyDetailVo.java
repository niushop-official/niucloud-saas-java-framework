package com.niu.core.service.api.sys.vo;

import com.niu.core.entity.verify.Verify;
import com.niu.core.enums.sys.VerifyTypeEnum;
import lombok.Data;

@Data
public class SysVerifyDetailVo extends Verify {

    private String nickname;

    private String mobile;

    private String headimg;

    /**
     * @return
     */
    public String getTypeName() {
        return VerifyTypeEnum.getTypeByName(this.getType());
    }

}
