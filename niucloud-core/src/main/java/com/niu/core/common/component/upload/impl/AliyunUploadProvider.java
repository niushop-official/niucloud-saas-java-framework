package com.niu.core.common.component.upload.impl;

import cn.hutool.json.JSONObject;
import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.niu.core.common.component.upload.IUploadProvider;
import com.niu.core.common.component.upload.annotation.UploadProvider;
import com.niu.core.common.component.upload.exception.UploadException;
import com.niu.core.common.component.upload.model.*;
import com.niu.core.common.component.upload.AbstractUploadProvider;
import com.niu.core.common.component.upload.model.*;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@UploadProvider(name = "aliyun", source = "core", describe = "阿里云存储")
@Slf4j
public class AliyunUploadProvider extends AbstractUploadProvider implements IUploadProvider {

    private String bucket;

    private String accessKey;

    private String secretKey;

    private String endpoint;

    private String region;

    private OSS ossClient;

    @Override
    public void init(JSONObject configObject) {
        this.bucket = configObject.getStr("bucket");
        this.accessKey = configObject.getStr("access_key");
        this.secretKey = configObject.getStr("secret_key");
        this.endpoint = configObject.getStr("endpoint");
        super.domain = configObject.getStr("domain");
        this.region = this.endpoint.split("\\.", -1)[0].replace("https://oss-", "").replace("http://oss-", "");

    }

    @Override
    public UploadModelResult upload(UploadModel uploadModel) {
        UploadModelResult uploadModelResult = new UploadModelResult();
        try {
            // 初始化
            CredentialsProvider credentialsProvider = new DefaultCredentialProvider(this.accessKey, this.accessKey);
            ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
            clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
            this.ossClient = OSSClientBuilder.create()
                    .endpoint(this.endpoint)
                    .credentialsProvider(credentialsProvider)
                    .clientConfiguration(clientBuilderConfiguration)
                    .region(region)
                    .build();
            // 上传的文件
            MultipartFile uploadFile = uploadModel.getUploadFile();
            // 上传操作
            String key = uploadModel.getUploadFilePath() + uploadModel.getUploadFileName();
            PutObjectRequest putObjectRequest = new PutObjectRequest(this.bucket, key, uploadModel.getUploadFile().getInputStream());
            PutObjectResult result = ossClient.putObject(putObjectRequest);
            // 设置返回参数
            uploadModelResult.setOriginalFilename(uploadFile.getOriginalFilename());
            uploadModelResult.setSize(uploadFile.getSize());
            uploadModelResult.setAccessUrl(key);
            uploadModelResult.setUploadMethod(super.getUploadMethod());
        } catch (IOException e) {
            e.printStackTrace();
            throw new UploadException(e.getMessage());
        } catch (OSSException e) {
            e.printStackTrace();
            throw new UploadException(e.getMessage());
        } catch (ClientException e) {
            e.printStackTrace();
            throw new UploadException(e.getMessage());
        } finally {
            if (this.ossClient != null) {
                this.ossClient.shutdown();
            }
        }
        return uploadModelResult;
    }

    @Override
    public DeleteModelResult delete(DeleteModel deleteModel) {
        DeleteModelResult deleteModelResult = new DeleteModelResult();
        try {
            // 初始化
            CredentialsProvider credentialsProvider = new DefaultCredentialProvider(this.accessKey, this.accessKey);
            ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
            clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
            this.ossClient = OSSClientBuilder.create()
                    .endpoint(this.endpoint)
                    .credentialsProvider(credentialsProvider)
                    .clientConfiguration(clientBuilderConfiguration)
                    .region(region)
                    .build();
            // 要删除的key
            String key = deleteModel.getFilePath();
            if (ossClient.doesObjectExist(this.bucket, key)) {
                ossClient.deleteObject(this.bucket, key);
            }
        } catch (OSSException e) {
            e.printStackTrace();
            throw new UploadException(e.getMessage());
        } catch (ClientException e) {
            e.printStackTrace();
            throw new UploadException(e.getMessage());
        } finally {
            if (this.ossClient != null) {
                this.ossClient.shutdown();
            }
        }
        return deleteModelResult;
    }

    @Override
    public ThumbModelResult thumb(ThumbModel thumbModel) {
        ThumbModelResult thumbModelResult = new ThumbModelResult();
        for (UploadThumbTypeEnum item : UploadThumbTypeEnum.values()) {
            if ("all".equals(thumbModel.getType()) || thumbModel.getType().equals(item.getType())) {
                String thumbFile = thumbModel.getFilePath() + "?x-oss-process=image/resize,h_" + item.getHeight() + ",w_" + item.getWidth();
                thumbModelResult.addDataMap(item.getType(), thumbFile);
            }
        }
        return thumbModelResult;
    }

}
