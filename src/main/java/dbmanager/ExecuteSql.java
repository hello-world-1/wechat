package dbmanager;

import java.sql.*;

public class ExecuteSql {
  private DBConnection connMgr; //管理数据库连接的对象
  public Connection con; //连接数据库的对象
  public Statement pst; //预编译执行的对象
  public PreparedStatement pst1;
  public PreparedStatement pst2;
  public Statement st;
  public ResultSet rs;
  public ResultSet rs1;
  private boolean isrelease = false;
  private String DBConnectionName = "jdbc";//定义，设置，获取数据库连接池的名字
  public void setDBConnectionName(String newValue) {
    if (newValue != null) {
      this.DBConnectionName = newValue;
    }
  }

  public String getDBConnectionName() {
    return this.DBConnectionName;
  }

  public ExecuteSql() throws Exception {
    this.DBConnectionName = "jdbc";
    getDBConnection();
  }
  
  /**
   * 得到其它数据库的链接  yuansh 2010-10-25
   * @param DBConnectionName
   * @throws Exception
   */
  public ExecuteSql(String DBConnectionName) throws Exception {
    this.DBConnectionName = DBConnectionName;
    getDBConnection();
  }

  /*
    建立数据库的连接
   */
  public void getDBConnection() throws Exception {
	
		connMgr = DBConnection.getInstance(); //建立连接实例
//	    System.out.println("connMgr.getConnection(DBConnectionName);"+DBConnectionName);
	    con = connMgr.getConnection(DBConnectionName);
	    if (con == null) {
	    	
	    	System.out.println(con == null);
	        throw new Exception("不能获取数据库的连接！");
	    }
	    isrelease = false;
  }

  public int getMaxId(String idName, String tableName) {
    if (idName == null && tableName == null) {
      return -1;
    }
    int maxid = 0;
    try {
      if (con == null) {
        this.getDBConnection();
      }
      pst1 = con.prepareStatement("select max(" + idName + ") from " +tableName);
      rs = pst1.executeQuery();
      if (rs.next()) {
        maxid = rs.getInt(1);
      }
      rs.close();
      pst.close();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      return maxid;
    }
  }

  
  public void destroy() {
    try {
      if (rs != null) {
        rs.close();
      }
      if (rs1 != null) {
          rs1.close();
      }
      if (st != null) {
        st.close();
      }
      if (this.pst != null) {
        this.pst.close();
      }
      if (this.pst1 != null) {
          this.pst1.close();
      }
      if (this.pst2 != null) {
          this.pst2.close();
      }      
      if (!isrelease) {
        connMgr.freeConnection(this.DBConnectionName, con);
        isrelease = true;
      }
    }
    catch (SQLException ex) {
    }
  }

  protected void finalize() {
    if (this.con != null) {
      this.destroy();
    }
  }
  
  public static void main(String args){
	  try {
		ExecuteSql sql = new ExecuteSql();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
}
