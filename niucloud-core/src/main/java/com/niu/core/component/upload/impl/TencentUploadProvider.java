package com.niu.core.component.upload.impl;

import cn.hutool.json.JSONObject;
import com.niu.core.component.upload.AbstractUploadProvider;
import com.niu.core.component.upload.IUploadProvider;
import com.niu.core.component.upload.annotation.UploadProvider;
import com.niu.core.component.upload.exception.UploadException;
import com.niu.core.component.upload.model.*;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@UploadProvider(name = "tencent", source = "core", describe = "腾讯云存储")
@Slf4j
public class TencentUploadProvider extends AbstractUploadProvider implements IUploadProvider {

    private String bucket;

    private String accessKey;

    private String secretKey;

    private String endpoint;

    private String region;

    @Override
    public void init(JSONObject configObject) {
        this.bucket = configObject.getStr("bucket");
        this.accessKey = configObject.getStr("access_key");
        this.secretKey = configObject.getStr("secret_key");
        this.endpoint = configObject.getStr("endpoint");
        super.domain = configObject.getStr("domain");
        this.region = configObject.getStr("region");
    }

    @Override
    public UploadModelResult upload(UploadModel uploadModel) {
        UploadModelResult uploadModelResult = new UploadModelResult();
        try {
            // 初始化
            COSCredentials cred = new BasicCOSCredentials(this.accessKey, this.secretKey);
            ClientConfig clientConfig = new ClientConfig(new Region(this.region));
            COSClient cosClient = new COSClient(cred, clientConfig);
            // 上传的文件
            MultipartFile uploadFile = uploadModel.getUploadFile();
            // 上传操作
            String key = uploadModel.getUploadFilePath() + uploadModel.getUploadFileName();
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(uploadFile.getSize());
            PutObjectRequest putObjectRequest = new PutObjectRequest(this.bucket, key, uploadFile.getInputStream(), objectMetadata);
            cosClient.putObject(putObjectRequest);
            // 设置返回参数
            uploadModelResult.setOriginalFilename(uploadFile.getOriginalFilename());
            uploadModelResult.setSize(uploadFile.getSize());
            uploadModelResult.setAccessUrl(key);
            uploadModelResult.setUploadMethod(super.getUploadMethod());
        } catch (CosServiceException e) {
            e.printStackTrace();
            throw new UploadException(e.getMessage());
        } catch (CosClientException e) {
            e.printStackTrace();
            throw new UploadException(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            throw new UploadException(e.getMessage());
        }
        return uploadModelResult;
    }

    @Override
    public DeleteModelResult delete(DeleteModel deleteModel) {
        DeleteModelResult deleteModelResult = new DeleteModelResult();
        try {
            // 初始化
            COSCredentials cred = new BasicCOSCredentials(this.accessKey, this.secretKey);
            ClientConfig clientConfig = new ClientConfig(new Region(this.region));
            COSClient cosClient = new COSClient(cred, clientConfig);
            // 要删除的key
            String key = deleteModel.getFilePath();
            if (cosClient.doesObjectExist(bucket, key)) {
                cosClient.deleteObject(bucket, key);
            }
        } catch (CosServiceException e) {
            throw new UploadException(e.getMessage());
        } catch (CosClientException e) {
            throw new UploadException(e.getMessage());
        }
        return deleteModelResult;
    }

    @Override
    public ThumbModelResult thumb(ThumbModel thumbModel) {
        ThumbModelResult thumbModelResult = new ThumbModelResult();
        for (UploadThumbTypeEnum item : UploadThumbTypeEnum.values()) {
            if ("all".equals(thumbModel.getType()) || thumbModel.getType().equals(item.getType())) {
                String thumbFile = thumbModel.getFilePath() + "?imageMogr2/thumbnail/" + item.getWidth() + "/x/" + item.getHeight();
                thumbModelResult.addDataMap(item.getType(), thumbFile);
            }
        }
        return thumbModelResult;
    }

}
