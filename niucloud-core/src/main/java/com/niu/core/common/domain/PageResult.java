package com.niu.core.common.domain;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class PageResult<E> {

    @Schema(required = true, description = "当前请求页")
    // 当前请求页
    private long currentPage;

    @Schema(required = true, description = "每页大小")
    // 每页大小
    private long perPage;

    @Schema(required = true, description = "总记录数")
    // 总记录数
    private long total;

    @Schema(required = true, description = "记录结果")
    // 记录结果
    private List<E> data;

    public PageResult() {

    }

    /**
     * @param page 页码
     * @param limit 数量
     */
    public PageResult(long page, long limit) {
        this.currentPage = page;
        this.perPage = limit;
    }

    /**
     * @param page 页码
     * @param limit 数量
     * @param total 总数
     */
    public PageResult(long page, long limit, long total) {
        this.currentPage = page;
        this.perPage = limit;
        this.total = total;
    }

    /**
     * @param page 页码
     * @param limit 数量
     * @param total 总数
     * @param list 数据
     */
    public PageResult(long page, long limit, long total, List<E> list) {
        this.currentPage = page;
        this.perPage = limit;
        this.total = total;
        this.data = list;
    }

    /**
     * @param page 页码
     * @param limit 数量
     */
    public static <E> PageResult<E> build(long page, long limit) {
        return new PageResult<>(page, limit);
    }

    /**
     * @param page 页码
     * @param limit 数量
     * @param total 总数
     */
    public static  PageResult  build(long page, long limit, long total) {
        return new PageResult(page, limit, total);
    }

    /**
     * @param page 页码
     * @param limit 数量
     * @param total 总数
     * @param list 数据
     */
    public static <T> PageResult<T> build(long page, long limit, long total, List<T> list) {
        return new PageResult<>(page, limit, total, list);
    }


    /**
     * 将Mybatis-plus分页插件（com.baomidou.mybatisplus.extension.plugins.pagination.Page）转换
     *
     * @param page 页码
     * @param limit 数量
     * @param pageObj 总数
     */
    public static <E> PageResult<E> build(long page, long limit, Page<E> pageObj) {
        PageResult<E> pageList = PageResult.build(page, limit);
        pageList.setTotal(pageObj.getTotal());
        pageList.setData(pageObj.getRecords());
        return pageList;
    }

    /**
     * 将Mybatis分页插件（com.github.pagehelper）转换
     *
     * @param page 页码
     * @param limit 数量
     * @param pageInfo 分页类
     */
    public static <E> PageResult<E> build(int page, int limit, PageInfo<E> pageInfo) {
        PageResult<E> pageList = PageResult.build(page, limit);
        if (pageInfo != null) {
            pageList.setTotal(pageInfo.getTotal());
            pageList.setData(pageInfo.getList());
        }
        return pageList;
    }

    /**
     * Mybatis-plus分页插件（com.baomidou.mybatisplus.extension.plugins.pagination.Page）转换
     *
     * @param page 分页类
     */
    public static <E> PageResult<E> build(Page<E> page) {
        PageResult<E> pageList = PageResult.build(page.getCurrent(), page.getSize());
        if (ObjectUtil.isNotEmpty(page)) {
            pageList = PageResult.build(page.getCurrent(), page.getSize());
            pageList.setTotal(page.getTotal());
            pageList.setData(page.getRecords());
        }
        return pageList;
    }

    /**
     * Mybatis-plus分页插件（com.baomidou.mybatisplus.core.metadata.IPage）转换
     *
     * @param page 分页类
     */
    public static <E> PageResult<E> build(IPage<E> page) {
        PageResult<E> pageList = null;
        if (page != null) {
            pageList = PageResult.build(page.getCurrent(), page.getSize());
            pageList.setTotal(page.getTotal());
            pageList.setData(page.getRecords());
        }
        return pageList;
    }

    public static <E> PageResult<E> build(IPage<?> page, List<E> data) {
        PageResult<E> pageList = null;
        if (page != null) {
            pageList = PageResult.build(page.getCurrent(), page.getSize());
            pageList.setTotal(page.getTotal());
            pageList.setData(data);
        }
        return pageList;
    }

    /**
     * PageHelper分页转换
     *
     * @param pageInfo 分页类
     */
    public static <E> PageResult<E> build(PageInfo<E> pageInfo) {
        PageResult<E> pageList = PageResult.build(pageInfo.getPageNum(), pageInfo.getPageSize());
        if (ObjectUtil.isNotEmpty(pageInfo)) {
            pageList.setTotal(pageInfo.getTotal());
            pageList.setData(pageInfo.getList());
        }
        return pageList;
    }

}
