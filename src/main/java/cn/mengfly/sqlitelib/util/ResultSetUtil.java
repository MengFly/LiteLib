package cn.mengfly.sqlitelib.util;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

public class ResultSetUtil {

    public static int getIntOrDefault(ResultSet resultSet, String colName, int defaultValue) {
        try {
            return resultSet.getInt(colName);
        } catch (SQLException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static <T> T getObjectOrDefault(ResultSet resultSet, Class<T> cls, String colName, T defaultValue) {
        String typeName;
        if (cls.isPrimitive()) {
            String name = cls.getName();
            typeName = name.substring(0, 1).toUpperCase(Locale.US) + name.substring(1);
        } else {
            typeName = cls.getSimpleName();
        }
        String methodName = "get" + typeName;
        if ("getChar".equals(methodName) || "getCharacter".equals(methodName)) {
            methodName = "getString";
        } else if ("getInteger".equals(methodName)) {
            methodName = "getInt";
        } else if ("getbyte[]".equalsIgnoreCase(methodName)) {
            methodName = "getBytes";
        }
        Class<? extends ResultSet> aClass = resultSet.getClass();
        try {
            Method method = aClass.getMethod(methodName, String.class);
            return (T) method.invoke(resultSet, colName);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }
}
