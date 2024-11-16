package com.niu.core.service.core.app.tools;

import com.niu.core.common.component.context.SpringContext;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.*;
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
        try {
            Connection connection = (Connection) SpringContext.getBean("dataSourceConnection");
            ScriptRunner scriptRunner = new ScriptRunner(connection);
            // 设置自动提交为：false
            scriptRunner.setAutoCommit(false);
            // 每条命令间的分隔符
            // scriptRunner.setDelimiter(";");
            // scriptRunner.setLogWriter(null); //设置普通日志
            // scriptRunner.setErrorLogWriter(null); //设置异常日志
            scriptRunner.runScript(fileReader);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("执行脚本异常.");
        }
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
