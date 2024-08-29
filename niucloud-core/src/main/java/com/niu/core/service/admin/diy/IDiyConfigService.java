package com.niu.core.service.admin.diy;


import com.niu.core.entity.diy.BottomConfigValue;
import com.niu.core.service.admin.diy.param.SetBottomConfigParam;
import com.niu.core.service.core.diy.ICoreDiyConfigService;
import com.niu.core.service.core.diy.param.StartUpPageConfigParam;
import com.niu.core.service.core.diy.vo.BottomConfigVo;
import com.niu.core.service.core.diy.vo.StartUpPageConfigVo;

import javax.annotation.Resource;
import java.util.List;

/**
 * 自定义配置接口
 */
public interface IDiyConfigService {

    List<BottomConfigVo> getBottomList();

    BottomConfigVo getBottomConfig(String key);

    void setBottomConfig(SetBottomConfigParam param);

}
