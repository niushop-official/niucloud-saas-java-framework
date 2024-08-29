package com.niu.core.service.admin.member.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.member.MemberLabel;
import com.niu.core.entity.member.MemberLevel;
import com.niu.core.mapper.member.MemberLevelMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.admin.member.param.MemberLevelParam;
import com.niu.core.service.admin.member.param.MemberLevelSearchParam;
import com.niu.core.service.admin.member.IMemberLevelService;
import com.niu.core.service.admin.member.vo.MemberLevelAllListVo;
import com.niu.core.service.admin.member.vo.MemberLevelInfoVo;
import com.niu.core.service.admin.member.vo.MemberLevelListVo;
import com.niu.core.service.core.member.ICoreMemberService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 会员等级实现
 */
@Service
public class MemberLevelServiceImpl implements IMemberLevelService {
        
    @Resource
    MemberLevelMapper memberLevelMapper;

    @Resource
    MemberMapper memberMapper;

    @Resource
    ICoreMemberService coreMemberService;

    /**
     * 会员等级列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<MemberLevelListVo>
     */
    @Override
    public PageResult<MemberLevelListVo> list(PageParam pageParam, MemberLevelSearchParam searchParam) {
        Integer siteId = RequestUtils.siteId();
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<MemberLevel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", siteId);
        queryWrapper.orderByDesc("level_id");
        if (ObjectUtil.isNotEmpty(searchParam.getLevelName())) queryWrapper.like("level_name", searchParam.getLevelName());

        IPage<MemberLevel> iPage = memberLevelMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<MemberLevelListVo> list = new LinkedList<>();
        for (MemberLevel item : iPage.getRecords()) {
            MemberLevelListVo vo = new MemberLevelListVo();
            BeanUtils.copyProperties(item, vo);
            vo.setMemberNum(memberMapper.selectCount(new QueryWrapper<Member>().eq("member_level", vo.getLevelId())));
            if (ObjectUtil.isNotEmpty(item.getLevelBenefits())) vo.setLevelBenefits(coreMemberService.getBenefitsContent(item.getSiteId(), JSONUtil.parseObj(item.getLevelBenefits()), "admin"));
            if (ObjectUtil.isNotEmpty(item.getLevelGifts())) vo.setLevelGifts(coreMemberService.getGiftContent(item.getSiteId(), JSONUtil.parseObj(item.getLevelGifts()), "admin"));
            list.add(vo);
        }
        return PageResult.build(page, limit).setData(list);
    }

    /**
     * 会员等级详情
     * @param id 主键
     * @return MemberLevelInfoVo
     */
    @Override
    public MemberLevelInfoVo info(Integer id) {
        Integer siteId = RequestUtils.siteId();

        MemberLevel model = memberLevelMapper.selectOne(
                new QueryWrapper<MemberLevel>()
                    .eq("site_id", siteId)
                    .eq("level_id", id)
                    .last("limit 1"));

        Assert.notNull(model, "等级不存在");

        MemberLevelInfoVo vo = new MemberLevelInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 会员等级添加
     * @param addParam 参数
     */
    @Override
    public void add(MemberLevelParam addParam) {
        Integer siteId = RequestUtils.siteId();

        MemberLevel model = new MemberLevel();

        model.setSiteId(siteId);
        model.setLevelName(addParam.getLevelName());
        model.setGrowth(addParam.getGrowth());
        model.setRemark(addParam.getRemark());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setLevelBenefits(addParam.getLevelBenefits().toString());
        model.setLevelGifts(addParam.getLevelGifts().toString());

        memberLevelMapper.insert(model);
    }

    /**
     * 会员等级编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, MemberLevelParam editParam) {
        Integer siteId = RequestUtils.siteId();

        UpdateWrapper<MemberLevel> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("site_id", siteId)
                .eq("level_id",  id);

        MemberLevel model = new MemberLevel();
        model.setLevelName(editParam.getLevelName());
        model.setGrowth(editParam.getGrowth());
        model.setRemark(editParam.getRemark());
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        model.setLevelBenefits(editParam.getLevelBenefits().toString());
        model.setLevelGifts(editParam.getLevelGifts().toString());

        memberLevelMapper.update(model, updateWrapper);
    }

    /**
     * 会员等级删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        Integer siteId = RequestUtils.siteId();

        Long memberNum = memberMapper.selectCount(new QueryWrapper<Member>()
                .eq("site_id", siteId)
                .eq("member_level", id));
        if (!memberNum.equals(0)) throw new CommonException("该等级下存在会员不允许删除");

        QueryWrapper<MemberLevel> queryWrapper = new QueryWrapper<MemberLevel>()
                .eq("site_id", siteId)
                .eq("level_id", id);

        memberLevelMapper.delete(queryWrapper);
    }

    /**
     * 全部会员等级
     * @return
     */
    public List<MemberLevelAllListVo> all() {
        Integer siteId = RequestUtils.siteId();

        MPJQueryWrapper<MemberLevel> queryWrapper = new MPJQueryWrapper<MemberLevel>();
        queryWrapper.select("level_id,level_name, growth,site_id,level_benefits,level_gifts").eq("site_id", siteId);

        List<MemberLevel> labels = memberLevelMapper.selectList(queryWrapper); // 调用 selectList 方法

        List<MemberLevelAllListVo> list = new LinkedList<>();
        for (MemberLevel item : labels) {
            MemberLevelAllListVo vo = new MemberLevelAllListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }
}
