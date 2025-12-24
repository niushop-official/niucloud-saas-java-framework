package com.niu.core.service.admin.sys.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.sys.SysUserRole;
import com.niu.core.enums.sys.RoleStatusEnum;
import com.niu.core.mapper.sys.SysUserRoleMapper;
import com.niu.core.service.admin.auth.IAuthService;
import com.niu.core.service.admin.sys.param.SysRoleModifyStatusParam;
import com.niu.core.service.admin.sys.param.SysRoleParam;
import com.niu.core.service.admin.sys.param.SysRoleSearchParam;
import com.niu.core.service.admin.sys.ISysRoleService;
import com.niu.core.service.admin.sys.vo.SysRoleInfoVo;
import com.niu.core.service.admin.sys.vo.SysRoleListVo;
import com.niu.core.common.domain.PageParam;
import com.niu.core.entity.sys.SysRole;
import com.niu.core.mapper.sys.SysRoleMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 角色实现类
 */
@Service
public class SysRoleServiceImpl implements ISysRoleService {

    @Resource
    SysRoleMapper sysRoleMapper;
    @Resource
    SysUserRoleMapper sysUserRoleMapper;
    @Lazy
    @Resource
    private IAuthService authService;

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
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
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
                        .eq("site_id", RequestUtils.siteId())
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
        SysRole isExist = sysRoleMapper.selectOne(new QueryWrapper<SysRole>()
            .select("role_id")
            .eq("role_name", addParam.getRoleName())
            .eq("site_id", RequestUtils.siteId())
            .last("limit 1")
        );
        if (isExist != null) throw new AdminException("管理员角色已存在");

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
        SysRole isExist = sysRoleMapper.selectOne(new QueryWrapper<SysRole>()
            .select("role_id")
            .eq("role_name", editParam.getRoleName())
            .eq("site_id", RequestUtils.siteId())
            .ne("role_id", roleId)
            .last("limit 1")
        );
        if (isExist != null) throw new AdminException("管理员角色已存在");

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
                        .eq("site_id", RequestUtils.siteId())
                        .eq("role_id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        Long userRoleCount=sysUserRoleMapper.selectCount(new QueryWrapper<SysUserRole>().eq("site_id", RequestUtils.siteId()).like("role_ids", "\"+id+\""));
        if(userRoleCount>0){
            throw new AdminException("存在管理员使用当前角色,不允许删除");
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
        QueryWrapper queryWrapper = new QueryWrapper<SysRole>();
        queryWrapper.eq("status", RoleStatusEnum.ON.getCode());
        // 判断roleIds不能为空
        if(ObjectUtil.isNotNull(roleIds) && roleIds.size()>0){
            queryWrapper.in("role_id", roleIds);
        }
        List<SysRole> roleList=sysRoleMapper.selectList(queryWrapper);
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
        SysUserRole sysUserRole = null;
        Boolean isAdmin=false;
        if (authService.isSuperAdmin()){
            isAdmin=true;
        }else {
            sysUserRole = sysUserRoleMapper.selectOne(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getSiteId, RequestUtils.siteId()).eq(SysUserRole::getUid, RequestUtils.uid()));
            if (ObjectUtil.isEmpty(sysUserRole)){
                return List.of();
            }
            isAdmin = sysUserRole.getIsAdmin() == 1 ? true : false;
        }
        if (!isAdmin){
            if (StringUtils.isNotEmpty(sysUserRole.getRoleIds()) && "[]".equals(sysUserRole.getRoleIds())){
                String roleIdsStr = sysUserRole.getRoleIds().replaceAll("[\\[\\]\"]", "");
                List<String> menuIdsByRoleIds = getMenuIdsByRoleIds(RequestUtils.siteId(), Arrays.stream(roleIdsStr.split(",")).collect(Collectors.toList()));
                for (SysRoleListVo sysRoleListVo : sysRoleListVoList) {
                    List<String> diff  = new ArrayList<>(Collections.singleton(sysRoleListVo.getRules()));
                    diff.removeAll(menuIdsByRoleIds);
                    if (!diff.isEmpty()){
                        sysRoleListVo.setDisabled(true);
                    }
                }
            }

        }
        sysRoleListVoList.forEach(sysRoleListVo -> sysRoleListVo.setRules(null));
        return sysRoleListVoList;
    }

    /**
     * 修改角色状态
     * @param editParam
     */
    @Override
    public void modifyStatus(SysRoleModifyStatusParam editParam) {
        SysRole model = sysRoleMapper.selectOne(
            new QueryWrapper<SysRole>()
                .eq("site_id", RequestUtils.siteId())
                .eq("role_id", editParam.getRoleId())
                .last("limit 1"));
        Assert.notNull(model, "角色不存在!");
        model.setStatus(editParam.getStatus());
        sysRoleMapper.updateById(model);
    }

}
