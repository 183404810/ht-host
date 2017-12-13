package ht.msc.mybatis.plugin;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;




public class JdkLoggingImpl implements Log
{
  private Logger log;

  public JdkLoggingImpl(Class<?> clazz)
  {
    this.log = Logger.getLogger(clazz.getName());
  }

  public boolean isDebugEnabled() {
    return this.log.isLoggable(Level.FINE);
  }

  public void error(String s, Throwable e) {
    LogRecord lr = new LogRecord(Level.SEVERE, s);
    lr.setSourceClassName(this.log.getName());
    lr.setThrown(e);

    this.log.log(lr);
  }

  public void error(String s) {
    LogRecord lr = new LogRecord(Level.SEVERE, s);
    lr.setSourceClassName(this.log.getName());

    this.log.log(lr);
  }

  public void debug(String s) {
    LogRecord lr = new LogRecord(Level.FINE, s);
    lr.setSourceClassName(this.log.getName());

    this.log.log(lr);
  }

  public void warn(String s) {
    LogRecord lr = new LogRecord(Level.WARNING, s);
    lr.setSourceClassName(this.log.getName());

    this.log.log(lr);
  }
}