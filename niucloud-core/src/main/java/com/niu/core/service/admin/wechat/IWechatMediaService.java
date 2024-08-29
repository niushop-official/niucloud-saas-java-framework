package com.niu.core.service.admin.wechat;

import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.wechat.param.WechatMediaParam;
import com.niu.core.service.admin.wechat.param.WechatMediaSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.wechat.vo.WechatMediaInfoVo;
import com.niu.core.service.admin.wechat.vo.WechatMediaListVo;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * 微信素材服务接口
 */
public interface IWechatMediaService {


    /**
     * 微信素材列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<WechatMediaListVo>
     */
    PageResult<WechatMediaListVo> list(PageParam pageParam, WechatMediaSearchParam searchParam);

    WechatMediaInfoVo image(MultipartFile file);

    WechatMediaInfoVo video(MultipartFile file);

    void syncNews(Integer offset);
}
