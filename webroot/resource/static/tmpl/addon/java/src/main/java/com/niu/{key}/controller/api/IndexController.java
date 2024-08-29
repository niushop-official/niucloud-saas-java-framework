package com.niu.{key}.controller.api;

import com.niu.core.common.domain.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/{key}")
public class IndexController {

    @GetMapping("/hello_world")
    public Result<?> index() {
        return Result.success("hello world");
    }
}
