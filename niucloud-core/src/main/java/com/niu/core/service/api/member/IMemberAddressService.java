package com.niu.core.service.api.member;

import com.niu.core.service.api.member.param.*;
import com.niu.core.service.api.member.vo.MemberAddressGetInfoVo;
import com.niu.core.service.api.member.vo.MemberAddressListInfoVo;

import java.util.List;

public interface IMemberAddressService {

    /**
     * 获取会员收货地址列表
     *
     * @param param
     * @return
     */
    public List<MemberAddressListInfoVo> getList(MemberAddressGetListParam param);

    /**
     * 获取会员收货地址信息
     *
     * @param param
     * @return
     */
    public MemberAddressGetInfoVo getInfo(MemberAddressGetInfoParam param);

    /**
     * 添加会员收货地址
     *
     * @param param
     * @return
     */
    public int add(MemberAddressAddParam param);

    /**
     * 会员收货地址编辑
     *
     * @param id
     * @param param
     * @return
     */
    public int edit(Integer id, MemberAddressEditParam param);

    /**
     * 删除会员收货地址
     *
     * @param param
     * @return
     */
    public int delete(MemberAddressDeleteParam param);

}
