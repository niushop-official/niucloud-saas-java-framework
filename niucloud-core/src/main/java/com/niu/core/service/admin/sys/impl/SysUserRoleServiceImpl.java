package com.niu.core.service.admin.sys.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.google.gson.JsonArray;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.TreeUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.entity.sys.SysArea;
import com.niu.core.entity.sys.SysRole;
import com.niu.core.enums.member.StatusEnum;
import com.niu.core.mapper.sys.SysRoleMapper;
import com.niu.core.service.admin.sys.param.SysUserRoleParam;
import com.niu.core.service.admin.sys.param.SysUserRoleSearchParam;
import com.niu.core.service.admin.sys.ISysUserRoleService;
import com.niu.core.service.admin.sys.vo.SysUserRoleInfoVo;
import com.niu.core.service.admin.sys.vo.SysUserRoleListVo;
import com.niu.core.common.domain.PageParam;
import com.niu.core.entity.sys.SysUserRole;
import com.niu.core.mapper.sys.SysUserRoleMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;

import java.util.Arrays;
import java.util.List;


/**
 * 用户权限实现类
 */
@Service
public class SysUserRoleServiceImpl implements ISysUserRoleService {

    @Resource
    SysUserRoleMapper sysUserRoleMapper;

    @Resource
    SysRoleMapper sysRoleMapper;

    @Resource
    Cached cached;

    /**
     * 用户权限新增
     *
     * @param addParam 参数
     */
    @Override
    public void add(SysUserRoleParam addParam) {
        Long userRoleCount=sysUserRoleMapper.selectCount(
                new QueryWrapper<SysUserRole>()
                        .eq("uid", addParam.getUid())
                        .eq("site_id", addParam.getSiteId())
                );
        if (userRoleCount>0){
            throw new CommonException("该用户已存在");
        }

        SysUserRole model = new SysUserRole();
        model.setUid(addParam.getUid());
        model.setSiteId(addParam.getSiteId()==null? RequestUtils.siteId():addParam.getSiteId());
        model.setRoleIds(addParam.getRoleIds());
        model.setCreateTime(DateUtils.currTime());
        model.setIsAdmin(addParam.getIsAdmin()==null?0:addParam.getIsAdmin());
        model.setStatus(ObjectUtil.isNull(addParam.getStatus())? StatusEnum.ON.getStatus() : addParam.getStatus());
        if(model.getIsAdmin() == 0){
            model.setRoleIds(addParam.getRoleIds() ==null ? JSONUtil.toJsonStr(new JsonArray()) : addParam.getRoleIds());
        }
        sysUserRoleMapper.insert(model);

        cached.remove("user_role_" + model.getUid() + "_" + model.getSiteId());
        cached.remove("user_role_list_" + model.getUid());
    }

    /**
     * 用户权限编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(SysUserRoleParam editParam) {
        SysUserRole model = sysUserRoleMapper.selectOne(
                new QueryWrapper<SysUserRole>()
                        .eq("uid", editParam.getUid())
                        .eq("site_id", editParam.getSiteId())
                        .last("limit 1"));
        Assert.notNull(model, "用户不存在关联权限!");
        Integer isAdmin=model.getIsAdmin();
        if(isAdmin>0){
            //超级管理员不允许改动权限
            throw new AdminException("超级管理员不允许改动权限");
        }
        model.setStatus(editParam.getStatus());
        model.setRoleIds(editParam.getRoleIds());
        sysUserRoleMapper.updateById(model);

        cached.remove("user_role_" + model.getUid() + "_" + model.getSiteId());
        cached.remove("user_role_list_" + model.getUid());
    }

    @Override
    public void editAdmin(SysUserRoleParam editParam) {
        SysUserRole model = sysUserRoleMapper.selectOne(
            new QueryWrapper<SysUserRole>()
                .eq("is_admin", 1)
                .eq("site_id", editParam.getSiteId())
                .last("limit 1"));
        Assert.notNull(model, "站点用户不存在!");

        // 清除原管理员权限缓存
        cached.remove("user_role_" + model.getUid() + "_" + model.getSiteId());
        cached.remove("user_role_list_" + model.getUid());

        model.setUid(editParam.getUid());
        sysUserRoleMapper.updateById(model);

        // 清除新管理员权限缓存
        cached.remove("user_role_" + model.getUid() + "_" + model.getSiteId());
        cached.remove("user_role_list_" + model.getUid());
    }

    /**
     * 用户权限删除
     *
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        SysUserRole model = sysUserRoleMapper.selectOne(
                new QueryWrapper<SysUserRole>()
                        .eq("id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        sysUserRoleMapper.delete(new QueryWrapper<SysUserRole>().eq("id", id));

        cached.remove("user_role_" + model.getUid() + "_" + model.getSiteId());
        cached.remove("user_role_list_" + model.getUid());
    }

    /**
     * 用户权限信息  获取用户对应站点权限
     *
     * @param site_id
     * @param uid
     * @return
     */
    @Override
    public SysUserRoleInfoVo getUserRole(Integer site_id, Integer uid) {
        return cached.rememberObject(Arrays.asList("user_role_" + uid + "_" + site_id), uniqueKey -> {
            SysUserRole model=sysUserRoleMapper.selectOne(new QueryWrapper<SysUserRole>().eq("uid", uid).eq("site_id", site_id).last(" limit 1"));
            SysUserRoleInfoVo vo = new SysUserRoleInfoVo();
            if(ObjectUtil.isNotNull(model)){
                BeanUtils.copyProperties(model, vo);
                return vo;
            }else{
                return null;
            }
        });
    }

    /**
     * 通过角色id组获取角色
     * @param userRoleIds
     * @param siteId
     * @return
     */
    @Override
    public List<String> getRoleByUserRoleIds(String userRoleIds, Integer siteId) {
        return sysRoleMapper.selectList(new QueryWrapper<SysRole>()
            .in("role_id", JSONUtil.parseArray(userRoleIds))
            .eq("status", StatusEnum.ON.getStatus())
            .eq("site_id", siteId)
        ).stream().map(i -> i.getRoleId().toString()).toList();
    }

}
