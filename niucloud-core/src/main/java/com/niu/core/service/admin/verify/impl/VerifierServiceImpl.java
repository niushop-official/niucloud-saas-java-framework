package com.niu.core.service.admin.verify.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.verify.Verifier;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.verify.VerifierMapper;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import com.niu.core.service.admin.verify.param.VerifierParam;
import com.niu.core.service.admin.verify.param.VerifierSearchParam;
import com.niu.core.service.admin.verify.IVerifierService;
import com.niu.core.service.admin.verify.vo.VerifierListVo;
import com.niu.core.service.admin.verify.vo.VerifierVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 核销员实现
 */
@Service
public class VerifierServiceImpl implements IVerifierService {
        
    @Resource
    VerifierMapper verifierMapper;

    @Resource
    MemberMapper memberMapper;

    /**
     * 核销员列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<VerifierListVo>
     */
    @Override
    public PageResult<VerifierListVo> list(PageParam pageParam, VerifierSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        MPJQueryWrapper<Verifier> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("v").innerJoin("?_member m ON v.member_id = m.member_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("v.*,m.member_no,m.username,m.nickname,m.mobile,m.headimg");
        queryWrapper.eq("v.site_id", RequestUtils.siteId());
        queryWrapper.orderByDesc("id");

        IPage<VerifierVo> iPage = verifierMapper.selectJoinPage(new Page<>(page, limit), VerifierVo.class, queryWrapper);
        List<VerifierListVo> list = new LinkedList<>();
        for (VerifierVo item : iPage.getRecords()) {
            VerifierListVo vo = new VerifierListVo();
            BeanUtils.copyProperties(item, vo);

            MemberBriefInfoVo memberInfoVo = new MemberBriefInfoVo();
            BeanUtils.copyProperties(item, memberInfoVo);
            vo.setMember(memberInfoVo);

            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 获取核销员列表
     * @return
     */
    public List<VerifierListVo> all() {
        MPJQueryWrapper<Verifier> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("v").innerJoin("?_member m ON v.member_id = m.member_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("v.*,m.member_no,m.username,m.nickname,m.mobile,m.headimg");
        queryWrapper.eq("v.site_id", RequestUtils.siteId());
        queryWrapper.orderByDesc("id");

        List<VerifierVo> verifierList = verifierMapper.selectJoinList(VerifierVo.class, queryWrapper);

        List<VerifierListVo> list = new LinkedList<>();
        for (VerifierVo item : verifierList) {
            VerifierListVo vo = new VerifierListVo();
            BeanUtils.copyProperties(item, vo);

            MemberBriefInfoVo memberInfoVo = new MemberBriefInfoVo();
            BeanUtils.copyProperties(item, memberInfoVo);
            vo.setMember(memberInfoVo);

            list.add(vo);
        }
        return list;
    }

    /**
     * 核销员添加
     * @param addParam 参数
     */
    @Override
    public void add(VerifierParam addParam) {
        Member member = memberMapper.selectOne(new QueryWrapper<Member>().select("member_id").eq("member_id", addParam.getMemberId()).eq("site_id", RequestUtils.siteId()));
        Assert.notNull(member, "会员不存在");

        Verifier verifier = verifierMapper.selectOne(new QueryWrapper<Verifier>().select("id").eq("member_id", addParam.getMemberId()).eq("site_id", RequestUtils.siteId()));
        if (ObjectUtil.isNotEmpty(verifier)) return;

        Verifier model = new Verifier();
        model.setSiteId(RequestUtils.siteId());
        model.setMemberId(addParam.getMemberId());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setVerifyType(JSONUtil.parseArray(addParam.getVerifyType()).toString());

        verifierMapper.insert(model);
    }

    /**
     * 核销员删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        verifierMapper.delete(new QueryWrapper<Verifier>().eq("id", id).eq("site_id", RequestUtils.siteId()));
    }

}
