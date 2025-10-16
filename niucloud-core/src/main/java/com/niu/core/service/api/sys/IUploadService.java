package com.niu.core.service.api.sys;


import com.niu.core.service.admin.sys.param.AttachmentUploadParam;
import com.niu.core.service.admin.sys.vo.AttachmentUploadVo;

public interface IUploadService {

    AttachmentUploadVo image(AttachmentUploadParam param);

    AttachmentUploadVo video(AttachmentUploadParam param);

}
