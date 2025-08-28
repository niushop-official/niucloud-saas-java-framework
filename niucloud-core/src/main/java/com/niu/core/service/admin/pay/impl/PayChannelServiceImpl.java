package com.niu.core.service.admin.pay.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.pay.PayChannel;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.core.mapper.pay.PayChannelMapper;
import com.niu.core.service.admin.pay.IPayChannelService;
import com.niu.core.service.admin.pay.param.PayChannelAllSetParam;
import com.niu.core.service.admin.pay.vo.PayChannelListVo;
import com.niu.core.service.admin.pay.vo.PayChanneltemVo;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * 支付渠道配置实现
 */
@Log4j2
@Service
public class PayChannelServiceImpl implements IPayChannelService {

    @Resource
    PayChannelMapper payChannelMapper;

    /**
     * 支付渠道配置列表
     *
     * @return PageResult<PayChannelListVo>
     */
    @Override
    public Map<String, PayChanneltemVo> list() {
        List<PayChannel> channelList = payChannelMapper.selectList(new QueryWrapper<PayChannel>()
                .eq("site_id", RequestUtils.siteId()));

        JSONObject payChannelList = new JSONObject();
        for (PayChannel item : channelList) {
            JSONObject payTypeList = payChannelList.getJSONObject(item.getChannel());
            if (payTypeList != null) {
                payTypeList.set(item.getType(), item);
            } else {
                payTypeList = new JSONObject();
                payTypeList.set(item.getType(), JSONUtil.parse(item));
            }
            payChannelList.set(item.getChannel(), payTypeList);
        }

        JSONObject payTypeEnum = PayTypeEnum.getType();

        Map<String, PayChanneltemVo> list = new HashMap<String, PayChanneltemVo>();

        for (ChannelEnum item : ChannelEnum.values()) {
            PayChanneltemVo vo = new PayChanneltemVo();
            vo.setKey(item.getCode());
            vo.setName(item.getName());

            JSONArray payType = new JSONArray();
            for (String key : payTypeEnum.keySet()) {
                JSONObject type = JSONUtil.parseObj(payTypeEnum.getJSONObject(key).toString());
                if (payChannelList.getJSONObject(item.getCode()) == null || payChannelList.getJSONObject(item.getCode()).getJSONObject(key) == null) {
                    type.set("status", 0);
                    type.set("config", new JSONObject().set("name", ""));
                    type.set("sort", 0);
                } else {
                    JSONObject config = payChannelList.getJSONObject(item.getCode()).getJSONObject(key);
                    type.set("status", config.getInt("status"));
                    try {
                        type.set("config", JSONUtil.parseObj(config.getStr("config")));
                    } catch (Exception e) {
                        type.set("config", new JSONObject().set("name", ""));
                    }
                    type.set("sort", config.getInt("sort"));
                }
                payType.add(type);
            }

            // 支付方式排序
            List<JSONObject> payTypeList = new ArrayList<>();
            for (int i = 0; i < payType.size(); i++) {
                payTypeList.add(payType.getJSONObject(i));
            }
            Comparator<JSONObject> comparator = (o1, o2) -> {
                int sort1 = o1.getInt("sort");
                int sort2 = o2.getInt("sort");
                return Integer.compare(sort1, sort2);
            };
            Collections.sort(payTypeList, comparator);
            payType = new JSONArray(payTypeList);

            vo.setPayType(payType);
            list.put(item.getCode(), vo);
        }
        return list;
    }

    /**
     * 多渠道设置
     *
     * @param param
     */
    public void setAll(PayChannelAllSetParam param) {
        for (String channelKey : param.getConfig().keySet()) {
            JSONObject channel = param.getConfig().getJSONObject(channelKey);
            JSONArray payTypeList = channel.getJSONArray("pay_type");
            for (int i = 0; i < payTypeList.size(); i++) {
                JSONObject payType = payTypeList.getJSONObject(i);
                set(channel.getStr("key"), payType.getStr("key"), payType);
            }
        }
    }

    /**
     * 设置
     *
     * @param channel
     * @param type
     * @param data
     */
    public void set(String channel, String type, JSONObject data) {
        PayChannel payChannel = payChannelMapper.selectOne(new QueryWrapper<PayChannel>()
                .eq("site_id", RequestUtils.siteId())
                .eq("channel", channel)
                .eq("type", type)
        );
        if (ObjectUtil.isNotEmpty(payChannel)) {
            payChannel.setConfig(data.getJSONObject("config").toString());
            payChannel.setSort(data.getInt("sort"));
            payChannel.setStatus(data.getInt("status"));
            payChannelMapper.updateById(payChannel);
        } else {
            PayChannel model = new PayChannel();
            model.setSiteId(RequestUtils.siteId());
            model.setChannel(channel);
            model.setType(type);
            model.setConfig(data.getJSONObject("config").toString());
            model.setSort(data.getInt("sort"));
            model.setStatus(data.getInt("status"));
            payChannelMapper.insert(model);
        }
    }

    /**
     * 通过渠道获取配置
     *
     * @param channel
     * @return
     */
    public List<PayChannelListVo> getListByChannel(String channel) {
        List<PayChannel> payChannel = payChannelMapper.selectList(new QueryWrapper<PayChannel>()
                .eq("site_id", RequestUtils.siteId())
                .eq("channel", channel)
        );

        List<PayChannelListVo> list = new LinkedList<>();
        for (PayChannel item : payChannel) {
            PayChannelListVo vo = new PayChannelListVo();
            BeanUtils.copyProperties(item, vo);
            if (ObjectUtil.isNotEmpty(item.getConfig()) && "transfer".equals(channel)) {
                JSONObject config = JSONUtil.parseObj(item.getConfig());
                // 定义需要隐藏的配置项列表
                String[] keysToHide = {
                        "mch_secret_key",
                        "mch_secret_cert",
                        "mch_public_cert_path",
                        "wechat_public_cert_path",
                        "wechat_public_cert_id"
                };

                for (String key : keysToHide) {
                    String value = config.getStr(key);
                    if (ObjectUtil.isNotEmpty(value)) {
                        try {
                            config.set(key, StringUtils.hide(value, 0, value.length()));
                        } catch (Exception e) {
                            log.error("字段:{},值:{},支付设置脱敏失败{}", key, value, e.getMessage());
                        }
                    }
                }
                vo.setConfig(config.toString());
            }
            list.add(vo);
        }

        return list;
    }

    /**
     * 设置转账设置
     *
     * @param param
     */
    public void setTransfer(JSONObject param) {
        JSONObject alipayConfig = param.getJSONObject("alipay_config");
        JSONObject wechatpayConfig = param.getJSONObject("wechatpay_config");

        if (wechatpayConfig != null) {
            this.set("transfer", "wechatpay", new JSONObject()
                    .set("config", wechatpayConfig)
                    .set("status", 1)
                    .set("sort", 0));
        }

        if (alipayConfig != null) {
            this.set("transfer", "alipay", new JSONObject()
                    .set("config", alipayConfig)
                    .set("status", 1)
                    .set("sort", 0));
        }
    }

}
