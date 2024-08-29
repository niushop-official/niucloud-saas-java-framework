package com.niu.core.service.admin.auth.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class EditAuthUserParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**名称 */
    private String realName;

    /**头像 */
    private String headImg;

    /**原始密码 */
    private String originalPassword;

    /**新密码 */
    private String password;
}
