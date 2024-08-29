package com.niu.core.service.admin.weapp.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.NiucloudUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.diy.DiyPage;
import com.niu.core.entity.weapp.WeappVersion;
import com.niu.core.enums.channel.WeappVersionStatusEnum;
import com.niu.core.mapper.weapp.WeappVersionMapper;
import com.niu.core.service.admin.diy.vo.DiyPageListVo;
import com.niu.core.service.admin.weapp.IWeappVersionService;
import com.niu.core.service.admin.weapp.param.WeappVersionAddParam;
import com.niu.core.service.admin.weapp.vo.WeappVersionListVo;
import com.niu.core.service.core.site.ICoreSiteService;
import com.niu.core.service.core.weapp.ICoreWeappCloudService;
import com.niu.core.service.core.weapp.ICoreWeappConfigService;
import com.niu.core.service.core.weapp.param.WeappUploadParam;
import com.niu.core.service.core.weapp.vo.WeappConfigVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * 小程序版本管理接口
 */
@Service
public class WeappVersionServiceImpl implements IWeappVersionService {

    @Resource
    WeappVersionMapper weappVersionMapper;

    @Resource
    ICoreWeappCloudService coreWeappCloudService;

    @Resource
    ICoreWeappConfigService coreWeappConfigService;

    @Resource
    ICoreSiteService coreSiteService;

    /**
     * 版本列表
     *
     * @param pageParam
     * @return
     */
    @Override
    public PageResult<WeappVersionListVo> list(PageParam pageParam) {
        Integer page  = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<WeappVersion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.orderByDesc("id");

        IPage<WeappVersion> iPage = weappVersionMapper.selectPage(new Page<WeappVersion>(page, limit), queryWrapper);

        List<WeappVersionListVo> list = new LinkedList<>();
        for(WeappVersion item : iPage.getRecords()) {
            WeappVersionListVo vo = new WeappVersionListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page,limit).setTotal(iPage.getTotal()).setData(list);
    }

    /**
     * 添加版本
     * @param param
     */
    @Override
    public void add(WeappVersionAddParam param) {
        if (!RequestUtils.handler().getScheme().equals("https")) throw new CommonException("微信小程序请求地址只支持https请先配置ssl");

        WeappConfigVo weappConfig = coreWeappConfigService.getWeappConfig(RequestUtils.siteId());
        if (weappConfig.getAppId().isEmpty()) throw new CommonException("还没有配置微信小程序");
        if (weappConfig.getUploadPrivateKey().isEmpty()) throw new CommonException("还没有配置微信小程序代码上传秘钥");

        WeappVersion uploading = weappVersionMapper.selectOne(new QueryWrapper<WeappVersion>().select("id").eq("site_id", RequestUtils.siteId()).eq("status", WeappVersionStatusEnum.APPLET_UPLOADING));
        if (uploading != null) throw new CommonException("小程序有正在上传的版本，请等待上一版本上传完毕后再进行操作");

        WeappVersion lastVersion = weappVersionMapper.selectOne(new QueryWrapper<WeappVersion>().select("version_no").eq("site_id", RequestUtils.siteId()).orderByDesc("version_no").last("limit 1"));
        Integer versionNo = lastVersion == null ? 1 : lastVersion.getVersionNo() + 1;

        List<String> addons = coreSiteService.getAddonKeysBySiteId(RequestUtils.siteId());

        WeappUploadParam uploadParam = new WeappUploadParam();
        uploadParam.setBaseUrl(RequestUtils.getDomain(true));
        uploadParam.setAppId(weappConfig.getAppId());
        uploadParam.setUploadPrivateKey(weappConfig.getUploadPrivateKey());
        uploadParam.setSiteId(RequestUtils.siteId());
        uploadParam.setVersion("1.0." + versionNo);
        uploadParam.setDesc(param.getDesc());
        uploadParam.setAddon(addons);
        String taskKey = coreWeappCloudService.uploadWeapp(uploadParam);

        WeappVersion model = new WeappVersion();
        model.setSiteId(RequestUtils.siteId());
        model.setVersion(uploadParam.getVersion());
        model.setVersionNo(versionNo);
        model.setDesc(param.getDesc());
        model.setTaskKey(taskKey);
        model.setCreateTime(System.currentTimeMillis() / 1000);

        weappVersionMapper.insert(model);
    }

    /**
     * 获取小程序编译日志
     *
     * @param key
     * @return
     */
    @Override
    public JSONObject getWeappCompileLog(String key) {
        JSONObject log = coreWeappCloudService.getWeappCompileLog(key);
        if (log != null) {
            JSONArray data = ObjectUtil.defaultIfNull(log.getByPath("data.0", JSONArray.class), new JSONArray());
            if (data.size() > 0) {
                JSONObject last = data.getJSONObject(data.size() - 1);
                if (last.getInt("code").equals(0)) {
                    WeappVersion model = new WeappVersion();
                    model.setStatus(WeappVersionStatusEnum.APPLET_UPLOAD_FAIL.getStatus());
                    model.setFailReason(last.getStr("msg", ""));
                    model.setUpdateTime(System.currentTimeMillis() / 1000);

                    weappVersionMapper.update(model, new QueryWrapper<WeappVersion>().eq("task_key", key));
                    return log;
                }
                if (last.getInt("percent").equals(100)) {
                    WeappVersion model = new WeappVersion();
                    model.setStatus(WeappVersionStatusEnum.APPLET_UPLOAD_SUCCESS.getStatus());
                    model.setUpdateTime(System.currentTimeMillis() / 1000);

                    weappVersionMapper.update(model, new QueryWrapper<WeappVersion>().eq("task_key", key));
                }
            }
        }
        return log;
    }

    /**
     * 获取微信小程序预览码
     *
     * @return
     */
    @Override
    public String getWeappPreviewImage() {
        return coreWeappCloudService.getWeappPreviewImage();
    }
}
