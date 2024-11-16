package com.niu.core.service.core.upload.driver;

import cn.hutool.json.JSONObject;
import com.niu.core.common.component.upload.exception.UploadException;
import com.niu.core.common.loader.upload.BaseUpload;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.admin.sys.param.AttachmentUploadParam;
import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 七牛云存储
 */
public class Qiniu extends BaseUpload {

    private Auth auth;

    public Qiniu(JSONObject config) {
        super(config);
        this.auth = Auth.create(this.config.getStr("access_key"), this.config.getStr("secret_key"));
    }

    @Override
    public void upload(AttachmentUploadParam param) {
        try {
            String name = param.getDir() + param.getNewFilename();
            String upToken = this.auth.uploadToken(this.config.getStr("bucket"), name);

            Configuration cfg = new Configuration(Region.region0());
            cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;

            UploadManager uploadManager = new UploadManager(cfg);
            uploadManager.put(param.getFile().getInputStream(), name, upToken, null, null);
        } catch (QiniuException e) {
            throw new UploadException(e.getMessage());
        } catch (IOException e) {
            throw new UploadException(e.getMessage());
        }
    }

    @Override
    public void delete(String filePath) {
        try {
            String bucket = this.config.getStr("bucket");
            Configuration cfg = new Configuration(Region.region0());
            BucketManager bucketManager = new BucketManager(this.auth, cfg);
            bucketManager.delete(bucket, filePath);
        } catch (QiniuException e) {
            throw new UploadException(e.getMessage());
        }
    }

    @Override
    public Map<String, String> thumb(String filePath, String thumbType) {
        HashMap<String, String> thumbList = new HashMap<String, String>();

        for (UploadThumbTypeEnum item : UploadThumbTypeEnum.values()) {
            if (thumbType.equals("all") || thumbType.equals(item.getType())) {
                String thumbFile = filePath + "?imageView2/2/w/" + item.getWidth() + "/h/" + item.getHeight();
                thumbList.put(item.getType(), thumbFile);
            }
        }

        return thumbList;
    }
}
