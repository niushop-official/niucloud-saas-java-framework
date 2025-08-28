package com.niu.core.common.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.component.context.cache.CacheFactory;
import com.niu.core.common.component.context.cache.Cached;
import lombok.Data;

/**
 * 数据库备份与恢复工具类（按数据库原始表顺序）
 * 按数据库中存储的原始表顺序进行备份
 */
public class DatabaseBackup {
    private final Cached cache;
    private final String cacheKey;
    private final String backupPath;
    private final long maxFileSize;
    private int currentFileIndex = 1;
    private String currentFile;
    private long currentFileSize = 0;
    private Map<String, Integer> tableOffset = new HashMap<>();
    private Map<String, Integer> tableTotalRows = new HashMap<>();
    private List<String> tables = new ArrayList<>();
    private List<String> excludeTables = new ArrayList<>();
    private int totalTables;
    private int processedTables = 0;
    private int restoreIndex = 0;
    private long startTime = 0;
    private final int maxExecuteTime = 10;
    private final Connection connection;
    private final boolean enableTableLock;

    /**
     * 构造方法
     */
    public DatabaseBackup(Connection connection, String backupPath,
                          long maxFileSize, List<String> excludeTables, String key, boolean enableTableLock) {
        this.connection = connection;
        this.backupPath = backupPath;
        this.maxFileSize = maxFileSize;
        this.excludeTables = excludeTables;
        this.enableTableLock = enableTableLock;

        this.cache = CacheFactory.getCacheOperator();
        this.cacheKey = "db_backup_" + key;

        // 初始化目录
        File dir = new File(backupPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.currentFile = backupPath + "/backup_" + currentFileIndex + ".sql";

        try {
            // 获取数据库原始表顺序
            this.tables = getAllTables();
            this.totalTables = this.tables.size();
            this.tableTotalRows.clear();
            this.tableOffset.clear();
            initTableTotalRows();
        } catch (SQLException e) {
            throw new RuntimeException("初始化表信息失败", e);
        }

        // 加载缓存状态
        loadStateFromCache();
        this.startTime = System.currentTimeMillis() / 1000;
    }

    /**
     * 获取所有表（使用数据库返回的原始顺序）
     * 关键：不进行任何排序，直接使用数据库元数据返回的顺序
     */
    private List<String> getAllTables() throws SQLException {
        if (!tables.isEmpty()) {
            return tables;
        }

        List<String> allTables = new LinkedList<>();
        // 通过数据库元数据获取表列表，不进行排序
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW TABLES")) {

            while (rs.next()) {
                String tableName = rs.getString(1);
                // 排除不需要备份的表
                if (!excludeTables.contains(tableName)) {
                    allTables.add(tableName);
                }
            }
        }

        // 直接返回数据库原始顺序的表列表
        return allTables;
    }

    /**
     * 从缓存加载状态（保持原始表顺序）
     */
    private void loadStateFromCache() {
        Object json = cache.get(cacheKey);
        if (ObjectUtil.isNotEmpty(json)) {
            BackupState state = JSONUtil.toBean(JSONUtil.parseObj(json), BackupState.class);
            this.currentFileIndex = state.getCurrentFileIndex();
            this.processedTables = state.getProcessedTables();
            this.tableOffset = new HashMap<>(state.getTableOffset());
            this.currentFileSize = state.getCurrentFileSize();
            this.restoreIndex = state.getRestoreIndex();
            this.tableTotalRows = new HashMap<>(state.getTableTotalRows());

            // 验证缓存中的表是否存在于当前表列表中
            if (!state.getTables().isEmpty()) {
                for (String table : state.getTables()) {
                    if (!this.tables.contains(table)) {
                        throw new RuntimeException("缓存中的表 " + table + " 不存在于当前数据库中，可能已被删除");
                    }
                }
            }
        }
    }

    /**
     * 保存状态到缓存
     */
    private void saveStateToCache() {
        BackupState state = new BackupState();
        state.setCurrentFileIndex(currentFileIndex);
        state.setProcessedTables(processedTables);
        state.setTableOffset(new HashMap<>(tableOffset));
        state.setCurrentFileSize(currentFileSize);
        state.setRestoreIndex(restoreIndex);
        state.setTables(new LinkedList<>(tables)); // 保存原始表顺序
        state.setTableTotalRows(new HashMap<>(tableTotalRows));
        cache.put(cacheKey, JSONUtil.parseObj(state).toString(), 3600);
    }

    /**
     * 备份状态实体类
     */
    @Data
    private static class BackupState {
        private int currentFileIndex;
        private int processedTables;
        private Map<String, Integer> tableOffset;
        private long currentFileSize;
        private int restoreIndex;
        private LinkedList<String> tables;
        private Map<String, Integer> tableTotalRows;
    }

    /**
     * 初始化表的总行数
     */
    private void initTableTotalRows() throws SQLException {
        if (!tableTotalRows.isEmpty()) {
            return;
        }
        for (String table : tables) {
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM `" + table + "`")) {
                if (rs.next()) {
                    tableTotalRows.put(table, rs.getInt("total"));
                }
            }
        }
    }

    /**
     * 分段备份数据库
     */
    public Object backupDatabaseSegment(int limit) throws SQLException, IOException {
        List<String> allTables = getAllTables();
        List<String> remainingTables = allTables.subList(processedTables, allTables.size());

        if (!remainingTables.isEmpty()) {
            for (String table : remainingTables) {
                // 备份前锁定表
                if (enableTableLock) {
                    lockTable(table);
                }

                try {
                    if (!tableOffset.containsKey(table)) {
                        tableOffset.put(table, 0);
                        backupTableStructure(table);
                    }

                    // 校验偏移量是否超过总行数
                    Integer totalRows = tableTotalRows.get(table);
                    if (totalRows != null && tableOffset.get(table) >= totalRows) {
                        processedTables++;
                        continue;
                    }

                    while (true) {
                        int offset = tableOffset.get(table);
                        List<Map<String, Object>> data = queryTableData(table, offset, limit);

                        if (data.isEmpty()) break;

                        try {
                            backupTableData(table, data);
                            tableOffset.put(table, offset + limit);
                        } catch (IOException e) {
                            saveStateToCache();
                            throw new IOException("备份表[" + table + "]数据失败", e);
                        }

                        // 检查超时
                        long currentTime = System.currentTimeMillis() / 1000;
                        if (currentTime - startTime > maxExecuteTime) {
                            saveStateToCache();
                            return processedTables;
                        }
                    }

                    long currentTime = System.currentTimeMillis() / 1000;
                    if (currentTime - startTime > maxExecuteTime) {
                        saveStateToCache();
                        return processedTables;
                    }

                    processedTables++;
                } finally {
                    // 解锁表
                    if (enableTableLock) {
                        unlockTables();
                    }
                }
            }
        }

        // 备份完成后清除缓存
        cache.remove(cacheKey);
        return true;
    }

    /**
     * 恢复数据库（恢复时也按备份文件中的原始顺序）
     */
    public Object restoreDatabase() throws SQLException, IOException {
        File dir = new File(backupPath);
        File[] backupFiles = dir.listFiles((d, name) ->
            name.startsWith("backup_") && name.endsWith(".sql"));

        if (backupFiles == null || backupFiles.length == 0) {
            return true;
        }

        // 按备份文件编号排序（保证恢复顺序）
        Arrays.sort(backupFiles, Comparator.comparingInt(f -> {
            String name = f.getName();
            return Integer.parseInt(name.substring(7, name.length() - 4));
        }));

        if (restoreIndex >= backupFiles.length) {
            cache.remove(cacheKey);
            return true;
        }

        // 恢复时禁用外键检查（避免因顺序问题导致失败）
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
        }

        File backupFile = backupFiles[restoreIndex];
        executeSqlFile(backupFile);

        // 恢复外键检查
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
        }

        restoreIndex++;
        saveStateToCache();
        return restoreIndex;
    }

    /**
     * 执行SQL文件
     */
    private void executeSqlFile(File file) throws SQLException, IOException {
        connection.setAutoCommit(false);
        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);
             Statement stmt = connection.createStatement()) {

            StringBuilder sql = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--")) continue;

                sql.append(line);
                if (line.endsWith(";")) {
                    String sqlStr = sql.toString().trim();
                    if (!sqlStr.isEmpty()) {
                        stmt.execute(sqlStr);
                    }
                    sql.setLength(0);

                    // 检查超时
                    long currentTime = System.currentTimeMillis() / 1000;
                    if (currentTime - startTime > maxExecuteTime) {
                        connection.commit();
                        saveStateToCache();
                        throw new SQLException("执行超时，已保存进度");
                    }
                }
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new SQLException("执行SQL文件失败：" + e.getMessage(), e);
        } finally {
            connection.setAutoCommit(true);
        }
    }

    /**
     * 查询表数据
     */
    private List<Map<String, Object>> queryTableData(String table, int offset, int limit) throws SQLException {
        String sql = "SELECT * FROM `" + table + "` LIMIT ? OFFSET ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setQueryTimeout(30);
            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> data = new ArrayList<>();
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(metaData.getColumnName(i), rs.getObject(i));
                    }
                    data.add(row);
                }
                return data;
            }
        }
    }

    /**
     * 转义值
     */
    private String escapeValue(Object value) {
        if (value == null) {
            return "NULL";
        } else if (value instanceof Number) {
            return value.toString();
        } else if (value instanceof Boolean) {
            return (Boolean) value ? "1" : "0";
        } else if (value instanceof Date) {
            return "'" + new java.sql.Timestamp(((Date) value).getTime()).toString().replace("'", "''") + "'";
        } else if (value instanceof byte[]) {
            return "0x" + bytesToHex((byte[]) value);
        } else {
            String str = value.toString().replace("'", "''");
            return "'" + str + "'";
        }
    }

    // 字节数组转十六进制
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * 获取表创建语句
     */
    private String getTableCreateQuery(String table) throws SQLException {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW CREATE TABLE `" + table + "`")) {

            if (rs.next()) {
                return rs.getString("Create Table");
            }
            throw new SQLException("表 [" + table + "] 不存在");
        }
    }

    /**
     * 备份表结构
     */
    private void backupTableStructure(String table) throws SQLException, IOException {
        String dropSql = "DROP TABLE IF EXISTS `" + table + "`;\n";
        writeToFile(dropSql);

        String createSql = getTableCreateQuery(table);
        String createLine = createSql + ";\n\n";
        writeToFile(createLine);
    }

    /**
     * 备份表数据
     */
    private void backupTableData(String table, List<Map<String, Object>> data) throws IOException, SQLException {
        if (data.isEmpty()) {
            return;
        }

        List<String> columns = getTableColumns(table);
        String columnsStr = columns.stream()
            .map(col -> "`" + col + "`")
            .collect(Collectors.joining(", "));

        List<String> valueSets = new ArrayList<>();
        for (Map<String, Object> row : data) {
            List<String> values = new ArrayList<>();
            for (String col : columns) {
                Object value = row.get(col);
                values.add(escapeValue(value));
            }
            valueSets.add("(" + String.join(", ", values) + ")");
        }

        String insertSql = "INSERT INTO `" + table + "` (" + columnsStr + ") VALUES\n"
            + String.join(",\n", valueSets) + ";\n\n";
        writeToFile(insertSql);
    }

    /**
     * 获取表的列信息
     */
    private List<String> getTableColumns(String table) throws SQLException {
        List<String> columns = new ArrayList<>();
        // 使用SHOW COLUMNS语句获取列信息，这比DatabaseMetaData更可靠
        String sql = "SHOW COLUMNS FROM `" + table + "`";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String columnName = rs.getString("Field");
                columns.add(columnName);
            }
        }
        if (columns.isEmpty()) {
            throw new SQLException("表 [" + table + "] 未找到有效列信息，请检查是否存在该表");
        }
        return columns;
    }

    /**
     * 写入内容到文件
     */
    private void writeToFile(String content) throws IOException {
        File file = new File(currentFile);
        long currentSize = file.exists() ? file.length() : 0;
        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
        long newSize = currentSize + contentBytes.length;

        if (newSize > maxFileSize) {
            startNewFile();
            file = new File(currentFile);
            currentSize = 0;
        }

        try (BufferedWriter writer = new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8))) {
            writer.write(content);
            writer.flush();
        }

        currentFileSize = file.length();
    }

    /**
     * 锁定表
     */
    private void lockTable(String table) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("LOCK TABLES `" + table + "` READ");
        }
    }

    /**
     * 解锁表
     */
    private void unlockTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("UNLOCK TABLES");
        }
    }

    private void startNewFile() {
        currentFileIndex++;
        currentFile = backupPath + "/backup_" + currentFileIndex + ".sql";
        currentFileSize = 0;
    }

    public int getBackupProgress() {
        if (totalTables == 0) return 100;
        return (int) Math.round((double) processedTables / totalTables * 100);
    }

    public int getRestoreProgress() {
        File dir = new File(backupPath);
        File[] backupFiles = dir.listFiles((d, name) ->
            name.startsWith("backup_") && name.endsWith(".sql"));
        if (backupFiles == null || backupFiles.length == 0) return 100;
        return (int) Math.round((double) restoreIndex / backupFiles.length * 100);
    }
}
