package com.niu.core.service.admin.diy;

import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.diy.param.*;
import com.niu.core.service.admin.diy.vo.DiyPageInfoVo;
import com.niu.core.service.admin.diy.vo.DiyPageListVo;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.core.diy.param.StartUpPageConfigParam;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * 自定义页面服务接口类
 */
public interface IDiyService {

    /**
     * 自定义页面列表
     *
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<DiyPageListedVo>
     */
    PageResult<DiyPageListVo> list(PageParam pageParam, DiyPageSearchParam searchParam);

    /**
     * 自定义页面列表
     * @param searchParam
     */
    List<DiyPageListVo> allList(DiyPageSearchParam searchParam);

    /**
     * 自定义页面详情
     *
     * @param id 主键ID
     * @return DiyPageInfoVo
     */
    DiyPageInfoVo info(Integer id);

    /**
     * 自定义页面详情
     *
     * @param name
     * @return
     */
    DiyPageInfoVo infoByName(String name);

    /**
     * 自定义页面添加
     *
     * @param addParam 参数
     */
    void add(DiyPageParam addParam);

    /**
     * 自定义页面编辑
     *
     * @param editParam 参数
     */
    void edit(Integer id, DiyPageParam editParam);

    /**
     * 自定义页面删除
     *
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 设为使用
     *
     * @param id 主键ID
     */
    void setUse(Integer id);

    /**
     * 获取自定义链接列表
     *
     * @return
     */
    JSONObject getLink();

    /**
     * 获取页面初始化数据
     * @param param
     */
    DiyPageInfoVo getPageInit(DiyPageInitParam param);

    JSONObject getComponentList(String type);

    /**
     * 获取默认页面数据
     *
     * @param type
     * @param addon
     * @return
     */
    JSONObject getFirstPageData(String type, String addon);

    /**
     * 获取页面模板
     *
     * @return
     */
    JSONObject getTemplate(TemplateParam param);

    /**
     * 设置启动页
     * @param value
     */
    void changeTemplate(StartUpPageConfigParam value);

    /**
     * 获取装修页面列表
     * @param searchParam
     */
    JSONObject getDecoratePage(DiyPageSearchParam searchParam);

    /**
     * 自定义页面分页列表，轮播搜索组件用
     */
    PageResult<DiyPageListVo> getPageByCarouselSearch(PageParam pageParam) ;

    /**
     * 设置 首页/个人中心 的第一个模板 设置为启动页
     * @param param
     */
    void setDiyData(SetDiyDataParam param);

    /**
     * 复制
     * @param id
     */
    void copy(Integer id);
}
