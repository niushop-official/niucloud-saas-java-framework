package com.niu.core.service.admin.sys;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.sys.param.SysPrinterTemplateParam;
import com.niu.core.service.admin.sys.param.SysPrinterTemplateSearchParam;
import com.niu.core.service.admin.sys.vo.SysPrinterTemplateInfoVo;
import com.niu.core.service.admin.sys.vo.SysPrinterTemplateListVo;

import java.util.List;

/**
 * 小票打印模板服务接口
 */
public interface ISysPrinterTemplateService {

    /**
     * 小票打印模板列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysPrinterTemplateListVo>
     */
    PageResult<SysPrinterTemplateListVo> page(PageParam pageParam, SysPrinterTemplateSearchParam searchParam);

    List<SysPrinterTemplateListVo> getList(SysPrinterTemplateSearchParam searchParam);

    /**
     * 小票打印模板详情
     *
     * @param id 主键ID
     * @return SysPrinterTemplateInfoVo
     */
    SysPrinterTemplateInfoVo info(Integer id);

    /**
     * 小票打印模板添加
     *
     * @param addParam 添加参数
     */
    void add(SysPrinterTemplateParam addParam);

    /**
     * 小票打印模板编辑
     *
     * @param id        主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, SysPrinterTemplateParam editParam);

    /**
     * 小票打印模板删除
     *
     * @param id 主键ID
     */
    void del(Integer id);

}
