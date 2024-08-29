package com.niu.core.service.admin.member.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.entity.member.MemberAddress;
import com.niu.core.mapper.member.MemberAddressMapper;
import com.niu.core.service.admin.member.param.MemberAddressParam;
import com.niu.core.service.admin.member.param.MemberAddressSearchParam;
import com.niu.core.service.admin.member.IMemberAddressService;
import com.niu.core.service.admin.member.vo.MemberAddressInfoVo;
import com.niu.core.service.admin.member.vo.MemberAddressListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 会员收货地址实现
 */
@Service
public class MemberAddressServiceImpl implements IMemberAddressService {
        
    @Resource
    MemberAddressMapper memberAddressMapper;

    /**
     * 会员收货地址列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<MemberAddressListVo>
     */
    @Override
    public PageResult<MemberAddressListVo> list(PageParam pageParam, MemberAddressSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<MemberAddress> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        IPage<MemberAddress> iPage = memberAddressMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<MemberAddressListVo> list = new LinkedList<>();
        for (MemberAddress item : iPage.getRecords()) {
            MemberAddressListVo vo = new MemberAddressListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit).setData(list);
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
                    .last("limit 1"));

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
        model.setMemberId(addParam.getMemberId());
        model.setSiteId(addParam.getSiteId());
        model.setName(addParam.getName());
        model.setMobile(addParam.getMobile());
        model.setProvinceId(addParam.getProvinceId());
        model.setCityId(addParam.getCityId());
        model.setDistrictId(addParam.getDistrictId());
        model.setAddress(addParam.getAddress());
        model.setAddressName(addParam.getAddressName());
        model.setFullAddress(addParam.getFullAddress());
        model.setLng(addParam.getLng());
        model.setLat(addParam.getLat());
        model.setIsDefault(addParam.getIsDefault());
        model.setType(addParam.getType());
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
                    .eq("id",  id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        model.setId(id);
        model.setMemberId(editParam.getMemberId());
        model.setSiteId(editParam.getSiteId());
        model.setName(editParam.getName());
        model.setMobile(editParam.getMobile());
        model.setProvinceId(editParam.getProvinceId());
        model.setCityId(editParam.getCityId());
        model.setDistrictId(editParam.getDistrictId());
        model.setAddress(editParam.getAddress());
        model.setAddressName(editParam.getAddressName());
        model.setFullAddress(editParam.getFullAddress());
        model.setLng(editParam.getLng());
        model.setLat(editParam.getLat());
        model.setIsDefault(editParam.getIsDefault());
        model.setType(editParam.getType());
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
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        memberAddressMapper.delete(new QueryWrapper<MemberAddress>().eq("id", id));
    }

}
