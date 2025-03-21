package com.niu.core.common.component.upload.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import com.niu.core.common.component.upload.AbstractUploadProvider;
import com.niu.core.common.component.upload.IUploadProvider;
import com.niu.core.common.component.upload.annotation.UploadProvider;
import com.niu.core.common.component.upload.exception.UploadException;
import com.niu.core.common.component.upload.model.*;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@UploadProvider(name = "qiniu", source = "core", describe = "七牛云存储")
@Slf4j
public class QiniuUploadProvider extends AbstractUploadProvider implements IUploadProvider {

    private String bucket;

    private String accessKey;

    private String secretKey;

    @Override
    public void init(JSONObject configObject) {
        this.bucket = configObject.getStr("bucket");
        this.accessKey = configObject.getStr("access_key");
        this.secretKey = configObject.getStr("secret_key");
        super.domain = configObject.getStr("domain");
    }

    @Override
    public UploadModelResult upload(UploadModel uploadModel) {
        UploadModelResult uploadModelResult = new UploadModelResult();
        try {
            Auth auth = Auth.create(this.accessKey, this.secretKey);
            // 上传的文件
            MultipartFile uploadFile = uploadModel.getUploadFile();
            // 上传操作
            String key = uploadModel.getUploadFilePath() + uploadModel.getUploadFileName();
            String upToken = auth.uploadToken(this.bucket, key);
            Configuration cfg = new Configuration(Region.region0());
            cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;
            UploadManager uploadManager = new UploadManager(cfg);
            uploadManager.put(uploadFile.getInputStream(), key, upToken, null, null);
            // 设置返回参数
            uploadModelResult.setOriginalFilename(uploadFile.getOriginalFilename());
            uploadModelResult.setSize(uploadFile.getSize());
            uploadModelResult.setAccessUrl(key);
            uploadModelResult.setUploadMethod(super.getUploadMethod());
        } catch (QiniuException e) {
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
            Auth auth = Auth.create(this.accessKey, this.secretKey);
            // 要删除的key
            String key = deleteModel.getFilePath();
            // 删除操作
            Configuration cfg = new Configuration(Region.region0());
            BucketManager bucketManager = new BucketManager(auth, cfg);
            bucketManager.delete(this.bucket, key);
        } catch (QiniuException e) {
            e.printStackTrace();
            throw new UploadException(e.getMessage());
        }
        return deleteModelResult;
    }

    @Override
    public ThumbModelResult thumb(ThumbModel thumbModel) {
        ThumbModelResult thumbModelResult = new ThumbModelResult();
        for (UploadThumbTypeEnum item : UploadThumbTypeEnum.values()) {
            if ("all".equals(thumbModel.getType()) || thumbModel.getType().equals(item.getType())) {
                String thumbFile = thumbModel.getFilePath() + "?imageView2/2/w/" + item.getWidth() + "/h/" + item.getHeight();
                thumbModelResult.addDataMap(item.getType(), thumbFile);
            }
        }
        return thumbModelResult;
    }

    @Override
    public String base64(Base64Model base64Model) {
        String key = base64Model.getUploadFilePath() + base64Model.getUploadFileName();
        try {
            Auth auth = Auth.create(this.accessKey, this.secretKey);
            byte[] decodedBytes = Base64.getDecoder().decode(base64Model.getContent());

            String upToken = auth.uploadToken(this.bucket, key);
            Configuration cfg = new Configuration(Region.region0());
            cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;

            UploadManager uploadManager = new UploadManager(cfg);
            Response response = uploadManager.put(decodedBytes, key, upToken);
        } catch (QiniuException e) {
            e.printStackTrace();
            throw new UploadException(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            throw new UploadException(e.getMessage());
        }
        return key;
    }

    @Override
    public String fetch(FetchModel fetchModel) {
        String key = fetchModel.getUploadFilePath() + fetchModel.getUploadFileName();
        try {
            Auth auth = Auth.create(this.accessKey, this.secretKey);
            byte[] bytes = HttpUtil.downloadBytes(fetchModel.getUrl());

            String upToken = auth.uploadToken(this.bucket, key);
            Configuration cfg = new Configuration(Region.region0());
            cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;

            UploadManager uploadManager = new UploadManager(cfg);
            Response response = uploadManager.put(bytes, key, upToken);
        } catch (QiniuException e) {
            e.printStackTrace();
            throw new UploadException(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            throw new UploadException(e.getMessage());
        }
        return key;
    }
}
