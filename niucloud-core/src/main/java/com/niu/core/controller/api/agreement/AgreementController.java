package com.niu.core.controller.api.agreement;

import cn.dev33.satoken.annotation.SaIgnore;
import com.niu.core.common.domain.Result;
import com.niu.core.service.api.agreement.IAgreementService;
import com.niu.core.service.api.agreement.param.AgreementInfoParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/api/agreement")
@Slf4j
public class AgreementController {

    @Resource
    private IAgreementService agreementService;

    /**
     * 协议
     *
     * @param key
     * @return
     */
    @SaIgnore
    @GetMapping("/{key}")
    public Result<?> info(@PathVariable("key") String key) {
        AgreementInfoParam param = new AgreementInfoParam();
        param.setKey(key);
        return Result.success(agreementService.info(param));
    }

}
