package ht.msc.mybatis.plugin;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;


public class ConnectionFactory
{
  private static ConnectionFactory instance = new ConnectionFactory();

  public static ConnectionFactory getInstance() {
    return instance;
  }

  public Connection getConnection(JDBCConnectionConfiguration config) throws SQLException
  {
    Driver driver = getDriver(config);

    Properties props = new Properties();

    props.put("remarksReporting", "true");

    if (StringUtility.stringHasValue(config.getUserId())) {
      props.setProperty("user", config.getUserId());
    }

    if (StringUtility.stringHasValue(config.getPassword())) {
      props.setProperty("password", config.getPassword());
    }

    props.putAll(config.getProperties());

    Connection conn = driver.connect(config.getConnectionURL(), props);

    if (conn == null) {
      throw new SQLException(Messages.getString("RuntimeError.7"));
    }

    return conn;
  }

  private Driver getDriver(JDBCConnectionConfiguration connectionInformation) {
    String driverClass = connectionInformation.getDriverClass();
    Driver driver;
    try
    {
      Class clazz = ObjectFactory.externalClassForName(driverClass);
      driver = (Driver)clazz.newInstance();
    }
    catch (Exception e)
    {
      throw new RuntimeException(Messages.getString("RuntimeError.8"), e);
    }
    return driver;
  }
}