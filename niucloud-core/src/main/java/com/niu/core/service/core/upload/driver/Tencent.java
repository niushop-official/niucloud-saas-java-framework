package com.niu.core.service.core.upload.driver;

import cn.hutool.json.JSONObject;
import com.niu.core.component.upload.exception.UploadException;
import com.niu.core.common.loader.upload.BaseUpload;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.admin.sys.param.AttachmentUploadParam;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 腾讯云存储
 */
public class Tencent extends BaseUpload {

    private COSClient cosClient;

    public Tencent(JSONObject config) {
        super(config);

        COSCredentials cred = new BasicCOSCredentials(this.config.getStr("access_key"), this.config.getStr("secret_key"));
        ClientConfig clientConfig = new ClientConfig(new Region(this.config.getStr("region")));
        this.cosClient = new COSClient(cred, clientConfig);
    }

    @Override
    public void upload(AttachmentUploadParam param) {
        try {
            String name = param.getDir() + param.getNewFilename();
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(param.getFile().getSize());
            PutObjectRequest putObjectRequest = new PutObjectRequest(this.config.getStr("bucket"), name, param.getFile().getInputStream(), objectMetadata);
            this.cosClient.putObject(putObjectRequest);
        } catch (CosServiceException e) {
            throw new UploadException(e.getMessage());
        } catch (CosClientException e) {
            throw new UploadException(e.getMessage());
        } catch (IOException e) {
            throw new UploadException(e.getMessage());
        }
    }

    @Override
    public void delete(String filePath) {
        try {
            String bucket = this.config.getStr("bucket");

            if (this.cosClient.doesObjectExist(bucket, filePath)) {
                this.cosClient.deleteObject(bucket, filePath);
            }
        } catch (CosServiceException e) {
            throw new UploadException(e.getMessage());
        } catch (CosClientException e) {
            throw new UploadException(e.getMessage());
        }
    }

    @Override
    public Map<String, String> thumb(String filePath, String thumbType) {
        HashMap<String, String> thumbList = new HashMap<String, String>();

        for (UploadThumbTypeEnum item : UploadThumbTypeEnum.values()) {
            if (thumbType.equals("all") || thumbType.equals(item.getType())) {
                String thumbFile = filePath + "?imageMogr2/thumbnail/" + item.getWidth() + "/x/" + item.getHeight();
                thumbList.put(item.getType(), thumbFile);
            }
        }

        return thumbList;
    }
}
