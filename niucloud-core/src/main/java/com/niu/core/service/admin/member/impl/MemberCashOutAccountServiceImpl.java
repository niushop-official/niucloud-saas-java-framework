package com.niu.core.service.admin.member.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.entity.member.MemberCashOutAccount;
import com.niu.core.mapper.member.MemberCashOutAccountMapper;
import com.niu.core.service.admin.member.param.MemberCashOutAccountParam;
import com.niu.core.service.admin.member.param.MemberCashOutAccountSearchParam;
import com.niu.core.service.admin.member.IMemberCashOutAccountService;
import com.niu.core.service.admin.member.vo.MemberCashOutAccountInfoVo;
import com.niu.core.service.admin.member.vo.MemberCashOutAccountListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 会员提现账户实现
 */
@Service
public class MemberCashOutAccountServiceImpl implements IMemberCashOutAccountService {
        
    @Resource
    MemberCashOutAccountMapper memberCashOutAccountMapper;

    /**
     * 会员提现账户列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<MemberCashOutAccountListVo>
     */
    @Override
    public PageResult<MemberCashOutAccountListVo> list(PageParam pageParam, MemberCashOutAccountSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<MemberCashOutAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("account_id");

        IPage<MemberCashOutAccount> iPage = memberCashOutAccountMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<MemberCashOutAccountListVo> list = new LinkedList<>();
        for (MemberCashOutAccount item : iPage.getRecords()) {
            MemberCashOutAccountListVo vo = new MemberCashOutAccountListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 会员提现账户详情
     * @param id 主键
     * @return MemberCashOutAccountInfoVo
     */
    @Override
    public MemberCashOutAccountInfoVo info(Integer id) {
        MemberCashOutAccount model = memberCashOutAccountMapper.selectOne(
                new QueryWrapper<MemberCashOutAccount>()
                    .eq("account_id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        MemberCashOutAccountInfoVo vo = new MemberCashOutAccountInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 会员提现账户添加
     * @param addParam 参数
     */
    @Override
    public void add(MemberCashOutAccountParam addParam) {
        MemberCashOutAccount model = new MemberCashOutAccount();
        model.setSiteId(addParam.getSiteId());
        model.setMemberId(addParam.getMemberId());
        model.setAccountType(addParam.getAccountType());
        model.setBankName(addParam.getBankName());
        model.setRealname(addParam.getRealname());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        model.setAccountNo(addParam.getAccountNo());
        memberCashOutAccountMapper.insert(model);
    }

    /**
     * 会员提现账户编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, MemberCashOutAccountParam editParam) {
        MemberCashOutAccount model = memberCashOutAccountMapper.selectOne(
                new QueryWrapper<MemberCashOutAccount>()
                    .eq("account_id",  id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
    model.setAccountId(id);
        model.setAccountId(editParam.getAccountId());
        model.setSiteId(editParam.getSiteId());
        model.setMemberId(editParam.getMemberId());
        model.setAccountType(editParam.getAccountType());
        model.setBankName(editParam.getBankName());
        model.setRealname(editParam.getRealname());
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        model.setAccountNo(editParam.getAccountNo());
        memberCashOutAccountMapper.updateById(model);
    }

    /**
     * 会员提现账户删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        MemberCashOutAccount model = memberCashOutAccountMapper.selectOne(
                new QueryWrapper<MemberCashOutAccount>()
                    .eq("account_id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        memberCashOutAccountMapper.delete(new QueryWrapper<MemberCashOutAccount>().eq("account_id", id));
    }

}
