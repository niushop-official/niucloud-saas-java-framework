package com.niu.core.listener.sys;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.event.sys.ExportDataEventDefiner;
import com.niu.core.service.admin.member.IMemberService;
import com.niu.core.service.admin.member.param.MemberSearchParam;
import com.niu.core.service.admin.member.vo.MemberListVo;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;

@EventCallback("core")
@Component
public class MemberExportDataListener extends ExportDataEventDefiner {

    private final Class<? extends MemberListVo> clazz = MemberListVo.class;

    @Resource
    IMemberService memberService;

    @Override
    public ExportDataEventResult handleCallback(ExportDataEvent exportDataEvent) {
        JSONArray result = new JSONArray();
        ExportDataEventResult exportDataEventResult = new ExportDataEventResult();
        if (exportDataEvent.getType().equals("member")) {
            JSONObject whereJson = exportDataEvent.getWhere();

            MemberSearchParam memberSearchParam = new MemberSearchParam();
            memberSearchParam.setMemberLabel(whereJson.getInt("member_label", 0));
            memberSearchParam.setMemberLevel(whereJson.getInt("member_level", 0));
            String[] createTime = whereJson.get("create_time", String[].class);
            memberSearchParam.setCreateTime(createTime);
            memberSearchParam.setKeyword(whereJson.getStr("keyword", ""));
            memberSearchParam.setRegisterChannel(whereJson.getStr("register_channel", ""));
            memberSearchParam.setRegisterType(whereJson.getStr("register_type", ""));
            memberSearchParam.setIsDel(whereJson.getInt("is_del", 0));
            PageResult<MemberListVo> memberListVoPageResult = memberService.list(exportDataEvent.getPageParam(), memberSearchParam);

            for (MemberListVo vo : memberListVoPageResult.getData()) {
                JSONObject jsonObject = new JSONObject();
                ReflectionUtils.doWithFields(clazz, field -> {
                    field.setAccessible(true);
                    String name = field.getName();
                    Object value = field.get(vo);

                    JsonSerialize anno = field.getAnnotation(JsonSerialize.class);
                    if (anno != null && anno.using().equals(BeanJsonSerializer.LongDateToStringSerializer.class) &&
                            value instanceof Long) {
                        value = DateUtils.timestampToString((Long) value);
                    }

                    jsonObject.set(StrUtil.toUnderlineCase(name), value);
                });
                result.add(jsonObject);
            }
        }
        exportDataEventResult.setResultData(result);
        return exportDataEventResult;
    }
}
