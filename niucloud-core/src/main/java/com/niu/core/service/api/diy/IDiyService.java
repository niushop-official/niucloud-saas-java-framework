package com.niu.core.service.api.diy;

import cn.hutool.json.JSONObject;
import com.niu.core.service.api.diy.param.DiyInfoParam;
import com.niu.core.service.api.diy.param.DiyShareParam;
import com.niu.core.service.api.diy.param.DiyTabbarListParam;
import com.niu.core.service.api.diy.param.DiyTabbarParam;
import com.niu.core.service.api.diy.vo.DiyInfoVo;
import com.niu.core.service.api.diy.vo.DiyShareVo;
import com.niu.core.service.api.diy.vo.DiyTabbarInfoVo;
import com.niu.core.service.api.diy.vo.DiyTabbarVo;
import com.niu.core.service.core.diy.vo.BottomConfigVo;

import java.util.List;

public interface IDiyService {

    /**
     * 自定义页面信息
     *
     * @param param
     * @return
     */
    Object info(DiyInfoParam param);

    /**
     * 底部菜单信息
     *
     * @param param
     * @return
     */
    DiyTabbarVo tabbar(DiyTabbarParam param);

    /**
     * 底部菜单信息
     *
     * @param param
     * @return
     */
    List<BottomConfigVo> tabbarList(DiyTabbarListParam param);

    /**
     * 获取页面分享信息
     *
     * @param param
     * @return
     */
    JSONObject share(DiyShareParam param);

}
