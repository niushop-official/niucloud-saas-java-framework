package com.niu.recharge.service.admin;

import cn.hutool.json.JSONArray;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.recharge.service.admin.param.RechargeEditSortParam;
import com.niu.recharge.service.admin.param.RechargeModifyStatusParam;
import com.niu.recharge.service.admin.param.RechargePageParam;
import com.niu.recharge.service.admin.param.RechargeParam;
import com.niu.recharge.service.admin.vo.RechargeInitVo;
import com.niu.recharge.service.admin.vo.RechargeListVo;
import com.niu.recharge.service.core.param.RechargeSetConfigParam;
import com.niu.recharge.service.core.vo.RechargeConfigVo;

public interface IRechargeService {
    void setRechargeConfig(RechargeSetConfigParam param);

    RechargeConfigVo getRechargeConfig();

    PageResult<RechargeListVo> getPage(PageParam pageParam, RechargePageParam param);

    void add(RechargeParam param);

    void edit(Integer id, RechargeParam param);

    RechargeInitVo getInit(Integer rechargeId);

    void del(Integer id);

    void modifyStatus(RechargeModifyStatusParam param);

    void editSort(RechargeEditSortParam param);

    JSONArray getPackageGiftDict();
}
