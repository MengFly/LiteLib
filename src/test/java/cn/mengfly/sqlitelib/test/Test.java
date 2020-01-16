package cn.mengfly.sqlitelib.test;

import cn.mengfly.sqlitelib.LiteLib;
import cn.mengfly.sqlitelib.SqlBuilder;

public class Test {

    public static void testCount(String dbFilePath) {
        LiteLib.use(dbFilePath);

        // test Count
        int count = LiteLib.table("data").map().count();
        System.out.println(count);
        assert count == 16729;

        // test Count by where
        int countByMpName = LiteLib.table("data")
                .where("mpname=?", "250").map().count();
        System.out.println(countByMpName);
        assert countByMpName == 7694;

        // test max value
        Float maxValue = LiteLib.table("data").map(Float.class).max("value2");
        System.out.println(maxValue);

        //test max id
        Integer maxId = LiteLib.table("data").map(Integer.class).max("id");
        System.out.println(maxId);
        assert maxId == 16729;

        // 查询Builder复用
        SqlBuilder builder = LiteLib.table("data")
                .where("mpname=? AND id>?", "250", "100")
                .limit(0, 1000);


        System.out.println(builder.map().count());
        System.out.println(builder.map(Float.class).max("value2"));
        System.out.println(builder.map(Integer.class).max("id"));
        System.out.println(builder.map(Float.class).min("value2"));

        SqlBuilder b2 = LiteLib.table("data").cols("id");
        System.out.println(b2.map(Long.class).sum());
        System.out.println(b2.map(Integer.class).avg());
        b2.where("id=?", "2");
        System.out.println(b2.map().exists());

    }


    public static void main(String[] args) {
        // enable log
        SqlBuilder.setLogEnable(true);
        String dbFilePath = "C:\\Users\\wangp\\Desktop\\Testupload.db";
        testCount(dbFilePath);
        LiteLib.destroy();
    }
}
