package com.niu.core.service.admin.site.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.config.GlobalConfig;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.entity.sys.SysUserRole;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.mapper.site.SiteGroupMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.mapper.sys.SysUserRoleMapper;
import com.niu.core.service.admin.site.param.SiteGroupAddParam;
import com.niu.core.service.admin.site.param.SiteGroupParam;
import com.niu.core.service.admin.site.param.SiteGroupSearchParam;
import com.niu.core.service.admin.site.ISiteGroupService;
import com.niu.core.service.admin.site.vo.SiteGroupListVo;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * 店铺分组（分组权限）实现
 */
@Service
public class SiteGroupServiceImpl implements ISiteGroupService {
        
    @Resource
    SiteGroupMapper siteGroupMapper;

    @Resource
    SiteMapper siteMapper;

    @Resource
    AddonMapper addonMapper;

    @Resource
    ICoreAddonService coreAddonService;

    @Resource
    SysUserRoleMapper sysUserRoleMapper;

    /**
     * 店铺分组（分组权限）列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SiteGroupListVo>
     */
    @Override
    public PageResult<SiteGroupListVo> list(PageParam pageParam, SiteGroupSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<SiteGroup> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(searchParam.getKeywords())) {
            queryWrapper.like("group_name", searchParam.getKeywords());
        }
        queryWrapper.orderByDesc("group_id");

        IPage<SiteGroup> iPage = siteGroupMapper.selectPage(new Page<>(page, limit), queryWrapper);

        //获取所有的addon
        List<Addon> addonList = addonMapper.selectList(new QueryWrapper<Addon>().orderByDesc("id"));
        List<SiteGroupListVo> list = new LinkedList<>();
        for (SiteGroup item : iPage.getRecords()) {
            SiteGroupListVo vo = new SiteGroupListVo();
            vo.setGroupId(item.getGroupId());
            vo.setGroupName(item.getGroupName());
            vo.setGroupDesc(item.getGroupDesc());
            vo.setCreateTime(item.getCreateTime());
            vo.setUpdateTime(item.getUpdateTime());
            JSONArray addonJson = JSONUtil.parseArray(item.getAddon());
            vo.setAddon(addonJson);
            JSONArray appJson = JSONUtil.parseArray(item.getApp());
            vo.setApp(appJson);
            List<String> addonStr = new ArrayList<>();
            List<String> appStr = new ArrayList<>();
            for(Addon addon : addonList)
            {
                if(addonJson.contains(addon.getKey())){
                    addonStr.add(addon.getTitle());
                }
                if(appJson.contains(addon.getKey())){
                    appStr.add(addon.getTitle());
                }
            }
            vo.setAddonName(addonStr);
            vo.setAppName(appStr);
            list.add(vo);
        }
        return PageResult.build(iPage.getCurrent(), iPage.getSize()).setTotal(iPage.getTotal()).setData(list);
    }

    @Override
    public List<SiteGroup> getAll() {

        QueryWrapper<SiteGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("group_id");
        return siteGroupMapper.selectList(queryWrapper);
    }

    /**
     * 店铺分组（分组权限）详情
     * @param id 主键
     * @return SiteGroupInfoVo
     */
    @Override
    public SiteGroup info(Integer id) {
        SiteGroup model = siteGroupMapper.selectOne(
                new QueryWrapper<SiteGroup>()
                    .eq("group_id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在");
        return model;
    }

    /**
     * 店铺分组（分组权限）添加
     * @param addParam 参数
     */
    @Override
    public void add(SiteGroupAddParam addParam) {
        JSONArray groupRoles=new JSONArray();
        groupRoles.addAll(addParam.getAddon());
        groupRoles.addAll(addParam.getApp());
        /**判断应用是否全部是有效的已安装应用 */
        checkAddon(groupRoles);
        SiteGroup model = new SiteGroup();
        model.setGroupName(addParam.getGroupName());
        model.setGroupDesc(addParam.getGroupDesc());
        model.setApp(JSONUtil.toJsonStr(addParam.getApp()));
        model.setAddon(JSONUtil.toJsonStr(addParam.getAddon()));
        model.setCreateTime(DateUtils.currTime());
        model.setUpdateTime(DateUtils.currTime());
        siteGroupMapper.insert(model);
    }

    /**
     * 店铺分组（分组权限）编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, SiteGroupParam editParam) {
        SiteGroup model = siteGroupMapper.selectOne(
                new QueryWrapper<SiteGroup>()
                    .eq("group_id",  id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        JSONArray groupRoles=new JSONArray();
        groupRoles.addAll(editParam.getAddon());
        groupRoles.addAll(editParam.getApp());
        /**判断应用是否全部是有效的已安装应用 */
        checkAddon(groupRoles);

        model.setGroupId(id);
        model.setGroupId(editParam.getGroupId());
        model.setGroupName(editParam.getGroupName());
        model.setGroupDesc(editParam.getGroupDesc());
        model.setApp(JSONUtil.toJsonStr(editParam.getApp()));
        model.setAddon(JSONUtil.toJsonStr(editParam.getAddon()));
        model.setUpdateTime(DateUtils.currTime());
        siteGroupMapper.updateById(model);
    }

    /**
     * 店铺分组（分组权限）删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        SiteGroup model = siteGroupMapper.selectOne(
                new QueryWrapper<SiteGroup>()
                    .eq("group_id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        long siteCount=siteMapper.selectCount(new QueryWrapper<Site>().eq("group_id", id));
        if(siteCount>0){
            throw new CommonException("SITE_GROUP_IS_EXIST");
        }
        siteGroupMapper.delete(new QueryWrapper<SiteGroup>().eq("group_id", id));
    }

    /**
     * 检测插件是否有效
     *
     * @param jsonArray
     */
    @Override
    public void checkAddon(JSONArray jsonArray) {
        Map<String, InstallAddonListVo> addonListVoMap= coreAddonService.getInstallAddonList();
        List<String> keys=new ArrayList<>(addonListVoMap.keySet());
        List<String> addonString=JSONUtil.toList(jsonArray, String.class);
        keys.retainAll(addonString);
        if(keys.size()!=addonString.size()){
            throw new AdminException("SITE_GROUP_APP_NOT_EXIST");
        }
    }

    /**
     * 查询店铺套餐以及用户创建的站点数量
     *
     * @param uid
     * @return
     */
    @Override
    public List<SiteGroupListVo> getUserSiteGroupAll(Integer uid) {
        List<SiteGroupListVo> siteGroupListVoList=new ArrayList<>();
        List<SiteGroup> siteGroupList= siteGroupMapper.selectList(new QueryWrapper<>());

        for (SiteGroup siteGroup:siteGroupList) {
            SiteGroupListVo siteGroupListVo=new SiteGroupListVo();
            BeanUtils.copyProperties(siteGroup, siteGroupListVo);
            siteGroupListVo.setSiteNum(getUserSiteGroupSiteNum(uid, siteGroup.getGroupId()));
            siteGroupListVoList.add(siteGroupListVo);
        }
        return siteGroupListVoList;
    }

    /**
     * 查询用户某个站点套餐的站点数量
     *
     * @param uid
     * @param groupId
     * @return
     */
    @Override
    public Integer getUserSiteGroupSiteNum(Integer uid, Integer groupId) {

        MPJQueryWrapper<SysUserRole> userRoleMPJQueryWrapper = new MPJQueryWrapper<>();
        userRoleMPJQueryWrapper.setAlias("sur")
                .leftJoin("?_site s ON sur.site_id = s.site_id".replace("?_", GlobalConfig.tablePrefix));
        userRoleMPJQueryWrapper.eq("sur.uid", uid);
        userRoleMPJQueryWrapper.eq("sur.is_admin", 1);
        userRoleMPJQueryWrapper.eq("s.group_id", groupId);
        Integer count=sysUserRoleMapper.selectJoinCount(userRoleMPJQueryWrapper);
        return count;
    }

}
