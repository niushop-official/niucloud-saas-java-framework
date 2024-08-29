package com.niu.core.service.admin.sys.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.sys.SysUserRole;
import com.niu.core.enums.sys.RoleStatusEnum;
import com.niu.core.mapper.sys.SysUserRoleMapper;
import com.niu.core.service.admin.sys.param.SysRoleParam;
import com.niu.core.service.admin.sys.param.SysRoleSearchParam;
import com.niu.core.service.admin.sys.ISysRoleService;
import com.niu.core.service.admin.sys.vo.SysRoleInfoVo;
import com.niu.core.service.admin.sys.vo.SysRoleListVo;
import com.niu.core.common.domain.PageParam;
import com.niu.core.entity.sys.SysRole;
import com.niu.core.mapper.sys.SysRoleMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * 角色实现类
 */
@Service
public class SysRoleServiceImpl implements ISysRoleService {

    @Resource
    SysRoleMapper sysRoleMapper;
    @Resource
    SysUserRoleMapper sysUserRoleMapper;

    /**
     * 角色列表
     *
     * @param pageParam 分页参数
     * @param searchParam  搜索参数
     * @return PageResult<SysRoleListVo>
     */
    @Override
    public PageResult<SysRoleListVo> list(PageParam pageParam, SysRoleSearchParam searchParam) {

        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("role_id");
        queryWrapper.eq("site_id", RequestUtils.siteId());
        if(ObjectUtil.isNotNull(searchParam.getRoleName()) && ObjectUtil.isNotEmpty(searchParam.getRoleName())){
            queryWrapper.like("role_name", searchParam.getRoleName());
        }
        IPage<SysRole> iPage = sysRoleMapper.selectPage(new Page<SysRole>(page, limit), queryWrapper);
        List<SysRoleListVo> list = new LinkedList<>();
        for (SysRole item : iPage.getRecords()) {
            SysRoleListVo vo = new SysRoleListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit).setData(list);
    }

    /**
     * 角色详情
     *
     * @param id 主键参数
     * @return SysRole
     */
    @Override
    public SysRoleInfoVo info(Integer id) {
        SysRole model = sysRoleMapper.selectOne(
                new QueryWrapper<SysRole>()
                        .eq("role_id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        SysRoleInfoVo vo = new SysRoleInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 角色新增
     *
     * @param addParam 参数
     */
    @Override
    public void add(SysRoleParam addParam) {
        SysRole model = new SysRole();
        model.setSiteId(RequestUtils.siteId());
        model.setRoleName(addParam.getRoleName());
        model.setRules(JSONUtil.toJsonStr(addParam.getRules()));
        model.setStatus(addParam.getStatus());
        model.setCreateTime(DateUtils.currTime());
        model.setUpdateTime(DateUtils.currTime());
        sysRoleMapper.insert(model);
    }

    /**
     * 角色编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer roleId, SysRoleParam editParam) {
        SysRole model = sysRoleMapper.selectOne(
                new QueryWrapper<SysRole>()
                        .eq("role_id", roleId)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        model.setSiteId(RequestUtils.siteId());
        model.setRoleName(editParam.getRoleName());
        model.setRules(JSONUtil.toJsonStr(editParam.getRules()));
        model.setStatus(editParam.getStatus());
        model.setUpdateTime(DateUtils.currTime());
        sysRoleMapper.updateById(model);
    }

    /**
     * 角色删除
     *
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        SysRole model = sysRoleMapper.selectOne(
                new QueryWrapper<SysRole>()
                        .eq("role_id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        Long userRoleCount=sysUserRoleMapper.selectCount(new QueryWrapper<SysUserRole>().like("role_ids", id));
        if(userRoleCount>0){
            throw new AdminException("USER_ROLE_NOT_ALLOW_DELETE");
        }
        sysRoleMapper.delete(new QueryWrapper<SysRole>().eq("role_id", id));
    }

    /**
     * 通过权限组id获取菜单
     *
     * @param siteId
     * @param roleIds
     * @return
     */
    @Override
    public List<String> getMenuIdsByRoleIds(Integer siteId, List<String> roleIds) {
        List<SysRole> roleList=sysRoleMapper.selectList(
                new QueryWrapper<SysRole>()
                        .eq("status", RoleStatusEnum.ON.getCode())
                        .in("role_id", roleIds)
                    );
        JSONArray roleMenuJson = new JSONArray();
        for (SysRole sysRole:roleList) {
            JSONArray ruleJson = JSONUtil.parseArray(sysRole.getRules());
            roleMenuJson.addAll(ruleJson);
        }
        //去重
        List<String> list = JSONUtil.toList(roleMenuJson, String.class);
        return CollectionUtil.distinct(list);
    }

    /**
     * 获取站点下所有的权限角色
     *
     * @return
     */
    @Override
    public List<SysRoleListVo> getAllRole() {
        QueryWrapper<SysRole> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.eq("status", 1);
        List<SysRole> roleList=sysRoleMapper.selectList(queryWrapper);
        List<SysRoleListVo> sysRoleListVoList=new ArrayList<>();
        for (SysRole sysRole:roleList) {
            SysRoleListVo vo=new SysRoleListVo();
            BeanUtils.copyProperties(sysRole, vo);
            sysRoleListVoList.add(vo);
        }
        return sysRoleListVoList;
    }

}
