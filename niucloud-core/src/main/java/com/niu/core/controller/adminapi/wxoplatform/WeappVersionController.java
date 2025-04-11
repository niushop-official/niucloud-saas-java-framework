package com.niu.core.controller.adminapi.wxoplatform;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.wxoplatform.IOplatformConfigService;
import com.niu.core.service.admin.wxoplatform.IWeappVersionService;
import com.niu.core.service.admin.wxoplatform.param.OplatformConfigParam;
import com.niu.core.service.admin.wxoplatform.vo.SiteGroupWeappVersionVo;
import com.niu.core.service.admin.wxoplatform.vo.WxOplatfromWeappVersionVo;
import com.niu.core.service.core.wxoplatform.vo.CoreOplatformStaticConfigVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.util.LinkedList;
import java.util.Map;

@RestController
@RequestMapping("adminapi/wxoplatform")
@SaCheckLogin
public class WeappVersionController {

    @Resource
    IWeappVersionService weappVersionService;


    /**
     * 获取最后一次提交记录
     *
     * @return
     */
    @GetMapping("/weapp/commit/last")
    public Result lastCommitRecord(){
        return Result.success(ObjectUtil.defaultIfNull(weappVersionService.getLastCommitRecord(), new LinkedList<>()));
    }

    /**
     * 获取小程序提交记录
     *
     * @param pageParam
     * @return
     */
    @GetMapping("/weapp/commit")
    public Result<PageResult<WxOplatfromWeappVersionVo>> commitRecord(@Validated PageParam pageParam){
        return Result.success(weappVersionService.list(pageParam));
    }

    /**
     * 平台提交小程序版本
     *
     * @return
     */
    @PostMapping("/weapp/version/commit")
    public Result weappCommit(@RequestBody Map<String, Object> params){
        Integer siteGroupId = ObjectUtil.isEmpty(params.get("site_group_id")) ? 0 : (Integer) params.get("site_group_id");
        weappVersionService.add(siteGroupId, siteGroupId == 0);
        return Result.success();
    }

    /**
     * 添加小程序版本
     *
     * @return
     */
    @PostMapping("/site/weapp/commit")
    public Result siteWeappCommit(){
        weappVersionService.siteWeappCommit();
        return Result.success();
    }


    /**
     * 按站点套餐获取提交记录
     *
     * @param pageParam
     * @return
     */
    @GetMapping("/sitegroup/commit")
    public Result<PageResult<SiteGroupWeappVersionVo>> getSiteGroupCommitRecord(@Validated PageParam pageParam){
        return Result.success(weappVersionService.getSiteGroupCommitRecord(pageParam));
    }
}
