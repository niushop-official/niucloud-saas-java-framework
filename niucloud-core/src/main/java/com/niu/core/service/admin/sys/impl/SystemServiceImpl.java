package com.niu.core.service.admin.sys.impl;

import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.service.admin.stat.vo.StatSystemVo;
import com.niu.core.service.admin.stat.vo.StatVersionVo;
import com.niu.core.service.admin.sys.ISystemService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class SystemServiceImpl implements ISystemService {


    @Resource
    private Cached cached;
    /**
     * 获取版权信息
     *
     * @return
     */
    @Override
    public StatSystemVo getSystemInfo() {
        StatSystemVo statSystemVo=new StatSystemVo();
        statSystemVo.setOs(System.getProperty("os.name", "Linux"));
        statSystemVo.setEnvironment(System.getProperty("catalina.home"));
        statSystemVo.setPhpV(System.getProperty("java.version"));

        StatVersionVo statVersionVo=new StatVersionVo();
        statVersionVo.setVersion("202406150001");
        statVersionVo.setCode("0.4.0");

        statSystemVo.setVersion(statVersionVo);
        return statSystemVo;
    }

    @Override
    public void clearCache() {
        cached.getAllKeys().stream().forEach(key -> cached.remove(key));;
    }
}
