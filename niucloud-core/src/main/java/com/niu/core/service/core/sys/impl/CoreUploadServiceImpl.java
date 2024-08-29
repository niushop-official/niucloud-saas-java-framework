package com.niu.core.service.core.sys.impl;

import cn.hutool.json.JSONObject;
import com.niu.core.common.exception.CommonException;
import com.niu.core.component.context.SpringContext;
import com.niu.core.component.upload.IUploadProvider;
import com.niu.core.component.upload.factory.UploadProviderFactory;
import com.niu.core.component.upload.model.DeleteModel;
import com.niu.core.component.upload.model.DeleteModelResult;
import com.niu.core.component.upload.model.UploadModel;
import com.niu.core.component.upload.model.UploadModelResult;
import com.niu.core.entity.sys.SysAttachment;
import com.niu.core.enums.sys.StorageEnum;
import com.niu.core.enums.upload.UploadRolesEnum;
import com.niu.core.mapper.sys.SysAttachmentMapper;
import com.niu.core.service.admin.sys.param.AttachmentUploadParam;
import com.niu.core.service.admin.sys.vo.AttachmentUploadVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.ICoreUploadService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@Service
public class CoreUploadServiceImpl implements ICoreUploadService {

    @Autowired
    private SysAttachmentMapper sysAttachmentMapper;

    private String rootPath = "upload";

    /**
     * 上传附件
     *
     * @param attachmentUploadParam
     */
    public AttachmentUploadVo upload(AttachmentUploadParam attachmentUploadParam) {
        // 上传相对目录
        attachmentUploadParam.setDir(this.rootPath + "/" + attachmentUploadParam.getDir());
        // 校验上传文件
        this.validate(attachmentUploadParam);
        // 生成新的文件名
        this.generateNewName(attachmentUploadParam);
        // 获取站点的上传配置
        ICoreConfigService coreConfigService = SpringContext.bean(ICoreConfigService.class);
        JSONObject rootJSONObject = coreConfigService.getConfigValue(attachmentUploadParam.getSiteId(), "STORAGE");
        String defaultValue = rootJSONObject.getStr("default", StorageEnum.LOCAL.getCode());
        JSONObject configObject = rootJSONObject.getJSONObject(defaultValue);
        // 根据配置初始化
        IUploadProvider uploadProvider = UploadProviderFactory.createAndInit(defaultValue, configObject);
        // 构建上传参数
        UploadModel uploadModel = new UploadModel();
        uploadModel.setUploadFile(attachmentUploadParam.getFile());
        uploadModel.setUploadType(attachmentUploadParam.getAttType());
        uploadModel.setUploadFilePath(attachmentUploadParam.getDir());
        uploadModel.setUploadFileName(attachmentUploadParam.getNewFilename());
        // 上传操作
        UploadModelResult uploadModelResult = uploadProvider.upload(uploadModel);
        // Service返回结果
        AttachmentUploadVo attachmentUploadVo = new AttachmentUploadVo();
        // 是否上传到相册
        if (attachmentUploadParam.getIsAttachment().equals(1)) {
            SysAttachment newSysAttachment = this.buildSysAttachment(attachmentUploadParam, uploadModelResult);
            sysAttachmentMapper.insert(newSysAttachment);
            attachmentUploadVo.setUrl(newSysAttachment.getUrl());
            attachmentUploadVo.setAttId(newSysAttachment.getAttId());
        } else {
            attachmentUploadVo.setUrl(uploadModelResult.getAccessUrl());
        }
        return attachmentUploadVo;
    }

    /**
     * 根据param和上传结果构建实体对象
     *
     * @param attachmentUploadParam
     * @param uploadModelResult
     * @return
     */
    private SysAttachment buildSysAttachment(AttachmentUploadParam attachmentUploadParam, UploadModelResult uploadModelResult) {
        SysAttachment sysAttachment = new SysAttachment();
        sysAttachment.setSiteId(attachmentUploadParam.getSiteId());
        sysAttachment.setName(attachmentUploadParam.getNewFilename());
        sysAttachment.setRealName(uploadModelResult.getOriginalFilename());
        sysAttachment.setPath(attachmentUploadParam.getDir() + attachmentUploadParam.getNewFilename());
        sysAttachment.setDir(attachmentUploadParam.getDir());
        sysAttachment.setAttSize(uploadModelResult.getSize());
        sysAttachment.setAttType(attachmentUploadParam.getAttType());
        sysAttachment.setStorageType(uploadModelResult.getUploadMethod());
        sysAttachment.setCateId(attachmentUploadParam.getCateId());
        sysAttachment.setCreateTime(System.currentTimeMillis() / 1000);
        sysAttachment.setUrl(uploadModelResult.getAccessUrl());
        return sysAttachment;
    }

    /**
     * 生成新文件名
     *
     * @param attachmentUploadParam
     */
    private void generateNewName(AttachmentUploadParam attachmentUploadParam) {
        String ext = FilenameUtils.getExtension(attachmentUploadParam.getFile().getOriginalFilename());
        String name = (System.currentTimeMillis()) + "_" + attachmentUploadParam.getStorageType() + "." + ext;
        attachmentUploadParam.setNewFilename(name);
    }

    /**
     * 上传文件校验
     *
     * @param attachmentUploadParam
     */
    private void validate(AttachmentUploadParam attachmentUploadParam) {
        // 上传文件内容校验
        MultipartFile file = attachmentUploadParam.getFile();
        Assert.notNull(file, "上传失败!");
        // 上传文件后缀和大小以及Mime类型
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        Long size = file.getSize();
        String mime = file.getContentType();
        for (UploadRolesEnum item : UploadRolesEnum.values()) {
            if (item.getName().equals(attachmentUploadParam.getAttType())) {
                if (!Arrays.asList(item.getExt()).contains(ext)) {
                    throw new CommonException("上传文件后缀不允许");
                }
                if (!Arrays.asList(item.getMime()).contains(mime)) {
                    throw new CommonException("上传文件MIME类型不允许");
                }
                if (item.getSize().compareTo(size) == -1) {
                    throw new CommonException("上传文件大小不符");
                }
            }
        }
    }

    @Override
    public void delete(Integer siteId, String storageType, String path) {
        // 获取站点的上传配置
        ICoreConfigService coreConfigService = SpringContext.bean(ICoreConfigService.class);
        JSONObject rootJSONObject = coreConfigService.getConfigValue(siteId, "STORAGE");
        String defaultValue = storageType;
        JSONObject configObject = rootJSONObject.getJSONObject(defaultValue);
        // 根据配置初始化
        IUploadProvider uploadProvider = UploadProviderFactory.createAndInit(defaultValue, configObject);
        DeleteModel deleteModel = new DeleteModel();
        deleteModel.setFilePath(path);
        DeleteModelResult deleteModelResult = uploadProvider.delete(deleteModel);
        if (deleteModelResult.isResult()) {
            // 删除成功 -> 忽略
        }
    }

}
