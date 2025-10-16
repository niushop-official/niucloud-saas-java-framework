package com.niu.core.enums.notice;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.enums.notice.vo.NoticeEnumListVo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.catalina.mbeans.MBeanUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通知类型
 */
public class NoticeEnum {

    /**
     * 获取消息类型
     * @return
     */
    public static Map<String, NoticeEnumListVo> getNotice(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        JSONObject notice = jsonModuleLoader.mergeResultElement(RequestUtils.siteId(), "notice/notice.json");
        Map<String, NoticeEnumListVo> noticeMap = new HashMap<>();
        for (Map.Entry<String, Object> noticeItem : notice.entrySet()) {
            NoticeEnumListVo vo = new NoticeEnumListVo();
            BeanUtil.copyProperties(noticeItem.getValue(), vo);
            noticeMap.put(noticeItem.getKey(), vo);
        }
        //组装notice
        Map<String, JSONObject> noticeType = new HashMap<>();
        Map<String , String> noticeTypeName = NoticeTypeEnum.getMap();
        for (Map.Entry<String, String> entry : noticeTypeName.entrySet()) {
            noticeType.put(entry.getKey(), jsonModuleLoader.mergeResultElement("notice/"+entry.getKey()+".json"));
        }
        for (Map.Entry<String, NoticeEnumListVo> noticeItemMap : noticeMap.entrySet()) {
            // 获取键和值
            for(Map.Entry<String, JSONObject> noticeTypeItem : noticeType.entrySet())
            {
                for (Map.Entry<String, Object> noticeTypeItemMap : noticeTypeItem.getValue().entrySet())
                {
                    // 判断键是否相等
                    if(noticeTypeItemMap.getKey().equals(noticeItemMap.getKey()))
                    {
                        List<String> support_type = noticeItemMap.getValue().getSupport_type();
                        if(ObjectUtil.isEmpty(support_type))
                        {
                            support_type = new ArrayList<>();

                        }
                        support_type.add(noticeTypeItem.getKey());
                        noticeItemMap.getValue().setSupport_type(support_type);
                        Map<String, JSONObject> support_type_map = noticeItemMap.getValue().getSupport_type_map();
                        if(ObjectUtil.isEmpty(support_type_map))
                        {
                            support_type_map = new HashMap<>();

                        }
                        support_type_map.put(noticeTypeItem.getKey(),(JSONObject) noticeTypeItemMap.getValue());
                        noticeItemMap.getValue().setSupport_type_map(support_type_map);
                    }
                }

            }
        }
        return noticeMap;
    }

    public static Map<String, NoticeEnumListVo> getNiuyunNotice(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        JSONObject notice = jsonModuleLoader.mergeResultElement(RequestUtils.siteId(), "notice/notice.json");
        Map<String, NoticeEnumListVo> noticeMap = new HashMap<>();

        for (Map.Entry<String, Object> noticeItem : notice.entrySet()) {
            NoticeEnumListVo vo = new NoticeEnumListVo();
            BeanUtil.copyProperties(noticeItem.getValue(), vo);
            noticeMap.put(noticeItem.getKey(), vo);
        }

        Map<String, String> noticeTypeName = NoticeTypeEnum.getMap();

        for (String type : noticeTypeName.keySet()) {
            JSONObject defaultConfig = jsonModuleLoader.mergeResultElement("notice/" + type + ".json");
            //加载牛云配置
            JSONObject niuyunConfig = jsonModuleLoader.mergeResultElement("notice/niuyun/" + type + ".json");

            for (Map.Entry<String, NoticeEnumListVo> entry : noticeMap.entrySet()) {
                String noticeKey = entry.getKey();
                NoticeEnumListVo vo = entry.getValue();

                // 优先使用niuyun目录的配置（如果存在）
                if (niuyunConfig != null && niuyunConfig.containsKey(noticeKey)) {
                    addOrUpdateSupportType(vo, type, niuyunConfig.getJSONObject(noticeKey));
                }

                // 如果没有niuyun配置，使用默认配置
                else if (defaultConfig != null && defaultConfig.containsKey(noticeKey)) {
                    addOrUpdateSupportType(vo, type, defaultConfig.getJSONObject(noticeKey));
                }
            }
        }

        return noticeMap;
    }

    private static void addOrUpdateSupportType(NoticeEnumListVo vo, String type, JSONObject config) {
        if (vo.getSupport_type() == null) {
            vo.setSupport_type(new ArrayList<>());
        }
        if (!vo.getSupport_type().contains(type)) {
            vo.getSupport_type().add(type);
        }

        if (vo.getSupport_type_map() == null) {
            vo.setSupport_type_map(new HashMap<>());
        }
        // 设置或更新配置（niuyun的配置会覆盖默认配置）
        vo.getSupport_type_map().put(type, config);
    }
}
