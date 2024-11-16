package com.niu.core.common.utils;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.json.JacksonUtils;
import com.niu.core.common.utils.json.JsonLoadUtils;
import com.niu.core.entity.sys.SysMenu;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * 树工具类
 */
public class TreeUtils {

    /**
     * JSONArray转树形结构
     *
     * @param arr   JSON数组
     * @param id    主键字段名
     * @param pid   上级字段名
     * @param child 子级字段名
     * @return JSONArray
     */
    public static JSONArray listToTree(JSONArray arr, String id, String pid, String child) {
        JSONArray jsonArray = new JSONArray();
        JSONObject hash = new JSONObject();
        // 顶级数组
        for (Object o : arr) {
            JSONObject json = (JSONObject) o;
            hash.put(json.getStr(id), json);
        }
        // 遍历结果集
        for (Object o : arr) {
            // 单条记录
            JSONObject aVal = (JSONObject) o;
            if (aVal.get(pid) != null) {
                // 在hash中取出key为单条记录中pid的值
                JSONObject hashVP = (JSONObject) hash.get(aVal.get(pid).toString());
                // 如果记录的pid存在,则说明它有父节点,将她添加到孩子节点的集合中
                if (hashVP != null) {
                    // 检查是否有child属性
                    if (hashVP.get(child) != null) {
                        JSONArray ch = (JSONArray) hashVP.get(child);
                        ch.add(aVal);
                        hashVP.put(child, ch);
                    } else {
                        JSONArray ch = new JSONArray();
                        ch.add(aVal);
                        hashVP.put(child, ch);
                    }
                } else {
                    jsonArray.add(aVal);
                }
            }
        }
        return jsonArray;
    }

    /**
     * @param jsonArray
     * @param parentKey
     * @param parentValue
     * @param currentKey
     * @param childrenKey
     * @return
     */
    public static List<cn.hutool.json.JSONObject> treeToList(cn.hutool.json.JSONArray jsonArray, String parentKey, String parentValue,
                                                             String currentKey, String childrenKey) {
        List<cn.hutool.json.JSONObject> jsonObjectList = new CopyOnWriteArrayList<>();
        treeToList(jsonObjectList, parentKey, parentValue, currentKey, childrenKey, jsonArray);
        return jsonObjectList;
    }

    /**
     * @param jsonObjectList
     * @param parentKey
     * @param parentValue
     * @param currentKey
     * @param childrenKey
     * @param jsonArray
     * @return
     */
    public static List<cn.hutool.json.JSONObject> treeToList(List<cn.hutool.json.JSONObject> jsonObjectList, String parentKey, String parentValue,
                                                             String currentKey, String childrenKey, cn.hutool.json.JSONArray jsonArray) {
        if (jsonObjectList == null) {
            jsonObjectList = new CopyOnWriteArrayList<>();
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            cn.hutool.json.JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject.get(parentKey) == null) jsonObject.put(parentKey, parentValue);
            String currentKeyValue = jsonObject.getStr(currentKey);
            cn.hutool.json.JSONArray childrenKeyArray = jsonObject.getJSONArray(childrenKey);
            jsonObject.remove(childrenKey);
            jsonObjectList.add(jsonObject);
            if (childrenKeyArray != null && childrenKeyArray.size() > 0) {
                treeToList(jsonObjectList, parentKey, currentKeyValue, currentKey, childrenKey, childrenKeyArray);
            }
        }
        return jsonObjectList;
    }

    public static void main(String[] args) {
        String path = "D:\\MyData\\NiuCloud\\NiuShop.20240620\\niucloud-admin-java\\webroot\\addon\\shop\\menu\\";
        File file = new File(path + "site.json");
        String jsonString = JsonLoadUtils.loadJsonString(file);
        cn.hutool.json.JSONArray jsonArray = JSONUtil.parseArray(jsonString);
        // System.out.println(jsonArray);
        List<cn.hutool.json.JSONObject> jsonObjectList = treeToList(jsonArray, "parent_key", "", "menu_key", "children");
        jsonObjectList.stream().forEach(json -> {
            System.out.println(json);
        });
        List<SysMenu> beanList = JacksonUtils.toSnakeCaseList(jsonObjectList, SysMenu.class);
        beanList.stream().forEach(b -> {
            // System.out.println(b);
        });
    }

}
