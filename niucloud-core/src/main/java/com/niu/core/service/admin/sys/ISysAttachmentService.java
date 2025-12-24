package com.niu.core.service.admin.sys;

import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.sys.param.*;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.sys.vo.AttachmentUploadVo;
import com.niu.core.service.admin.sys.vo.SysAttachmentCategoryListVo;
import com.niu.core.service.admin.sys.vo.SysAttachmentInfoVo;
import com.niu.core.service.admin.sys.vo.SysAttachmentListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 附件管理服务接口
 */
public interface ISysAttachmentService {


    /**
     * 附件管理列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysAttachmentListVo>
     */
    PageResult<SysAttachmentListVo> list(PageParam pageParam, SysAttachmentSearchParam searchParam);

    /**
     * 删除附件
     */
    void del(SysAttachmentDelParam param);

    /**
     * 移动图片分组
     * @param param
     */
    void batchMoveCategory(SysAttachmentMoveParam param);

    /**
     * 查询分组列表
     * @param searchParam
     * @return
     */
    List<SysAttachmentCategoryListVo> getCategoryList(SysAttachmentCategorySearchParam searchParam);

    /**
     * 添加分组
     * @param addParam
     */
    void addCategory(SysAttachmentCategoryParam addParam);

    /**
     * 编辑分组
     * @param id
     * @param editParam
     */
    void editCategory(Integer id, SysAttachmentCategoryParam editParam);

    /**
     * 删除分组
     * @param id
     */
    void delCategory(Integer id);

    /**
     * 上传图片
     * @param param
     */
    AttachmentUploadVo image(AttachmentUploadParam param);

    /**
     * 上传视频
     * @param param
     */
    AttachmentUploadVo video(AttachmentUploadParam param);

    /**
     * 上传文档
     * @param param
     */
    AttachmentUploadVo document(AttachmentUploadParam param);

    /**
     * 上传音频
     * @param param
     */
    AttachmentUploadVo audio(AttachmentUploadParam param);
}
