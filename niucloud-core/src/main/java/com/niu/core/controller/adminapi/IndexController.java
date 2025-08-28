package com.niu.core.controller.adminapi;

import com.niu.core.common.domain.Result;
import com.niu.core.enums.pay.OrderStatusEnum;
import jdk.jfr.Description;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Description("首页")
@RestController
@RequestMapping("index")
public class IndexController {


    @Description("加载测试")
    @GetMapping("/load")
    public Result<?> testLoad() {
        return Result.success();
    }

    @Description("支付测试")
    @GetMapping("/test_pay")
    public Result<?> testPay(String key) {
        return Result.success();
    }

    @Description("枚举测试")
    @GetMapping("/test_enum")
    public Result<Map<Integer, String>> testEnum() {
        OrderStatusEnum[] orderStatusEnum = OrderStatusEnum.values();
        Map<Integer, String> map = new HashMap<>();
        for (OrderStatusEnum enums : orderStatusEnum) {
            map.put(enums.getStatus(), enums.getName());
        }
        return Result.success(map);
    }

    @Description("通用测试")
    @GetMapping("/test")
    public Result test() {
        System.out.println("Math.ceil()+++++");
        System.out.println(Math.round((double) 1 / 9 * 100));

        return Result.success();
    }
}
