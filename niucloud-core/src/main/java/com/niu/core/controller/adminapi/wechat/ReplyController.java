package com.niu.core.controller.adminapi.wechat;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.wechat.param.WechatDefaultReplyParam;
import com.niu.core.service.admin.wechat.param.WechatReplyParam;
import com.niu.core.service.admin.wechat.param.WechatReplySearchParam;
import com.niu.core.service.admin.wechat.IWechatReplyService;
import com.niu.core.service.admin.wechat.param.WechatSubscribeReplyParam;
import com.niu.core.service.admin.wechat.vo.WechatReplyInfoVo;
import com.niu.core.service.admin.wechat.vo.WechatReplyListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
 * 公众号消息回调控制器
 */

@RestController
@RequestMapping("adminapi/wechat/reply")
@SaCheckLogin
public class ReplyController {

    @Resource
    IWechatReplyService wechatReplyService;

    /**
     * 关键字回复
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<WechatReplyListVo>>
     */
    @GetMapping("/keywords")
    public Result<PageResult<WechatReplyListVo>> getKeywordLists(@Validated PageParam pageParam, @Validated WechatReplySearchParam searchParam) {
        return Result.success(wechatReplyService.getKeywordList(pageParam, searchParam));
    }

    /**
     * 关键字回复详情
     */
    @GetMapping("/keywords/{id}")
    public Result<WechatReplyInfoVo> keyword(@PathVariable Integer id) {
        return Result.success(wechatReplyService.getKeywordInfo(id));
    }

    /**
     * 添加关键字回复
     * @return Result
     */
    @PostMapping("/keywords")
    public Result addKeyword(@Validated @RequestBody WechatReplyParam addParam) {
        wechatReplyService.addKeyword(addParam);
        return Result.success();
    }

    /**
     * 编辑关键字回复
     * @return Result
     */
    @PutMapping("/keywords/{id}")
    public Result editKeyword(@PathVariable Integer id, @Validated @RequestBody WechatReplyParam editParam) {
        wechatReplyService.editKeyword(id, editParam);
        return Result.success();
    }

    /**
     * 删除添加关键字回复
     */
    @DeleteMapping("/keywords/{id}")
    public Result delKeyword(@PathVariable Integer id) {
        wechatReplyService.delKeyword(id);
        return Result.success();
    }

    /**
     * 默认回复
     */
    @GetMapping("/default")
    public Result<WechatReplyInfoVo> defaultReply() {
        return Result.success(wechatReplyService.getDefault());
    }

    /**
     * 修改默认回复
     */
    @PutMapping("/default")
    public Result editDefault(@Validated @RequestBody WechatDefaultReplyParam param) {
        wechatReplyService.editDefault(param);
        return Result.success();
    }

    /**
     * 获取关注回复
     */
    @GetMapping("/subscribe")
    public Result<WechatReplyInfoVo> subscribe() {
        return Result.success(wechatReplyService.getSubscribe());
    }

    /**
     * 修改关注回复
     */
    @PutMapping("/subscribe")
    public Result editSubscribe(@Validated @RequestBody WechatSubscribeReplyParam param) {
        wechatReplyService.editSubscribe(param);
        return Result.success();
    }
}
