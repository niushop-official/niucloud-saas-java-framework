package com.niu.core.service.api.sys.impl;

import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateFormatUtils;
import com.niu.core.service.admin.sys.param.AttachmentUploadParam;
import com.niu.core.service.admin.sys.vo.AttachmentUploadVo;
import com.niu.core.service.api.sys.ITaskService;
import com.niu.core.service.api.sys.IUploadService;
import com.niu.core.service.core.member.ICoreMemberConfigService;
import com.niu.core.service.core.member.ICoreMemberService;
import com.niu.core.service.core.sys.ICoreUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
@Slf4j
public class UploadServiceImpl implements IUploadService {

    @Resource
    ICoreUploadService coreUploadService;

    /**
     * 上传图片
     *
     * @param param
     */
    @Override
    public AttachmentUploadVo image(AttachmentUploadParam param) {
        param.setSiteId(RequestUtils.siteId());
        param.setIsAttachment(0);
        param.setAttType("image");
        param.setDir("attachment/image/" + param.getSiteId() + "/" + DateFormatUtils.getUploadFormat() + "/");
        return coreUploadService.upload(param);
    }
}
