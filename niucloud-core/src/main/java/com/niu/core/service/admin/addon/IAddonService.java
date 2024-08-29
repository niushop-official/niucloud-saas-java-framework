package com.niu.core.service.admin.addon;

import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageResult;
import com.niu.core.entity.addon.Addon;
import com.niu.core.service.admin.addon.param.AddonParam;
import com.niu.core.service.admin.addon.param.AddonSearchParam;
import com.niu.core.service.admin.addon.vo.AddonInfoVo;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.addon.vo.LocalAddonListVo;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;

import java.util.ArrayList;
import java.util.Map;

/**
 * 插件服务接口类
 */
public interface IAddonService {

    /**
     * 获取本地插件
     */
    LocalAddonListVo getLocalAddonList();

    /**
     * 插件列表
     *
     * @param pageParam 分页参数
     * @param addonSearchParam 搜索参数
     * @return PageResult<AddonListedVo>
     */
    PageResult<Addon> list(PageParam pageParam, AddonSearchParam addonSearchParam);

    /**
     * 插件详情
     *
     * @param id 主键ID
     * @return AddonDetailVo
     */
    AddonInfoVo info(Integer id);

    /**
     * 插件新增
     *
     * @param addonParam 参数
     */
    void add(AddonParam addonParam);

    /**
     * 插件删除
     *
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 安装插件
     *
     * @param addon
     */
    JSONObject install(String addon, String model);

    /**
     * 云安装进度
     *
     * @param addon
     * @return
     */
    JSONObject cloudInstallLog(String addon);

    /**
     * 取消安装
     */
    void cancleInstall();

    /**
     * 安装检测
     *
     * @param addon
     */
    JSONObject installCheck(String addon);

    /**
     * 获取安装任务
     *
     * @return
     */
    JSONObject getInstallTask();

    /**
     * 获取已安装的插件列表
     *
     * @return
     */
    Map<String, InstallAddonListVo> getInstallList();

    /**
     * 卸载插件
     *
     * @param addon
     */
    JSONObject uninstall(String addon);

    /**
     * 卸载环境检测
     *
     * @param addon
     */
    JSONObject uninstallCheck(String addon);

    /**
     * 通过key 获取标题
     *
     * @param keys
     * @return JSONArray 字符串
     */
    String getTitleListByKey(String keys);

    /**
     * 下载插件
     *
     * @param addon
     * @param version
     */
    void download(String addon, String version);
}
