package com.niu.shop.service.admin.marketing;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.marketing.param.ShopManjianBatchParam;
import com.niu.shop.service.admin.marketing.param.ShopManjianCheckGoodsParam;
import com.niu.shop.service.admin.marketing.param.ShopManjianGoodsCheckParam;
import com.niu.shop.service.admin.marketing.param.ShopManjianInitParam;
import com.niu.shop.service.admin.marketing.vo.ShopManjianInitVo;
import com.niu.shop.service.admin.marketing.vo.ShopManjianMemberVo;
import com.niu.shop.service.core.marketing.param.ShopManjianParam;
import com.niu.shop.service.core.marketing.param.ShopManjianSearchParam;
import com.niu.shop.service.core.marketing.vo.ShopManjianInfoVo;
import com.niu.shop.service.core.marketing.vo.ShopManjianListVo;
import org.springframework.transaction.annotation.Transactional;

/**
 * 满减活动服务接口
 */
public interface IShopManjianService {

    /**
     * 满减活动列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopManjianListVo>
     */
    PageResult<ShopManjianListVo> page(PageParam pageParam, ShopManjianSearchParam searchParam);

    /**
     * 满减活动详情
     *
     * @param id 主键ID
     * @return ShopManjianInfoVo
     */
    ShopManjianInfoVo info(Integer id);

    PageResult<ShopManjianMemberVo> member(PageParam pageParam, Integer manjianId);

    /**
     * 满减活动添加
     *
     * @param addParam 添加参数
     */
    void add(ShopManjianParam addParam);

    ShopManjianInitVo getInit(ShopManjianInitParam param);

    /**
     * 满减活动编辑
     *
     * @param id        主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, ShopManjianParam editParam);

    @Transactional
    void closeManjian(Integer manjianId);

    /**
     * 满减活动删除
     *
     * @param id 主键ID
     */
    void del(Integer id);

    void checkGoods(ShopManjianCheckGoodsParam param);

    void batchClose(ShopManjianBatchParam batchParam);

    void batchDel(ShopManjianBatchParam batchParam);

}
