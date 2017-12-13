package ht.msc.mybatis.plugin;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages
{
 
private static final String BUNDLE_NAME = "ht.msc.mybatis.plugin.messages";
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("ht.msc.mybatis.plugin.messages");
  public static String getString(String key)
  {
    try
    {
      return RESOURCE_BUNDLE.getString(key); } catch (MissingResourceException localMissingResourceException) {
    }
    return '!' + key + '!';
  }
  /*public static void main(String args[]){
	  //ResourceBundle.getBundle("java.lang.String");
	  ResourceBundle myResources = ResourceBundle.getBundle("java.lang.String");
 
	 // Messages.getString("Progress.0");
  }*/
  public static String getString(String key, String parm1)
  {
    try {
      return MessageFormat.format(RESOURCE_BUNDLE.getString(key), 
        new Object[] { parm1 }); } catch (MissingResourceException localMissingResourceException) {
    }
    return '!' + key + '!';
  }

  public static String getString(String key, String parm1, String parm2)
  {
    try {
      return MessageFormat.format(RESOURCE_BUNDLE.getString(key), 
        new Object[] { parm1, parm2 }); } catch (MissingResourceException localMissingResourceException) {
    }
    return '!' + key + '!';
  }

  public static String getString(String key, String parm1, String parm2, String parm3)
  {
    try
    {
      return MessageFormat.format(RESOURCE_BUNDLE.getString(key), 
        new Object[] { parm1, parm2, parm3 }); } catch (MissingResourceException localMissingResourceException) {
    }
    return '!' + key + '!';
  }
}