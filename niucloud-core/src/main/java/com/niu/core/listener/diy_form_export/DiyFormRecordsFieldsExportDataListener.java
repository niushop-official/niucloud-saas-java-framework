package com.niu.core.listener.diy_form_export;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.event.sys.ExportDataEventDefiner;
import com.niu.core.service.admin.diy_form.IDiyFormRecordsService;
import com.niu.core.service.admin.diy_form.param.DiyFormRecordsSearchParam;
import com.niu.core.service.admin.diy_form.vo.DiyFormFieldsListVo;
import com.niu.core.service.admin.diy_form.vo.DiyFormRecordsListVo;
import com.niu.core.service.admin.member.IMemberService;
import com.niu.core.service.admin.member.param.MemberSearchParam;
import com.niu.core.service.admin.member.vo.MemberListVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.util.List;

@EventCallback("core")
@Component
public class DiyFormRecordsFieldsExportDataListener extends ExportDataEventDefiner {

    private final Class<? extends MemberListVo> clazz = MemberListVo.class;

    @Resource
    IDiyFormRecordsService diyFormRecordsService;

    @Override
    public ExportDataEventResult handleCallback(ExportDataEvent exportDataEvent) {
        JSONArray result = new JSONArray();
        ExportDataEventResult exportDataEventResult = new ExportDataEventResult();
        if (exportDataEvent.getType().equals("diy_form_records_fields")) {
            JSONObject whereJson = exportDataEvent.getWhere();

            RequestUtils.setSiteId(exportDataEvent.getSiteId());

            DiyFormRecordsSearchParam searchParam = new DiyFormRecordsSearchParam();
            searchParam.setFormId(whereJson.getInt("form_id", 0));
            searchParam.setKeyword(whereJson.getStr("keyword", ""));

            List<DiyFormFieldsListVo> records = diyFormRecordsService.getFieldStatList(searchParam);
            if (ObjectUtil.isNotEmpty(records)) {
                for (DiyFormFieldsListVo vo : records) {
                     vo.getValueList().stream().forEach(item -> {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.set("field_name", vo.getFieldName());
                        jsonObject.set("render_value", item.get("render_value"));
                        jsonObject.set("write_count", item.get("write_count"));
                        jsonObject.set("write_percent", item.get("write_percent"));
                        result.add(jsonObject);
                    });
                }
            }
        }
        exportDataEventResult.setResultData(result);
        return exportDataEventResult;
    }
}
