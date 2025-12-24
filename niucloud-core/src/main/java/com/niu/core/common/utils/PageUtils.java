package com.niu.core.common.utils;

import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;

import java.util.List;

public class PageUtils {

    /**
     * 手动分页
     *
     * @param data
     * @param limit
     * @param page
     * @param <T>
     * @return
     */
    public static <T> PageResult<T> build(int page, int limit, List<T> data) {
        // 检查页码和每页大小是否合理
        if (page < 1 || limit < 1) {
            throw new CommonException("分页参数异常");
        }

        // 计算开始索引
        int startIndex = (page - 1) * limit;

        // 如果开始索引已经超出数据集合的总长度，说明请求的页码过大，返回空集合
        if (startIndex >= data.size()) {
            return PageResult.build(page, limit, 0, List.of());
        }

        // 计算结束索引
        int endIndex = Math.min(startIndex + limit, data.size());

        // 返回分页后的数据集合
        return PageResult.build(page, limit, data.size(), data.subList(startIndex, endIndex));
    }
}