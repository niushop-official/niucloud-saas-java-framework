package com.niu.core.service.admin.sys.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.google.gson.JsonArray;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.config.GlobalConfig;
import com.niu.core.enums.member.StatusEnum;
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

import javax.annotation.Resource;


/**
 * 用户权限实现类
 */
@Service
public class SysUserRoleServiceImpl implements ISysUserRoleService {

    @Resource
    SysUserRoleMapper sysUserRoleMapper;

    /**
     * 用户权限列表
     *
     * @param pageParam 分页参数
     * @param searchParam  搜索参数
     * @return PageResult<SysUserRoleListVo>
     */
    @Override
    public PageResult<SysUserRoleListVo> list(PageParam pageParam, SysUserRoleSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<SysUserRole> queryWrapper = new MPJQueryWrapper<>();

        //sql语句
        queryWrapper.select("nsu.uid, nsu.username, nsu.head_img, nsu.password, nsu.real_name, nsu.last_ip, nsu.last_time, nsu.create_time, nsu.login_count, nsu.status, nsu.is_del, nsu.delete_time, nsu.update_time, nsur.id, nsur.site_id, nsur.role_ids, nsur.is_admin")
                .setAlias("nsur")
                .leftJoin("?_sys_user nsu ON nsur.uid = nsu.uid".replace("?_", GlobalConfig.tablePrefix));

        //查询条件判断组装
        if (ObjectUtil.isNotEmpty(searchParam.getUid())) {
            queryWrapper.eq("nsu.uid", searchParam.getUid());
        }

        //排序
        queryWrapper.orderByDesc("id");
        //分页查询
        IPage<SysUserRoleListVo> iPage = sysUserRoleMapper.selectJoinPage(
                new Page<>(page, limit),
                SysUserRoleListVo.class,
                queryWrapper);


        return PageResult.build(iPage);
    }

    /**
     * 用户权限详情
     *
     * @param id 主键参数
     * @return SysUserRole
     */
    @Override
    public SysUserRoleInfoVo info(Integer id) {
        SysUserRole model = sysUserRoleMapper.selectOne(
                new QueryWrapper<SysUserRole>()
                        .eq("id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        SysUserRoleInfoVo vo = new SysUserRoleInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

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
                        .eq("site_id", addParam.getUid())
                );
        if (userRoleCount>0){
            throw new CommonException("SITE_USER_EXIST");
        }

        SysUserRole model = new SysUserRole();
        model.setUid(addParam.getUid());
        model.setSiteId(addParam.getSiteId()==null? RequestUtils.siteId():addParam.getSiteId());
        model.setRoleIds(addParam.getRoleIds());
        model.setCreateTime(DateUtils.currTime());
        model.setIsAdmin(addParam.getIsAdmin()==null?0:addParam.getIsAdmin());
        model.setStatus(ObjectUtil.isNull(addParam.getStatus())? StatusEnum.ON.getStatus() : addParam.getStatus());
        if(model.getStatus()<1){
            model.setRoleIds(addParam.getRoleIds()==null? JSONUtil.toJsonStr(new JsonArray()):addParam.getRoleIds());
        }
        sysUserRoleMapper.insert(model);
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
        Assert.notNull(model, "数据不存在!");
        Integer isAdmin=model.getIsAdmin();
        if(isAdmin>0){
            //超级管理员不允许改动权限
            throw new AdminException("ADMIN_NOT_ALLOW_EDIT_ROLE");
        }
        model.setStatus(editParam.getStatus());
        model.setRoleIds(editParam.getRoleIds());
        sysUserRoleMapper.updateById(model);
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
        SysUserRole model=sysUserRoleMapper.selectOne(new QueryWrapper<SysUserRole>().eq("uid", uid).eq("site_id", site_id).last(" limit 1"));
        SysUserRoleInfoVo vo = new SysUserRoleInfoVo();
        if(ObjectUtil.isNotNull(model)){
            BeanUtils.copyProperties(model, vo);
            return vo;
        }else{
            return null;
        }
    }

}
