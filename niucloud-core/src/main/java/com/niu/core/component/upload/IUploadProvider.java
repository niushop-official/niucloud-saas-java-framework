package com.niu.core.component.upload;

import cn.hutool.json.JSONObject;
import com.niu.core.component.upload.model.*;

public interface IUploadProvider {

    /**
     * 根据配置初始化
     *
     * @param configObject
     */
    public void init(JSONObject configObject);

    /**
     * @param location
     * @return
     */
    public String getAccessUrl(String location);

    /**
     * 上传操作
     *
     * @param uploadModel
     * @return
     */
    public UploadModelResult upload(UploadModel uploadModel);

    /**
     * 删除操作
     *
     * @param deleteModel
     * @return
     */
    public DeleteModelResult delete(DeleteModel deleteModel);

    /**
     * @param thumbModel
     * @return
     */
    public ThumbModelResult thumb(ThumbModel thumbModel);

}
