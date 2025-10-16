package com.niu.core.listener.sys;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.enums.site.AddonChildMenuEnum;
import com.niu.core.event.sys.ExportDataEventDefiner;
import com.niu.core.event.sys.ShowCustomerEventDefiner;
import com.niu.core.service.admin.member.IMemberService;
import com.niu.core.service.admin.member.param.MemberSearchParam;
import com.niu.core.service.admin.member.vo.MemberListVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventCallback("core")
@Component
public class ShowCustomerListener extends ShowCustomerEventDefiner {

    private final Class<? extends MemberListVo> clazz = MemberListVo.class;

    @Resource
    IMemberService memberService;

    @Override
    public ShowCustomerEventResult handleCallback(ShowCustomerEvent exportDataEvent) {
        ShowCustomerEventResult showCustomerEventResult = new ShowCustomerEventResult();
        Map<String, List<MenuItem>> result = new HashMap<>();

        // 系统工具菜单组
        List<MenuItem> systemToolMenus = new ArrayList<>();
        systemToolMenus.add(new MenuItem(
                "核销管理",
                "管理核销员及核销记录",
                "static/resource/images/marketing/verifier.png",
                "verify",
                "/marketing/verify/index"
        ));
        systemToolMenus.add(new MenuItem(
                "万能表单",
                "适用于各种应用场景，满足多样化的业务需求",
                "static/resource/images/diy_form/icon.png",
                "diy_form",
                "/diy_form/list"
        ));
        systemToolMenus.add(new MenuItem(
                "小票打印",
                "支持打印机添加，便捷创建小票打印模板",
                "static/resource/images/tool/printer_icon.png",
                "printer_management",
                "/printer/list"
        ));
        systemToolMenus.add(new MenuItem(
                "数据导出",
                "展示导出文件，支持删除与下载",
                "static/resource/images/tool/export_icon.png",
                "setting_export",
                "/setting/export"
        ));

        // 营销活动菜单组
        List<MenuItem> marketingActiveMenus = new ArrayList<>();
        marketingActiveMenus.add(new MenuItem(
                "签到管理",
                "客户每日签到发放奖励",
                "static/resource/images/marketing/sign.png",
                "sign",
                "/marketing/sign/config"
        ));

        // 工具菜单组（空）
        List<MenuItem> markingToolMenus = new ArrayList<>();

        // 放入结果Map
        result.put(AddonChildMenuEnum.SYSTEM_TOOL.getKey(), systemToolMenus);
        result.put(AddonChildMenuEnum.MARKETING_TOOL.getKey(), markingToolMenus);
        result.put(AddonChildMenuEnum.MARKETING_ACTIVE.getKey(), marketingActiveMenus);
        showCustomerEventResult.setData(result);
        return showCustomerEventResult;
    }
}
