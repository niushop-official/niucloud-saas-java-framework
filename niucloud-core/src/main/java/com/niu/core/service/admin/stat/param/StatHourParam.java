package com.niu.core.service.admin.stat.param;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import javax.validation.constraints.*;

/**
 * 小时统计参数
*/
@Data
public class StatHourParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "addon参数缺失")
    private String addon;

    @NotNull(message = "field参数缺失")
    private String field;

    @NotNull(message = "fieldTotal参数缺失")
    private BigDecimal fieldTotal;

    @NotNull(message = "year参数缺失")
    private Integer year;

    @NotNull(message = "month参数缺失")
    private Integer month;

    @NotNull(message = "day参数缺失")
    private Integer day;

    @NotNull(message = "startTime参数缺失")
    private Long startTime;

    @NotNull(message = "lastTime参数缺失")
    private Integer lastTime;

    @NotNull(message = "hour0参数缺失")
    private BigDecimal hour0;

    @NotNull(message = "hour1参数缺失")
    private BigDecimal hour1;

    @NotNull(message = "hour2参数缺失")
    private BigDecimal hour2;

    @NotNull(message = "hour3参数缺失")
    private BigDecimal hour3;

    @NotNull(message = "hour4参数缺失")
    private BigDecimal hour4;

    @NotNull(message = "hour5参数缺失")
    private BigDecimal hour5;

    @NotNull(message = "hour6参数缺失")
    private BigDecimal hour6;

    @NotNull(message = "hour7参数缺失")
    private BigDecimal hour7;

    @NotNull(message = "hour8参数缺失")
    private BigDecimal hour8;

    @NotNull(message = "hour9参数缺失")
    private BigDecimal hour9;

    @NotNull(message = "hour10参数缺失")
    private BigDecimal hour10;

    @NotNull(message = "hour11参数缺失")
    private BigDecimal hour11;

    @NotNull(message = "hour12参数缺失")
    private BigDecimal hour12;

    @NotNull(message = "hour13参数缺失")
    private BigDecimal hour13;

    @NotNull(message = "hour14参数缺失")
    private BigDecimal hour14;

    @NotNull(message = "hour15参数缺失")
    private BigDecimal hour15;

    @NotNull(message = "hour16参数缺失")
    private BigDecimal hour16;

    @NotNull(message = "hour17参数缺失")
    private BigDecimal hour17;

    @NotNull(message = "hour18参数缺失")
    private BigDecimal hour18;

    @NotNull(message = "hour19参数缺失")
    private BigDecimal hour19;

    @NotNull(message = "hour20参数缺失")
    private BigDecimal hour20;

    @NotNull(message = "hour21参数缺失")
    private BigDecimal hour21;

    @NotNull(message = "hour22参数缺失")
    private BigDecimal hour22;

    @NotNull(message = "hour23参数缺失")
    private BigDecimal hour23;

}
