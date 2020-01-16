package cn.mengfly.sqlitelib;

import cn.mengfly.sqlitelib.func.SqlConsumer;
import cn.mengfly.sqlitelib.util.ResultSetUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author wangp
 */
public class SqlExecutor<T> {

    private static final String OP_SELECT = "SELECT";
    private static final String OP_UPDATE = "UPDATE";
    private static final String OP_INSERT = "INSERT";
    private static final String OP_DELETE = "DELETE";

    private SqlBuilder sqlBuilder;
    private Class<T> cls;
    private Map<String, String> mapper;
    private SqlConsumer<T> convert;

    SqlExecutor(SqlBuilder sqlBuilder) {
        this.sqlBuilder = sqlBuilder;
    }

    SqlExecutor(SqlBuilder sqlResult, SqlConsumer<T> resultSetFunction) {
        this(sqlResult);
        this.convert = resultSetFunction;
    }

    SqlExecutor(SqlBuilder sqlResult, Class<T> cls, Map<String, String> mapper) {
        this(sqlResult);
        this.cls = cls;
        this.mapper = mapper;
    }

    public List<T> findAll() {
        List<T> resultList = new ArrayList<>();
        SqliteHelper.getInstance().executeSql(generateSql(OP_SELECT),
                resultSet -> resultList.add(convertResult(resultSet)));
        return resultList;
    }

    public T findOne() {
        int limitStart = sqlBuilder.getLimitStart();
        int limitEnd = sqlBuilder.getLimitEnd();
        // 动态改变limit达到查询单个数据的目的
        sqlBuilder.limit(0, 1);
        List<T> all = findAll();
        sqlBuilder.limit(limitStart, limitEnd);
        return all.isEmpty() ? null : all.get(0);
    }

    public T max() {
        checkOneCol();
        return max(sqlBuilder.getCols()[0]);
    }

    public T max(String col) {
        String[] rawCol = sqlBuilder.getCols();
        sqlBuilder.cols("MAX(" + col + ") AS m");
        AtomicReference<T> max = new AtomicReference<>(null);
        SqliteHelper.getInstance().executeSql(generateSql(OP_SELECT),
                resultSet -> max.set(ResultSetUtil.getObjectOrDefault(resultSet, cls, "m", null)));
        sqlBuilder.cols(rawCol);
        return max.get();
    }

    public T min() {
        checkOneCol();
        return min(sqlBuilder.getCols()[0]);
    }

    public T min(String col) {
        String[] cols = sqlBuilder.getCols();
        sqlBuilder.cols("MIN(" + col + ") AS m");
        AtomicReference<T> min = new AtomicReference<>(null);
        SqliteHelper.getInstance().executeSql(generateSql(OP_SELECT),
                resultSet -> min.set(ResultSetUtil.getObjectOrDefault(resultSet, cls, "m", null)));
        sqlBuilder.cols(cols);
        return min.get();
    }

    public T sum() {
        checkOneCol();
        return sum(sqlBuilder.getCols()[0]);
    }

    public T sum(String col) {
        String[] cols = sqlBuilder.getCols();
        sqlBuilder.cols("SUM(" + col + ") AS s");
        AtomicReference<T> sum = new AtomicReference<>(null);
        SqliteHelper.getInstance().executeSql(generateSql(OP_SELECT),
                resultSet -> sum.set(ResultSetUtil.getObjectOrDefault(resultSet, cls, "s", null)));
        sqlBuilder.cols(cols);
        return sum.get();
    }

    public T avg() {
        checkOneCol();
        return avg(sqlBuilder.getCols()[0]);
    }

    public T avg(String col) {
        String[] cols = sqlBuilder.getCols();
        sqlBuilder.cols("AVG(" + col + ") AS a");
        AtomicReference<T> avg = new AtomicReference<>(null);
        SqliteHelper.getInstance().executeSql(generateSql(OP_SELECT),
                resultSet -> avg.set(ResultSetUtil.getObjectOrDefault(resultSet, cls, "a", null)));
        return avg.get();
    }


    public int count() {
        String[] cols = sqlBuilder.getCols();
        // 动态改变查询行达到查询个数的目的
        sqlBuilder.cols("COUNT(*) AS c");
        AtomicInteger count = new AtomicInteger(0);
        SqliteHelper.getInstance().executeSql(generateSql(OP_SELECT),
                resultSet -> count.set(ResultSetUtil.getIntOrDefault(resultSet, "c", 0)));
        // 使用后恢复
        sqlBuilder.cols(cols);
        return count.get();
    }

    public boolean exists() {
        String[] cols = sqlBuilder.getCols();
        sqlBuilder.cols("COUNT(*) > 0 AS e");
        AtomicInteger count = new AtomicInteger(0);
        SqliteHelper.getInstance().executeSql(generateSql(OP_SELECT),
                resultSet -> count.set(ResultSetUtil.getIntOrDefault(resultSet, "e", 0)));
        sqlBuilder.cols(cols);
        return count.get() > 0;
    }


    String generateSql(String op) {
        String sql = op + " " + sqlBuilder.generateSql();
        if (SqlBuilder.isLogEnable()) {
            System.out.println(sql);
        }
        return sql;

    }

    private T convertResult(ResultSet resultSet) {
        if (cls == null && convert == null) {
            throw new IllegalArgumentException("Not Found Generate Item ben, Please ensure you Call the method with(...)");
        }
        if (convert != null) {
            try {
                return convert.convertResult(resultSet);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return generateItemByCls(resultSet);
    }

    private T generateItemByCls(ResultSet resultSet) {
        // todo 怎么从ResultSet中获取到对象？
        return null;
    }

    private void checkOneCol() {
        String[] cols = sqlBuilder.getCols();
        if (cols == null) {
            throw new IllegalArgumentException("can't fount the max column");
        }
        if (cols.length != 1) {
            throw new IllegalArgumentException("max column length need be one");
        }
    }

}
