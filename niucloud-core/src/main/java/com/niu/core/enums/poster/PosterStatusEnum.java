package com.niu.core.enums.poster;

import lombok.Getter;

@Getter
public enum PosterStatusEnum {
    ON("1"),
    OFF("2");

    private final String status;

    PosterStatusEnum(String status) {
        this.status = status;
    }
}
