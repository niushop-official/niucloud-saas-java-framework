package com.niu.core.controller.adminapi.notice;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.notice.INoticeService;
import com.niu.core.service.admin.notice.ISmsService;
import com.niu.core.service.admin.notice.param.EditMessageStatusParam;
import com.niu.core.service.admin.notice.vo.SmsTypeVo;
import com.niu.core.service.core.notice.vo.AddonNoticeListVo;
import com.niu.core.service.core.notice.vo.NoticeInfoVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 消息管理控制器
 */

@RestController
@RequestMapping("adminapi/notice")
@SaCheckLogin
public class NoticeController {

    @Resource
    INoticeService noticeService;

    @Resource
    ISmsService smsService;

    /**
     * 消息列表
     * @return Response
     */
    @GetMapping("/notice")
    public Result<List<AddonNoticeListVo>> list() {
        return Result.success(noticeService.getAddonList());
    }

    /**
     * 获取消息内容
     * @param key
     */
    @GetMapping("/notice/{key}")
    public Result<NoticeInfoVo> info(@Validated @PathVariable("key") String key) {
        return Result.success(noticeService.getInfo(key));
    }

    /**
     * 消息修改
     * @param
     * @return Result<PayInfoVo>
     */
    @PostMapping("/notice/edit")
    public Result<?> edit(@Validated @RequestBody JSONObject data) {
        noticeService.edit(data.getStr("key"), data);
        return Result.success();
    }

    /**
     * 短信配置列表
     * @return Response
     */
    @GetMapping("/notice/sms/list")
    public Result<List<SmsTypeVo>> smsList(){
        return Result.success(smsService.getList());
    }

    /**
     * 短信配置列表
     * @return Response
     */
    @GetMapping("/notice/sms/{sms_type}")
    public Result<SmsTypeVo> smsConfig(@PathVariable("sms_type") String smsType){
        return Result.success(smsService.getConfig(smsType));
    }

    /**
     * 短信配置列表
     * @return Response
     */
    @PutMapping("/notice/sms/{sms_type}")
    public Result<?> editSms(@PathVariable("sms_type") String smsType, @RequestBody JSONObject data){
        smsService.setConfig(smsType, data);
        return Result.success();
    }

    /**
     * 消息启动与关闭
     *
     * @return
     */
    @PostMapping("/notice/editstatus")
    public Result editStatus(@Validated @RequestBody EditMessageStatusParam param) {
        noticeService.editMessageStatus(param);
        return Result.success();
    }
}
