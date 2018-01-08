package ht.msc.mybatis.plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class ConnectionUtils
{
  private static String driver = "com.mysql.jdbc.Driver";
  private static String url = "jdbc:mysql://localhost:3306/stock?useUnicode=true&amp;characterEncoding=utf-8";
  private static String user = "root";
  private static String password = "123456";

  public static String getDriver() {
    return driver;
  }
  public static void setDriver(String driver) {
    driver = driver;
  }
  public static String getUrl() {
    return url;
  }
  public static void setUrl(String url) {
    url = url;
  }
  public static String getUser() {
    return user;
  }
  public static void setUser(String user) {
    user = user;
  }
  public static String getPassword() {
    return password;
  }
  public static void setPassword(String password) {
    password = password;
  }

  public static void initConnection(String driver, String url, String user, String password)
  {
    try
    {
      setDriver(driver);
      setUrl(url);
      setUser(user);
      setPassword(password);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static Connection getConnection(String driver, String url, String user, String password) {
    Connection conn = null;
    try {
      Class.forName(driver).newInstance();
      conn = DriverManager.getConnection(url, user, password);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return conn;
  }
  public static void closeConnection(Connection conn, Statement s, ResultSet rs) {
    try {
      if (rs != null) {
        rs.close();
      }
      if (s != null) {
        s.close();
      }
      if (conn != null)
        conn.close();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}