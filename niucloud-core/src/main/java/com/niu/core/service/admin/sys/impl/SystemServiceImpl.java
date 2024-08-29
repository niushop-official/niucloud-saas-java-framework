package com.niu.core.service.admin.sys.impl;

import com.niu.core.service.admin.stat.vo.StatSystemVo;
import com.niu.core.service.admin.stat.vo.StatVersionVo;
import com.niu.core.service.admin.sys.ISystemService;
import org.springframework.stereotype.Service;

@Service
public class SystemServiceImpl implements ISystemService {

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
}
