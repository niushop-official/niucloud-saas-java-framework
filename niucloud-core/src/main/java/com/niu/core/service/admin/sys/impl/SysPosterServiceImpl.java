package com.niu.core.service.admin.sys.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.entity.sys.SysPoster;
import com.niu.core.enums.poster.PosterTypeEnum;
import com.niu.core.mapper.sys.SysPosterMapper;
import com.niu.core.service.admin.sys.ISysPosterService;
import com.niu.core.service.admin.sys.param.SysPosterInitParam;
import com.niu.core.service.admin.sys.param.SysPosterParam;
import com.niu.core.service.admin.sys.param.SysPosterSearchParam;
import com.niu.core.service.admin.sys.param.SysPosterTemplateSearchParam;
import com.niu.core.service.admin.sys.vo.SysPosterInfoVo;
import com.niu.core.service.admin.sys.vo.SysPosterInitVo;
import com.niu.core.service.admin.sys.vo.SysPosterListVo;
import com.niu.core.service.core.poster.ICorePosterService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 海报实现
 */
@Service
public class SysPosterServiceImpl implements ISysPosterService {

    @Resource
    SysPosterMapper sysPosterMapper;

    @Resource
    ICorePosterService corePosterService;

    /**
     * 海报列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysPosterListVo>
     */
    @Override
    public PageResult<SysPosterListVo> page(PageParam pageParam, SysPosterSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<SysPoster> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.orderByDesc("id");

        if (ObjectUtil.isNotEmpty(searchParam.getName())) queryWrapper.like("name", searchParam.getName());
        if (ObjectUtil.isNotEmpty(searchParam.getType())) queryWrapper.eq("type", searchParam.getType());

        IPage<SysPoster> iPage = sysPosterMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<SysPosterListVo> list = new LinkedList<>();
        for (SysPoster item : iPage.getRecords()) {
            SysPosterListVo vo = new SysPosterListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 海报列表
     *
     * @param searchParam 搜索参数
     * @return
     */
    @Override
    public List<SysPosterListVo> list(SysPosterSearchParam searchParam) {
        QueryWrapper<SysPoster> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.orderByDesc("id");

        if (ObjectUtil.isNotEmpty(searchParam.getName())) queryWrapper.like("name", searchParam.getName());
        if (ObjectUtil.isNotEmpty(searchParam.getType())) queryWrapper.eq("type", searchParam.getType());

        List<SysPoster> records = sysPosterMapper.selectList(queryWrapper);
        List<SysPosterListVo> list = new LinkedList<>();
        for (SysPoster item : records) {
            SysPosterListVo vo = new SysPosterListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }

    /**
     * 海报详情
     * @param id 主键
     * @return SysPosterInfoVo
     */
    @Override
    public SysPosterInfoVo info(Integer id) {
        SysPoster model = sysPosterMapper.selectOne(
                new QueryWrapper<SysPoster>()
                        .eq("id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "海报不存在");

        SysPosterInfoVo vo = new SysPosterInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 海报添加
     * @param addParam 参数
     */
    @Override
    public void add(SysPosterParam addParam) {
        if (addParam.getIsDefault().equals(1)) {
            UpdateWrapper update = new UpdateWrapper<>();
            update.setSql("is_default = 0");
            update.eq("type", addParam.getType());
            update.eq("is_default", 1);
            update.eq("site_id", RequestUtils.siteId());
            sysPosterMapper.update(null, update);
        }
        SysPoster model = new SysPoster();
        BeanUtils.copyProperties(addParam, model);
        model.setSiteId(RequestUtils.siteId());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        sysPosterMapper.insert(model);
    }

    /**
     * 海报编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, SysPosterParam editParam) {
        SysPoster model = sysPosterMapper.selectOne(
                new QueryWrapper<SysPoster>()
                        .eq("id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "海报不存在!");

        if (editParam.getIsDefault().equals(1)) {
            UpdateWrapper update = new UpdateWrapper<>();
            update.setSql("is_default = 0");
            update.eq("type", editParam.getType());
            update.eq("is_default", 1);
            update.eq("site_id", RequestUtils.siteId());
            sysPosterMapper.update(null, update);
        }

        BeanUtils.copyProperties(editParam, model);
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        sysPosterMapper.updateById(model);
    }

    /**
     * 海报删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        SysPoster model = sysPosterMapper.selectOne(
                new QueryWrapper<SysPoster>()
                        .eq("id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "海报不存在!");

        sysPosterMapper.delete(new QueryWrapper<SysPoster>().eq("id", id));
    }

    /**
     * 自定义海报初始化数据
     *
     * @param param
     * @return
     */
    @Override
    public SysPosterInitVo init(SysPosterInitParam param) {
        SysPosterInitVo vo = new SysPosterInitVo();
        BeanUtils.copyProperties(param, vo);

        if (ObjectUtil.isNotEmpty(param.getId()) && param.getId() > 0) {
            SysPoster poster = sysPosterMapper.selectOne(
                    new QueryWrapper<SysPoster>()
                            .eq("id", param.getId())
                            .eq("site_id", RequestUtils.siteId()));
            Assert.notNull(poster, "海报不存在!");
            BeanUtils.copyProperties(poster, vo);
        }

        vo.setPosterType(PosterTypeEnum.getType(vo.getType()));
        if (ObjectUtil.isNotEmpty(vo.getPosterType()) && ObjectUtil.isEmpty(vo.getAddon())) vo.setAddon(vo.getPosterType().getStr("addon", ""));

        JSONObject components = JsonModuleLoader.build().mergeResultElement("poster/components.json");

        vo.setComponent(new JSONObject());
        for (String key : components.keySet()) {
            JSONObject item = components.getJSONObject(key);
            if (item.getJSONObject("list") == null || item.getJSONObject("list").keySet().size() == 0) {
                continue;
            }
            JSONArray support = ObjectUtil.defaultIfNull(item.getJSONArray("support"), new JSONArray());
            if (!key.equals(vo.getType()) && support.size() > 0 &&  !support.contains(vo.getType())) {
                continue;
            }
            vo.getComponent().put(key, item);
        }

        return vo;
    }

    /**
     * 海报模板
     *
     * @param param
     * @return
     */
    @Override
    public JSONArray template(SysPosterTemplateSearchParam param) {
        return corePosterService.template(param.getAddon(), param.getType());
    }

    /**
     * 修改自定义海报状态
     *
     * @param id
     * @param status
     */
    @Override
    public void modifyStatus(Integer id, Integer status) {
        SysPoster model = new SysPoster();
        model.setStatus(status);
        sysPosterMapper.update(model, new QueryWrapper<SysPoster>().eq("id", id).eq("site_id", RequestUtils.siteId()));
    }

    /**
     * 将自定义海报修改为默认海报
     *
     * @param id
     */
    @Override
    public void modifyDefault(Integer id) {
        SysPoster model = sysPosterMapper.selectOne(
                new QueryWrapper<SysPoster>()
                        .eq("id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "海报不存在!");

        UpdateWrapper update = new UpdateWrapper<>();
        update.setSql("is_default = 0");
        update.eq("type", model.getType());
        update.eq("is_default", 1);
        update.eq("site_id", RequestUtils.siteId());
        sysPosterMapper.update(null, update);

        SysPoster updateModel = new SysPoster();
        updateModel.setIsDefault(1);
        updateModel.setId(id);
        sysPosterMapper.updateById(updateModel);
    }

}