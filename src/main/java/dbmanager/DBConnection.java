package dbmanager;

import javax.servlet.ServletException;
import javax.servlet.SingleThreadModel;
import javax.servlet.http.HttpServlet;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class DBConnection  extends HttpServlet
    implements SingleThreadModel {

  static private DBConnection instance; //唯一实例
  static private int clients;

  private Vector drivers = new Vector();
  private PrintWriter log;
  private Hashtable pools = new Hashtable();
  private String defaultPoolName = "jdbc";
  /**ƒ®
   * 返回唯一实例.如果是第一次调用此方法,则创建实例
   *
   * @return Clsdbconnection 唯一实例
   */
  static synchronized public DBConnection getInstance() {
    if (instance == null) {
      instance = new DBConnection();
    }
    clients++;
    return instance;
  }

  /**
   * 建构函数私有以防止其它对象创建本类实例
   */
  private DBConnection() {
    try {
      init();
    }
    catch (ServletException ex) {
    }
  }

  /**
   * 将连接对象返回给由名字指定的连接池
   *
   * @param name 在属性文件中定义的连接池名字
   * @param con 连接对象
   */
  public void freeConnection(String name, Connection con) {
    DBConnectionPool pool = (DBConnectionPool) pools.get(name);
    if (pool != null) {
      pool.freeConnection(con);
    }
  }

  public void freeConnection(Connection con) {
    DBConnectionPool pool = (DBConnectionPool) pools.get(this.defaultPoolName);
    if (pool != null) {
      pool.freeConnection(con);
    }
  }

  /**
   * 获得一个可用的(空闲的)连接.如果没有可用连接,且已有连接数小于最大连接数
   * 限制,则创建并返回新连接
   *
   * @param name 在属性文件中定义的连接池名字
   * @return Connection 可用连接或null
   */
  public Connection getConnection(String name) {
	  
	 // System.out.println("进入这里了"+name);
    DBConnectionPool pool = (DBConnectionPool) pools.get(name);
    
    
    if (pool != null) {
    	 //System.out.println("返回对象");
      return pool.getConnection();
    }
    
    //System.out.println("返回空对象");
    return null;
  }

  public Connection getConnection() {
	DBConnectionPool pool = (DBConnectionPool) pools.get(this.defaultPoolName);
	if (pool != null) {
	    return pool.getConnection();
	}
	return null;
}

  /**
   * 获得一个可用连接.若没有可用连接,且已有连接数小于最大连接数限制,
   * 则创建并返回新连接.否则,在指定的时间内等待其它线程释放连接.
   *
   * @param name 连接池名字
   * @param time 以毫秒计的等待时间
   * @return Connection 可用连接或null
   */
  public Connection getConnection(String name, long time) {
    DBConnectionPool pool = (DBConnectionPool) pools.get(name);
    if (pool != null) {
      return pool.getConnection(time);
    }
    return null;
  }

  /**
   * 关闭所有连接,撤销驱动程序的注册
   */
  public synchronized void release() {
    // 等待直到最后一个客户程序调用
    if (--clients != 0) {
      return;
    }
    Enumeration allPools = pools.elements();
    while (allPools.hasMoreElements()) {
      DBConnectionPool pool = (DBConnectionPool) allPools.nextElement();
      pool.release();
    }
    Enumeration allDrivers = drivers.elements();
    while (allDrivers.hasMoreElements()) {
      Driver driver = (Driver) allDrivers.nextElement();
      try {
        DriverManager.deregisterDriver(driver);
        //log("撤销JDBC驱动程序 " + driver.getClass().getName() + "的注册");
        
        System.out.println("撤销JDBC驱动程序 " + driver.getClass().getName() + "的注册");
      }
      catch (SQLException e) {
        //log();
    	  e.printStackTrace();
        System.out.println( "无法撤销下列JDBC驱动程序的注册: " + driver.getClass().getName());
      }
    }
  }

  /**
   * 根据指定属性创建连接池实例.
   *
   * @param props 连接池属性
   */
  private void createPools(Properties props) {
    Enumeration propNames = props.propertyNames();
    while (propNames.hasMoreElements()) {
      String name = (String) propNames.nextElement();
      if (name.endsWith(".url")) {
        String poolName = name.substring(0, name.lastIndexOf("."));
        String url = props.getProperty(poolName + ".url");
        if (url == null) {
        	System.out.println("没有为连接池" + poolName + "指定URL");
          continue;
        }
        String user = props.getProperty(poolName + ".username");
        String password = props.getProperty(poolName + ".password");
        String maxconn = props.getProperty("cpool.maxPoolSize", "0");
        
        //System.out.println("hjhj"+maxconn+password+user);
        int max;
        try {
          max = Integer.valueOf(maxconn).intValue();
        }
        catch (NumberFormatException e) {
        	System.out.println("错误的最大连接数限制: " + maxconn + " .连接池: " + poolName);
          max = 0;
        }
        DBConnectionPool pool = new DBConnectionPool(poolName, url, user,
            password, max);
        //获取数据库连接池的连接超时和被调用的次数
        String connectionCount = props.getProperty("cpool.maxIdleTimeExcessConnections");
        String connectionTimeOut = props.getProperty("cpool.checkoutTimeout");
        try {
          if (connectionCount != null) {
            pool.setConnectionCount(Integer.parseInt(connectionCount));
          }
        }
        catch (Exception error) {
        	System.out.println("错误的连接调用最大数限制设置: connectionCount=" + connectionCount + " .连接池: " +
              poolName);
          error.printStackTrace();
        }
        try {
          if (connectionTimeOut != null) {
            pool.setConnectionTimeOut(Integer.parseInt(connectionTimeOut));
          }
        }
        catch (Exception error) {
        	System.out.println("错误的连接调用超时设置: connectionTimeOut=" + connectionTimeOut + " .连接池: " + poolName);
          error.printStackTrace();
        }

        pools.put(poolName, pool);
//        System.out.println("成功创建连接池" + poolName);
      }
    }
  }

  //Initialize global variables
  public void init() throws ServletException {
    InputStream is = getClass().getResourceAsStream("/conf/jdbc.properties");
    Properties dbProps = new Properties();
    try {
      dbProps.load(is);
    }
    catch (Exception e) {
      System.err.println("不能读取属性文件. " + "请确保jdbc.properties在CLASSPATH指定的路径中");
      return;
    }
    String logFile = dbProps.getProperty("logfile", "dbconnection.log");
    try {
      log = new PrintWriter(new FileWriter(logFile, true), true);
    }
    catch (IOException e) {
      System.err.println("无法打开日志文件: " + logFile);
      log = new PrintWriter(System.err);
    }
    loadDrivers(dbProps);
    System.out.println(dbProps);
    createPools(dbProps);
  }

  /**
   * 装载和注册所有JDBC驱动程序
   *
   * @param props 属性
   */
  private void loadDrivers(Properties props) {
    String driverClasses = props.getProperty("jdbc.driverClassName");
    System.out.println(driverClasses);
    StringTokenizer st = new StringTokenizer(driverClasses);
    while (st.hasMoreElements()) {
      String driverClassName = st.nextToken().trim();
      try {
        Driver driver = (Driver) Class.forName(driverClassName).newInstance();
        DriverManager.registerDriver(driver);
        drivers.addElement(driver);
        log("成功注册JDBC驱动程序" + driverClassName);
      }
      catch (Exception e) {
        log("无法注册JDBC驱动程序: " + driverClassName + ", 错误: ");
      }
    }
  }

  //Clean up resources
  public void destroy() {

  }

  /**
   * 将文本信息写入日志文件
   *@param msg 输入字符串
   */
  public void log(String msg) {
    log.println(new Date() + ": " + msg);
  }

  /**
   * 将文本信息与异常写入日志文件
   * @param msg 信息
   * @param e 异常
   */
  private void log(Throwable e, String msg) {
    log.println(new Date() + ": " + msg);
    e.printStackTrace(log);
  }

  /**
   * 此内部类定义了一个连接池.它能够根据要求创建新连接,直到预定的最
   * 大连接数为止.在返回连接给客户程序之前,它能够验证连接的有效性.
   */
  class DBConnectionPool {
    private Vector freeConnections = new Vector(); //连接对象池
    private int maxConn; //最大连接数
    private int connectionCount = 0; //一个连接可以被调用的次数
    private long connectionTimeOut = 0; //一个连接使用的最大时长
    private String password; //数据库密码
    private String URL; //数据库连接定位
    private String user; //数据库用户名称
    private String name; //连接池名称

    public void setConnectionCount(int connectionCount) {
      this.connectionCount = connectionCount;
    }

    public void setConnectionTimeOut(long connectionTimeOut) {
      this.connectionTimeOut = connectionTimeOut;
    }

    /**
     * 创建新的连接池
     *
     * @param name 连接池名字
     * @param URL 数据库的JDBC URL
     * @param user 数据库帐号,或 null
     * @param password 密码,或 null
     * @param maxConn 此连接池允许建立的最大连接数
     */
    public DBConnectionPool(String name, String URL, String user, String password, int maxConn) {
      this.name = name;
      this.URL = URL;
      this.user = user;
      this.password = password;
      this.maxConn = maxConn;
    }

    /**
     * 将不再使用的连接返回给连接池
     *
     * @param con 客户程序释放的连接
     */
    public synchronized void freeConnection(Connection con) {
      // 将指定连接加入到向量末尾
      boolean isrelease = false;
      long currentTime = System.currentTimeMillis();
      for (int i = 0; i < freeConnections.size(); i++) {
        connectionObject conObj = (connectionObject) freeConnections.get(i);
        if (!isrelease && con == conObj.con) {
          conObj.release();
          isrelease = true;
        }
        //判断正在使用的连接否超时
        if (this.connectionTimeOut > 0 && conObj.isUsing &&
            currentTime > (conObj.recentUseTime + this.connectionTimeOut)) {
          conObj.close();
          //log("连接池：" + this.name + "删除一个超时占用的连接！");
        }
      }
      if (isrelease) {
        notifyAll();
      }
      else {
        try {
          con.close();
          //log("关闭了一个不由数据库连接池创建的连接！");
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      }
    }

    /**
     * 从连接池获得一个可用连接.如没有空闲的连接且当前连接数小于最大连接
     * 数限制,则创建新连接.如原来登记为可用的连接不再有效,则从向量删除之,
     * 然后递归调用自己以尝试新的可用连接.
     * @return conn 返回连接
     */
    public synchronized Connection getConnection() {
      Connection con = null;
      for (int i = 0; i < freeConnections.size(); i++) {
        connectionObject conObj = (connectionObject) freeConnections.get(i);
        if (conObj.isUsing == false) {
          //判断调用次数是否超过受限制的次数
          if (this.connectionCount > 0 &&
              conObj.loadCount > this.connectionCount) {
            conObj.close();
          }
          con = conObj.getCon();
          if (con == null) {
            freeConnections.remove(i);
            i--;
          }
          else {
            break;
          }
        }
      }
      if (con == null && freeConnections.size() < maxConn) {
        connectionObject conObj = new connectionObject(newConnection());
        freeConnections.add(conObj);
        con = conObj.getCon();
      }

      return con;
    }

    /**
     * 从连接池获取可用连接.可以指定客户程序能够等待的最长时间
     * 参见前一个getConnection()方法.
     *
     * @param timeout 以毫秒计的等待时间限制
     * @return con 返回连接
     */
    public synchronized Connection getConnection(long timeout) {
      long startTime = new Date().getTime();
      Connection con;
      while ( (con = getConnection()) == null) {
        try {
          wait(timeout);
        }
        catch (InterruptedException e) {}
        if ( (new Date().getTime() - startTime) >= timeout) {
          // wait()返回的原因是超时
          return null;
        }
      }
      return con;
    }

    /**
     * 关闭所有连接
     */

    public synchronized void release() {
      Enumeration allConnections = freeConnections.elements();
      while (allConnections.hasMoreElements()) {
        Connection con = (Connection) allConnections.nextElement();
        try {
          con.close();
          log("关闭连接池" + name + "中的一个连接");
        }
        catch (SQLException e) {
          log(e, "无法关闭连接池" + name + "中的连接");
        }
      }
      freeConnections.removeAllElements();
    }

    /**
     * 创建新的连接
     * @return con 返回连接
     */
    private Connection newConnection() {
      Connection con = null;
      char a = 34;
      try {
        if (user == null) {
          con = DriverManager.getConnection(URL);
        }
        else {
          con = DriverManager.getConnection(URL, user, password);
        }
        //log("连接池" + name + "创建一个新的连接");
      }
      catch (SQLException e) {
        log(e, "无法创建下列URL的连接: " + URL);
        return null;
      }
      return con;
    }
  }

  class connectionObject {
    private Connection con = null;
    private int loadCount = 0; //调用次数
    private boolean isUsing = false; //连接是否已经被锁定
    private int releaseCount = 0; //连接释放次数
    private long recentUseTime = 0; //最近一次调用的时间
    public connectionObject(Connection con) {
      this.con = con;
    }

    //获取连接
    public Connection getCon() {
      if (this.con == null) {
        return null;
      }
      try {
        if (this.con.isClosed()) {
          this.con = null;
          return null;
        }
      }
      catch (Exception ex) {
        this.con = null;
        return null;
      }
      this.loadCount++;
      this.recentUseTime = System.currentTimeMillis();
      this.isUsing = true;
      return this.con;
    }

    //释放连接
    public void release() {
      if (this.loadCount == this.releaseCount + 1) {
        this.isUsing = false;
        this.releaseCount++;
      }
    }

    //关闭连接
    public void close() {
      try {
        this.con.close();
        this.con = null;
      }
      catch (SQLException ex) {
        ex.printStackTrace();
      }
    }

  }
  public static void Main(){
	DBConnection connection = new DBConnection();
  }
  
}