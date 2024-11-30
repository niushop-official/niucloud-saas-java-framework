package com.niu.core.service.admin.sys;

import cn.hutool.json.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.sys.param.SysPrinterModifyStatusParam;
import com.niu.core.service.admin.sys.param.SysPrinterParam;
import com.niu.core.service.admin.sys.param.SysPrinterSearchParam;
import com.niu.core.service.admin.sys.vo.SysPrinterInfoVo;
import com.niu.core.service.admin.sys.vo.SysPrinterListVo;
import com.niu.core.service.core.sys.param.SysPrinterPrintTicketParam;
import com.niu.core.service.core.sys.vo.SysPrinterPrintTicketVo;

import java.util.List;

/**
 * 小票打印机服务接口
 */
public interface ISysPrinterService {

    /**
     * 小票打印机列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysPrinterListVo>
     */
    PageResult<SysPrinterListVo> page(PageParam pageParam, SysPrinterSearchParam searchParam);

    List<SysPrinterListVo> getList(SysPrinterSearchParam param);

    /**
     * 小票打印机详情
     *
     * @param id 主键ID
     * @return SysPrinterInfoVo
     */
    SysPrinterInfoVo info(Integer id);

    /**
     * 小票打印机添加
     *
     * @param addParam 添加参数
     */
    void add(SysPrinterParam addParam);

    /**
     * 小票打印机编辑
     *
     * @param id        主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, SysPrinterParam editParam);

    /**
     * 小票打印机删除
     *
     * @param id 主键ID
     */
    void del(Integer id);

    void modifyStatus(SysPrinterModifyStatusParam param);

    String refreshToken(Integer id);

    void testPrint(Integer id);

    JSON getType();

    JSONObject getBrand();

    SysPrinterPrintTicketVo printTicket(SysPrinterPrintTicketParam param);
}
