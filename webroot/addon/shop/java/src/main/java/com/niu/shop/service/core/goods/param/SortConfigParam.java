package com.niu.shop.service.core.goods.param;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SortConfigParam {
    @JsonProperty("sort_type")
    private String sortType;

    @JsonProperty("default_sort")
    private int defaultSort;

    @JsonProperty("init")
    private InitConfigParam init;

    @JsonProperty("sort_column")
    private String sortColumn;

    // Getters and Setters
    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }

    public int getDefaultSort() {
        return defaultSort;
    }

    public void setDefaultSort(int defaultSort) {
        this.defaultSort = defaultSort;
    }

    public InitConfigParam getInit() {
        return init;
    }

    public void setInit(InitConfigParam init) {
        this.init = init;
    }

    public String getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }
}
