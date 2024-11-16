package com.niu.core.service.admin.sys;

import cn.hutool.json.JSONArray;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.sys.param.SysPosterInitParam;
import com.niu.core.service.admin.sys.param.SysPosterParam;
import com.niu.core.service.admin.sys.param.SysPosterSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.sys.param.SysPosterTemplateSearchParam;
import com.niu.core.service.admin.sys.vo.SysPosterInfoVo;
import com.niu.core.service.admin.sys.vo.SysPosterInitVo;
import com.niu.core.service.admin.sys.vo.SysPosterListVo;

import java.util.List;

/**
 * 海报服务接口
 */
public interface ISysPosterService {

    /**
     * 海报分页列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysPosterListVo>
     */
    PageResult<SysPosterListVo> page(PageParam pageParam, SysPosterSearchParam searchParam);

    /**
     * 海报列表
     * @param searchParam 搜索参数
     * @return PageResult<SysPosterListVo>
     */
    List<SysPosterListVo> list(SysPosterSearchParam searchParam);

    /**
     * 海报详情
     * @param id 主键ID
     * @return SysPosterInfoVo
     */
    SysPosterInfoVo info(Integer id);

    /**
     * 海报添加
     * @param addParam 添加参数
     */
    void add(SysPosterParam addParam);

    /**
     * 海报编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, SysPosterParam editParam);

    /**
     * 海报删除
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 自定义海报初始化数据
     * @param param
     */
    SysPosterInitVo init(SysPosterInitParam param);

    /**
     * 海报模板
     * @param param
     * @return
     */
    JSONArray template(SysPosterTemplateSearchParam param);

    /**
     * 修改自定义海报状态
     *
     * @param id
     * @param status
     */
    void modifyStatus(Integer id, Integer status);

    /**
     * 将自定义海报修改为默认海报
     *
     * @param id
     */
    void modifyDefault(Integer id);
}
