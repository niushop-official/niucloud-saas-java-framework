package com.niu.core.service.api.diy.vo;

import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.niu.core.enums.diy_form.DiyFormComponentEnum;
import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
* 万能表单填写字段表列表
*/
@Data
public class DiyFormRecordsFieldsListVo implements Serializable {

        private static final long serialVersionUID = 1L;

        /**  */
        private Integer id;

        /** 站点id */
        private Integer siteId;

        /** 所属万能表单id */
        private Integer formId;

        /** 关联表单字段id */
        private Integer formFieldId;

        /** 关联表单填写记录id */
        private Integer recordId;

        /** 填写会员id */
        private Integer memberId;

        /** 字段唯一标识 */
        private String fieldKey;

        /** 字段类型 */
        private String fieldType;

        /** 字段名称 */
        private String fieldName;

        /** 字段值，根据类型展示对应效果 */
        private String fieldValue;

        /** 字段是否必填 0:否 1:是 */
        private Integer fieldRequired;

        /** 字段是否隐藏 0:否 1:是 */
        private Integer fieldHidden;

        /** 字段内容防重复 0:否 1:是 */
        private Integer fieldUnique;

        /** 隐私保护 0:关闭 1:开启 */
        private Integer privacyProtection;

        /** 字段修改次数 */
        private Integer updateNum;

        private Object handleFieldValue;

        private Object renderValue;

        /**
         * 最后解析
         * @return
         */
        public Object getHandleFieldValue(){
                DiyFormComponentEnum diyFormComponentEnum = new DiyFormComponentEnum();
                JSONObject component = diyFormComponentEnum.getComponent(this.fieldType);
                if(component.containsKey("convert")){
                        String driver = component.getStr("convert");
                        if(ObjectUtil.isNotEmpty(driver))
                        {
                                try {
                                        Class<?> clazz = ClassLoaderUtil.loadClass(driver);
                                        Object obj = clazz.getDeclaredConstructor().newInstance();
                                        Method method = clazz.getMethod("convert", String.class, String.class);
                                        return method.invoke(obj, this.fieldValue, this.fieldType);
                                }catch (Exception e){
                                        e.printStackTrace();
                                }

                        }


                }
                return this.fieldValue;

        }

        public Object getRenderValue() {

                DiyFormComponentEnum diyFormComponentEnum = new DiyFormComponentEnum();
                JSONObject component = diyFormComponentEnum.getComponent(this.fieldType);
                if(component.containsKey("render")){
                        String driver = component.getStr("render");
                        if(ObjectUtil.isNotEmpty(driver))
                        {
                                try{
                                        Class<?> clazz = ClassLoaderUtil.loadClass(driver);
                                        Object obj = clazz.getDeclaredConstructor().newInstance();
                                        Method method = clazz.getMethod("render", String.class, String.class);
                                        return method.invoke(obj, this.fieldValue, this.fieldType);
                                }catch (Exception e){
                                        e.printStackTrace();
                                }

                        }


                }
                return this.fieldValue;
        }

        public String getDetailComponent(){
                DiyFormComponentEnum diyFormComponentEnum = new DiyFormComponentEnum();
                JSONObject component = diyFormComponentEnum.getComponent(this.fieldType);
                if(ObjectUtil.isNotEmpty(component) && component.containsKey("value") && component.containsKey("field") && component.containsKey("detailComponent"))
                {
                        return component.getStr("detailComponent");
                }else{
                        return "/src/app/views/diy_form/components/detail-form-render.vue";
                }
        }




}
