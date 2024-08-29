package com.niu.core.service.core.diy;

import com.niu.core.entity.diy.BottomConfigValue;
import com.niu.core.service.core.diy.param.StartUpPageConfigParam;
import com.niu.core.service.core.diy.vo.BottomConfigVo;
import com.niu.core.service.core.diy.vo.StartUpPageConfigVo;

import java.util.List;

public interface ICoreDiyConfigService {

    List<BottomConfigVo> getBottomList();

    BottomConfigVo getBottomConfig(Integer siteId, String key);

    void setBottomConfig(Integer siteId, BottomConfigValue bottomConfigValue, String key);

    void setStartUpPageConfig(Integer siteId, StartUpPageConfigParam value, String name);

    StartUpPageConfigVo getStartUpPageConfig(Integer siteId, String name);
}
