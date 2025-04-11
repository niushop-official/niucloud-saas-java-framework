package com.niu.core.service.core.pay.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.entity.pay.PayTransferScene;
import com.niu.core.enums.pay.TransferSceneEnum;
import com.niu.core.enums.sys.ConfigKeyEnum;
import com.niu.core.mapper.pay.PayTransferSceneMapper;
import com.niu.core.service.core.pay.ICoreTransferSceneService;
import com.niu.core.service.core.pay.param.SetTradeSceneParam;
import com.niu.core.service.core.pay.vo.TransferSceneInfo;
import com.niu.core.service.core.pay.vo.WechatTransferSceneListVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoreTransferSceneServiceImpl implements ICoreTransferSceneService {

    private final ICoreConfigService coreConfigService;
    private final PayTransferSceneMapper payTransferSceneMapper;

    /**
     * 获取微信转账场景配置
     * @param siteId
     */
    @Override
    public JSONObject getWechatTransferSceneConfig(Integer siteId) {
        JSONObject config = coreConfigService.getConfigValue(siteId, ConfigKeyEnum.WECHAT_TRANSFER_SCENE_CONFIG.getName());
        if (ObjectUtil.isNull(config)) return new JSONObject();
        return config;
    }

    /**
     * 设置微信转账场景配置
     * @param siteId
     */
    @Override
    public void setWechatTransferSceneConfig(Integer siteId, JSONObject data) {
        coreConfigService.setConfig(siteId, ConfigKeyEnum.WECHAT_TRANSFER_SCENE_CONFIG.getName(), data);
    }

    /**
     * 获取转账场景
     * @param siteId
     */
    @Override
    public Map<String, WechatTransferSceneListVo> getWechatTransferScene(Integer siteId) {
        Map<String, TransferSceneEnum> tradeScenelist = TransferSceneEnum.getWechatTransferSceneMap();

        JSONObject config = getWechatTransferSceneConfig(siteId);

        JSONObject tradeSceneEventArray = JsonModuleLoader.build().mergeResultElement(RequestUtils.siteId(), "pay/wechat_transfer_scene.json");

        Map<String, PayTransferScene> tradeSceneColumn = payTransferSceneMapper.selectList(new QueryWrapper<PayTransferScene>().eq("site_id", siteId)).stream().collect(Collectors.toMap(PayTransferScene::getType, i -> i));

        JSONObject tradeSceneList = new JSONObject();
        for(String key: tradeSceneEventArray.keySet()) {
            JSONObject tradeSceneItem = tradeSceneEventArray.getJSONObject(key);

            PayTransferScene tradeSceneSelectData = tradeSceneColumn.get(key);
            if (tradeSceneSelectData == null) {
                tradeSceneList.putByPath(tradeSceneItem.getStr("scene") + "." + key, tradeSceneItem);
            } else {
                tradeSceneList.putByPath(tradeSceneItem.getStr("scene") + "." + key, tradeSceneSelectData);
                tradeSceneList.putByPath(tradeSceneItem.getStr("scene") + "." + key + ".infos", JSONUtil.parseObj(tradeSceneSelectData.getInfos()));
                tradeSceneList.putByPath(tradeSceneItem.getStr("scene") + "." + key + ".name", tradeSceneItem.getStr("name"));
            }
        }

        Map<String, WechatTransferSceneListVo> list = new HashMap<>();
        for (Map.Entry<String, TransferSceneEnum> item : tradeScenelist.entrySet()) {
            WechatTransferSceneListVo vo = new WechatTransferSceneListVo();
            vo.setName(item.getValue().getName());
            vo.setTransferSceneReportInfos(item.getValue().getTransferSceneReportInfos());
            vo.setUserRecvPerception(item.getValue().getUserRecvPerception());
            vo.setSceneId(config.getStr(item.getValue().getScene(), ""));
            if (tradeSceneList.get(item.getValue().getScene()) != null) {
                vo.setTradeSceneData(tradeSceneList.getJSONObject(item.getValue().getScene()));
            }
            list.put(item.getValue().getScene(), vo);
        }

        return list;
    }

    /**
     * 通过业务设置转账场景的配置信息
     * @param siteId
     */
    @Override
    public void setTradeScene(Integer siteId, String type, SetTradeSceneParam param) {
        PayTransferScene tradeScene = payTransferSceneMapper.selectOne(new QueryWrapper<PayTransferScene>().eq("site_id", siteId).eq("type", type));

        PayTransferScene model = new PayTransferScene();
        model.setSiteId(siteId);
        model.setType(type);
        model.setPerception(param.getPerception());
        model.setScene(param.getScene());
        model.setInfos(param.getInfos().toString());

        if (tradeScene == null) {
            model.setCreateTime(System.currentTimeMillis() / 1000);
            payTransferSceneMapper.insert(model);
        } else {
            model.setId(tradeScene.getId());
            payTransferSceneMapper.updateById(model);
        }
    }

    /**
     * 通过业务获取转账场景的配置信息
     */
    @Override
    public TransferSceneInfo getSceneInfoByType(Integer siteId, String type) {
        PayTransferScene tradeScene = payTransferSceneMapper.selectOne(new QueryWrapper<PayTransferScene>().eq("site_id", siteId).eq("type", type));

        TransferSceneInfo vo = new TransferSceneInfo();
        if (tradeScene == null) {
            JSONObject transferScene = JsonModuleLoader.build().mergeResultElement(RequestUtils.siteId(), "pay/wechat_transfer_scene.json");
            vo = JSONUtil.toBean(transferScene.getJSONObject(type), TransferSceneInfo.class);
        } else {
            BeanUtil.copyProperties(tradeScene, vo);
        }

        JSONObject config = getWechatTransferSceneConfig(siteId);
        vo.setSceneId(config.getStr(vo.getScene(), ""));

        return vo;
    }
}
