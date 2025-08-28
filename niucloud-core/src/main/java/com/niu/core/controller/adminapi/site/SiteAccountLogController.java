package com.niu.core.controller.adminapi.site;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.enums.site.SiteAccountLogEnum;
import com.niu.core.service.admin.site.ISiteAccountLogService;
import com.niu.core.service.admin.site.param.SiteAccountLogParam;
import com.niu.core.service.admin.site.param.SiteAccountLogSearchParam;
import com.niu.core.service.admin.site.vo.SiteAccountLogInfoVo;
import com.niu.core.service.admin.site.vo.SiteAccountLogListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import jdk.jfr.Description;

/**
 * 站点账单记录控制器
 */

@RestController
@RequestMapping("/adminapi/site/account")
@SaCheckLogin
@Description("站点账单记录")
public class SiteAccountLogController {

    @Resource
    ISiteAccountLogService siteAccountLogService;

    /**
     * 站点账单记录列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<SiteAccountLogListVo>>
     */
    @GetMapping("")
    @Description("站点账单记录列表")
    public Result<PageResult<SiteAccountLogListVo>> list(@Validated PageParam pageParam,
                                                         @Validated SiteAccountLogSearchParam searchParam) {
        PageResult<SiteAccountLogListVo> list = siteAccountLogService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 站点账单记录详情
     * @param id 主键ID
     * @return Result<SiteAccountLogInfoVo>
     */
    @GetMapping("/{id}")
    @Description("站点账单记录详情")
    public Result<SiteAccountLogInfoVo> info(@Validated @PathVariable("id") Integer id) {
        SiteAccountLogInfoVo info = siteAccountLogService.info(id);
        return Result.success(info);
    }
    @GetMapping("/stat")
    @Description("账单统计")
    public Result<Map<String, Object>> stat(){
        return Result.success(siteAccountLogService.stat());
    }

    /**
     * 账户类型
     * @return
     */
    @GetMapping("/type")
    @Description("账户类型")
    public Result<Map<String, String>> accountType(){
        return Result.success(SiteAccountLogEnum.getMap());
    }








}
