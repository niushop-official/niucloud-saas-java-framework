package com.niu.shop.service.core.goods.param;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class InitConfigParam {
    @JsonProperty("sort_type")
    private Map<String, String> sortType;

    @JsonProperty("sort_column")
    private Map<String, String> sortColumn;

    // Getters and Setters
    public Map<String, String> getSortType() {
        return sortType;
    }

    public void setSortType(Map<String, String> sortType) {
        this.sortType = sortType;
    }

    public Map<String, String> getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(Map<String, String> sortColumn) {
        this.sortColumn = sortColumn;
    }
}
