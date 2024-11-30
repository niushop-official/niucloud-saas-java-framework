package com.niu.core.common.utils;

import cn.hutool.core.date.DateUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class DatabaseBackup {

    private String dbUrl;

    private String dbUser;

    private String dbPassword;

    private static final int MAX_FILE_SIZE = 6 * 1024 * 1024;

    private String backupFilePath;

    private List<String> filter;

    public DatabaseBackup(String dbUrl, String dbUser, String dbPassword) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    /**
     * 设置备份目录
     * @param path
     * @return
     */
    public DatabaseBackup setDir(String path) {
        this.backupFilePath = path;
        return this;
    }

    /**
     * 设置无需备份的表
     * @param filter
     * @return
     */
    public DatabaseBackup setFilter(String[] filter) {
        this.filter = Arrays.asList(filter);
        return this;
    }

    /**
     * 备份数据库
     */
    public void backup() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        FileWriter writer = null;
        File file = null;
        String fileName = DateUtil.format(DateUtil.date(), "yyyyMMdd");
        int fileCounter = 0;

        try {
            // 建立数据库连接
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            // 获取所有表名
            statement = connection.prepareStatement("SHOW TABLES");
            resultSet = statement.executeQuery();

            file = new File(this.backupFilePath + fileName + "-" + fileCounter + ".sql");
            writer = new FileWriter(file);

            while (resultSet.next()) {
                String tableName = resultSet.getString(1);

                if (this.filter.size() > 0 && this.filter.contains(tableName)) continue;

                writer.write("-- Table structure for table `" + tableName + "`\n");
                writer.write("DROP TABLE IF EXISTS `" + tableName + "`;\n");
                statement = connection.prepareStatement("SHOW CREATE TABLE " + tableName);
                ResultSet createTableResult = statement.executeQuery();
                if (createTableResult.next()) {
                    writer.write(createTableResult.getString(2) + ";\n\n");
                }

                // 备份表数据
                writer.write("-- Dumping data for table `" + tableName + "`\n");
                statement = connection.prepareStatement("SELECT * FROM " + tableName);
                ResultSet dataResult = statement.executeQuery();

                int isRestart = 0;

                while (dataResult.next()) {
                    StringBuilder sqlBuilder = null;
                    if (dataResult.isFirst() || isRestart == 1) {
                        sqlBuilder = new StringBuilder("INSERT INTO " + tableName + " VALUES\n(");
                        isRestart = 0;
                    } else {
                        sqlBuilder = new StringBuilder("(");
                    }

                    for (int i = 1; i <= dataResult.getMetaData().getColumnCount(); i++) {
                        if (i > 1) sqlBuilder.append(", ");
                        Object value = dataResult.getObject(i);
                        if (value instanceof String || value instanceof java.util.Date) {
                            sqlBuilder.append("'").append(value).append("'");
                        } else {
                            sqlBuilder.append(value);
                        }
                    }

                    if (dataResult.isLast()) {
                        sqlBuilder.append(");\n");
                    } else if (file.length() >= MAX_FILE_SIZE) {
                        isRestart = 1;
                        sqlBuilder.append(");\n");
                    } else {
                        sqlBuilder.append("),\n");
                    }

                    writer.write(sqlBuilder.toString());

                    if (isRestart == 1) {
                        writer.close();
                        fileCounter++;
                        file = new File(this.backupFilePath + fileName + "-" + fileCounter + ".sql");
                        writer = new FileWriter(file);
                    }
                }
                writer.write("\n");
            }
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
                if (writer != null) {
                    writer.flush(); // 确保数据被写入文件
                    writer.close();
                }
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }
}
