package com.niu.core.common.component.loader;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

public interface ILoaderProvider {

    /**
     * 按插件名称加载JSON文件，返回：JSON
     *
     * @param addon
     * @param location
     * @return
     */
    public JSON loadJSON(String addon, String location);

    /**
     * 按插件名称加载JSON文件，返回：JSONObject
     *
     * @param addon
     * @param location
     * @return
     */
    public JSONObject loadJSONObject(String addon, String location);

    /**
     * 按插件名称加载JSON文件，返回：JSONArray
     *
     * @param addon
     * @param location
     * @return
     */
    public JSONArray loadJSONArray(String addon, String location);

    /**
     * 合并结果集元素（将JSON对象按跟节点合并成一个JSON对象）
     * <p>
     * eg:  {demo1:{name1:value1}}  {demo2:{name2:value2}}  合并后：{demo1:{name1:value1},demo2:{name2:value2}}
     *
     * @param location
     * @return
     */
    public JSONObject mergeJSONObject(String location);

    /**
     * 合并结果集元素（将JSON数组合并成一个JSON数组）
     * <p>
     * eg:  [{name1:value1}]  [{name2:value2}] 合并后： [{name1:value1},{name2:value2}]
     *
     * @param location
     * @return
     */
    public JSONArray mergeJSONArray(String location);

}
