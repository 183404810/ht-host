package ht.msc.mybatis.plugin;


public class LogFactory
{
  private static AbstractLogFactory logFactory;
  static
  {
    try
    {
      ObjectFactory.internalClassForName("org.apache.log4j.Logger");
      logFactory = new Log4jLoggingLogFactory();
    } catch (Exception localException) {
      logFactory = new JdkLoggingLogFactory();
    }
  }

  public static Log getLog(Class<?> clazz) {
    try {
      return logFactory.getLog(clazz);
    } catch (Throwable t) {
      throw new RuntimeException(Messages.getString("RuntimeError.21", 
        clazz.getName(), t.getMessage()), t);
    }
  }

  public static synchronized void forceJavaLogging()
  {
    logFactory = new JdkLoggingLogFactory();
  }

  public static void setLogFactory(AbstractLogFactory logFactory)
  {
    logFactory = logFactory;
  }

  private static class JdkLoggingLogFactory
    implements AbstractLogFactory
  {
    public Log getLog(Class<?> clazz)
    {
      return new JdkLoggingImpl(clazz);
    }
  }

  private static class Log4jLoggingLogFactory implements AbstractLogFactory {
    public Log getLog(Class<?> clazz) {
      return new Log4jImpl(clazz);
    }
  }
}