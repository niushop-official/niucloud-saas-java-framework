package com.niu.core.service.admin.site.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.crypto.PasswordEncipher;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.config.GlobalConfig;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.sys.SysRole;
import com.niu.core.entity.sys.SysUser;
import com.niu.core.entity.sys.SysUserRole;
import com.niu.core.enums.member.StatusEnum;
import com.niu.core.mapper.sys.SysRoleMapper;
import com.niu.core.mapper.sys.SysUserMapper;
import com.niu.core.mapper.sys.SysUserRoleMapper;
import com.niu.core.service.admin.site.ISiteUserService;
import com.niu.core.service.admin.site.param.SiteGroupSearchParam;
import com.niu.core.service.admin.site.param.SiteUserParam;
import com.niu.core.service.admin.site.param.SiteUserSearchParam;
import com.niu.core.service.admin.site.vo.SiteListVo;
import com.niu.core.service.admin.site.vo.SiteUserVo;
import com.niu.core.service.admin.sys.ISysUserRoleService;
import com.niu.core.service.admin.sys.ISysUserService;
import com.niu.core.service.admin.sys.param.SysUserParam;
import com.niu.core.service.admin.sys.param.SysUserRoleParam;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class SiteUserServiceImpl implements ISiteUserService {
    @Resource
    SysUserRoleMapper sysUserRoleMapper;
    @Resource
    SysRoleMapper sysRoleMapper;
    @Resource
    ISysUserService sysUserService;
    @Resource
    ISysUserRoleService sysUserRoleService;
    @Resource
    SysUserMapper sysUserMapper;
    /**
     * 用户管理员列表
     *
     * @param pageParam
     * @param searchParam
     * @return
     */
    @Override
    public PageResult<SiteUserVo> list(PageParam pageParam, SiteUserSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        MPJQueryWrapper<SysUserRole> userRoleMPJQueryWrapper = new MPJQueryWrapper<>();
        userRoleMPJQueryWrapper.select("nsur.id, nsur.is_admin, nsu.status, nsur.site_id, nsur.role_ids, nsu.uid, nsu.username, nsu.head_img, nsu.real_name, nsu.last_ip, nsu.last_time, nsu.create_time, nsu.login_count")
                .setAlias("nsur")
                .leftJoin("?_sys_user nsu ON nsur.uid = nsu.uid".replace("?_", GlobalConfig.tablePrefix));
        if(ObjectUtil.isNotNull(searchParam.getUsername()) && ObjectUtil.isNotEmpty(searchParam.getUsername())){
            userRoleMPJQueryWrapper.like("nsu.username", searchParam.getUsername());
        }
        userRoleMPJQueryWrapper.eq("nsur.site_id", RequestUtils.siteId());

        userRoleMPJQueryWrapper.orderByDesc("nsur.is_admin");
        userRoleMPJQueryWrapper.orderByDesc("nsur.id");
        IPage<SiteUserVo> iPage = sysUserRoleMapper.selectJoinPage(new Page<>(page, limit), SiteUserVo.class, userRoleMPJQueryWrapper);
        for (SiteUserVo siteUserVo:iPage.getRecords()) {
            List<String> roleArray=new ArrayList<>();
            if(ObjectUtil.isNotEmpty(siteUserVo.getRoleIds()) && JSONUtil.parseArray(siteUserVo.getRoleIds()).size()>0){
                QueryWrapper<SysRole> roleQueryWrapper=new QueryWrapper<>();
                roleQueryWrapper.in("role_id", JSONUtil.parseArray(siteUserVo.getRoleIds()));
                List<SysRole> roleList=sysRoleMapper.selectList(roleQueryWrapper);
                for (SysRole sysRole:roleList) {
                    roleArray.add(sysRole.getRoleName());
                }
            }
            siteUserVo.setRoleArray(roleArray);
        }
        return PageResult.build(page, limit).setTotal(iPage.getTotal()).setData(iPage.getRecords());
    }

    /**
     * 添加站点用户信息
     *
     * @param siteUserParam
     */
    @Transactional
    @Override
    public void add(SiteUserParam siteUserParam) {
        sysUserService.addSiteUser(siteUserParam, RequestUtils.siteId());
    }

    /**
     * 查询用户信息
     *
     * @param uid
     * @return
     */
    @Override
    public SiteUserVo getInfo(Integer uid) {
        MPJQueryWrapper<SysUserRole> userRoleMPJQueryWrapper = new MPJQueryWrapper<>();
        userRoleMPJQueryWrapper.select("nsur.id, nsur.is_admin, nsur.status,nsur.site_id, nsur.role_ids, nsu.uid, nsu.username, nsu.head_img, nsu.real_name, nsu.last_ip, nsu.last_time, nsu.create_time, nsu.login_count")
                .setAlias("nsur")
                .leftJoin("?_sys_user nsu ON nsur.uid = nsu.uid".replace("?_", GlobalConfig.tablePrefix));
        userRoleMPJQueryWrapper.eq("nsur.site_id", RequestUtils.siteId());
        userRoleMPJQueryWrapper.eq("nsu.uid", uid);

        SiteUserVo siteUserVo = sysUserRoleMapper.selectJoinOne(SiteUserVo.class, userRoleMPJQueryWrapper);
        return siteUserVo;
    }

    /**
     * 编辑用户信息
     *
     * @param uid
     * @param siteUserParam
     */
    @Transactional
    @Override
    public void edit(Integer uid, SiteUserParam siteUserParam) {
        try{
            SysUserParam sysUserParam=new SysUserParam();
            sysUserParam.setHeadImg(siteUserParam.getHeadImg());
            if(ObjectUtil.isNotEmpty(siteUserParam.getPassword())){
                sysUserParam.setPassword(siteUserParam.getPassword());
            }
            sysUserParam.setUsername(siteUserParam.getUsername());
            sysUserParam.setStatus(siteUserParam.getStatus());
            sysUserParam.setRealName(siteUserParam.getRealName());
            sysUserService.edit(uid, sysUserParam);
            //创建用户站点管理权限
            List<String> roleIds=siteUserParam.getRoleIds();
            SysUserRoleParam sysUserRoleParam=new SysUserRoleParam();
            sysUserRoleParam.setSiteId(RequestUtils.siteId());
            sysUserRoleParam.setRoleIds(JSONUtil.toJsonStr(roleIds));
            sysUserRoleParam.setUid(uid);
            sysUserRoleParam.setStatus(siteUserParam.getStatus());
            sysUserRoleService.edit(sysUserRoleParam);
        }catch (Exception e){
            throw new AdminException(e.getMessage());
        }
    }

    /**
     * 锁定用户
     *
     * @param uid
     */
    @Override
    public void lock(Integer uid) {
        SysUser sysUser=sysUserMapper.selectById(uid);
        sysUser.setStatus(StatusEnum.OFF.getStatus());
        sysUserMapper.updateById(sysUser);

    }

    /**
     * 解锁用户
     *
     * @param uid
     */
    @Override
    public void unlock(Integer uid) {
        SysUser sysUser=sysUserMapper.selectById(uid);
        sysUser.setStatus(StatusEnum.ON.getStatus());
        sysUserMapper.updateById(sysUser);
    }
}
