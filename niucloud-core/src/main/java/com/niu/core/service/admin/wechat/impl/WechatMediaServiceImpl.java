package com.niu.core.service.admin.wechat.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.WechatUtils;
import com.niu.core.entity.wechat.WechatMedia;
import com.niu.core.enums.channel.WechatMediaTypeEnum;
import com.niu.core.mapper.wechat.WechatMediaMapper;
import com.niu.core.service.admin.sys.param.AttachmentUploadParam;
import com.niu.core.service.admin.sys.vo.AttachmentUploadVo;
import com.niu.core.service.admin.wechat.param.WechatMediaSearchParam;
import com.niu.core.service.admin.wechat.IWechatMediaService;
import com.niu.core.service.admin.wechat.vo.WechatMediaInfoVo;
import com.niu.core.service.admin.wechat.vo.WechatMediaListVo;
import com.niu.core.service.core.sys.ICoreUploadService;
import me.chanjar.weixin.mp.bean.material.WxMpMaterial;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialNewsBatchGetResult;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialUploadResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


/**
 * 微信素材实现
 */
@Service
public class WechatMediaServiceImpl implements IWechatMediaService {
        
    @Resource
    WechatMediaMapper wechatMediaMapper;

    @Resource
    ICoreUploadService coreUploadService;

    /**
     * 微信素材列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<WechatMediaListVo>
     */
    @Override
    public PageResult<WechatMediaListVo> list(PageParam pageParam, WechatMediaSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<WechatMedia> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.orderByDesc("id");

        if (ObjectUtil.isNotEmpty(searchParam.getType())) queryWrapper.eq("type", searchParam.getType());

        IPage<WechatMedia> iPage = wechatMediaMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<WechatMediaListVo> list = new LinkedList<>();
        for (WechatMedia item : iPage.getRecords()) {
            WechatMediaListVo vo = new WechatMediaListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 上传图片素材
     * @param file
     */
    public WechatMediaInfoVo image(MultipartFile file) {
        AttachmentUploadParam param = new AttachmentUploadParam();
        param.setSiteId(RequestUtils.siteId());
        param.setStorageType("local");
        param.setFile(file);
        param.setAttType("image");
        param.setDir("attachment/image/" + param.getSiteId() + "/" + (new SimpleDateFormat("yyyyMM/dd").format(new Date())) + "/");
        AttachmentUploadVo uploadRes = coreUploadService.upload(param);

        WxMpMaterial wxMaterial = new WxMpMaterial();
        wxMaterial.setFile(new File(WebAppEnvs.get().webRootDownResource + uploadRes.getUrl()));
        wxMaterial.setName(param.getNewFilename());

        try {
            WxMpMaterialUploadResult res = WechatUtils.mp(RequestUtils.siteId()).getMaterialService().materialFileUpload(WechatMediaTypeEnum.IMAGE.getType(), wxMaterial);

            WechatMedia model = new WechatMedia();
            model.setSiteId(RequestUtils.siteId());
            model.setType(WechatMediaTypeEnum.IMAGE.getType());
            model.setValue(uploadRes.getUrl());
            model.setMediaId(res.getMediaId());
            model.setCreateTime(System.currentTimeMillis() / 1000);
            wechatMediaMapper.insert(model);

            WechatMediaInfoVo vo = new WechatMediaInfoVo();
            BeanUtils.copyProperties(model, vo);
            return vo;
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 上传视频素材
     * @param file
     */
    public WechatMediaInfoVo video(MultipartFile file) {
        AttachmentUploadParam param = new AttachmentUploadParam();
        param.setSiteId(RequestUtils.siteId());
        param.setStorageType("local");
        param.setFile(file);
        param.setAttType("video");
        param.setDir("attachment/video/" + param.getSiteId() + "/" + (new SimpleDateFormat("yyyyMM/dd").format(new Date())) + "/");
        AttachmentUploadVo uploadRes = coreUploadService.upload(param);

        WxMpMaterial wxMaterial = new WxMpMaterial();
        wxMaterial.setFile(new File(WebAppEnvs.get().webRootDownResource + uploadRes.getUrl()));
        wxMaterial.setName(param.getNewFilename());
        wxMaterial.setVideoIntroduction((new SimpleDateFormat("yyyyMM/dd").format(new Date())) + "上传");
        wxMaterial.setVideoTitle(param.getNewFilename());

        try {
            WxMpMaterialUploadResult res = WechatUtils.mp(RequestUtils.siteId()).getMaterialService().materialFileUpload(WechatMediaTypeEnum.VIDEO.getType(), wxMaterial);

            WechatMedia model = new WechatMedia();
            model.setSiteId(RequestUtils.siteId());
            model.setType(WechatMediaTypeEnum.VIDEO.getType());
            model.setValue(uploadRes.getUrl());
            model.setMediaId(res.getMediaId());
            model.setCreateTime(System.currentTimeMillis() / 1000);
            wechatMediaMapper.insert(model);

            WechatMediaInfoVo vo = new WechatMediaInfoVo();
            BeanUtils.copyProperties(model, vo);
            return vo;
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     *  同步图文素材
     */
    public void syncNews(Integer offset) {
        try {
            Integer count = 20;

            WxMpMaterialNewsBatchGetResult result = WechatUtils.mp(RequestUtils.siteId()).getMaterialService().materialNewsBatchGet(offset, count);
            if (result.getItemCount() > 0) {
                for (WxMpMaterialNewsBatchGetResult.WxMaterialNewsBatchGetNewsItem item: result.getItems()) {
                    WechatMedia media = wechatMediaMapper.selectOne(new QueryWrapper<WechatMedia>().eq("site_id", RequestUtils.siteId()).eq("media_id", item.getMediaId()));

                    if (ObjectUtil.isNotEmpty(media)) {
                        media.setValue(JSONUtil.toJsonStr(item.getContent()));
                        media.setUpdateTime(System.currentTimeMillis() / 1000);
                        wechatMediaMapper.updateById(media);
                    } else {
                        WechatMedia model = new WechatMedia();
                        model.setSiteId(RequestUtils.siteId());
                        model.setType(WechatMediaTypeEnum.VIDEO.getType());
                        model.setValue(JSONUtil.toJsonStr(item.getContent()));
                        model.setMediaId(item.getMediaId());
                        model.setCreateTime(System.currentTimeMillis() / 1000);
                        wechatMediaMapper.insert(model);
                    }
                }
            }
            if (offset <  Math.ceil(result.getTotalCount() / count)) {
                offset++;
                this.syncNews(offset);
            }
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }
}
