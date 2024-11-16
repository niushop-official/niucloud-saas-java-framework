package com.niu.core.controller.adminapi.sys;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.sys.ISysAttachmentService;
import com.niu.core.service.admin.sys.param.*;
import com.niu.core.service.admin.sys.vo.*;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 附件管理控制器
 */

@RestController
@RequestMapping("adminapi/sys")
@SaCheckLogin
public class SysAttachmentController {

    @Resource
    ISysAttachmentService sysAttachmentService;

    /**
     * 附件管理列表
     *
     * @param pageParam   分页
     * @param searchParam 搜索条件
     * @return Result<PageResult < SysAttachmentListVo>>
     */
    @GetMapping("/attachment")
    public Result<PageResult<SysAttachmentListVo>> list(@Validated PageParam pageParam, @Validated SysAttachmentSearchParam searchParam) {
        PageResult<SysAttachmentListVo> list = sysAttachmentService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 删除附件
     *
     * @return
     */
    @DeleteMapping("/attachment/del")
    public Result del(@RequestBody SysAttachmentDelParam param) {
        sysAttachmentService.del(param);
        return Result.success();
    }

    /**
     * 移动图片分组
     *
     * @param param
     * @return
     */
    @PutMapping("/attachment/batchmove")
    public Result batchMoveCategory(@Validated @RequestBody SysAttachmentMoveParam param) {
        sysAttachmentService.batchMoveCategory(param);
        return Result.success();
    }

    /**
     * 附件图片上传
     *
     * @return
     */
    @PostMapping("/image")
    public Result<AttachmentUploadVo> image(@RequestParam("file") MultipartFile multipartFile, AttachmentUploadParam param) {
        param.setFile(multipartFile);
        return Result.success(sysAttachmentService.image(param));
    }

    /**
     * 附件视频上传
     *
     * @return
     */
    @PostMapping("/video")
    public Result<AttachmentUploadVo> video(@RequestParam("file") MultipartFile multipartFile, AttachmentUploadParam param) {
        param.setFile(multipartFile);
        return Result.success(sysAttachmentService.video(param));
    }

    /**
     * 附件上传
     *
     * @return
     */
    @PostMapping("/document/{type}")
    public Result<AttachmentUploadVo> document(@PathVariable String type, @RequestParam("file") MultipartFile multipartFile, AttachmentUploadParam param) {
        param.setFile(multipartFile);
        param.setDocumentType(type);
        return Result.success(sysAttachmentService.document(param));
    }

    /**
     * 附件分组列表
     *
     * @param searchParam
     * @return
     */
    @GetMapping("/attachment/category")
    public Result<List<SysAttachmentCategoryListVo>> getCategoryList(@Validated SysAttachmentCategorySearchParam searchParam) {
        List<SysAttachmentCategoryListVo> list = sysAttachmentService.getCategoryList(searchParam);
        return Result.success(list);
    }

    /**
     * 新增附件分组
     *
     * @return Result
     */
    @PostMapping("/attachment/category")
    public Result addCategory(@Validated @RequestBody SysAttachmentCategoryParam addParam) {
        sysAttachmentService.addCategory(addParam);
        return Result.success();
    }

    /**
     * 附件分组添加
     *
     * @param id
     * @return Result<Object>
     */
    @PutMapping("/attachment/category/{id}")
    public Result<Object> editCategory(@PathVariable Integer id, @Validated @RequestBody SysAttachmentCategoryParam editParam) {
        sysAttachmentService.editCategory(id, editParam);
        return Result.success();
    }

    /**
     * 删除附件分组
     *
     * @return Result<Object>
     */
    @DeleteMapping("/attachment/category/{id}")
    public Result<Object> delCategory(@PathVariable Integer id) {
        sysAttachmentService.delCategory(id);
        return Result.success();
    }

    public Result<Object> moveCategory(Integer attrId) {
        return Result.success();
    }

    @GetMapping("attachment/icon_category")
    public Result<Object> getIconCategoryList() {
        return Result.success(new ArrayList<>());
    }


    @GetMapping("attachment/icon")
    public Result<Object> getIconList() {
        return Result.success(new ArrayList<>());
    }


}
