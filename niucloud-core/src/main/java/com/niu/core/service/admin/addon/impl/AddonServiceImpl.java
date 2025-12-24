package com.niu.core.service.admin.addon.impl;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.NiucloudUtils;
import com.niu.core.common.utils.file.FileTools;
import com.niu.core.common.utils.image.ImageUtils;
import com.niu.core.common.utils.json.JsonLoadUtils;
import com.niu.core.enums.sys.CacheTagEnum;
import com.niu.core.service.admin.addon.param.AddonParam;
import com.niu.core.service.admin.addon.param.AddonSearchParam;
import com.niu.core.service.admin.addon.IAddonService;
import com.niu.core.service.admin.addon.vo.*;
import com.niu.core.common.domain.PageParam;
import com.niu.core.entity.addon.Addon;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.service.admin.niucloud.INiucloudService;
import com.niu.core.service.admin.niucloud.vo.ModuleListVo;
import com.niu.core.service.core.addon.ICoreAddonInstallService;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import com.niu.core.service.core.niucloud.ICoreNiucloudConfigService;
import com.niu.core.service.core.niucloud.vo.NiucloudConfigVo;
import com.niu.core.service.core.site.vo.SiteInfoCacheVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 插件实现类
 */
@Service
public class AddonServiceImpl implements IAddonService {

    /**
     * 站点插件tag
     */
    public static String cacheTagName = CacheTagEnum.ADDON_CACHE.getTagName();

    /**
     * 是否使用缓存
     */
    public static final boolean useCache = true;

    @Resource
    private Cached cached;

    @Resource
    AddonMapper addonMapper;

    @Resource
    ICoreAddonInstallService iCoreAddonInstallService;

    @Resource
    ICoreAddonService iCoreAddonService;

    @Resource
    INiucloudService niucloudService;

    @Resource
    private ICoreNiucloudConfigService coreNiucloudConfigService;


    /**
     * 获取本地插件
     */
    @Override
    public LocalAddonListVo getLocalAddonList() {
        LocalAddonListVo vo = new LocalAddonListVo();

        Map<String, LocalAddonInfoVo> list = new HashMap<>();

        // 获取已安装的插件
        Map<String, InstallAddonListVo> installAddonList = iCoreAddonService.getInstallAddonList();

        try {
            List<ModuleListVo> moduleList = niucloudService.getModuleList();

            for (ModuleListVo item : moduleList) {
                ModuleListVo.App app = item.getApp();
                LocalAddonInfoVo addonInfoVo = new LocalAddonInfoVo();
                addonInfoVo.setTitle(app.getAppName());
                addonInfoVo.setDesc(app.getAppDesc());
                addonInfoVo.setKey(app.getAppKey());
                addonInfoVo.setAuthor(item.getSiteName());
                addonInfoVo.setVersion(item.getVersion());
                addonInfoVo.setIsLocal(false);
                addonInfoVo.setIsDownload(false);
                addonInfoVo.setType(app.getAppType());
                addonInfoVo.setIcon(app.getAppLogo());
                addonInfoVo.setCover(app.getWindowLogo()[0]);
                list.put(app.getAppKey(), addonInfoVo);
            }

            // 获取本地所有的插件
            List<File> localAddons = Files.list(Paths.get(WebAppEnvs.get().webRootDownAddon))
                    .map(path -> path.toFile())
                    .filter(file -> file.isDirectory())
                    .collect(Collectors.toList());

            for (File file : localAddons) {
                if (new File(file, "info.json").exists()) {
                    JSONObject info = JSONUtil.parseObj(JsonLoadUtils.loadJsonString(new File(file, "info.json")));
                    String addon = info.getStr("key");
                    if (list.get(addon) != null) {
                        LocalAddonInfoVo addonInfoVo = list.get(addon);
                        addonInfoVo.setIsDownload(true);
                        addonInfoVo.setIsLocal(false);
                        if (installAddonList.get(addon) != null) {
                            addonInfoVo.setInstallInfo(installAddonList.get(addon));
                        }
                        list.put(addon, addonInfoVo);
                    } else {
                        LocalAddonInfoVo localAddonInfoVo = JSONUtil.toBean(info, LocalAddonInfoVo.class);
                        localAddonInfoVo.setIsLocal(true);
                        localAddonInfoVo.setIsDownload(true);
                        if (installAddonList.get(addon) != null)
                            localAddonInfoVo.setInstallInfo(installAddonList.get(addon));
                        localAddonInfoVo.setIcon(ImageUtils.imageToBase64(file + "/resource/icon.png"));
                        localAddonInfoVo.setCover(ImageUtils.imageToBase64(file + "/resource/cover.png"));
                        list.put(addon, localAddonInfoVo);
                    }
                }
            }
            //已安装但是文件没有的
            for (InstallAddonListVo item : installAddonList.values()) {
                if (list.get(item.getKey()) == null){
                    LocalAddonInfoVo addonInfoVo = new LocalAddonInfoVo();
                    BeanUtils.copyProperties(item, addonInfoVo);
                    addonInfoVo.setKey(item.getKey());
                    addonInfoVo.setIsLocal(false);
                    addonInfoVo.setIsDownload(false);
                    addonInfoVo.setInstallInfo(item);
                    list.put(item.getKey(), addonInfoVo);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            vo.setError(e.getMessage());
        }

        vo.setList(list);
        return vo;
    }

    /**
     * 插件列表
     *
     * @param pageParam     分页参数
     * @param addonSearchParam 搜索参数
     * @return PageResult<AddonListVo>
     */
    @Override
    public PageResult<AddonListVo> list(PageParam pageParam, AddonSearchParam addonSearchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<Addon> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (ObjectUtil.isNotEmpty(addonSearchParam.getTitle())) queryWrapper.like("title", addonSearchParam.getTitle());

        IPage<Addon> iPage = addonMapper.selectPage(new Page<Addon>(page, limit), queryWrapper);
        List<AddonListVo> list = new LinkedList<>();
        for (Addon item : iPage.getRecords()) {
            AddonListVo vo = new AddonListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 插件详情
     *
     * @param id 主键参数
     * @return Addon
     */
    @Override
    public AddonInfoVo info(Integer id) {
        Addon model = addonMapper.selectOne(
                new QueryWrapper<Addon>()
                        .eq("id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        AddonInfoVo vo = new AddonInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 插件新增
     *
     * @param addonParam 参数
     */
    @Override
    public void add(AddonParam addonParam) {
        Addon model = new Addon();

        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setInstallTime(addonParam.getInstallTime());
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        model.setCover(addonParam.getCover());
        model.setType(addonParam.getType());
        model.setSupportApp(addonParam.getSupportApp());
        model.setIsStar(addonParam.getIsStar());
        model.setCompile(String.join(",", addonParam.getCompile()));
        // BeanUtil.copyProperties(sysPositionEditParam, sysPosition);
        addonMapper.insert(model);
    }

    /**
     * 插件删除
     *
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        addonMapper.delete(new QueryWrapper<Addon>().eq("id", id));
    }

    /**
     *
     * @param addon
     */
    @Override
    public JSONObject install(String addon, String mode) {
        return iCoreAddonInstallService.install(addon, mode);
    }

    /**
     * 获取安装任务
     */
    @Override
    public JSONObject getInstallTask() {
        return iCoreAddonInstallService.getInstallTask();
    }

    /**
     * 取消安装
     */
    @Override
    public void cancleInstall() {
        iCoreAddonInstallService.cancleInstall();
    }

    /**
     * 插件安装检测
     * @param addon
     */
    public JSONObject installCheck(String addon) {
        return iCoreAddonInstallService.installCheck(addon);
    }

    /**
     * 获取已安装的插件列表
     * @return
     */
    @Override
    public Map<String, InstallAddonListVo> getInstallList() {
        return iCoreAddonService.getInstallAddonList();
    }

    /**
     * 卸载插件
     * @param addon
     */
    @Override
    public JSONObject uninstall(String addon) {
        return iCoreAddonInstallService.uninstall(addon);
    }

    /**
     * 插件卸载环境检测
     * @param addon
     */
    public JSONObject uninstallCheck(String addon) {
        return iCoreAddonInstallService.uninstallCheck(addon);
    }

    /**
     * 通过key 获取标题
     *
     * @param keys
     * @return JSONArray 字符串
     */
    @Override
    public String getTitleListByKey(String keys) {

        JSONArray jsonKey=JSONUtil.parseArray(keys);
        JSONArray jsonArray=new JSONArray();
        if(jsonKey.size()>0){
            List<String> keyList=jsonKey.toList(String.class);
            List<Addon> addonList=addonMapper.selectList(new QueryWrapper<Addon>().in("`key`", keyList));
            for (Addon addon:addonList) {
                jsonArray.put(addon.getTitle());
            }
        }
        return jsonArray.toString();
    }

    @Override
    public List<Addon> getAddonListByKeys(List<String> addonKeys, String type) {

        return cached.rememberObject(useCache, cacheTagName, Arrays.asList("getAddonListByKeys", addonKeys, type), uniqueKey -> {

            QueryWrapper<Addon> query = new QueryWrapper<>();
            if(type.equals(""))
            {
                query.in("`key`", addonKeys);
            }else{
                query.in("`key`", addonKeys).eq("type", type);
            }
            return addonMapper.selectList(query);
        });

    }

    /**
     * 下载插件
     *
     * @param addon
     * @param version
     */
    public void download(String addon, String version) {
        NiucloudUtils instance = NiucloudUtils.getInstance();

        Map<String, Object> actionQuery = new HashMap<>();
        actionQuery.put("data[app_key]", addon);
        actionQuery.put("data[version]", version);
        actionQuery.put("data[product_key]", GlobalConfig.productKey);
        JSONObject actionToken = niucloudService.getActionToken("download", actionQuery);

        Map<String, Object> query = new HashMap<>();
        query.put("authorize_code", instance.getCode());
        query.put("addon_name", addon);
        query.put("version", version);
        query.put("token", actionToken == null ? "" : actionToken.getStr("token"));

        HttpResponse headResponse = new NiucloudUtils.Cloud().build("cloud/download").header("Range", "bytes=0-").query(query).method(Method.HEAD).execute();
        String totalLength = headResponse.header("Content-range");
        String length = totalLength.split("/")[1];

        String downloadDir = WebAppEnvs.get().webRootDownResource + "download/";
        FileTools.createDirs(downloadDir);

        File file = new File(downloadDir + addon + ".zip");
        if (file.exists()) file.delete();

        HttpResponse response = new NiucloudUtils.Cloud().build("cloud/download").header("Range", "bytes=0-" + length).query(query).method(Method.GET).execute();

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(response.bodyBytes());
            ZipUtil.unzip(file.getPath(), WebAppEnvs.get().webRootDownAddon, Charset.forName(System.getProperty("sun.jnu.encoding")));
        }  catch (Exception e) {
            e.printStackTrace();
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public JSONArray getIndexAddonList(IndexAddonListParam param) {
        Map<String, Object> params = new HashMap<>();
        NiucloudConfigVo config = coreNiucloudConfigService.getNiucloudConfig();
        params.put("code", config.getAuthCode());
        params.put("secret", config.getAuthSecret());
        params.put("labels", Arrays.asList(ObjectUtil.isEmpty(param.getLabelId()) ? new String[]{} : new String[] { param.getLabelId() }));
        params.put("product_key", GlobalConfig.productKey);
        params.put("is_recommend", 1);
        params.put("recommend_type", "JAVA");
        params.put("order_field", "sale_num desc, visit_num desc");
        JSONObject jsonObject = NiucloudUtils.Niucloud.get("store/saas/app", params);
        return jsonObject.getJSONArray("data");
    }

    /**
     * 云安装进度
     *
     * @param addon
     * @return
     */
    @Override
    public JSONObject cloudInstallLog(String addon) {
        return iCoreAddonInstallService.cloudInstallLog(addon);
    }
}
