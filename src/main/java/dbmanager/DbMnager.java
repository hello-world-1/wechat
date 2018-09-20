//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package dbmanager;


import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DbMnager {
    private static int imc_pid = 0;
    private static int imc_updata_count = 0;

    public DbMnager() {
    }

    public static Connection getConn() {
        ExecuteSql sql = null;
        Connection con = null;

        try {
            sql = new ExecuteSql();
            con = sql.con;
        } catch (Exception var3) {
            ;
        }

        return con;
    }


    /**
     * 字符串处理
     *
     * @param str
     * @return
     */
    public static String str(String str) {
        if (null == str || "".equals(str.trim()) || "NULL".equals(str.toUpperCase())) {
            str = "";
        }

        return str.trim();
    }

    public static Connection getConn(String conname) {
        ExecuteSql sql = null;
        Connection con = null;

        try {
            sql = new ExecuteSql(conname);
            con = sql.con;
        } catch (Exception var4) {
            ;
        }

        return con;
    }


    /**
     * 执行更新或者删除程序
     *
     * @param sql
     * @param conname
     * @return
     */
    public static int Execute(String sql, String conname) {
        ExecuteSql con = null;
        byte i = 1;

        try {
            con = new ExecuteSql(conname);
            con.pst1 = con.con.prepareStatement(sql);
            con.pst1.executeUpdate();
        } catch (Exception var8) {
            con.destroy();
            i = 0;
            var8.printStackTrace();
        } finally {
            con.destroy();
        }

        return i;
    }


    /**
     * 查询某个表，并把数据映射到实体类中
     *
     * @param sqlString
     * @param classType
     * @param connname
     * @param <T>
     * @return
     */
    public static <T> List<T> getBeanic(String sqlString, Class<?> classType, String connname) {
        ArrayList objList = new ArrayList();
        ExecuteSql con = null;
        try {
            con = new ExecuteSql(connname);
            con.rs = con.con.prepareStatement(sqlString).executeQuery();
            Field[] var17 = classType.getDeclaredFields();

            while (con.rs.next()) {
                Object objectCopy = classType.getConstructor(new Class[0]).newInstance(new Object[0]);
                for (int i = 0; i < var17.length; ++i) {
                    Field field = var17[i];
                    String fieldName = field.getName();
                    Object value = null;
                    if (field.getType().equals(String.class)) {
                        value = con.rs.getString(fieldName);
                        if (value == null) {
                            value = "";
                        }
                    }

                    if (field.getType().equals(Integer.TYPE)) {
                        value = Integer.valueOf(con.rs.getInt(fieldName));
                    }

                    if (field.getType().equals(Date.class)) {
                        value = con.rs.getDate(fieldName);
                    }

                    String firstLetter = fieldName.substring(0, 1).toUpperCase();
                    String setMethodName = "set" + firstLetter + fieldName.substring(1);
                    Method setMethod = classType.getMethod(setMethodName, new Class[]{field.getType()});
                    setMethod.invoke(objectCopy, new Object[]{value});
                }
                objList.add(objectCopy);
            }
        } catch (Exception var171) {
            var171.printStackTrace();
        } finally {
            con.destroy();
        }

        return objList;
    }


    /**
     * 查询单个字段
     *
     * @param sql
     * @param conNmae
     * @return
     */
    public static String QueryOne(String sql, String conNmae) {
        ExecuteSql db = null;
        String str = "";

        try {
            db = new ExecuteSql(conNmae);

            for (db.rs = db.con.prepareStatement(sql).executeQuery(); db.rs.next();

                 str = str + "," + db.rs.getString(1)) {
                ;
            }

            if (str.length() > 1) {
                str = str.substring(1);
            }
        } catch (Exception var17) {
            var17.printStackTrace();

            try {
                db.destroy();
            } catch (Exception var16) {
                ;
            }
        } finally {
            try {
                db.destroy();
            } catch (Exception var15) {
                ;
            }
        }
        return str;
    }


    public static void main(String[] args) throws IOException {
        //每隔1秒查询一次数据库是否有信息
//        System.out.println("===" + JBDate.getNowDateTime());
//
//
//        DbMnager dbMnager = new DbMnager();
//        dbMnager.ExecuteBatch();
//
//        System.out.println("===" + JBDate.getNowDateTime());
    }

    /**
     * 批处理例子
     */
    public static void ExecuteBatchSql(List<String> sqls, String name) {
        ExecuteSql con = null;
        try {
            con = new ExecuteSql(name);
            con.st = con.con.createStatement();
            for (String sql:sqls) {
                con.st.addBatch(sql);
            }
            con.st.executeBatch();
            con.st.clearBatch();
        } catch (Exception var8) {
            con.destroy();
            var8.printStackTrace();
        } finally {
            con.destroy();
        }
    }





    /**
     * 批处理例子
     */
    public void ExecuteBatch() {
        String sql = "INSERT INTO `test_user` VALUES (?, ?, ?)";
        ExecuteSql con = null;
        try {
            con = new ExecuteSql("oa");
            con.pst1 = con.con.prepareStatement(sql);
            for (int i = 0; i <= 100000; i++) {
                con.pst1.setInt(1, i);
                con.pst1.setString(2, i + "param2");
                con.pst1.setString(3, i + "param2");
                con.pst1.addBatch();          // 加入批量处理
            }
            con.pst1.executeBatch();
        } catch (Exception var8) {
            con.destroy();
            var8.printStackTrace();
        } finally {
            con.destroy();
        }
    }
}
