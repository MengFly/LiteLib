package cn.mengfly.sqlitelib;

import cn.mengfly.sqlitelib.func.SqlConsumer;
import org.sqlite.util.StringUtils;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

public class SqlBuilder {
    private static boolean LOG_ENABLE = false;

    public static void setLogEnable(boolean logEnable) {
        LOG_ENABLE = logEnable;
    }

    public static boolean isLogEnable() {
        return LOG_ENABLE;
    }

    private String tableName;
    private String[] cols;
    private String where;
    private Object[] whereConditions;
    private String[] orderCols;

    private int limitStart = -1;
    private int limitEnd = -1;

    private Class<?> mapCls;

    private SqlBuilder(String tableName) {
        this.tableName = tableName;
    }

    static SqlBuilder table(String tableName) {
        return new SqlBuilder(tableName);
    }

    static <T> SqlBuilder table(Class<T> cls) {
        SqlBuilder builder = new SqlBuilder(cls.getSimpleName().toLowerCase());
        builder.mapCls = cls;
        return builder;
    }

    public SqlBuilder cols(String... cols) {
        this.cols = cols;
        return this;
    }

    public SqlBuilder where(String where, String... whereConditions) {
        this.where = where;
        this.whereConditions = whereConditions;
        return this;
    }

    public SqlBuilder orders(String... orderCols) {
        this.orderCols = orderCols;
        return this;
    }

    public SqlBuilder limit(int limit, int offset) {
        this.limitStart = limit;
        this.limitEnd = offset;
        return this;
    }

    String getTableName() {
        return tableName;
    }

    String[] getCols() {
        return cols;
    }

    String getWhere() {
        return where;
    }

    Object[] getWhereConditions() {
        return whereConditions;
    }

    String[] getOrderCols() {
        return orderCols;
    }

    int getLimitStart() {
        return limitStart;
    }

    int getLimitEnd() {
        return limitEnd;
    }

    String generateSql() {
        if (tableName == null) {
            throw new IllegalArgumentException("tableName Cannot null");
        }
        if ((limitStart == -1 && limitEnd != -1)
                || (limitStart != -1 && limitEnd == -1)) {
            throw new IllegalArgumentException("limit and offset must both to set");
        }
        if (cols == null) {
            cols = new String[]{"*"};
        }
        StringBuilder sqlResult = new StringBuilder();
        String columns = StringUtils.join(Arrays.asList(cols), ",");
        sqlResult.append(columns).append(" ");
        sqlResult.append("FROM ").append(tableName).append(" ");
        if (where != null) {
            where = where.replaceAll("\\?", "%s");
            sqlResult.append("WHERE ")
                    .append(String.format(Locale.CHINA, where, whereConditions))
                    .append(" ");
        }
        if (orderCols != null) {
            sqlResult.append("ORDER BY ").append(StringUtils.join(Arrays.asList(orderCols), ",")).append(" ");
        }
        if (limitStart != -1 && limitEnd != -1) {
            sqlResult.append("LIMIT ").append(limitStart).append(", ").append(limitEnd).append(" ");
        }
        return sqlResult.toString();
    }

    public SqlExecutor<Void> map() {
        return map(Void.class);
    }

    public <T> SqlExecutor<T> map(Class<T> cls) {
        return map(cls, null);
    }

    public <T> SqlExecutor<T> map(Class<T> cls, Map<String, String> mapper) {
        return new SqlExecutor<T>(this, cls, mapper);
    }

    public <T> SqlExecutor<T> map(SqlConsumer<T> convert) {
        return new SqlExecutor<T>(this, convert);
    }


}
