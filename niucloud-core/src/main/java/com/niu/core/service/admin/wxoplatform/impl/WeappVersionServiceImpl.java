package com.niu.core.service.admin.wxoplatform.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.config.GlobalConfig;
import com.niu.core.entity.oplatform.WxOplatfromWeappVersion;
import com.niu.core.entity.sys.SysRole;
import com.niu.core.entity.sys.SysUserRole;
import com.niu.core.mapper.oplatform.WxOplatfromWeappVersionMapper;
import com.niu.core.service.admin.site.vo.SiteUserVo;
import com.niu.core.service.admin.wxoplatform.IWeappVersionService;
import com.niu.core.service.admin.wxoplatform.vo.WxOplatfromWeappVersionVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class WeappVersionServiceImpl implements IWeappVersionService {

    @Resource
    WxOplatfromWeappVersionMapper wxOplatfromWeappVersionMapper;

    /**
     * 获取最后一次提交记录
     *
     * @return
     */
    @Override
    public WxOplatfromWeappVersionVo getLastCommitRecord() {
        QueryWrapper<WxOplatfromWeappVersion> queryWrapper=new QueryWrapper<>();
        queryWrapper.orderByDesc(" id ");
        WxOplatfromWeappVersion wxOplatfromWeappVersion=wxOplatfromWeappVersionMapper.selectOne(queryWrapper);
        WxOplatfromWeappVersionVo wxOplatfromWeappVersionVo=new WxOplatfromWeappVersionVo();
        BeanUtils.copyProperties(wxOplatfromWeappVersion, wxOplatfromWeappVersionVo);
        return wxOplatfromWeappVersionVo;
    }

    /**
     * 小程序版本列表分页
     *
     * @param pageParam
     * @return
     */
    @Override
    public PageResult<WxOplatfromWeappVersionVo> list(PageParam pageParam) {

        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<WxOplatfromWeappVersion> queryWrapper=new QueryWrapper<>();
        queryWrapper.orderByDesc(" id ");



        MPJQueryWrapper<WxOplatfromWeappVersion> wxOplatfromMPJQueryWrapper = new MPJQueryWrapper<>();
        wxOplatfromMPJQueryWrapper.select("wowv.id, wowv.site_group_id, wowv.template_id, wowv.user_version, wowv.user_desc, wowv.task_key, wowv.status, wowv.fail_reason, wowv.version_no, wowv.create_time, wowv.update_time, nsg.site_group_name as group_name")
                .setAlias("wowv")
                .leftJoin("?_site_group nsg ON nsg.group_id = wowv.site_group_id".replace("?_", GlobalConfig.tablePrefix));
        wxOplatfromMPJQueryWrapper.orderByDesc(" wowv.id ");

        IPage<WxOplatfromWeappVersionVo> iPage = wxOplatfromWeappVersionMapper.selectJoinPage(new Page<>(page, limit), WxOplatfromWeappVersionVo.class, wxOplatfromMPJQueryWrapper);
        return PageResult.build(page, limit).setTotal(iPage.getTotal()).setData(iPage.getRecords());
    }




}
