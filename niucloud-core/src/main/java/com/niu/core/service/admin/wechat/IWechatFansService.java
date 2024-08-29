package com.niu.core.service.admin.wechat;

import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.wechat.param.WechatFansParam;
import com.niu.core.service.admin.wechat.param.WechatFansSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.wechat.vo.WechatFansInfoVo;
import com.niu.core.service.admin.wechat.vo.WechatFansListVo;

/**
 * 微信粉丝列服务接口
 */
public interface IWechatFansService {


    /**
     * 微信粉丝列列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<WechatFansListVo>
     */
    PageResult<WechatFansListVo> list(PageParam pageParam, WechatFansSearchParam searchParam);

    /**
     * 微信粉丝列详情
     * @param id 主键ID
     * @return WechatFansInfoVo
     */
    WechatFansInfoVo info(Integer id);

    /**
     * 微信粉丝列添加
     * @param addParam 添加参数
     */
    void add(WechatFansParam addParam);

    /**
     * 微信粉丝列编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, WechatFansParam editParam);

    /**
     * 微信粉丝列删除
     * @param id 主键ID
     */
    void del(Integer id);

}
