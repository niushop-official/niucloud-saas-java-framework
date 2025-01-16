package com.niu.core.service.admin.pay.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.QrcodeUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.sys.SysPoster;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.core.mapper.sys.SysPosterMapper;
import com.niu.core.service.admin.pay.IPayService;
import com.niu.core.service.admin.pay.param.GetFriendspayInfoByTradeParam;
import com.niu.core.service.admin.pay.param.PayParam;
import com.niu.core.service.admin.pay.param.PaySearchParam;
import com.niu.core.service.admin.pay.vo.FriendsPayInfoByTradeVo;
import com.niu.core.service.admin.pay.vo.PayInfoVo;
import com.niu.core.service.admin.pay.vo.PayListVo;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.core.service.core.pay.vo.GetInfoByTradeVo;
import com.niu.core.service.core.pay.vo.PayTypeVo;
import com.niu.core.service.core.sys.ICoreSysConfigService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * 支付记录实现
 */
@Service
public class PayServiceImpl implements IPayService {

    @Resource
    PayMapper payMapper;

    @Resource
    ICorePayService corePayService;

    @Resource
    SysPosterMapper sysPosterMapper;

    @Resource
    MemberMapper memberMapper;

    @Resource
    ICoreSysConfigService coreSysConfigService;

    /**
     * 支付记录列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<PayListVo>
     */
    @Override
    public PageResult<PayListVo> list(PageParam pageParam, PaySearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<Pay> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        IPage<Pay> iPage = payMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<PayListVo> list = new LinkedList<>();
        for (Pay item : iPage.getRecords()) {
            PayListVo vo = new PayListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 支付记录详情
     *
     * @param id 主键
     * @return PayInfoVo
     */
    @Override
    public PayInfoVo info(Integer id) {
        Pay model = payMapper.selectOne(
                new QueryWrapper<Pay>()
                        .eq("id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        PayInfoVo vo = new PayInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 支付记录添加
     *
     * @param addParam 参数
     */
    @Override
    public void add(PayParam addParam) {
        Pay model = new Pay();
        model.setSiteId(addParam.getSiteId());
        model.setMainId(addParam.getMainId());
        model.setOutTradeNo(addParam.getOutTradeNo());
        model.setTradeType(addParam.getTradeType());
        model.setTradeId(addParam.getTradeId());
        model.setTradeNo(addParam.getTradeNo());
        model.setBody(addParam.getBody());
        model.setMoney(addParam.getMoney());
        model.setVoucher(addParam.getVoucher());
        model.setStatus(addParam.getStatus());
        model.setJson(addParam.getJson());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setPayTime(addParam.getPayTime());
        model.setCancelTime(addParam.getCancelTime());
        model.setType(addParam.getType());
        model.setMchId(addParam.getMchId());
        model.setMainType(addParam.getMainType());
        model.setChannel(addParam.getChannel());
        model.setFailReason(addParam.getFailReason());
        payMapper.insert(model);
    }

    /**
     * 支付记录编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, PayParam editParam) {
        Pay model = payMapper.selectOne(
                new QueryWrapper<Pay>()
                        .eq("id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        model.setId(id);
        model.setSiteId(editParam.getSiteId());
        model.setMainId(editParam.getMainId());
        model.setOutTradeNo(editParam.getOutTradeNo());
        model.setTradeType(editParam.getTradeType());
        model.setTradeId(editParam.getTradeId());
        model.setTradeNo(editParam.getTradeNo());
        model.setBody(editParam.getBody());
        model.setMoney(editParam.getMoney());
        model.setVoucher(editParam.getVoucher());
        model.setStatus(editParam.getStatus());
        model.setJson(editParam.getJson());
        model.setPayTime(editParam.getPayTime());
        model.setCancelTime(editParam.getCancelTime());
        model.setType(editParam.getType());
        model.setMchId(editParam.getMchId());
        model.setMainType(editParam.getMainType());
        model.setChannel(editParam.getChannel());
        model.setFailReason(editParam.getFailReason());
        payMapper.updateById(model);
    }

    /**
     * 支付记录删除
     *
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        Pay model = payMapper.selectOne(
                new QueryWrapper<Pay>()
                        .eq("id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        payMapper.delete(new QueryWrapper<Pay>().eq("id", id));
    }

    @Override
    public FriendsPayInfoByTradeVo getFriendspayInfoByTrade(GetFriendspayInfoByTradeParam param) {
        GetInfoByTradeVo payInfo = corePayService.getInfoByTrade(RequestUtils.siteId(), param.getTradeType(), param.getTradeId(), param.getChannel(), "friendspay");
        if (ObjectUtil.isEmpty(payInfo)) {
            return new FriendsPayInfoByTradeVo();
        }

        FriendsPayInfoByTradeVo vo = new FriendsPayInfoByTradeVo();
        BeanUtils.copyProperties(payInfo, vo);
        vo.setConfig(payInfo.getConfig());
        vo.setCreateTime(DateUtils.timestampToString(payInfo.getCreateTime()));

        if (payInfo.getCancelTime() > 0) {
            vo.setCancelTime(DateUtils.timestampToString(payInfo.getCancelTime()));
        }

        if (payInfo.getPayTime() > 0) {
            vo.setPayTime(DateUtils.timestampToString(payInfo.getPayTime()));
        }

        QueryWrapper<SysPoster> posterQueryWrapper = new QueryWrapper<>();
        posterQueryWrapper.eq("site_id", RequestUtils.siteId())
                .eq("type", "friendspay")
                .eq("status", 1)
                .eq("is_default", 1);

        SysPoster poster = sysPosterMapper.selectOne(posterQueryWrapper);
        if (ObjectUtil.isNotEmpty(poster)) {
            vo.setPosterId(poster.getId());
        }

        QueryWrapper<Member> memberQueryWrapper = new QueryWrapper<>();
        memberQueryWrapper.eq("site_id", RequestUtils.siteId())
                .eq("member_id", vo.getMainId());
        Member member = memberMapper.selectOne(memberQueryWrapper);
        vo.setMember(member);

        QR qrcode = getQrcode(param.getTradeType(), param.getTradeId(), param.getChannel());
        vo.setLink(qrcode.link);
        vo.setQrcode(qrcode.qrcode);
        return vo;
    }

    @Override
    public List<PayTypeVo> getPayTypeList() {
        List<PayTypeVo> payTypeList = corePayService.getPayTypeByTrade(RequestUtils.siteId(), "", ChannelEnum.H5);
        if (ObjectUtil.isEmpty(payTypeList)) {
            return Collections.emptyList();
        }

        return payTypeList.stream().filter(o -> o.getKey().equals("balancepay") || o.getKey().equals("friendspay")).toList();
    }

    @Override
    public Object pay(com.niu.core.common.loader.pay.param.PayParam param) {
        param.setSiteId(RequestUtils.siteId());
        return corePayService.pay(param);
    }

    private QR getQrcode(String tradeType, Integer tradeId, String channel) {
        String url = coreSysConfigService.getSceneDomain(RequestUtils.siteId()).getWapUrl();
        String page = "app/pages/friendspay/money";
        String dir = "upload/" + RequestUtils.siteId() + "/friendspay_qrcode";
        String path = "";

        Map<String, Object> data = Map.of("id", tradeId, "type", tradeType);
        try {
            path = QrcodeUtils.qrcodeToFile(RequestUtils.siteId(), channel, url, page, data, dir);
        } catch (Exception e) {
        }
        url = url + "/" + page;

        List<String> scene = data.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).toList();
        url += "?" + String.join("&", scene);

        return new QR(url, path);
    }

    @Data
    @AllArgsConstructor
    private static class QR {
        private String link;
        private String qrcode;
    }

}
