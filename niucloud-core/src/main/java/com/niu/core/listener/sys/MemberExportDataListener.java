package com.niu.core.listener.sys;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.domain.PageResult;
import com.niu.core.event.sys.ExportDataEventDefiner;
import com.niu.core.service.admin.member.IMemberService;
import com.niu.core.service.admin.member.param.MemberSearchParam;
import com.niu.core.service.admin.member.vo.MemberListVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@EventCallback("core")
@Component
public class MemberExportDataListener extends ExportDataEventDefiner {

    @Resource
    IMemberService memberService;

    @Override
    public ExportDataEventResult handleCallback(ExportDataEvent exportDataEvent) {
        JSONArray result=new JSONArray();
        ExportDataEventResult exportDataEventResult=new ExportDataEventResult();
        if(exportDataEvent.getType().equals("member")){
            JSONObject whereJson=exportDataEvent.getWhere();

            MemberSearchParam memberSearchParam=new MemberSearchParam();
            memberSearchParam.setMemberLabel(whereJson.getInt("member_label", 0));
            memberSearchParam.setMemberLevel(whereJson.getInt("member_level", 0));
            String[] createTime=whereJson.get("create_time", String[].class);
            memberSearchParam.setCreateTime(createTime);
            memberSearchParam.setKeyword(whereJson.getStr("keyword", ""));
            memberSearchParam.setRegisterChannel(whereJson.getStr("register_channel", ""));
            memberSearchParam.setRegisterType(whereJson.getStr("register_type", ""));
            memberSearchParam.setIsDel(whereJson.getInt("is_del", 0));
            PageResult<MemberListVo> memberListVoPageResult= memberService.list(exportDataEvent.getPageParam(), memberSearchParam);
            List<MemberListVo> memberList=memberListVoPageResult.getData();
            result= JSONUtil.parseArray(memberList);
        }
        exportDataEventResult.setResultData(result);
        return exportDataEventResult;
    }
}
