package com.niu.core.service.core.notice.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.loader.notice.NoticeLoader;
import com.niu.core.common.utils.reflect.ReflectCallField;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.sys.SysNotice;
import com.niu.core.enums.notice.NoticeEnum;
import com.niu.core.enums.notice.vo.NoticeEnumListVo;
import com.niu.core.event.notice.SendNoticeEventDefiner;
import com.niu.core.mapper.sys.SysNoticeMapper;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.notice.ICoreNoticeService;
import com.niu.core.service.core.notice.vo.AddonNoticeListVo;
import com.niu.core.service.core.notice.vo.NoticeInfoVo;
import com.niu.core.service.core.site.ICoreSiteService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 消息管理服务层
 */
@Service
public class CoreNoticeServiceImpl implements ICoreNoticeService {

    @Resource
    SysNoticeMapper sysNoticeMapper;

    @Resource
    ICoreSiteService coreSiteService;

    @Override
    public SysNotice find(Integer siteId, String key) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("site_id", siteId);
        queryWrapper.eq("`key`", key);
        SysNotice sysNotice = sysNoticeMapper.selectOne(queryWrapper);
        return sysNotice;
    }

    @Override
    public Map<String, NoticeInfoVo> getList(Integer siteId, String[] keys) {
        QueryWrapper queryWrapper = new QueryWrapper<SysNotice>();
        queryWrapper.eq("site_id", siteId);
        if (keys.length > 0) queryWrapper.in("`key`", keys);

        Map<String, SysNotice> noticeList = (Map<String, SysNotice>) sysNoticeMapper.selectList(queryWrapper).stream().collect(Collectors.toMap(SysNotice::getKey, SysNotice::getClass));

        Map<String, NoticeInfoVo> list = new HashMap<>();
        for (Map.Entry<String, NoticeEnumListVo> noticeMap : NoticeEnum.getNotice().entrySet()) {
            NoticeInfoVo vo = new NoticeInfoVo();
            NoticeEnumListVo notice = noticeMap.getValue();
            BeanUtil.copyProperties(notice, vo);
            if (noticeList.get(noticeMap.getKey()) != null)
                BeanUtil.copyProperties(noticeList.get(noticeMap.getKey()), vo);
            if (notice.getSupport_type().contains("sms")) vo.setSms(notice.getSupport_type_map().get("sms"));
            if (notice.getSupport_type().contains("wechat")) vo.setWechat(notice.getSupport_type_map().get("wechat"));
            if (notice.getSupport_type().contains("weapp")) vo.setWeapp(notice.getSupport_type_map().get("weapp"));
            list.put(noticeMap.getKey(), vo);
        }

        return list;
    }

    @Override
    public List<AddonNoticeListVo> getAddonList(Integer siteId) {
        List<SysNotice> list = sysNoticeMapper.selectList(new QueryWrapper<SysNotice>().eq("site_id", siteId));
        Map<String, SysNotice> map = new HashMap<>();
        if (ObjectUtil.isNotNull(list)) {
            for (SysNotice item : list) {
                map.put(item.getKey(), item);
            }
        }
        Map<String, NoticeInfoVo> notice = new HashMap<>();
        for (Map.Entry<String, NoticeEnumListVo> noticeMap : NoticeEnum.getNotice().entrySet()) {

            NoticeInfoVo vo = new NoticeInfoVo();
            BeanUtil.copyProperties(noticeMap.getValue(), vo);
            if (map.containsKey(noticeMap.getKey())) {
                BeanUtil.copyProperties(map.get(noticeMap.getKey()), vo);
            }

            //针对短信，微信公众号，小程序配置
            if (ObjectUtil.isNotEmpty(noticeMap.getValue().getSupport_type_map())) {
                for (Map.Entry<String, JSONObject> supportTypeMap : noticeMap.getValue().getSupport_type_map().entrySet()) {
                    if (supportTypeMap.getKey().equals("sms")) {
                        vo.setSms(supportTypeMap.getValue());
                    }
                    if (supportTypeMap.getKey().equals("wechat")) {
                        vo.setWechat(supportTypeMap.getValue());
                    }
                    if (supportTypeMap.getKey().equals("weapp")) {
                        vo.setWeapp(supportTypeMap.getValue());
                    }

                }
            }
            notice.put(noticeMap.getKey(), vo);
        }

        List<Addon> addonList = coreSiteService.getSiteAddons(siteId);
        Addon adminAddon = new Addon();
        adminAddon.setKey("system");
        adminAddon.setTitle("系统");
        addonList.add(0, adminAddon);
        List<AddonNoticeListVo> noticeAddonList = new ArrayList<>();
        for (Addon addon : addonList) {
            AddonNoticeListVo noticeListVo = new AddonNoticeListVo();
            noticeListVo.setKey(addon.getKey());
            noticeListVo.setTitle(addon.getTitle());
            noticeListVo.setNotice(new ArrayList<>());
            for (Map.Entry<String, NoticeInfoVo> noticeMap : notice.entrySet()) {
                if (noticeListVo.getKey().equals(noticeMap.getValue().getAddon())) {
                    noticeListVo.getNotice().add(noticeMap.getValue());
                }
            }
            noticeAddonList.add(noticeListVo);
        }
        return noticeAddonList;
    }

    @Override
    public NoticeInfoVo getInfo(Integer siteId, String key) {
        if (!NoticeEnum.getNotice().containsKey(key)) throw new CommonException("消息类型不存在");

        NoticeInfoVo vo = new NoticeInfoVo();
        NoticeEnumListVo notice = NoticeEnum.getNotice().get(key);
        BeanUtil.copyProperties(notice, vo);

        for (String type : notice.getSupport_type_map().keySet()) {
            ReflectCallField.setForInstance(vo, type, notice.getSupport_type_map().get(type));
        }

        SysNotice model = sysNoticeMapper.selectOne(new QueryWrapper<SysNotice>().eq("site_id", siteId).eq("`key`", key));
        if (model != null) {
            BeanUtil.copyProperties(model, vo);
        }

        return vo;
    }

    @Override
    public void edit(Integer siteId, String key, JSONObject data) {
        QueryWrapper queryWrapper = new QueryWrapper<SysNotice>();
        queryWrapper.eq("`key`", key);
        queryWrapper.eq("site_id", siteId);

        SysNotice model = sysNoticeMapper.selectOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(model)) {
            SysNotice sysNotice = JSONUtil.toBean(data, SysNotice.class);
            sysNoticeMapper.update(sysNotice, queryWrapper);
        } else {
            SysNotice sysNotice = JSONUtil.toBean(data, SysNotice.class);
            sysNotice.setSiteId(siteId);
            NoticeEnumListVo noticeEnum = NoticeEnum.getNotice().get(key);
            BeanUtil.copyProperties(noticeEnum, sysNotice);
            sysNoticeMapper.insert(sysNotice);
        }
    }

    /**
     * 发送消息
     *
     * @param siteId
     * @param key
     * @param data
     */
    @Override
    public void send(Integer siteId, String key, Map<String, Object> data) {
        NoticeInfoVo notice = getInfo(siteId, key);
        if (notice == null) return;

        if (notice.getAsync()) {
            asyncSend(siteId, notice, data);
        } else {
            syncSend(siteId, notice, data);
        }
    }

    /**
     * 消息同步发送
     */
    public void syncSend(Integer siteId, NoticeInfoVo notice, Map<String, Object> data) {
        SendNoticeEventDefiner.SendNoticeEvent event = new SendNoticeEventDefiner.SendNoticeEvent();
        event.setSiteId(siteId);
        event.addAppSign("core");
        event.setName("SendNoticeEvent");
        event.setKey(notice.getKey());
        event.setNoticeData(NoticeLoader.getDriver(notice.getKey()).noticeData(data));
        event.setNotice(notice);
        EventAndSubscribeOfPublisher.publishAndCallback(event);
    }

    /**
     * 消息异步发送
     */
    @Async
    public void asyncSend(Integer siteId, NoticeInfoVo notice, Map<String, Object> data) {
        SendNoticeEventDefiner.SendNoticeEvent event = new SendNoticeEventDefiner.SendNoticeEvent();
        event.setSiteId(siteId);
        event.addAppSign("core");
        event.setName("SendNoticeEvent");
        event.setKey(notice.getKey());
        event.setNoticeData(NoticeLoader.getDriver(notice.getKey()).noticeData(data));
        event.setNotice(notice);
        EventAndSubscribeOfPublisher.publishAndCallback(event);
    }
}
