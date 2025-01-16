package com.niu.core.service.core.app.tools;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.component.context.SpringContext;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;

public final class SQLScriptRunnerTools {

    /**
     * @param connection
     * @param fileReader
     */
    public static void execScript(Connection connection, Reader fileReader) {
        try {
            ScriptRunner scriptRunner = new ScriptRunner(connection);
            // 设置自动提交为：false
            scriptRunner.setAutoCommit(false);
            // 每条命令间的分隔符
            // scriptRunner.setDelimiter(";");
            // scriptRunner.setLogWriter(null); //设置普通日志
            // scriptRunner.setErrorLogWriter(null); //设置异常日志
            scriptRunner.runScript(fileReader);
            // 提交
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("执行脚本异常.");
        }
    }

    /**
     * @param fileReader
     */
    public static void execScript(Reader fileReader) {
        JdbcTemplate jdbcTemplate = SpringContext.bean(JdbcTemplate.class);
        String sql = IoUtil.read(fileReader);
        if (ObjectUtil.hasEmpty(jdbcTemplate, sql)) {
            return;
        }
        jdbcTemplate.execute(sql);
    }

    /**
     * @param file
     */
    public static void execScript(File file) {
        try (FileReader fileReader = new FileReader(file)) {
            execScript(fileReader);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("执行脚本异常.");
        }
    }

    /**
     * @param scriptContent
     */
    public static void execScript(String scriptContent) {
        try (Reader reader = new StringReader(scriptContent)) {
            execScript(reader);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("执行脚本异常.");
        }
    }

}
