package com.niu.core.service.admin.site;

import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.site.param.SiteAccountLogParam;
import com.niu.core.service.admin.site.param.SiteAccountLogSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.site.vo.SiteAccountLogInfoVo;
import com.niu.core.service.admin.site.vo.SiteAccountLogListVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 站点账单记录服务接口
 */
public interface ISiteAccountLogService {


    /**
     * 站点账单记录列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SiteAccountLogListVo>
     */
    PageResult<SiteAccountLogListVo> list(PageParam pageParam, SiteAccountLogSearchParam searchParam);

    /**
     * 站点账单记录详情
     * @param id 主键ID
     * @return SiteAccountLogInfoVo
     */
    SiteAccountLogInfoVo info(Integer id);

    /**
     * 站点统计
     * @return
     */
    Map<String, Object> stat();

}
