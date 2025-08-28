package com.niu.core.service.admin.diy;


import cn.hutool.json.JSONObject;
import com.niu.core.service.admin.diy.param.DiyThemeColorParam;
import com.niu.core.service.admin.diy.param.DiyThemeParam;
import com.niu.core.service.admin.diy.param.DiyThemeSetParam;
import com.niu.core.service.admin.diy.param.DiyThemeTitleParam;
import com.niu.core.service.admin.diy.vo.DiyThemeInfoVo;

import java.util.List;

/**
 * 自定义页面主题接口
 */
public interface IDiyThemeService {

    JSONObject getDiyTheme();

    void setDiyTheme(DiyThemeSetParam data);

    List<DiyThemeInfoVo> getDefaultThemeColor(DiyThemeColorParam data);

    void addDiyTheme(DiyThemeParam diyThemeParam);

    void editDiyTheme(Integer id, DiyThemeParam diyThemeParam);

    void delDiyTheme(Integer id);

    Boolean checkDiyThemeTitleUnique(DiyThemeTitleParam diyThemeTitleParam);
}
