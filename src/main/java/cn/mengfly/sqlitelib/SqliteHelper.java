package cn.mengfly.sqlitelib;

import java.io.File;
import java.sql.*;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 提供与最底层的Sql连接与操作
 *
 * @author wangp
 */
public class SqliteHelper {

    private Connection curConnection;
    private String curSqliteDbPath;

    private SqliteHelper(String curSqliteDbPath) {
        this.curSqliteDbPath = curSqliteDbPath;
    }


    public Connection getConnection() throws SQLException {
        Objects.requireNonNull(curSqliteDbPath,
                "Sqlite db filePath is null, Please check your sqlite filePath or recall the method setDbFile(String)");
        if (checkConnectionUsable(curConnection)) {
            return curConnection;
        }
        String url = "jdbc:sqlite:" + curSqliteDbPath;
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        curConnection = DriverManager.getConnection(url);
        return curConnection;
    }

    private boolean checkConnectionUsable(Connection connection) {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public void setDbFile(String filePath) {
        if (!Objects.equals(curSqliteDbPath, filePath)) {
            // close old connection and set new sqlite db path
            closeConnection();
            this.curSqliteDbPath = filePath;
        }
    }

    public void setDbFile(File file) {
        this.setDbFile(file.getAbsolutePath());
    }

    public void executeSql(String sql, Consumer<ResultSet> convertResult) {
        try {
            Connection connection = getConnection();
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(sql)) {
                    while (resultSet.next()) {
                        convertResult.accept(resultSet);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void closeConnection() {
        if (curConnection == null || curSqliteDbPath == null) {
            return;
        }
        if (checkConnectionUsable(curConnection)) {
            try {
                curConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static String cleanSql(String sql) {
        if (sql.endsWith(" *; *")) {
            sql = sql.substring(0, sql.indexOf(";"));
        }
        return sql;
    }

    private static SqliteHelper mInstance = null;

    public static SqliteHelper getInstance() {
        return getInstance(null);
    }

    public static SqliteHelper getInstance(String dbFilePath) {
        if (mInstance == null) {
            mInstance = new SqliteHelper(dbFilePath);
        } else {
            if (dbFilePath != null) {
                mInstance.setDbFile(dbFilePath);
            }
        }
        return mInstance;
    }


}
