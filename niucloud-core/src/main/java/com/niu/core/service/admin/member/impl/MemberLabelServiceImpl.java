package com.niu.core.service.admin.member.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.member.MemberLabel;
import com.niu.core.mapper.member.MemberLabelMapper;
import com.niu.core.service.admin.member.param.MemberLabelEditParam;
import com.niu.core.service.admin.member.param.MemberLabelParam;
import com.niu.core.service.admin.member.param.MemberLabelSearchParam;
import com.niu.core.service.admin.member.IMemberLabelService;
import com.niu.core.service.admin.member.vo.MemberLabelAllListVo;
import com.niu.core.service.admin.member.vo.MemberLabelInfoVo;
import com.niu.core.service.admin.member.vo.MemberLabelListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 会员标签实现
 */
@Service
public class MemberLabelServiceImpl implements IMemberLabelService {
        
    @Resource
    MemberLabelMapper memberLabelMapper;

    /**
     * 会员标签列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<MemberLabelListVo>
     */
    @Override
    public PageResult<MemberLabelListVo> list(PageParam pageParam, MemberLabelSearchParam searchParam) {
        Integer siteId = RequestUtils.siteId();

        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<MemberLabel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", siteId);
        queryWrapper.orderByDesc("label_id");
        if (ObjectUtil.isNotEmpty(searchParam.getLabelName())) queryWrapper.like("label_name", searchParam.getLabelName());

        IPage<MemberLabel> iPage = memberLabelMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<MemberLabelListVo> list = new LinkedList<>();
        for (MemberLabel item : iPage.getRecords()) {
            MemberLabelListVo vo = new MemberLabelListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit).setData(list);
    }

    /**
     * 会员标签详情
     * @param id 主键
     * @return MemberLabelInfoVo
     */
    @Override
    public MemberLabelInfoVo info(Integer id) {
        Integer siteId = RequestUtils.siteId();

        MemberLabel model = memberLabelMapper.selectOne(
                new QueryWrapper<MemberLabel>()
                    .eq("site_id", siteId)
                    .eq("label_id", id)
                    .last("limit 1"));

        Assert.notNull(model, "标签不存在");

        MemberLabelInfoVo vo = new MemberLabelInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 会员标签添加
     * @param addParam 参数
     */
    @Override
    public void add(MemberLabelEditParam addParam) {
        Integer siteId = RequestUtils.siteId();

        MemberLabel model = new MemberLabel();
        model.setSiteId(siteId);
        model.setLabelName(addParam.getLabelName());
        model.setMemo(addParam.getMemo());
        model.setSort(addParam.getSort());
        model.setCreateTime(System.currentTimeMillis() / 1000);

        memberLabelMapper.insert(model);
    }

    /**
     * 会员标签编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, MemberLabelEditParam editParam) {
        Integer siteId = RequestUtils.siteId();

        UpdateWrapper<MemberLabel> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("site_id", siteId)
                .eq("label_id",  id);

        MemberLabel model = new MemberLabel();
        model.setLabelName(editParam.getLabelName());
        model.setMemo(editParam.getMemo());
        model.setSort(editParam.getSort());
        model.setUpdateTime(System.currentTimeMillis() / 1000);

        memberLabelMapper.update(model, updateWrapper);
    }

    /**
     * 会员标签删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        Integer siteId = RequestUtils.siteId();

        QueryWrapper<MemberLabel> queryWrapper = new QueryWrapper<MemberLabel>()
                .eq("site_id", siteId)
                .eq("label_id", id);

        memberLabelMapper.delete(queryWrapper);
    }

    /**
     * 查询全部会员标签
     * @return
     */
    @Override
    public List<MemberLabelAllListVo> all() {
        Integer siteId = RequestUtils.siteId();

        MPJQueryWrapper<MemberLabel> queryWrapper = new MPJQueryWrapper<MemberLabel>();
        queryWrapper.select("label_id,label_name").eq("site_id", siteId);

        List<MemberLabel> labels = memberLabelMapper.selectList(queryWrapper); // 调用 selectList 方法

        List<MemberLabelAllListVo> list = new LinkedList<>();
        for (MemberLabel item : labels) {
            MemberLabelAllListVo vo = new MemberLabelAllListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }
}
