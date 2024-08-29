package com.niu.core.controller.api.sys;

import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.sys.param.AttachmentUploadParam;
import com.niu.core.service.api.sys.ITaskService;
import com.niu.core.service.api.sys.IUploadService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/file")
public class UploadController {

    @Resource
    IUploadService uploadService;

    @PostMapping("/image")
    public Result image(@RequestParam("file") MultipartFile file) {
        AttachmentUploadParam param = new AttachmentUploadParam();
        param.setFile(file);
        return Result.success(uploadService.image(param));
    }

    @PostMapping("/image/fetch")
    public Result imageFetch() {
        return Result.success();
    }

    @PostMapping("/image/base64")
    public Result imageBase64() {
        return Result.success();
    }
}
