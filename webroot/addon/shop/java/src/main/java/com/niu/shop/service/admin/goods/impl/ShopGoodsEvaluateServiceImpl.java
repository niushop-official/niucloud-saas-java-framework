package com.niu.shop.service.admin.goods.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.shop.entity.goods.ShopGoodsEvaluate;
import com.niu.shop.enums.goods.GoodsEvaluateEnum;
import com.niu.shop.mapper.goods.ShopGoodsEvaluateMapper;
import com.niu.shop.service.admin.goods.IShopGoodsEvaluateService;
import com.niu.shop.service.admin.goods.param.ShopGoodsEvaluateParam;
import com.niu.shop.service.admin.goods.param.ShopGoodsEvaluateSearchParam;
import com.niu.shop.service.admin.goods.vo.ShopGoodsEvaluateInfoVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsEvaluateListVo;
import com.niu.shop.service.core.goods.ICoreGoodsStatService;
import com.niu.shop.service.core.goods.param.GoodsAddStatParam;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


/**
 * 商品评价实现
 */
@Service
public class ShopGoodsEvaluateServiceImpl implements IShopGoodsEvaluateService {

    @Resource
    ICoreGoodsStatService coreGoodsStatService;

    @Resource
    ShopGoodsEvaluateMapper shopGoodsEvaluateMapper;

    private ICoreUploadService coreUploadService() {
        return SpringContext.bean(ICoreUploadService.class);
    }

    /**
     * 商品评价列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsEvaluateListVo>
     */
    @Override
    public PageResult<ShopGoodsEvaluateListVo> list(PageParam pageParam, ShopGoodsEvaluateSearchParam searchParam) {

        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<ShopGoodsEvaluate> queryWrapper = new MPJQueryWrapper<>();

        queryWrapper.setAlias("ge")
                .leftJoin("?_shop_goods ng ON ge.goods_id = ng.goods_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("ge.evaluate_id,ge.site_id,ge.order_id,ge.order_goods_id,ge.goods_id,ge.member_id,ge.content,ge.images,ge.is_anonymous,ge.scores,ge.is_audit,ge.explain_first,ge.create_time,ge.topping,ng.goods_name, ng.goods_cover");
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsName()))
            queryWrapper.like("ng.goods_name", searchParam.getGoodsName());
        queryWrapper.eq("ge.site_id", RequestUtils.siteId());
        queryWrapper.orderByDesc("ge.create_time");

        IPage<ShopGoodsEvaluateListVo> iPage = shopGoodsEvaluateMapper.selectJoinPage(new Page<>(page, limit), ShopGoodsEvaluateListVo.class, queryWrapper);

        for (ShopGoodsEvaluateListVo item : iPage.getRecords()) {
            item.setImages(JSONUtil.parseArray(item.getImages()).stream().toArray(String[]::new));
            if (ObjectUtil.isNotEmpty(item.getImages())) {
                List<String> listImageSmall = new ArrayList<>();
                List<String> listImageBig = new ArrayList<>();
                for (String image : (String[]) item.getImages()) {
                    listImageSmall.add(coreUploadService().thumb(item.getSiteId(), image, UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType()));
                    listImageBig.add(coreUploadService().thumb(item.getSiteId(), image, UploadThumbTypeEnum.BIG.getType()).getDataMap().get(UploadThumbTypeEnum.BIG.getType()));
                }
                item.setImageSmall(listImageSmall);
                item.setImageBig(listImageBig);
            }

            if (ObjectUtil.isNotEmpty(item.getGoodsCover())) {
                item.setGoodsCoverThumbSmall(coreUploadService().thumb(item.getSiteId(), item.getGoodsCover(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType()));
                item.setGoodsCoverThumbMid(coreUploadService().thumb(item.getSiteId(), item.getGoodsCover(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType()));
            }

            ShopGoodsEvaluateListVo.Goods goods = new ShopGoodsEvaluateListVo.Goods();
            BeanUtil.copyProperties(item, goods);
            item.setGoods(goods);
        }
        return PageResult.build(iPage);
    }


    /**
     * 商品评价详情
     *
     * @param id 主键
     * @return ShopGoodsEvaluateInfoVo
     */
    @Override
    public ShopGoodsEvaluateInfoVo info(Integer id) {

        ShopGoodsEvaluate model = shopGoodsEvaluateMapper.selectOne(
                new QueryWrapper<ShopGoodsEvaluate>()
                        .eq("evaluate_id", id)
                        .eq("site_id", RequestUtils.siteId())
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        ShopGoodsEvaluateInfoVo vo = new ShopGoodsEvaluateInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 商品评价添加
     *
     * @param addParam 参数
     */
    @Override
    public void add(ShopGoodsEvaluateParam addParam) {

        ShopGoodsEvaluate model = new ShopGoodsEvaluate();

        model.setSiteId(RequestUtils.siteId());
        model.setGoodsId(addParam.getGoodsId());
        model.setMemberHead(addParam.getMemberHead());
        model.setMemberName(addParam.getMemberName());
        model.setContent(addParam.getContent());
        model.setImages(JSONUtil.parseArray(addParam.getImages()).toString());
        model.setIsAnonymous(addParam.getIsAnonymous());
        model.setScores(addParam.getScores());
        model.setIsAudit(GoodsEvaluateEnum.AUDIT_NO.getStatus());
        model.setCreateTime(DateUtils.StringToTimestamp(addParam.getCreateTime()));
        shopGoodsEvaluateMapper.insert(model);
    }

    /**
     * 商品评价编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, ShopGoodsEvaluateParam editParam) {

        ShopGoodsEvaluate model = shopGoodsEvaluateMapper.selectOne(
                new QueryWrapper<ShopGoodsEvaluate>()
                        .eq("evaluate_id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        model.setEvaluateId(id);
        shopGoodsEvaluateMapper.updateById(model);
    }

    /**
     * 商品评价删除
     *
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        ShopGoodsEvaluate model = shopGoodsEvaluateMapper.selectOne(
                new QueryWrapper<ShopGoodsEvaluate>()
                        .eq("evaluate_id", id)
                        .eq("site_id", RequestUtils.siteId())
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        shopGoodsEvaluateMapper.delete(new QueryWrapper<ShopGoodsEvaluate>().eq("evaluate_id", id));

        GoodsAddStatParam param = new GoodsAddStatParam();
        param.setSiteId(model.getSiteId());
        param.setGoodsId(model.getGoodsId());
        param.setEvaluateNum(-1);
        coreGoodsStatService.addStat(param);
    }

    @Override
    public void auditAdopt(Integer id) {
        ShopGoodsEvaluate shopGoodsEvaluate = new ShopGoodsEvaluate();
        shopGoodsEvaluate.setIsAudit(GoodsEvaluateEnum.AUDIT_ADOPT.getStatus());
        shopGoodsEvaluateMapper.update(shopGoodsEvaluate,
                new QueryWrapper<ShopGoodsEvaluate>()
                        .eq("evaluate_id", id)
                        .eq("site_id", RequestUtils.siteId()));

        ShopGoodsEvaluate record = shopGoodsEvaluateMapper.selectById(id);

        GoodsAddStatParam param = new GoodsAddStatParam();
        param.setSiteId(record.getSiteId());
        param.setGoodsId(record.getGoodsId());
        param.setEvaluateNum(1);
        coreGoodsStatService.addStat(param);
    }

    @Override
    public void auditRefuse(Integer id) {

        ShopGoodsEvaluate shopGoodsEvaluate = new ShopGoodsEvaluate();
        shopGoodsEvaluate.setIsAudit(GoodsEvaluateEnum.AUDIT_REFUSE.getStatus());
        shopGoodsEvaluateMapper.update(shopGoodsEvaluate,
                new QueryWrapper<ShopGoodsEvaluate>()
                        .eq("evaluate_id", id)
                        .eq("site_id", RequestUtils.siteId()));
    }

    @Override
    public void reply(Integer id, String explainFirst) {

        ShopGoodsEvaluate shopGoodsEvaluate = new ShopGoodsEvaluate();
        shopGoodsEvaluate.setExplainFirst(explainFirst);
        shopGoodsEvaluateMapper.update(shopGoodsEvaluate,
                new QueryWrapper<ShopGoodsEvaluate>()
                        .eq("evaluate_id", id)
                        .eq("site_id", RequestUtils.siteId()));
    }

    @Override
    public void topping(Integer id) {

        ShopGoodsEvaluate shopGoodsEvaluate = new ShopGoodsEvaluate();
        shopGoodsEvaluate.setTopping(1);
        shopGoodsEvaluate.setUpdateTime(System.currentTimeMillis() / 1000);
        shopGoodsEvaluateMapper.update(shopGoodsEvaluate,
                new QueryWrapper<ShopGoodsEvaluate>()
                        .eq("evaluate_id", id)
                        .eq("site_id", RequestUtils.siteId()));
    }

    @Override
    public void cancelTopping(Integer id) {

        ShopGoodsEvaluate shopGoodsEvaluate = new ShopGoodsEvaluate();
        shopGoodsEvaluate.setTopping(0);
        shopGoodsEvaluate.setUpdateTime(System.currentTimeMillis() / 1000);
        shopGoodsEvaluateMapper.update(shopGoodsEvaluate,
                new QueryWrapper<ShopGoodsEvaluate>()
                        .eq("evaluate_id", id)
                        .eq("site_id", RequestUtils.siteId()));
    }

}
