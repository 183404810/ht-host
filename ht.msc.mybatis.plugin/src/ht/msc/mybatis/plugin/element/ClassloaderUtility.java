package ht.msc.mybatis.plugin.element;

import ht.msc.mybatis.plugin.Messages;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;


public class ClassloaderUtility
{
  public static ClassLoader getCustomClassloader(List<String> entries)
  {
    List urls = new ArrayList();

    if (entries != null) {
      for (String classPathEntry : entries) {
        File file = new File(classPathEntry);
        if (!file.exists()) {
          throw new RuntimeException(Messages.getString(
            "RuntimeError.9", classPathEntry));
        }
        try
        {
          urls.add(file.toURI().toURL());
        }
        catch (MalformedURLException localMalformedURLException) {
          throw new RuntimeException(Messages.getString(
            "RuntimeError.9", classPathEntry));
        }
      }
    }

    ClassLoader parent = Thread.currentThread().getContextClassLoader();

    URLClassLoader ucl = new URLClassLoader((URL[])urls.toArray(
      new URL[urls
      .size()]), parent);

    return ucl;
  }
}