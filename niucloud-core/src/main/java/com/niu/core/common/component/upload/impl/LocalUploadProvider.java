package com.niu.core.common.component.upload.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.component.upload.AbstractUploadProvider;
import com.niu.core.common.component.upload.IUploadProvider;
import com.niu.core.common.component.upload.annotation.UploadProvider;
import com.niu.core.common.component.upload.exception.UploadException;
import com.niu.core.common.component.upload.model.*;
import com.niu.core.common.utils.file.FileTools;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

@UploadProvider(name = "local", source = "core", describe = "本地存储")
@Slf4j
public class LocalUploadProvider extends AbstractUploadProvider implements IUploadProvider {

    private String rootUploadDir;

    @Override
    public void init(JSONObject configObject) {
        if (configObject != null) {
            this.rootUploadDir = configObject.getStr("rootUploadDir");
        }
        if (StrUtil.isEmpty(rootUploadDir)) {
            this.rootUploadDir = WebAppEnvs.get().webRootDownResource;
        }
    }

    @Override
    public UploadModelResult upload(UploadModel uploadModel) {
        UploadModelResult uploadModelResult = new UploadModelResult();
        try {
            // 上传的文件
            MultipartFile multipartFile = uploadModel.getUploadFile();
            // 不存在时创建目录
            FileTools.createDirs(this.rootUploadDir + uploadModel.getUploadFilePath());
            // 存储的相对目录
            String key = uploadModel.getUploadFilePath() + uploadModel.getUploadFileName();
            // 存储的完整路径
            String saveFilePath = this.rootUploadDir + key;
            multipartFile.transferTo(new File(saveFilePath));
            // 设置返回参数
            uploadModelResult.setOriginalFilename(multipartFile.getOriginalFilename());
            uploadModelResult.setSize(multipartFile.getSize());
            uploadModelResult.setAccessUrl(key);
            uploadModelResult.setUploadMethod(super.getUploadMethod());
        } catch (Exception e) {
            e.printStackTrace();
            throw new UploadException(e.getMessage());
        }
        return uploadModelResult;
    }

    @Override
    public DeleteModelResult delete(DeleteModel deleteModel) {
        DeleteModelResult deleteModelResult = new DeleteModelResult();
        try {
            // 要删除的key
            String key = deleteModel.getFilePath();
            // 删除操作
            FileTools.deleteFile(this.rootUploadDir + key);
        } catch (Exception e) {
            e.printStackTrace();
            throw new UploadException(e.getMessage());
        }
        return deleteModelResult;
    }

    @Override
    public ThumbModelResult thumb(ThumbModel thumbModel) {
        ThumbModelResult thumbModelResult = new ThumbModelResult();
        try {
            File file = new File(this.rootUploadDir + thumbModel.getFilePath());
            if (file.exists()) {
                String filename = file.getName();
                for (UploadThumbTypeEnum item : UploadThumbTypeEnum.values()) {
                    if (thumbModel.getType().equals("all") || thumbModel.getType().equals(item.getType())) {
                        String thumbName = thumbModel.getFilePath().replace(filename, item.getWidth() + "x" + item.getHeight() + "_" + filename);
                        File thumbFile = new File(this.rootUploadDir + thumbName);
                        if (!thumbFile.exists()) {
                            Thumbnails.of(file).size(item.getWidth(), item.getHeight()).toFile(thumbFile);
                        }
                        thumbModelResult.addDataMap(item.getType(), thumbName);
                    }
                }
            }
        } catch (Exception e) {
            return thumbModelResult;
        }
        return thumbModelResult;
    }

    @Override
    public String base64(Base64Model base64Model) {
        try {
            // 不存在时创建目录
            FileTools.createDirs(this.rootUploadDir + base64Model.getUploadFilePath());
            byte[] decodedBytes = Base64.getDecoder().decode(base64Model.getContent());
            String key = base64Model.getUploadFilePath() + base64Model.getUploadFileName();
            // 将解码后的字节写入文件（请替换为你想保存的文件路径）
            Files.write(Paths.get(this.rootUploadDir, key), decodedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            throw new UploadException(e.getMessage());
        }
        return base64Model.getUploadFilePath() + base64Model.getUploadFileName();
    }

    @Override
    public String fetch(FetchModel fetchModel) {
        try {
            // 不存在时创建目录
            FileTools.createDirs(this.rootUploadDir + fetchModel.getUploadFilePath());
            HttpUtil.downloadFile(fetchModel.getUrl(), new File(this.rootUploadDir + fetchModel.getUploadFilePath(), fetchModel.getUploadFileName()).getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            throw new UploadException(e.getMessage());
        }
        return fetchModel.getUploadFilePath() + fetchModel.getUploadFileName();
    }
}
