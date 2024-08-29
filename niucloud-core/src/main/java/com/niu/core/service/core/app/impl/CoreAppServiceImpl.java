package com.niu.core.service.core.app.impl;

import com.niu.core.service.core.app.ICoreAppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Connection;

@Service
@Slf4j
public class CoreAppServiceImpl implements ICoreAppService {

    @Override
    public void initAppBasic(Connection connection) {
        log.info("initAppBasic() begin");
        // 1、初始化系统数据库schema

        // 2、初始化系统菜单

        // 3、初始化系统默认用户和角色
        log.info("initAppBasic() ended");
    }

}
