package com.niu.core.service.admin.sys.impl;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.utils.QrcodeUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.event.test.TestEvent;
import com.niu.core.service.admin.stat.vo.StatSystemVo;
import com.niu.core.service.admin.stat.vo.StatVersionVo;
import com.niu.core.service.admin.sys.ISystemService;
import com.niu.core.service.admin.sys.param.SpreadQrcodeParam;
import com.niu.core.service.admin.sys.vo.SpreadQrcodeVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class SystemServiceImpl implements ISystemService {


    @Resource
    private Cached cached;

    @Resource
    private DataSource dataSource;
    /**
     * 获取版权信息
     *
     * @return
     */
    @Override
    public StatSystemVo getInfo() {
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
        cached.getAllKeys().stream().forEach(key -> cached.remove(key));
    }

    @Override
    public Map<String, Object> getSystemInfo() {
        List<Map<String, String>> serverInfo = new ArrayList<>();
        serverInfo.add(Map.of("name", "服务器系统", "server", System.getProperty("os.name")));
        serverInfo.add(Map.of("name", "服务器设置", "server", System.getProperty("os.version")));
        serverInfo.add(Map.of("name", "Java版本", "server", System.getProperty("java.version")));
        // 2. 扩展/功能检查
        List<Map<String, Object>> systemVariables = new ArrayList<>();

        // 检查数据库连接
        boolean hasJdbc = checkJdbc();
        systemVariables.add(Map.of(
                "name", "JDBC",
                "need", "开启",
                "status", hasJdbc
        ));

        // 检查SSL支持
        boolean hasSSL = checkSSL();
        systemVariables.add(Map.of(
                "name", "SSL",
                "need", "开启",
                "status", hasSSL
        ));

        // 3. 目录权限检查
        List<Map<String, Object>> dirsList = new ArrayList<>();

        // 检查runtime目录
        File runtimeDir = new File(WebAppEnvs.get().webRootDownResource);
        boolean runtimeAccess = checkDirectoryAccess(runtimeDir);
        dirsList.add(Map.of(
                "path", runtimeDir.getAbsolutePath(),
                "need", "可读可写",
                "path_name", "/runtime",
                "name", "runtime",
                "status", runtimeAccess
        ));

        // 检查upload目录
        Path upload = Paths.get(WebAppEnvs.get().webRootDownPublic, "upload");
        String uploadUrl = upload.toString();
        File uploadDir = new File(uploadUrl);
        boolean uploadAccess = checkDirectoryAccess(uploadDir);
        dirsList.add(Map.of(
                "path", uploadDir.getAbsolutePath(),
                "need", "可读可写",
                "path_name", "/public/upload",
                "name", "upload",
                "status", uploadAccess
        ));

        systemVariables.addAll(dirsList);

        // 4. 版本信息
        List<Map<String, String>> serverVersion = new ArrayList<>();
        serverVersion.add(Map.of(
                "name", "Java版本",
                "demand", "要求Java 8+",
                "server", System.getProperty("java.version")
        ));

        // 获取数据库版本
        String dbVersion = getDatabaseVersion();
        serverVersion.add(Map.of(
                "name", "数据库版本",
                "demand", "要求MySQL 5.7+",
                "server", dbVersion
        ));

        // 5. 进程检查
        List<Map<String, Object>> process = new ArrayList<>();
        boolean queueRunning = checkQueueProcess();
        process.add(Map.of(
                "name", "队列监听进程",
                "need", "开启",
                "status", queueRunning
        ));



        return Map.of(
                "server", serverInfo,
                "dirs_list", dirsList,
                "system_variables", systemVariables,
                "server_version", serverVersion,
                "process", process
        );
    }

    /**
     * 获取推广二维码
     * @param param
     * @return
     */
    @Override
    public SpreadQrcodeVo getSpreadQrcode(SpreadQrcodeParam param) {
        SpreadQrcodeVo vo = new SpreadQrcodeVo();

        try {
            Map<String, Object> data = new HashMap<>();
            for (SpreadQrcodeParam.Param qrcodeParam : param.getParams()) {
                JSONArray jsonArray = JSONUtil.parseArray(qrcodeParam);
                JSONObject jsonObject = JSONUtil.parseObj(jsonArray);
                data.put(jsonObject.getStr("name"), qrcodeParam.getColumnValue());
            }
            String dir = "upload/qrcode/" + RequestUtils.siteId() + "/" + param.getFolder();
            vo.setWeappPath(QrcodeUtils.qrcodeToFile(RequestUtils.siteId(), "weapp", "", param.getPage(), data, dir));
        } catch (Exception e) {

        }
        return vo;
    }

    private boolean checkJdbc() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private boolean checkSSL() {
        try {
            javax.net.ssl.SSLContext.getInstance("TLS");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkDirectoryAccess(File dir) {
        return dir.exists() && dir.canRead() && dir.canWrite();
    }

    public String getDatabaseVersion() {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            return metaData.getDatabaseProductVersion();
        }catch (SQLException e) {
            return "未知";
        }
    }

    private boolean checkQueueProcess() {
        Path path = Paths.get(WebAppEnvs.get().webRootDownResource, "upload" + UUID.randomUUID().toString() + ".txt");
        TestEvent event = new TestEvent();
        event.setName("test");
        event.setUrl(path);
        try {
            EventAndSubscribeOfPublisher.publishAll(event);
            TimeUnit.SECONDS.sleep(5);
            boolean exists = Files.exists(path);
            if (exists){
                Files.deleteIfExists(path);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
