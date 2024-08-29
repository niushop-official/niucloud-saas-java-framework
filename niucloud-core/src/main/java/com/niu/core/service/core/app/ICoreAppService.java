package com.niu.core.service.core.app;

import java.sql.Connection;

public interface ICoreAppService {

    /**
     * 初始化系统
     *
     * @param connection
     */
    void initAppBasic(Connection connection);

}
