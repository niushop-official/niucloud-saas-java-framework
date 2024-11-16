package com.niu.core.service.core.upload.driver;

import cn.hutool.json.JSONObject;
import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.niu.core.common.component.upload.exception.UploadException;
import com.niu.core.common.loader.upload.BaseUpload;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.admin.sys.param.AttachmentUploadParam;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云存储
 */
public class Aliyun extends BaseUpload {

    private OSS ossClient;

    public Aliyun(JSONObject config) {
        super(config);

        CredentialsProvider credentialsProvider = new DefaultCredentialProvider(this.config.getStr("access_key"), this.config.getStr("secret_key"));
        String region = this.config.getStr("endpoint").split("\\.", -1)[0].replace("https://oss-", "").replace("http://oss-", "");

        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);

        this.ossClient = OSSClientBuilder.create()
                .endpoint(this.config.getStr("endpoint"))
                .credentialsProvider(credentialsProvider)
                .clientConfiguration(clientBuilderConfiguration)
                .region(region)
                .build();
    }

    @Override
    public void upload(AttachmentUploadParam param) {
        try {
            String name = param.getDir() + param.getNewFilename();
            PutObjectRequest putObjectRequest = new PutObjectRequest(this.config.getStr("bucket"), name, param.getFile().getInputStream());
            PutObjectResult result = ossClient.putObject(putObjectRequest);
        } catch (IOException e) {
            throw new UploadException(e.getMessage());
        } catch (OSSException e) {
            throw new UploadException(e.getMessage());
        } catch (ClientException e) {
            throw new UploadException(e.getMessage());
        } finally {
            if (this.ossClient != null) {
                this.ossClient.shutdown();
            }
        }
    }

    @Override
    public void delete(String filePath) {
        try {
            String bucket = this.config.getStr("bucket");

            if (ossClient.doesObjectExist(bucket, filePath)) {
                ossClient.deleteObject(bucket, filePath);
            }
        } catch (OSSException e) {
            throw new UploadException(e.getMessage());
        } catch (ClientException e) {
            throw new UploadException(e.getMessage());
        } finally {
            if (this.ossClient != null) {
                this.ossClient.shutdown();
            }
        }
    }

    @Override
    public Map<String, String> thumb(String filePath, String thumbType) {
        HashMap<String, String> thumbList = new HashMap<String, String>();

        for (UploadThumbTypeEnum item : UploadThumbTypeEnum.values()) {
            if (thumbType.equals("all") || thumbType.equals(item.getType())) {
                String thumbFile = filePath + "?x-oss-process=image/resize,h_" + item.getHeight() + ",w_" + item.getWidth();
                thumbList.put(item.getType(), thumbFile);
            }
        }

        return thumbList;
    }
}
