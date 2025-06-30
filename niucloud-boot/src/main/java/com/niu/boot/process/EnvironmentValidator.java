package com.niu.boot.process;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.niu.boot.config.BootConfig;
import com.niu.boot.utils.FileTools;
import org.yaml.snakeyaml.Yaml;
import redis.clients.jedis.Jedis;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class EnvironmentValidator {

    private static EnvironmentValidator bootAppManager;
    private BootConfig bootConfig;

    private Map<String, Object> applicationProdYamlMap;
    private Map<String, Map<String, Object>> springMap;

    private String mysqlUrl;
    private String mysqlUsername;
    private String mysqlPassword;
    private String redisHost;
    private int redisPort;
    private String redisPassword;

    /**
     * @param bootConfig
     */
    private EnvironmentValidator(BootConfig bootConfig) {
        this.bootConfig = bootConfig;
    }

    /**
     * @param bootConfig
     * @return
     */
    public static EnvironmentValidator getInstance(BootConfig bootConfig) {
        if (bootAppManager == null) {
            bootAppManager = new EnvironmentValidator(bootConfig);
        }
        return bootAppManager;
    }

    /**
     * 是否检查环境
     *
     * @return
     */
    public boolean checkEnvironment() {
        if ("prod".equals(this.bootConfig.environment())) {
            return true;
        }
        return true;
    }

    /**
     * @return
     */
    public boolean checkJreUsable() {
        if (this.bootConfig.getJavaVersion().startsWith("17")) {
            System.out.println("当前Java版本: " + bootConfig.getJavaVersion());
            return true;
        }
        return false;
    }

    /**
     * 读取配置文件
     *
     * @return
     */
    public boolean readAppEnv() {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = new FileInputStream(this.bootConfig.getAppEnvPath())) {
            this.applicationProdYamlMap = yaml.load(inputStream);
            this.springMap = (Map<String, Map<String, Object>>) applicationProdYamlMap.get("spring");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * @return
     */
    public boolean checkMysqlUsable() {
        Map<String, Object> datasourceMap = this.springMap.get("datasource");
        String mysqlUrl = (String) datasourceMap.get("url");
        if (StrUtil.isEmpty(mysqlUrl)) {
            System.err.println("spring.datasource.url未配置.");
            return false;
        }
        this.mysqlUrl = mysqlUrl;
        String mysqlUsername = (String) datasourceMap.get("username");
        if (StrUtil.isEmpty(mysqlUsername)) {
            System.err.println("spring.datasource.username未配置.");
            return false;
        }
        this.mysqlUsername = mysqlUsername;
        String mysqlPassword = "";
        try {
            if (datasourceMap.get("password") instanceof String) {
                mysqlPassword = (String) datasourceMap.get("password");
            } else {
                mysqlPassword = String.valueOf(datasourceMap.get("password"));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        this.mysqlPassword = mysqlPassword;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(mysqlUrl, mysqlUsername, mysqlPassword);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT VERSION()");
            if (resultSet.next()) {
                String version = resultSet.getString(1);
                System.out.println("MySQL版本: " + version);
            }
            resultSet = statement.executeQuery("SELECT DATABASE()");
            String databaseName = "";
            if (resultSet.next()) {
                databaseName = resultSet.getString(1);
            }
            int total = 0;
            resultSet = statement.executeQuery("SELECT COUNT(*) AS table_count FROM information_schema.tables WHERE table_schema = '" + databaseName + "'");
            if (resultSet.next()) {
                total = resultSet.getInt(1);
            }
            System.out.println("MySQL连接的数据库: " + databaseName + "(" + total + ")");
            if (total <= 0) {
                FileTools.writeTempContent(bootConfig.applicationName() + "/inited", "init");
            } else {
                FileTools.writeTempContent(bootConfig.applicationName() + "/inited", "created");
            }
        } catch (Exception e) {
            // e.printStackTrace();   ignore
            System.err.println(e.getMessage());
            return false;
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (Exception e) {
                    // ignore
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                    // ignore
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        return true;
    }

    /**
     * @return
     */
    public boolean checkRedisUsable() {
        Map<String, Object> redisMap = (Map<String, Object>) this.springMap.get("data").get("redis");
        String redisHost = (String) redisMap.get("host");
        if (StrUtil.isEmpty(redisHost)) {
            System.err.println("spring.redis.host未配置.");
            return false;
        }
        this.redisHost = redisHost;
        Integer redisPort = (Integer) redisMap.get("port");
        if (redisPort == null) {
            System.err.println("spring.redis.port未配置.");
            return false;
        }
        this.redisPort = redisPort;
        String redisPassword = "";
        try {
            if (redisMap.get("password") instanceof String) {
                redisPassword = (String) redisMap.get("password");
            } else {
                redisPassword = String.valueOf(redisMap.get("password"));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        Jedis jedis = null;
        try {
            jedis = new Jedis(redisHost, redisPort);
            if (StrUtil.isNotEmpty(redisPassword) && !"null".equalsIgnoreCase(redisPassword)) {
                this.redisPassword = redisPassword;
                jedis.auth(redisPassword);
            } else {
                this.redisPassword = "";
            }
            jedis.ping();
            String info = jedis.info();
            String redisVersionRegex = "redis_version:(.*)";
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(redisVersionRegex);
            java.util.regex.Matcher matcher = pattern.matcher(info);
            if (matcher.find()) {
                System.out.println("Redis版本: " + matcher.group(1));
            } else {
                System.out.println("Redis版本: 未知");
            }
        } catch (Exception e) {
            // ignore
            System.err.println(e.getMessage());
            return false;
        } finally {
            if (jedis != null) {
                try {
                    jedis.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        return true;
    }

    /**
     * 构建yml启动配置
     */
    public boolean buildBootYml() {
        if (!this.copyApplicationYml()) {
            return false;
        }
        // 读取文件
        String entryString = "BOOT-INF/classes/application-template.yml";
        String content = "";
        InputStream inputStream = null;
        try {
            JarFile jarFile = new JarFile(this.bootConfig.getJarPath()); // 替换为你的JAR文件路径
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String entryName = jarEntry.getName();
                if (entryString.equals(entryName)) {
                    inputStream = jarFile.getInputStream(jarEntry);
                    break;
                }
            }
            content = IoUtil.read(inputStream, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IoUtil.close(inputStream);
        }
        // 内容处理
        if (content == null) {
            return false;
        }
        content = content.replace("{{mysql.url}}", this.mysqlUrl);
        content = content.replace("{{mysql.username}}", this.mysqlUsername);
        content = content.replace("{{mysql.password}}", this.mysqlPassword);
        content = content.replace("{{redis.host}}", this.redisHost);
        content = content.replace("{{redis.port}}", String.valueOf(this.redisPort));
        content = content.replace("{{redis.password}}", this.redisPassword);
        //
        FileOutputStream outputStream = null;
        Writer writer = null;
        try {
            outputStream = new FileOutputStream(bootConfig.getApplicationYmlBoot());
            writer = new OutputStreamWriter(outputStream, "UTF-8");
            writer.write(content);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IoUtil.close(writer);
            IoUtil.close(outputStream);
        }
        return true;
    }

    /**
     * @return
     */
    private boolean copyApplicationYml() {
        // 读取文件
        String entryString = "BOOT-INF/classes/application.yml";
        String content = "";
        InputStream inputStream = null;
        try {
            JarFile jarFile = new JarFile(this.bootConfig.getJarPath()); // 替换为你的JAR文件路径
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String entryName = jarEntry.getName();
                if (entryString.equals(entryName)) {
                    inputStream = jarFile.getInputStream(jarEntry);
                    break;
                }
            }
            content = IoUtil.read(inputStream, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IoUtil.close(inputStream);
        }
        // 内容处理
        if (content == null) {
            return false;
        }
        FileOutputStream outputStream = null;
        Writer writer = null;
        try {
            outputStream = new FileOutputStream(bootConfig.getApplicationYml());
            writer = new OutputStreamWriter(outputStream, "UTF-8");
            writer.write(content);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IoUtil.close(writer);
            IoUtil.close(outputStream);
        }
        return true;
    }

}
