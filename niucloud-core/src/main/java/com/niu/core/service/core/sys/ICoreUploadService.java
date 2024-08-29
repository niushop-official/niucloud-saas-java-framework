package com.niu.core.service.core.sys;

import com.niu.core.service.admin.sys.param.AttachmentUploadParam;
import com.niu.core.service.admin.sys.vo.AttachmentUploadVo;

public interface ICoreUploadService {

    /**
     * 上传附件
     *
     * @param param
     */
    AttachmentUploadVo upload(AttachmentUploadParam param);

    /**
     * 删除附件
     *
     * @param siteId
     * @param storageType
     * @param path
     */
    void delete(Integer siteId, String storageType, String path);

}
