package com.niu.core.service.api.sys.vo;

import cn.hutool.json.JSONObject;
import com.niu.core.service.admin.sys.vo.SysMapVo;
import com.niu.core.service.api.member.vo.MemberLevelInfoVo;
import com.niu.core.service.core.diy.vo.BottomConfigVo;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import lombok.Data;
import com.niu.core.service.admin.member.vo.LoginConfigVo;

import java.util.List;

@Data
public class SysInitVo {

    private List<BottomConfigVo> tabbarList;
    private SysMapVo mapConfig;
    private SiteInfoVo siteInfo;
    private List<MemberLevelInfoVo> memberLevel;
    private LoginConfigVo loginConfig;
    private JSONObject themeList;
}
