package com.niu.shop.service.admin.delivery.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.shop.entity.delivery.ShopDeliveryShippingTemplate;
import com.niu.shop.entity.delivery.ShopDeliveryShippingTemplateItem;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.mapper.delivery.ShopDeliveryShippingTemplateItemMapper;
import com.niu.shop.mapper.delivery.ShopDeliveryShippingTemplateMapper;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryShippingTemplateParam;
import com.niu.shop.service.admin.delivery.param.ShopDeliveryShippingTemplateSearchParam;
import com.niu.shop.service.admin.delivery.IShopDeliveryShippingTemplateService;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryShippingTemplateInfoVo;
import com.niu.shop.service.admin.delivery.vo.ShopDeliveryShippingTemplateListVo;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 运费模板实现
 */
@Service
public class ShopDeliveryShippingTemplateServiceImpl extends ServiceImpl<ShopDeliveryShippingTemplateItemMapper, ShopDeliveryShippingTemplateItem> implements IShopDeliveryShippingTemplateService {
        
    @Resource
    ShopDeliveryShippingTemplateMapper shopDeliveryShippingTemplateMapper;

    @Resource
    ShopDeliveryShippingTemplateItemMapper shopDeliveryShippingTemplateItemMapper;

    @Resource
    ShopGoodsMapper shopGoodsMapper;

    /**
     * 运费模板分页列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopDeliveryShippingTemplateListVo>
     */
    @Override
    public PageResult<ShopDeliveryShippingTemplateListVo> page(PageParam pageParam, ShopDeliveryShippingTemplateSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<ShopDeliveryShippingTemplate> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(searchParam.getTemplateName())) queryWrapper.like("template_name", searchParam.getTemplateName());
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.orderByDesc("template_id");

        IPage<ShopDeliveryShippingTemplate> iPage = shopDeliveryShippingTemplateMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<ShopDeliveryShippingTemplateListVo> list = new LinkedList<>();
        for (ShopDeliveryShippingTemplate item : iPage.getRecords()) {
            ShopDeliveryShippingTemplateListVo vo = new ShopDeliveryShippingTemplateListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 运费模板列表
     * @param searchParam 搜索参数
     * @return PageResult<ShopDeliveryShippingTemplateListVo>
     */
    @Override
    public List<ShopDeliveryShippingTemplateListVo> list(ShopDeliveryShippingTemplateSearchParam searchParam) {
        QueryWrapper<ShopDeliveryShippingTemplate> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(searchParam.getTemplateName())) queryWrapper.like("template_name", searchParam.getTemplateName());
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.orderByDesc("template_id");

        List<ShopDeliveryShippingTemplate> record = shopDeliveryShippingTemplateMapper.selectList(queryWrapper);
        List<ShopDeliveryShippingTemplateListVo> list = new LinkedList<>();
        for (ShopDeliveryShippingTemplate item : record) {
            ShopDeliveryShippingTemplateListVo vo = new ShopDeliveryShippingTemplateListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }

    /**
     * 运费模板详情
     * @param id 主键
     * @return ShopDeliveryShippingTemplateInfoVo
     */
    @Override
    public ShopDeliveryShippingTemplateInfoVo info(Integer id) {
        ShopDeliveryShippingTemplate model = shopDeliveryShippingTemplateMapper.selectOne(
                new QueryWrapper<ShopDeliveryShippingTemplate>()
                    .eq("template_id", id)
                    .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "运费模板不存在");

        ShopDeliveryShippingTemplateInfoVo vo = new ShopDeliveryShippingTemplateInfoVo();
        BeanUtils.copyProperties(model, vo);

        vo.setFeeData(CollectUtils.convert(shopDeliveryShippingTemplateItemMapper.selectList(new QueryWrapper<ShopDeliveryShippingTemplateItem>()
                .eq("template_id", model.getTemplateId()).ne("fee_area_ids", "").groupBy("fee_area_ids")
            ), ShopDeliveryShippingTemplateInfoVo.FeeData.class));
        vo.setNoDeliveryData(CollectUtils.convert(shopDeliveryShippingTemplateItemMapper.selectList(new QueryWrapper<ShopDeliveryShippingTemplateItem>()
                .eq("template_id", model.getTemplateId()).ne("no_delivery_area_ids", "").groupBy("no_delivery_area_ids")
            ), ShopDeliveryShippingTemplateInfoVo.NoDeliveryData.class));
        vo.setFreeShippingData(CollectUtils.convert(shopDeliveryShippingTemplateItemMapper.selectList(new QueryWrapper<ShopDeliveryShippingTemplateItem>()
                .eq("template_id", model.getTemplateId()).ne("free_shipping_area_ids", "").groupBy("free_shipping_area_ids")
            ), ShopDeliveryShippingTemplateInfoVo.FreeShippingData.class));

        return vo;
    }

    /**
     * 运费模板添加
     * @param addParam 参数
     */
    @Override
    @Transactional
    public void add(ShopDeliveryShippingTemplateParam addParam) {
        ShopDeliveryShippingTemplate model = new ShopDeliveryShippingTemplate();
        BeanUtils.copyProperties(addParam, model);
        model.setSiteId(RequestUtils.siteId());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        shopDeliveryShippingTemplateMapper.insert(model);

        List<ShopDeliveryShippingTemplateItem> list = new LinkedList<>();
        for (int i = 0; i < addParam.getArea().size(); i++) {
            JSONObject data = addParam.getArea().getJSONObject(i);
            ShopDeliveryShippingTemplateItem item = JSONUtil.toBean(data, ShopDeliveryShippingTemplateItem.class);
            item.setTemplateId(model.getTemplateId());
            item.setSiteId(model.getSiteId());
            item.setFeeType(model.getFeeType());
            item.setFeeAreaIds(data.getStr("fee_area_ids", ""));
            item.setFeeAreaNames(data.getStr("fee_area_names", ""));
            item.setNoDeliveryAreaIds(data.getStr("no_delivery_area_ids", ""));
            item.setNoDeliveryAreaNames(data.getStr("no_delivery_area_names", ""));
            item.setFreeShippingAreaIds(data.getStr("free_shipping_area_ids", ""));
            item.setFreeShippingAreaNames(data.getStr("free_shipping_area_names", ""));
            list.add(item);
        }
        super.saveBatch(list);
    }

    /**
     * 运费模板编辑
     *
     * @param editParam 参数
     */
    @Override
    @Transactional
    public void edit(Integer id, ShopDeliveryShippingTemplateParam editParam) {
        ShopDeliveryShippingTemplate model = shopDeliveryShippingTemplateMapper.selectOne(
                new QueryWrapper<ShopDeliveryShippingTemplate>()
                        .eq("template_id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "运费模板不存在");
        BeanUtils.copyProperties(editParam, model);
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        shopDeliveryShippingTemplateMapper.updateById(model);

        shopDeliveryShippingTemplateItemMapper.delete(new QueryWrapper<ShopDeliveryShippingTemplateItem>().eq("template_id", id));

        List<ShopDeliveryShippingTemplateItem> list = new LinkedList<>();
        for (int i = 0; i < editParam.getArea().size(); i++) {
            JSONObject data = editParam.getArea().getJSONObject(i);
            ShopDeliveryShippingTemplateItem item = JSONUtil.toBean(data, ShopDeliveryShippingTemplateItem.class);
            item.setTemplateId(model.getTemplateId());
            item.setSiteId(model.getSiteId());
            item.setFeeType(model.getFeeType());
            item.setFeeAreaIds(data.getStr("fee_area_ids", ""));
            item.setFeeAreaNames(data.getStr("fee_area_names", ""));
            item.setNoDeliveryAreaIds(data.getStr("no_delivery_area_ids", ""));
            item.setNoDeliveryAreaNames(data.getStr("no_delivery_area_names", ""));
            item.setFreeShippingAreaIds(data.getStr("free_shipping_area_ids", ""));
            item.setFreeShippingAreaNames(data.getStr("free_shipping_area_names", ""));
            list.add(item);
        }
        super.saveBatch(list);
    }

    /**
     * 运费模板删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        Long count = shopGoodsMapper.selectCount(new QueryWrapper<ShopGoods>().eq("delivery_template_id", id));
        if (count > 0) throw new CommonException("运费模板有商品正在使用中不能删除");

        ShopDeliveryShippingTemplate model = shopDeliveryShippingTemplateMapper.selectOne(
                new QueryWrapper<ShopDeliveryShippingTemplate>()
                        .eq("template_id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "运费模板数据不存在");

        shopDeliveryShippingTemplateMapper.delete(new QueryWrapper<ShopDeliveryShippingTemplate>().eq("template_id", id));
        shopDeliveryShippingTemplateItemMapper.delete(new QueryWrapper<ShopDeliveryShippingTemplateItem>().eq("template_id", id));
    }

}
