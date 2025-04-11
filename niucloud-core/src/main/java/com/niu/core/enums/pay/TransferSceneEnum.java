package com.niu.core.enums.pay;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信转账场景
 */
@Getter
@AllArgsConstructor
public enum TransferSceneEnum {
    YJBC("yjbc", "佣金报酬", Arrays.asList("劳务报酬", "报销款", "企业补贴", "开工利是"), Arrays.asList("岗位类型", "报酬说明")),
    XJYX("xjyx", "现金营销", Arrays.asList("活动奖励", "现金奖励"), Arrays.asList("活动名称", "奖励说明")),
    QYPF("qypf", "企业赔付", Arrays.asList("退款", "商家赔付"), Arrays.asList("赔付原因")),
    CGHK("cghk", "采购货款", Arrays.asList("货款"), Arrays.asList("采购商品名称")),
    ESHS("eshs", "二手回收", Arrays.asList("二手回收货款"), Arrays.asList("回收商品名称")),
    GYBZ("gybz", "公益补助", Arrays.asList("公益补助金"), Arrays.asList("公益活动名称", "公益活动备案编号")),
    XZBT("xzbt", "行政补贴", Arrays.asList("行政补贴", "行政奖励"), Arrays.asList("补贴类型")),
    BXLP("bxlp", "保险理赔", Arrays.asList("保险理赔款"), Arrays.asList("保险产品备案编号", "保险名称", "保险操作单号"));

    private final String scene;
    private final String name;
    private final List<String> userRecvPerception;
    private final List<String> transferSceneReportInfos;

    public String getScene() {
        return scene;
    }

    public String getName() {
        return name;
    }

    public List<String> getUserRecvPerception() {
        return userRecvPerception;
    }

    public List<String> getTransferSceneReportInfos() {
        return transferSceneReportInfos;
    }

    public static java.util.Map<String, TransferSceneEnum> getWechatTransferSceneMap() {
        java.util.Map<String, TransferSceneEnum> sceneMap = new java.util.HashMap<>();
        for (TransferSceneEnum scene : values()) {
            sceneMap.put(scene.getScene(), scene);
        }
        return sceneMap;
    }

}
