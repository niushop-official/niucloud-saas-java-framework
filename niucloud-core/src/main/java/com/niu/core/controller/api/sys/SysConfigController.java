package com.niu.core.controller.api.sys;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.event.sys.InitWapEvent;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.admin.diy.IDiyThemeService;
import com.niu.core.service.api.diy.IDiyService;
import com.niu.core.service.api.diy.param.DiyTabbarListParam;
import com.niu.core.service.api.login.ILoginService;
import com.niu.core.service.api.member.IMemberLevelService;
import com.niu.core.service.api.member.param.MemberLevelParam;
import com.niu.core.service.api.sys.ISysConfigService;
import com.niu.core.service.api.sys.param.*;
import com.niu.core.service.api.sys.vo.SysInitVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.site.ICoreSiteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
public class SysConfigController {

    @Resource
    private ISysConfigService sysConfigService;

    @Resource
    private ILoginService loginService;

    @Resource
    private IDiyService diyService;

    @Resource
    private ICoreSiteService coreSiteService;

    @Resource
    private IMemberLevelService memberLevelService;

    @Resource
    private IDiyThemeService diyThemeService;

    @Resource
    private MemberMapper memberMapper;

    /**
     * 版权信息
     *
     * @param param
     * @return
     */
    @GetMapping("/copyright")
    public Result<?> copyright(SysConfigCopyrightParam param) {
        return Result.success(sysConfigService.copyright(param));
    }

    /**
     * 站点信息
     *
     * @param param
     * @return
     */
    @GetMapping("/site")
    public Result<?> site(SysConfigSiteParam param) {
        return Result.success(sysConfigService.site(param));
    }

    /**
     * 场景域名
     *
     * @param param
     * @return
     */
    @GetMapping("/scene_domain")
    public Result<?> sceneDomain(SysConfigSceneDomainParam param) {
        return Result.success(sysConfigService.sceneDomain(param));
    }

    /**
     * 获取地图设置
     *
     * @return
     */
    @GetMapping("/map")
    public Result<?> map(SysConfigMapParam param) {
        return Result.success(sysConfigService.map(param));
    }

    /**
     * 获取初始化数据信息
     *
     * @param url
     * @return
     */
    @GetMapping("/init")
    public Result init(@RequestParam(name = "url", defaultValue = "") String url) {
        SysInitVo vo = new SysInitVo();

        vo.setTabbarList(diyService.tabbarList(new DiyTabbarListParam()));
        vo.setMapConfig(sysConfigService.map(new SysConfigMapParam()));
        vo.setLoginConfig(loginService.getLoginConfig());
        vo.setSiteInfo(coreSiteService.getSiteCache(RequestUtils.siteId()));
        vo.setMemberLevel(memberLevelService.list(new MemberLevelParam()));
        vo.setThemeList(diyThemeService.getDiyTheme());

        if (ObjectUtil.isEmpty(vo.getSiteInfo().getSiteId())) {
            InitWapEvent event = new InitWapEvent();
            event.setSiteId(RequestUtils.siteId());
            EventAndSubscribeOfPublisher.publishAll(event);
        }

        return Result.success(vo);
    }

    /**
     * 获取公众号用户是否绑定手机
     */
    @GetMapping("/member_mobile_exist")
    public Result<?> getMemberMobileExist(@RequestParam(value = "openid", defaultValue = "") String openid, @RequestHeader(value = "Channel", defaultValue = "h5") String channel) {
        return Result.success(sysConfigService.getMemberMobileExist(openid, channel));
    }

//    /*
//     * 获取用户是否绑定手机号
//     *
//     * @param sysMemberMobileParam
//     * @return
//     */
//    @GetMapping("/member_mobile_exist")
//    public Result getMemberMobileExist(SysMemberMobileParam sysMemberMobileParam) {
//        // 根据渠道确定openid字段
//        String openidField;
//        String channel = RequestUtils.channel();
//        switch (channel) {
//            case "wechat":
//                openidField = "wx_openid";
//                break;
//            case "weapp":
//                openidField = "weapp_openid";
//                break;
//            default:
//                openidField = "";
//                break;
//        }
//
//        // 初始化返回结果
//        Map<String, Object> res = new HashMap<>();
//        res.put("member_mobile_exist", 0);
//
//        // 检查用户是否绑定手机号
//        if (sysMemberMobileParam.getOpenid() != null && !sysMemberMobileParam.getOpenid().isEmpty()) {
//            if (openidField != null && !openidField.isEmpty()) {
//                // 构造查询条件
//                QueryWrapper queryWrapper = new QueryWrapper();
//                queryWrapper.eq(openidField, sysMemberMobileParam.getOpenid());
//                queryWrapper.ne("mobile", "");
//
//                // 查询用户数量
//                Long count = memberMapper.selectCount(queryWrapper);
//                res.put("member_mobile_exist", count > 0 ? 1 : 0);
//            }
//        }
//
//        return Result.success(res);
//    }

}
