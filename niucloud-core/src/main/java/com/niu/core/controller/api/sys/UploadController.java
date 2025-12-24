package com.niu.core.controller.api.sys;

import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.sys.param.AttachmentUploadParam;
import com.niu.core.service.api.sys.IBase64Service;
import com.niu.core.service.api.sys.IFetchService;
import com.niu.core.service.api.sys.ITaskService;
import com.niu.core.service.api.sys.IUploadService;
import com.niu.core.service.api.sys.param.Base64ImageParam;
import com.niu.core.service.api.sys.param.FetchImageParam;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/file")
public class UploadController {

    @Resource
    IUploadService uploadService;

    @Resource
    IBase64Service base64Service;

    @Resource
    IFetchService fetchService;

    @PostMapping("/image")
    public Result image(@RequestParam("file") MultipartFile file) {
        AttachmentUploadParam param = new AttachmentUploadParam();
        param.setFile(file);
        return Result.success(uploadService.image(param));
    }

    @PostMapping("/video")
    public Result video(@RequestParam("file") MultipartFile file) {
        AttachmentUploadParam param = new AttachmentUploadParam();
        param.setFile(file);
        return Result.success(uploadService.video(param));
    }

    @PostMapping("/image/fetch")
    public Result imageFetch(@RequestBody FetchImageParam param) {
        return Result.success(fetchService.image(param));
    }

    /**
     * base64图片上传
     * @return
     */
    @PostMapping("/image/base64")
    public Result imageBase64(@RequestBody Base64ImageParam param) {
        return Result.success(base64Service.image(param.getContent()));
    }
}
