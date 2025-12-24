package com.niu.shop.service.core.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.mapper.goods.ShopGoodsSkuMapper;
import com.niu.shop.service.admin.goods.param.ShopGoodsVerifySkuNoParam;
import com.niu.shop.service.core.goods.param.SortConfigParam;
import com.niu.shop.service.core.goods.vo.InitConfigVo;
import com.niu.shop.service.core.goods.vo.ShopConfigUniqueVo;
import com.niu.shop.service.core.goods.vo.ShopConfigVo;
import com.niu.shop.service.core.goods.vo.SortConfigVo;
import com.niu.shop.service.core.goods.ICoreGoodsConfigService;
import com.niu.shop.service.core.goods.param.ShopConfigParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品配置服务类
 */
@Service
public class CoreGoodsConfigServiceImpl implements ICoreGoodsConfigService {

    @Resource
    ICoreConfigService configService;

    @Resource
    ShopGoodsSkuMapper shopGoodsSkuMapper;

    /**
     * 获取商品搜索配置
     */
    @Override
    public JSONObject getSearchConfig() {
        //获取站点
        Integer siteId = RequestUtils.siteId();

        //查询商品搜索配置
        CoreSysConfigVo coreSysConfigVo = configService.getConfig(siteId,"GOODS_SEARCH_CONFIG");
        if(ObjectUtil.isNotEmpty(coreSysConfigVo.getValue()) && ObjectUtil.isNotNull(coreSysConfigVo.getValue())){
            return coreSysConfigVo.getValueJson();
        }else{
            Map<String, Object> result = new HashMap<>();
            result.put("default_word", "");

            List<String> searchWords = new ArrayList<>();
            result.put("search_words", searchWords);

            // 将Map转换为JSONObject
            JSONObject jsonObject = new JSONObject(result);
            return jsonObject;
        }
    }

    /**
     * 设置商品搜索配置
     * @param shopConfigParam
     */
    @Override
    public void setSearchConfig(ShopConfigParam shopConfigParam) {
        //获取站点
        Integer siteId = RequestUtils.siteId();

        //获取数据
        String searchWords = shopConfigParam.getSearchWords();
        String[] searchWordslist = null;
        if(ObjectUtil.isNotNull(shopConfigParam.getSearchWords()) && ObjectUtil.isNotEmpty(shopConfigParam.getSearchWords())){
            searchWordslist = searchWords.split(",");
        }else{
            searchWordslist = new String[0];
        }

        //保存数据
        ShopConfigVo shopConfigVo = new ShopConfigVo();
        shopConfigVo.setDefault_word(shopConfigParam.getDefaultWord());
        shopConfigVo.setSearch_words(searchWordslist);
        JSONObject jsonObject = new JSONObject(shopConfigVo);
        configService.setConfig(siteId,"GOODS_SEARCH_CONFIG",jsonObject);
    }

    /**
     * 获取商品编码唯一值配置
     */
    @Override
    public JSONObject getUniqueConfig() {
        //获取站点
        Integer siteId = RequestUtils.siteId();

        //查询商品编码唯一值配置并返回数据
        CoreSysConfigVo coreSysConfigVo = configService.getConfig(siteId,"GOODS_UNIQUE_CONFIG");
        if(ObjectUtil.isNotEmpty(coreSysConfigVo.getValue()) && ObjectUtil.isNotNull(coreSysConfigVo.getValue())){
            return coreSysConfigVo.getValueJson();
        }else{
            Map<String, Object> result = new HashMap<>();
            result.put("is_enable", 0);

            // 将Map转换为JSONObject
            JSONObject jsonObject = new JSONObject(result);
            return jsonObject;
        }
    }

    /**
     * 设置商品编码唯一值配置
     * @param shopConfigParam
     */
    @Override
    public void setUniqueConfig(ShopConfigParam shopConfigParam) {
        //获取站点
        Integer siteId = RequestUtils.siteId();

        //保存数据
        ShopConfigUniqueVo shopConfigVo = new ShopConfigUniqueVo();
        shopConfigVo.setIs_enable(shopConfigParam.getIsEnable());
        JSONObject jsonObject = new JSONObject(shopConfigVo);
        configService.setConfig(siteId,"GOODS_UNIQUE_CONFIG",jsonObject);
    }

    /**
     * 获取商品排序配置
     */
    @Override
    public SortConfigVo getSortConfig() {
        //获取站点
        Integer siteId = RequestUtils.siteId();

        //查询商品排序配置并返回数据
        CoreSysConfigVo coreSysConfigVo = configService.getConfig(siteId,"GOODS_SORT_CONFIG");
        if(ObjectUtil.isNotEmpty(coreSysConfigVo.getValue()) && ObjectUtil.isNotNull(coreSysConfigVo.getValue())){
            SortConfigVo sortConfigVo = JSONUtil.toBean(coreSysConfigVo.getValueJson(), SortConfigVo.class);
            return sortConfigVo;
        }else{
            SortConfigVo sortConfigVo = new SortConfigVo();
            sortConfigVo.setSortType("desc");
            sortConfigVo.setSortColumn("create_time");
            sortConfigVo.setDefaultSort(0);
            InitConfigVo initConfigVo = new InitConfigVo();
            Map<String, String> sortType = new HashMap<>();
            sortType.put("asc","正序排序");
            sortType.put("desc","倒序排序");
            initConfigVo.setSortType(sortType);
            Map<String, String> sortColumn = new HashMap<>();
            sortColumn.put("sort","排序号");
            sortColumn.put("sale_price","价格");
            sortColumn.put("sale_num","销量");
            sortColumn.put("create_time","创建时间");
            initConfigVo.setSortColumn(sortColumn);
            sortConfigVo.setInit(initConfigVo);

            return sortConfigVo;
        }
    }

    /**
     * 设置商品排序配置
     * @param sortConfigParam
     */
    @Override
    public void setSortConfig(SortConfigParam sortConfigParam) {
        //获取站点
        Integer siteId = RequestUtils.siteId();

        //保存数据
        SortConfigVo sortConfigVo = new SortConfigVo();
        sortConfigVo.setDefaultSort(sortConfigParam.getDefaultSort());
        sortConfigVo.setSortColumn(sortConfigParam.getSortColumn());
        sortConfigVo.setSortType(sortConfigParam.getSortType());
        InitConfigVo initConfigVo = new InitConfigVo();
        initConfigVo.setSortColumn(sortConfigParam.getInit().getSortColumn());
        initConfigVo.setSortType(sortConfigParam.getInit().getSortType());
        sortConfigVo.setInit(initConfigVo);

        configService.setConfig(siteId,"GOODS_SORT_CONFIG", JSONUtil.parseObj(sortConfigVo));
    }

    @Override
    public Boolean verifySkuNo(ShopGoodsVerifySkuNoParam param) {
        // 获取商品编码唯一配置
        JSONObject sysConfigInfo = getUniqueConfig();
        
        // 如果未开启商品编码唯一验证，则直接返回通过
        if (ObjectUtil.isNotNull(sysConfigInfo) && sysConfigInfo.getInt("is_enable") == 0) {
            return true;
        }
        
        // 验证sku_no参数是否存在
        if (ObjectUtil.isEmpty(param.getSkuNo())) {
            throw new AdminException("缺少参数sku_no");
        }
        
        // 处理sku_no参数，支持多个编码（逗号分隔）
        String[] skuNoArr = param.getSkuNo().split(",");
        List<String> validSkuNoList = new ArrayList<>();
        
        // 过滤空值
        for (String skuNo : skuNoArr) {
            if (ObjectUtil.isNotEmpty(skuNo.trim())) {
                validSkuNoList.add(skuNo.trim());
            }
        }
        
        // 如果过滤后没有有效的商品编码，则直接返回通过
        if (validSkuNoList.isEmpty()) {
            return true;
        }
        
        // 检查参数中是否存在重复的商品编码
        if (validSkuNoList.size() > 1) {
            Map<String, Integer> countMap = new HashMap<>();
            for (String skuNo : validSkuNoList) {
                countMap.put(skuNo, countMap.getOrDefault(skuNo, 0) + 1);
            }
            
            // 查找重复的编码
            for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
                if (entry.getValue() > 1) {
                    throw new AdminException("商品编码[" + entry.getKey() + "]已存在");
                }
            }
        }
        
        // 构建数据库查询条件，使用FIND_IN_SET函数
        QueryWrapper<ShopGoodsSku> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        
        // 构建OR条件，使用FIND_IN_SET查询
        StringBuilder findInSetCondition = new StringBuilder();
        for (int i = 0; i < validSkuNoList.size(); i++) {
            if (i > 0) {
                findInSetCondition.append(" OR ");
            }
            findInSetCondition.append("FIND_IN_SET('").append(validSkuNoList.get(i)).append("', sku_no)");
        }
        queryWrapper.apply(findInSetCondition.toString());
        
        // 排除当前商品ID（如果存在）
        if (ObjectUtil.isNotNull(param.getGoodsId()) && ObjectUtil.isNotEmpty(param.getGoodsId())) {
            queryWrapper.ne("goods_id", param.getGoodsId());
        }
        
        // 查询数据库中是否存在相同的商品编码
        ShopGoodsSku existingSku = shopGoodsSkuMapper.selectOne(queryWrapper);
        if (ObjectUtil.isNotNull(existingSku) && ObjectUtil.isNotEmpty(existingSku.getSkuNo())) {
            // 找出冲突的商品编码
            String[] existingSkuNoArr = existingSku.getSkuNo().split(",");
            for (String skuNo : validSkuNoList) {
                for (String existingSkuNo : existingSkuNoArr) {
                    if (skuNo.equals(existingSkuNo.trim())) {
                        throw new AdminException("商品编码[" + skuNo + "]已存在");
                    }
                }
            }
        }
        
        return true;
    }

}
