package com.niu.core.service.api.member.vo;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.util.List;

@Data
public class MemberSignOperateVo {

    private String title;

    private String info;

    private JSONObject awards;

}
