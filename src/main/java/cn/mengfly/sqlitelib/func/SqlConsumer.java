package cn.mengfly.sqlitelib.func;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface SqlConsumer<T> {

    T convertResult(ResultSet resultSet) throws SQLException;

}
