package com.niu.core.service.admin.sys;

import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.sys.param.SysPosterParam;
import com.niu.core.service.admin.sys.param.SysPosterSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.sys.vo.SysPosterInfoVo;
import com.niu.core.service.admin.sys.vo.SysPosterListVo;

/**
 * 海报服务接口
 */
public interface ISysPosterService {


    /**
     * 海报列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysPosterListVo>
     */
    PageResult<SysPosterListVo> list(PageParam pageParam, SysPosterSearchParam searchParam);

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

}
