package com.niu.core.service.admin.auth.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
@Data
public class LoginConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer isCaptcha = 0; // 是否启用验证码
    private Integer isSiteCaptcha = 0; // 是否启用站点验证码
    private String bg = ""; // 平台端登录背景
    private String siteBg = ""; // 站点登录端背景

}
