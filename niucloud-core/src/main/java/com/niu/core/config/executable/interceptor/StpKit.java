package com.niu.core.config.executable.interceptor;

import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;

public class StpKit {
    /**
     * 默认原生会话对象
     */
    public static final StpLogic DEFAULT = StpUtil.stpLogic;

    /**
     * Admin 会话对象，管理 Admin 表所有账号的登录、权限认证
     */
    public static final StpLogic admin = new StpLogic("admin");

/*    *//**
     * site会话对象，管理站点会员以及权限对象
     *//*
    public static final StpLogic site = new StpLogic("site");*/

    /**
     * member 会话对象，管理会员登录
     */
    public static final StpLogic member = new StpLogic("member");
}
