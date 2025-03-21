package com.niu.core.service.api.member.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.niu.core.common.exception.BaseException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.entity.member.MemberAddress;
import com.niu.core.entity.sys.SysArea;
import com.niu.core.mapper.member.MemberAddressMapper;
import com.niu.core.service.api.member.IMemberAddressService;
import com.niu.core.service.api.member.param.*;
import com.niu.core.service.api.member.vo.MemberAddressGetInfoVo;
import com.niu.core.service.api.member.vo.MemberAddressListInfoVo;
import com.niu.core.service.api.sys.ISysAreaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class MemberAddressServiceImpl implements IMemberAddressService {

    @Autowired
    private MemberAddressMapper memberAddressMapper;

    @Resource
    private ISysAreaService sysAreaService;

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    @Override
    public int add(MemberAddressAddParam param) {
        // 校验区域有效性
//        this.checkAndFillAddressValue(param);
        // 默认地址设定
        if (param.getIsDefault() != null && param.getIsDefault() == 1) {
            try {
                this.deleteDefaultAddress(param.memberId());
            } catch (Exception e) {
                log.error("更新会员默认地址数据库操作错误：", e.getMessage());
                throw new BaseException("添加会员收货地址错误.");
            }
        }
        // 插入实体表
        MemberAddress newMemberAddress = CollectUtils.build(param, MemberAddress.class);
        // 前置业务条件
        newMemberAddress.setSiteId(param.siteId());
        newMemberAddress.setMemberId(param.memberId());
        // 插入显式的设置为空
        newMemberAddress.setId(null);
        int result = this.memberAddressMapper.insert(newMemberAddress);
        if (result <= 0) {
            throw new BaseException("添加会员收货地址失败.");
        }
        return result;
    }

    /**
     * 校验地址的有效性
     *
     * @param memberAddress
     */
    private void checkAndFillAddressValue(MemberAddress memberAddress) {
        SysArea provinceArea = null;
        try {
            provinceArea = sysAreaService.getAreaById(memberAddress.getProvinceId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("省份不存在.");
        }
        if (provinceArea == null) {
            throw new BaseException("省份不存在.");
        }
        SysArea cityArea = null;
        try {
            cityArea = sysAreaService.getAreaById(memberAddress.getCityId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("城市不存在.");
        }
        if (cityArea == null) {
            throw new BaseException("城市不存在.");
        }
        SysArea districtArea = null;
        try {
            districtArea = sysAreaService.getAreaById(memberAddress.getDistrictId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("区县不存在.");
        }
        if (districtArea == null) {
            throw new BaseException("区县不存在.");
        }
    }

    /**
     * @param memberId
     */
    private void deleteDefaultAddress(Integer memberId) {
        UpdateWrapper<MemberAddress> updateWrapper = new UpdateWrapper();
        updateWrapper.eq("member_id", memberId);
        MemberAddress updateMemberAddress = new MemberAddress();
        updateMemberAddress.setIsDefault(0);
        this.memberAddressMapper.update(updateMemberAddress, updateWrapper);
    }

    @Override
    public int delete(MemberAddressDeleteParam param) {
        QueryWrapper<MemberAddress> queryWrapper = new QueryWrapper();
        queryWrapper.eq("site_id", param.siteId());
        queryWrapper.eq("member_id", param.memberId());
        queryWrapper.eq("id", param.getId());
        int result = this.memberAddressMapper.delete(queryWrapper);
        if (result == 0) {
            throw new BaseException("删除会员收货地址失败.");
        }
        return result;
    }

    @Override
    public int edit(Integer id, MemberAddressEditParam param) {
        // 校验区域有效性
        // this.checkAndFillAddressValue(param);
        // 默认地址设定
        if (param.getIsDefault() != null && param.getIsDefault() == 1) {
            try {
                deleteDefaultAddress(param.memberId());
            } catch (Exception e) {
                log.error("更新会员默认地址数据库操作错误：", e.getMessage());
                throw new BaseException("添加会员收货地址错误.");
            }
        }
        // 存在校验
        MemberAddress oldMemberAddress = this.getMemberAddress(param.siteId(), param.memberId(), param.getId());
        if (oldMemberAddress == null) {
            throw new BaseException("[" + oldMemberAddress.getId() + "]会员收货地址不存在");
        }
        // 修改实体表
        MemberAddress newMemberAddress = CollectUtils.build(param, MemberAddress.class);
        newMemberAddress.setId(oldMemberAddress.getId());
        return this.memberAddressMapper.updateById(newMemberAddress);
    }

    /**
     * @param siteId
     * @param memberId
     * @param id
     * @return
     */
    private MemberAddress getMemberAddress(Integer siteId, Integer memberId, Integer id) {
        QueryWrapper<MemberAddress> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id,member_id,name,mobile,address,address_name,full_address,is_default,lng,lat,province_id,city_id,district_id");
        queryWrapper.eq("site_id", siteId);
        queryWrapper.eq("member_id", memberId);
        queryWrapper.eq("id", id);
        queryWrapper.orderByDesc(Arrays.asList("is_default", "id"));
        return memberAddressMapper.selectOne(queryWrapper);
    }

    @Override
    public MemberAddressGetInfoVo getInfo(MemberAddressGetInfoParam param) {
        MemberAddress memberAddress = this.getMemberAddress(param.siteId(), param.memberId(), param.getId());
        MemberAddressGetInfoVo memberAddressGetInfoVo = new MemberAddressGetInfoVo();
        BeanUtils.copyProperties(memberAddress, memberAddressGetInfoVo);
        return memberAddressGetInfoVo;
    }

    @Override
    public List<MemberAddressListInfoVo> getList(MemberAddressGetListParam param) {
        QueryWrapper<MemberAddress> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id,member_id,name,mobile,address,address_name,full_address,is_default,lng,lat");
        queryWrapper.eq("site_id", param.siteId());
        queryWrapper.eq("member_id", param.memberId());
        queryWrapper.orderByDesc(Arrays.asList("is_default", "id"));
        List<MemberAddress> memberAddressList = memberAddressMapper.selectList(queryWrapper);
        List<MemberAddressListInfoVo> memberAddressListInfoVo = CollectUtils.convert(memberAddressList, MemberAddressListInfoVo.class);
        return memberAddressListInfoVo;
    }

}
