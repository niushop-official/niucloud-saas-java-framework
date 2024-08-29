package com.niu.core.service.core.upload.driver;

import cn.hutool.json.JSONObject;
import com.niu.core.component.context.WebAppEnvs;
import com.niu.core.component.upload.exception.UploadException;
import com.niu.core.common.loader.upload.BaseUpload;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.admin.sys.param.AttachmentUploadParam;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 本地上传
 */
public class Local extends BaseUpload {

    private String rootPath = WebAppEnvs.get().webRootDownResource;

    public Local(JSONObject config) {
        super(config);
    }

    @Override
    public void upload(AttachmentUploadParam param) {
        try {
            MultipartFile file = param.getFile();
            String savePath = this.rootPath + param.getDir();

            File fileExist = new File(savePath);
            if (!fileExist.exists()) {
                if (!fileExist.mkdirs()) {
                    throw new UploadException("创建上传目录失败");
                }
            }
            file.transferTo(new File(savePath, param.getNewFilename()));
        } catch (Exception e) {
            throw new UploadException(e.getMessage());
        }
    }

    /**
     * 删除文件
     * @param filePath
     */
    @Override
    public void delete(String filePath) {
        try {
            File file = new File(this.rootPath + filePath);
            if (!file.exists()) return;

            file.delete();

            // 删除缩略文件
            String filename = file.getName();

            for (UploadThumbTypeEnum item : UploadThumbTypeEnum.values()) {
                String thumbPath = filePath.replace(filename, item.getWidth() + "x" + item.getHeight() + "_" + filename);
                File thumbFile = new File(this.rootPath + thumbPath);
                if (thumbFile.exists()) thumbFile.delete();
            }
        } catch (Exception e) {
            throw new UploadException(e.getMessage());
        }
    }

    /***
     * 生成缩略图
     * @param filePath
     * @param thumbType
     */
    public Map<String, String> thumb(String filePath, String thumbType) {
        try {
            File file = new File(this.rootPath + filePath);
            HashMap<String, String> thumbList = new HashMap<String, String>();

            if (file.exists()) {
                String filename = file.getName();

                for (UploadThumbTypeEnum item : UploadThumbTypeEnum.values()) {
                    if (thumbType.equals("all") || thumbType.equals(item.getType())) {
                        String thumbName = filePath.replace(filename, item.getWidth() + "x" + item.getHeight() + "_" + filename);
                        File thumbFile = new File(this.rootPath + thumbName);
                        if (!thumbFile.exists()) {
                            Thumbnails.of(file).size(item.getWidth(), item.getHeight()).toFile(thumbFile);
                        }
                        thumbList.put(item.getType(), thumbName);
                    }
                }
            }

            return thumbList;
        } catch (Exception e) {
            throw new UploadException(e.getMessage());
        }
    }
}
