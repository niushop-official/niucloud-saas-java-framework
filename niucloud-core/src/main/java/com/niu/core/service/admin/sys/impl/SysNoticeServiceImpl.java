package com.niu.core.service.admin.sys.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.entity.sys.SysNotice;
import com.niu.core.mapper.sys.SysNoticeMapper;
import com.niu.core.service.admin.sys.param.SysNoticeParam;
import com.niu.core.service.admin.sys.param.SysNoticeSearchParam;
import com.niu.core.service.admin.sys.ISysNoticeService;
import com.niu.core.service.admin.sys.vo.SysNoticeInfoVo;
import com.niu.core.service.admin.sys.vo.SysNoticeListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 通知模型实现
 */
@Service
public class SysNoticeServiceImpl implements ISysNoticeService {
        
    @Resource
    SysNoticeMapper sysNoticeMapper;

    /**
     * 通知模型列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysNoticeListVo>
     */
    @Override
    public PageResult<SysNoticeListVo> list(PageParam pageParam, SysNoticeSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<SysNotice> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        IPage<SysNotice> iPage = sysNoticeMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<SysNoticeListVo> list = new LinkedList<>();
        for (SysNotice item : iPage.getRecords()) {
            SysNoticeListVo vo = new SysNoticeListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 通知模型详情
     * @param id 主键
     * @return SysNoticeInfoVo
     */
    @Override
    public SysNoticeInfoVo info(Integer id) {
        SysNotice model = sysNoticeMapper.selectOne(
                new QueryWrapper<SysNotice>()
                    .eq("id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        SysNoticeInfoVo vo = new SysNoticeInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 通知模型添加
     * @param addParam 参数
     */
    @Override
    public void add(SysNoticeParam addParam) {
        SysNotice model = new SysNotice();
        model.setSiteId(addParam.getSiteId());
        model.setKey(addParam.getKey());
        model.setSmsContent(addParam.getSmsContent());
        model.setIsWechat(addParam.getIsWechat());
        model.setIsWeapp(addParam.getIsWeapp());
        model.setIsSms(addParam.getIsSms());
        model.setWechatTemplateId(addParam.getWechatTemplateId());
        model.setWeappTemplateId(addParam.getWeappTemplateId());
        model.setSmsId(addParam.getSmsId());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setWechatFirst(addParam.getWechatFirst());
        model.setWechatRemark(addParam.getWechatRemark());
        sysNoticeMapper.insert(model);
    }

    /**
     * 通知模型编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, SysNoticeParam editParam) {
        SysNotice model = sysNoticeMapper.selectOne(
                new QueryWrapper<SysNotice>()
                    .eq("id",  id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        model.setId(id);
        model.setSiteId(editParam.getSiteId());
        model.setKey(editParam.getKey());
        model.setSmsContent(editParam.getSmsContent());
        model.setIsWechat(editParam.getIsWechat());
        model.setIsWeapp(editParam.getIsWeapp());
        model.setIsSms(editParam.getIsSms());
        model.setWechatTemplateId(editParam.getWechatTemplateId());
        model.setWeappTemplateId(editParam.getWeappTemplateId());
        model.setSmsId(editParam.getSmsId());
        model.setWechatFirst(editParam.getWechatFirst());
        model.setWechatRemark(editParam.getWechatRemark());
        sysNoticeMapper.updateById(model);
    }

    /**
     * 通知模型删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        SysNotice model = sysNoticeMapper.selectOne(
                new QueryWrapper<SysNotice>()
                    .eq("id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        sysNoticeMapper.delete(new QueryWrapper<SysNotice>().eq("id", id));
    }

}
