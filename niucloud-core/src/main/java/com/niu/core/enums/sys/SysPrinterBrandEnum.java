package com.niu.core.enums.sys;

import lombok.Getter;

@Getter
public enum SysPrinterBrandEnum {
    NONE("", ""),
    YI_LIAN_YUN("yilianyun", "易联云打印机");

    private final String brand;
    private final String name;

    SysPrinterBrandEnum(String brand, String name) {
        this.brand = brand;
        this.name = name;
    }

    public static String getNameByBrand(String brand) {
        for (SysPrinterBrandEnum value : SysPrinterBrandEnum.values()) {
            if (value.getBrand().equals(brand)) {
                return value.getName();
            }
        }
        return "";
    }

    public static SysPrinterBrandEnum getEnumByBrand(String brand) {
        for (SysPrinterBrandEnum value : SysPrinterBrandEnum.values()) {
            if (value.getBrand().equals(brand)) {
                return value;
            }
        }
        return NONE;
    }
}
