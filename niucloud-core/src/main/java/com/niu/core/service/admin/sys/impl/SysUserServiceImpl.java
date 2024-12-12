package com.niu.core.service.admin.sys.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.utils.IpUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.crypto.PasswordEncipher;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.entity.sys.SysUserRole;
import com.niu.core.entity.user.UserCreateSiteLimit;
import com.niu.core.enums.member.StatusEnum;
import com.niu.core.mapper.sys.SysUserRoleMapper;
import com.niu.core.mapper.user.UserCreateSiteLimitMapper;
import com.niu.core.service.admin.auth.IAuthService;
import com.niu.core.service.admin.auth.ILoginService;
import com.niu.core.service.admin.auth.impl.LoginServiceImpl;
import com.niu.core.service.admin.site.param.SiteUserParam;
import com.niu.core.service.admin.sys.ISysUserRoleService;
import com.niu.core.service.admin.sys.param.*;
import com.niu.core.service.admin.sys.ISysUserService;
import com.niu.core.service.admin.sys.vo.*;
import com.niu.core.common.domain.PageParam;
import com.niu.core.entity.sys.SysUser;
import com.niu.core.mapper.sys.SysUserMapper;
import com.niu.core.common.utils.date.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 后台管理员实现类
 */
@Service
public class SysUserServiceImpl implements ISysUserService {

    @Resource
    SysUserMapper sysUserMapper;
    @Resource
    SysUserRoleMapper sysUserRoleMapper;
    @Resource
    UserCreateSiteLimitMapper userCreateSiteLimitMapper;
    @Resource
    ISysUserRoleService sysUserRoleService;

    IAuthService authService;

    @Resource
    Cached cached;

    public ILoginService getLoginService() {
        return (ILoginService) SpringContext.getBean(LoginServiceImpl.class);
    }


    @Resource
    public void setAuthService(@Lazy IAuthService authService) {
        this.authService = authService;
    }

    /**
     * 后台管理员列表
     *
     * @param pageParam 分页参数
     * @param searchParam  搜索参数
     * @return PageResult<SysUserListVo>
     */
    @Override
    public PageResult<SysUserListVo> list(PageParam pageParam, SysUserSearchParam searchParam) {
        authService.isSuperAdmin();
        Integer superAdminUid=(Integer) cached.tag("adminAuth").get("superAdminUid");

        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("uid");

        if(ObjectUtil.isNotNull(searchParam.getUsername()) && ObjectUtil.isNotEmpty(searchParam.getUsername())){
            queryWrapper.like("username", searchParam.getUsername()).or().like("real_name", searchParam.getUsername());
        }
        if(ObjectUtil.isNotNull(searchParam.getLastTime()) && ObjectUtil.isNotEmpty(searchParam.getLastTime())){
            long startTime=0;
            long endTime=0;
            if(ObjectUtil.isNotNull(searchParam.getLastTime()[0])){
                startTime=DateUtils.StringToTimestamp(searchParam.getLastTime()[0]);
            }
            if(ObjectUtil.isNotNull(searchParam.getLastTime()[1])){
                endTime=DateUtils.StringToTimestamp(searchParam.getLastTime()[1]);
            }
            if(startTime>0 && endTime>0){
                queryWrapper.between("last_time", startTime, endTime);
            }else if (startTime>0){
                queryWrapper.ge("last_time", startTime);
            }else if (endTime>0){
                queryWrapper.le("last_time", endTime);
            }
        }

        IPage<SysUser> iPage = sysUserMapper.selectPage(new Page<SysUser>(page, limit), queryWrapper);

        List<SysUserListVo> list = new LinkedList<>();
        for (SysUser item : iPage.getRecords()) {
            SysUserListVo vo = new SysUserListVo();
            BeanUtils.copyProperties(item, vo);
            Long siteNum=sysUserRoleMapper.selectCount(new QueryWrapper<SysUserRole>().eq("uid", item.getUid()).ne("site_id", RequestUtils.defaultSiteId()));
            vo.setSiteNum(siteNum.intValue());
            vo.setIsSuperAdmin(superAdminUid==item.getUid());
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 后台管理员详情
     *
     * @param id 主键参数
     * @return SysUser
     */
    @Override
    public SysUserDetailVo info(Integer id) {

        authService.isSuperAdmin();
        Integer superAdminUid=(Integer) cached.tag("adminAuth").get("superAdminUid");

        SysUser model = sysUserMapper.selectOne(
                new QueryWrapper<SysUser>()
                        .eq("uid", id)
                        .last("limit 1"));
        Assert.notNull(model, "用户数据不存在");

        SysUserDetailVo vo = new SysUserDetailVo();
        BeanUtils.copyProperties(model, vo);

        MPJQueryWrapper<SysUserRole> userRoleMPJQueryWrapper = new MPJQueryWrapper<>();
        userRoleMPJQueryWrapper.select("sur.*, s.site_id, s.site_name, s.app_type, s.status as siteStatus, s.expire_time")
                .setAlias("sur")
                .leftJoin("?_site s ON sur.site_id = s.site_id".replace("?_", GlobalConfig.tablePrefix));
        userRoleMPJQueryWrapper.eq("sur.uid", vo.getUid());
        List<SysUserSiteRoleVo> userSiteRoleVoList=sysUserRoleMapper.selectJoinList(SysUserSiteRoleVo.class,userRoleMPJQueryWrapper);
        vo.setIsSuperAdmin(superAdminUid==id);
        vo.setRoles(userSiteRoleVoList);
        return vo;
    }

    /**
     * 后台管理员新增
     *
     * @param addParam 参数
     */
    @Transactional
    @Override
    public Integer add(SysUserParam addParam) {
        if(checkUserName(addParam.getUsername())){
            throw new AdminException("USERNAME_REPEAT");
        }
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(addParam, sysUser);
        sysUser.setCreateTime(DateUtils.currTime());
        sysUser.setUpdateTime(DateUtils.currTime());
        sysUserMapper.insert(sysUser);
        Integer uid=sysUser.getUid();

        //添加用户建站限制
        List<SysCreateSiteLimitParam> createSiteLimitParamList=addParam.getCreateSiteLimitParamList();
        if(ObjectUtil.isNotNull(createSiteLimitParamList) && createSiteLimitParamList.size()>0){
            for (SysCreateSiteLimitParam sysCreateSiteLimitParam:createSiteLimitParamList) {
                UserCreateSiteLimit userCreateSiteLimit=new UserCreateSiteLimit();
                userCreateSiteLimit.setUid(uid);
                userCreateSiteLimit.setGroupId(sysCreateSiteLimitParam.getGroupId());
                userCreateSiteLimit.setMonth(sysCreateSiteLimitParam.getMonth());
                userCreateSiteLimit.setNum(sysCreateSiteLimitParam.getNum());
                userCreateSiteLimitMapper.insert(userCreateSiteLimit);
            }
        }
        return uid;
    }


    /**
     * 后台管理员编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer uid, SysUserParam editParam) {
        SysUser model = sysUserMapper.selectOne(
                new QueryWrapper<SysUser>()
                        .eq("uid", uid)
                        .last("limit 1"));
        Assert.notNull(model, "数据不存在!");
        Boolean isOffStatus=false;
        if(ObjectUtil.isNotNull(editParam.getStatus())){
            model.setStatus(editParam.getStatus());
            if(editParam.getStatus()== StatusEnum.OFF.getStatus()){
                isOffStatus=true;
            }
        }
        if(ObjectUtil.isNotNull(editParam.getHeadImg())){
            model.setHeadImg(editParam.getHeadImg());
        }
        if(ObjectUtil.isNotNull(editParam.getRealName())){
            model.setRealName(editParam.getRealName());
        }
        Boolean isChangePassword=false;
        if(ObjectUtil.isNotNull(editParam.getPassword()) && ObjectUtil.isNotEmpty(editParam.getPassword())){
            model.setPassword(PasswordEncipher.encode(editParam.getPassword()));
            isChangePassword=true;
        }
        /**
         * 更新用戶信息
         */
        model.setUpdateTime(DateUtils.currTime());
        sysUserMapper.updateById(model);
        if(isOffStatus || isChangePassword){
            getLoginService().clearToken(uid, RequestUtils.appType(), "");
        }
    }

    /**
     * 后台管理员删除
     *
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        SysUser model = sysUserMapper.selectOne(
                new QueryWrapper<SysUser>()
                        .eq("uid", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        sysUserMapper.delete(new QueryWrapper<SysUser>().eq("uid", id));
    }

    /**
     * 通过账号获取账号信息
     *
     * @param userName
     * @return
     */
    @Override
    public SysUserInfoVo getUserInfoByUserName(String userName) {
        SysUser model=sysUserMapper.selectOne(new QueryWrapper<SysUser>().eq("username", userName).last("limit 1"));
        Assert.notNull(model, "账号或密码错误");
        SysUserInfoVo vo = new SysUserInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 修改用户登录信息
     *
     * @param uid
     */
    @Override
    public void editUserLoginInfo(Integer uid) {
        SysUser model = sysUserMapper.selectOne(
                new QueryWrapper<SysUser>()
                        .eq("uid", uid)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        model.setLastTime(DateUtils.currTime());
        model.setLastIp(IpUtils.getIpAddr(RequestUtils.handler()));
        model.setLoginCount(model.getLoginCount()+1);
        sysUserMapper.updateById(model);
    }

    /**
     * 添加对应站点用户（添加站点，同时添加站点用户,用于添加站点以及站点添加站点用户）
     *
     * @param siteUserParam
     * @param siteId
     */
    @Transactional
    @Override
    public Integer addSiteUser(SiteUserParam siteUserParam, Integer siteId) {
        Integer uid=0;
        try {
            if(ObjectUtil.isNotNull(siteUserParam.getUid()) && siteUserParam.getUid()>0){
                QueryWrapper<SysUser> queryWrapper=new QueryWrapper<>();
                queryWrapper.eq("uid", siteUserParam.getUid());
                SysUser sysUser=sysUserMapper.selectOne(queryWrapper);
                uid=siteUserParam.getUid();
                if(ObjectUtil.isNull(sysUser)){
                    throw new AdminException("USER_NOT_EXIST");
                }
            }else{
                SysUserParam sysUserParam=new SysUserParam();
                sysUserParam.setHeadImg(siteUserParam.getHeadImg());
                sysUserParam.setPassword(PasswordEncipher.encode(siteUserParam.getPassword()));
                sysUserParam.setLastIp("");
                sysUserParam.setLoginCount(0);
                sysUserParam.setUsername(siteUserParam.getUsername());
                sysUserParam.setIsDel(0);
                sysUserParam.setLastTime(DateUtils.currTime());
                sysUserParam.setStatus(siteUserParam.getStatus());
                sysUserParam.setRealName(siteUserParam.getRealName());
                //添加用户
                uid=this.add(sysUserParam);
            }
            List<String> roleIds=siteUserParam.getRoleIds();
            Integer isAdmin=siteUserParam.getIsAdmin()==null?0:siteUserParam.getIsAdmin();
            /**
             * 创建用户站点管理权限
             */
            SysUserRoleParam sysUserRoleParam=new SysUserRoleParam();
            sysUserRoleParam.setRoleIds(JSONUtil.toJsonStr(roleIds));
            sysUserRoleParam.setSiteId(siteId);
            sysUserRoleParam.setUid(uid);
            sysUserRoleParam.setStatus(siteUserParam.getStatus());
            sysUserRoleParam.setIsAdmin(isAdmin);
            sysUserRoleService.add(sysUserRoleParam);
        }catch (Exception e){
            throw new AdminException(e.getMessage());
        }
        return uid;
    }

    /**
     * 检测用户账号是否存在
     *
     * @param userName
     * @return
     */
    @Override
    public boolean checkUserName(String userName) {
        QueryWrapper<SysUser> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("username", userName);
        long count=sysUserMapper.selectCount(queryWrapper);
        if(count>0){
            return true;
        }
        return false;
    }


    /**
     * 获取所有用户数据
     * @return
     */
    @Override
    public List<SysUser> getUserAll() {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("uid, username, head_img").orderByDesc("uid");
        return sysUserMapper.selectList(queryWrapper);
    }

    /**
     * 获取用户站点创建限制
     *
     * @param uid
     * @return
     */
    @Override
    public List<SysUserCreateSiteLimitVo> getUserCreateSiteLimit(Integer uid) {
        List<UserCreateSiteLimit> userCreateSiteLimitList=userCreateSiteLimitMapper.selectList(new QueryWrapper<UserCreateSiteLimit>().eq("uid", uid));
        List<SysUserCreateSiteLimitVo> userCreateSiteLimitVoList=new ArrayList<>();
        for (UserCreateSiteLimit item:userCreateSiteLimitList) {
            SysUserCreateSiteLimitVo userCreateSiteLimitVo=new SysUserCreateSiteLimitVo();
            BeanUtil.copyProperties(item, userCreateSiteLimitVo);
            userCreateSiteLimitVoList.add(userCreateSiteLimitVo);
        }
        return userCreateSiteLimitVoList;
    }

    /**
     * 获取用户站点创建限制
     *
     * @param id
     * @return
     */
    @Override
    public SysUserCreateSiteLimitVo getUserCreateSiteLimitInfo(Integer id) {
        UserCreateSiteLimit userCreateSiteLimit=userCreateSiteLimitMapper.selectById(id);
        SysUserCreateSiteLimitVo sysUserCreateSiteLimitVo=new SysUserCreateSiteLimitVo();
        if(ObjectUtil.isNotNull(userCreateSiteLimit)){
            BeanUtil.copyProperties(userCreateSiteLimit, sysUserCreateSiteLimitVo);
        }
        return sysUserCreateSiteLimitVo;
    }

    /**
     * 添加用户站点创建限制
     *
     * @param userCreateSiteLimitAddParam
     */
    @Transactional
    @Override
    public void addUserCreateSiteLimit(SysUserCreateSiteLimitAddParam userCreateSiteLimitAddParam) {
        userCreateSiteLimitMapper.delete(new QueryWrapper<UserCreateSiteLimit>().eq("uid", userCreateSiteLimitAddParam.getUid()).eq("group_id", userCreateSiteLimitAddParam.getGroupId()));
        UserCreateSiteLimit userCreateSiteLimit=new UserCreateSiteLimit();
        userCreateSiteLimit.setNum(userCreateSiteLimitAddParam.getNum());
        userCreateSiteLimit.setUid(userCreateSiteLimitAddParam.getUid());
        userCreateSiteLimit.setMonth(userCreateSiteLimitAddParam.getMonth());
        userCreateSiteLimit.setGroupId(userCreateSiteLimitAddParam.getGroupId());
        userCreateSiteLimitMapper.insert(userCreateSiteLimit);
    }

    /**
     * 编辑用户站点创建限制
     *
     * @param userCreateSiteLimitEditParam
     */
    @Transactional
    @Override
    public void editUserCreateSiteLimit(SysUserCreateSiteLimitEditParam userCreateSiteLimitEditParam) {
        UserCreateSiteLimit userCreateSiteLimit=userCreateSiteLimitMapper.selectById(userCreateSiteLimitEditParam.getId());
        userCreateSiteLimit.setNum(userCreateSiteLimitEditParam.getNum());
        userCreateSiteLimit.setMonth(userCreateSiteLimitEditParam.getMonth());
        userCreateSiteLimitMapper.updateById(userCreateSiteLimit);
    }

    /**
     * 删除用户站点创建限制
     *
     * @param id
     */
    @Transactional
    @Override
    public void delUserCreateSiteLimit(Integer id) {
        userCreateSiteLimitMapper.deleteById(id);
    }

    /**
     * 通过用户id获取用户信息
     *
     * @param uid
     * @return
     */
    @Override
    public SysUser find(Integer uid) {
        SysUser sysuser=sysUserMapper.selectById(uid);
        Assert.notNull(sysuser, "用户数据不存在!");
        return sysuser;
    }

}
