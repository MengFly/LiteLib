package cn.mengfly.sqlitelib;

import java.util.List;

public class LiteLib {

    public static void use(String dbFilePath) {
        SqliteHelper.getInstance().setDbFile(dbFilePath);
    }

    public static SqlBuilder table(String tableName) {
        return SqlBuilder.table(tableName);
    }

    public static <T> T max(String tableName, String column, Class<T> cls) {
        return SqlBuilder.table(tableName).map(cls).max(column);
    }

    public static <T> T min(String tableName, String column, Class<T> cls) {
        return SqlBuilder.table(tableName).map(cls).min(column);
    }

    public static <T> List<T> find(String tableName, Class<T> cls) {
        return SqlBuilder.table(tableName).map(cls).findAll();
    }

    public static <T> T findOne(Class<T> cls) {
        return SqlBuilder.table(cls.getSimpleName().toLowerCase()).map(cls).findOne();
    }

    public static <T> T max(String column, Class<T> cls) {
        return SqlBuilder.table(cls.getSimpleName().toLowerCase()).map(cls).max(column);
    }

    public static <T> T min(String column, Class<T> cls) {
        return SqlBuilder.table(cls.getSimpleName().toLowerCase()).map(cls).min(column);
    }

    public static <T> List<T> find(Class<T> cls) {
        return SqlBuilder.table(cls.getSimpleName().toLowerCase()).map(cls).findAll();
    }

    public static void destroy() {
        SqliteHelper.getInstance().closeConnection();
    }

}
