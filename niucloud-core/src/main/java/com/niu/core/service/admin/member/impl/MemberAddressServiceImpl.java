package com.niu.core.service.admin.member.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.member.MemberAddress;
import com.niu.core.mapper.member.MemberAddressMapper;
import com.niu.core.service.admin.member.param.MemberAddressParam;
import com.niu.core.service.admin.member.param.MemberAddressSearchParam;
import com.niu.core.service.admin.member.IMemberAddressService;
import com.niu.core.service.admin.member.vo.MemberAddressInfoVo;
import com.niu.core.service.admin.member.vo.MemberAddressListVo;
import com.niu.core.service.admin.sys.ISysAreaService;
import org.bouncycastle.cert.ocsp.Req;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import jakarta.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 会员收货地址实现
 */
@Service
public class MemberAddressServiceImpl implements IMemberAddressService {
        
    @Resource
    MemberAddressMapper memberAddressMapper;

    @Resource
    ISysAreaService sysAreaService;

    /**
     * 会员收货地址列表
     * @return List<MemberAddressListVo>
     */
    @Override
    public List<MemberAddressListVo> list(MemberAddressSearchParam searchParam) {
        QueryWrapper<MemberAddress> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        queryWrapper.eq("site_id", RequestUtils.siteId());
        if (ObjectUtil.isNotEmpty(searchParam.getMemberId())) queryWrapper.eq("member_id", searchParam.getMemberId());

        List<MemberAddress> records = memberAddressMapper.selectList(queryWrapper);
        List<MemberAddressListVo> list = new LinkedList<>();
        for (MemberAddress item : records) {
            MemberAddressListVo vo = new MemberAddressListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }

    /**
     * 会员收货地址详情
     * @param id 主键
     * @return MemberAddressInfoVo
     */
    @Override
    public MemberAddressInfoVo info(Integer id) {
        MemberAddress model = memberAddressMapper.selectOne(
                new QueryWrapper<MemberAddress>()
                    .eq("id", id)
                    .eq("site_id", RequestUtils.siteId())
        );

        Assert.notNull(model, "数据不存在");

        MemberAddressInfoVo vo = new MemberAddressInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 会员收货地址添加
     * @param addParam 参数
     */
    @Override
    public void add(MemberAddressParam addParam) {
        MemberAddress model = new MemberAddress();
        BeanUtils.copyProperties(addParam, model);
        model.setSiteId(RequestUtils.siteId());
        JSONObject addressInfo = sysAreaService.getAddress(addParam.getFullAddress());
        if (addressInfo != null && addressInfo.getInt("status") == 0 && addressInfo.getStr("message", "").equals("Success")) {
            model.setLng(ObjectUtil.defaultIfNull(addressInfo.getByPath("result.location.lng"), "").toString());
            model.setLat(ObjectUtil.defaultIfNull(addressInfo.getByPath("result.location.lat"), "").toString());
        }
        if (addParam.getIsDefault() == 1) {
            memberAddressMapper.update(null,
                new UpdateWrapper<MemberAddress>()
                    .set("is_default", 0)
                    .eq("member_id", model.getMemberId())
                    .eq("site_id", model.getSiteId()));
        }
        memberAddressMapper.insert(model);
    }

    /**
     * 会员收货地址编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, MemberAddressParam editParam) {
        MemberAddress model = memberAddressMapper.selectOne(
                new QueryWrapper<MemberAddress>()
                    .eq("id", id)
                    .eq("site_id", RequestUtils.siteId())
        );

        Assert.notNull(model, "数据不存在!");
        BeanUtils.copyProperties(editParam, model);
        JSONObject addressInfo = sysAreaService.getAddress(editParam.getFullAddress());
        if (addressInfo != null && addressInfo.getInt("status") == 0 && addressInfo.getStr("message", "").equals("Success")) {
            model.setLng(ObjectUtil.defaultIfNull(addressInfo.getByPath("result.location.lng"), "").toString());
            model.setLat(ObjectUtil.defaultIfNull(addressInfo.getByPath("result.location.lat"), "").toString());
        }
        if (editParam.getIsDefault() == 1) {
            memberAddressMapper.update(null,
                new UpdateWrapper<MemberAddress>()
                    .set("is_default", 0)
                    .eq("member_id", model.getMemberId())
                    .eq("site_id", model.getSiteId()));
        }
        memberAddressMapper.updateById(model);
    }

    /**
     * 会员收货地址删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        MemberAddress model = memberAddressMapper.selectOne(
                new QueryWrapper<MemberAddress>()
                    .eq("id", id)
                    .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "数据不存在!");

        memberAddressMapper.delete(new QueryWrapper<MemberAddress>().eq("id", id));
    }

}

