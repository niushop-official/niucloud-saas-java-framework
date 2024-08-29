package com.niu.core.enums.channel;

import com.niu.core.enums.member.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 站点账户枚举类
 */
@Getter
@AllArgsConstructor
public enum WeappVersionStatusEnum {
    APPLET_UPLOADING("上传中", 0),
    APPLET_UPLOAD_SUCCESS("上传成功", 1),
    APPLET_AUDITING("审核中", 2),
    APPLET_UPLOAD_FAIL("上传失败", -1),
    APPLET_AUDIT_FAIL("审核未通过", -2),
    ;
    private final String name;
    private final Integer status;

    public static Map<Integer, String> getMap(){
        Map<Integer, String> map = new HashMap<>();
        for(WeappVersionStatusEnum item : WeappVersionStatusEnum.values())
        {
            map.put(item.getStatus(), item.getName());
        }
        return map;
    }

    /**
     * 通过status获取当前名称
     * @param status
     * @return
     */
    public static String getNameByStatus(Integer status){
        for(WeappVersionStatusEnum item : WeappVersionStatusEnum.values())
        {
            if(item.getStatus().equals(status))
            {
                return item.getName();
            }
        }
        return "";
    }
}
