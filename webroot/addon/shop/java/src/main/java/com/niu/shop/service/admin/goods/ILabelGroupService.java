package com.niu.shop.service.admin.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.shop.service.admin.goods.param.LabelGroupModifySortParam;
import com.niu.shop.service.admin.goods.param.LabelGroupParam;
import com.niu.shop.service.admin.goods.param.LabelGroupSearchParam;
import com.niu.shop.service.admin.goods.vo.LabelGroupInfoVo;
import com.niu.shop.service.admin.goods.vo.LabelGroupListVo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 商品标签分组服务接口
 */
public interface ILabelGroupService {

    /**
     * 商品标签分组列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<LabelGroupListVo>
     */
    PageResult<LabelGroupListVo> page(PageParam pageParam, LabelGroupSearchParam searchParam);

    /**
     * 商品标签分组详情
     * @param id 主键ID
     * @return  LabelGroupInfoVo
     */
    LabelGroupInfoVo info(Integer id);

    /**
     * 商品标签分组添加
     * @param addParam 添加参数
     */
    void add(LabelGroupParam addParam);

    /**
     * 商品标签分组编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, LabelGroupParam editParam);

    /**
     * 商品标签分组删除
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 标签分组列表
     * @param searchParam
     * @return
     */
    List<LabelGroupListVo> list(LabelGroupSearchParam searchParam);

    /**
     * 修改排序
     * @param param
     */
    void modifySort(LabelGroupModifySortParam param);
}
