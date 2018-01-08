package ht.msc.mybatis.plugin;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ObjectFactory
{
  private static List<ClassLoader> externalClassLoaders = new ArrayList();
  private static List<ClassLoader> resourceClassLoaders = new ArrayList();

  public static synchronized void addResourceClassLoader(ClassLoader classLoader)
  {
    resourceClassLoaders.add(classLoader);
  }

  public static synchronized void addExternalClassLoader(ClassLoader classLoader)
  {
    externalClassLoaders.add(classLoader);
  }

  public static Class<?> externalClassForName(String type)
    throws ClassNotFoundException
  {
    for (ClassLoader classLoader : externalClassLoaders) {
      try {
        return Class.forName(type, true, classLoader);
      }
      catch (Throwable localThrowable)
      {
      }

    }

    return internalClassForName(type);
  }

  public static Object createExternalObject(String type)
  {
	Object answer;
    try
    {
      Class clazz = externalClassForName(type);
      answer = clazz.newInstance();
    }
    catch (Exception e)
    {
      throw new RuntimeException(Messages.getString(
        "RuntimeError.6", type), e);
    }
    
    return answer;
  }

  public static Class<?> internalClassForName(String type) throws ClassNotFoundException
  {
    Class clazz = null;
    try
    {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      clazz = Class.forName(type, true, cl);
    }
    catch (Exception localException)
    {
    }
    if (clazz == null) {
      clazz = Class.forName(type, true, ObjectFactory.class.getClassLoader());
    }

    return clazz;
  }

  public static URL getResource(String resource)
  {
    for (ClassLoader classLoader : resourceClassLoaders) {
      URL url = classLoader.getResource(resource);
      if (url != null) {
        return url;
      }
    }

    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    URL url = cl.getResource(resource);

    if (url == null) {
      url = ObjectFactory.class.getClassLoader().getResource(resource);
    }

    return url;
  }

  public static Object createInternalObject(String type)
  {
	Object answer;
    try
    {
      Class clazz = internalClassForName(type);
      answer = clazz.newInstance();
    }
    catch (Exception e)
    {
      throw new RuntimeException(Messages.getString(
        "RuntimeError.6", type), e);
    }
    return answer;
  }

  public static JavaTypeResolver createJavaTypeResolver(Context context, List<String> warnings)
  {
    JavaTypeResolverConfiguration config = context
      .getJavaTypeResolverConfiguration();
    String type;
    if ((config != null) && (config.getConfigurationType() != null)) {
      type = config.getConfigurationType();
      if ("DEFAULT".equalsIgnoreCase(type))
        type = JavaTypeResolverDefaultImpl.class.getName();
    }
    else {
      type = JavaTypeResolverDefaultImpl.class.getName();
    }

    JavaTypeResolver answer = (JavaTypeResolver)createInternalObject(type);
    answer.setWarnings(warnings);

    if (config != null) {
      answer.addConfigurationProperties(config.getProperties());
    }

    answer.setContext(context);

    return answer;
  }

  public static Plugin createPlugin(Context context, PluginConfiguration pluginConfiguration)
  {
    Plugin plugin = (Plugin)createInternalObject(pluginConfiguration
      .getConfigurationType());
    plugin.setContext(context);
    plugin.setProperties(pluginConfiguration.getProperties());
    return plugin;
  }

  public static CommentGenerator createCommentGenerator(Context context)
  {
    CommentGeneratorConfiguration config = context
      .getCommentGeneratorConfiguration();
    String type;
    if ((config == null) || (config.getConfigurationType() == null))
    {
      type = YougouCommentGenerator.class.getName();
    }
    else type = config.getConfigurationType();

    CommentGenerator answer = (CommentGenerator)createInternalObject(type);

    if (config != null) {
      answer.addConfigurationProperties(config.getProperties());
    }

    return answer;
  }

  public static JavaFormatter createJavaFormatter(Context context) {
    String type = context.getProperty("javaFormatter");
    if (!StringUtility.stringHasValue(type)) {
      type = DefaultJavaFormatter.class.getName();
    }

    JavaFormatter answer = (JavaFormatter)createInternalObject(type);

    answer.setContext(context);

    return answer;
  }

  public static JSFormatter createJSFormatter(Context context) {
    String type = context.getProperty("jsFormatter");
    if (!StringUtility.stringHasValue(type)) {
      type = DefaultJSFormatter.class.getName();
    }

    JSFormatter answer = (JSFormatter)createInternalObject(type);

    answer.setContext(context);

    return answer;
  }

  public static XmlFormatter createXmlFormatter(Context context) {
    String type = context.getProperty("xmlFormatter");
    if (!StringUtility.stringHasValue(type)) {
      type = DefaultXmlFormatter.class.getName();
    }

    XmlFormatter answer = (XmlFormatter)createInternalObject(type);

    answer.setContext(context);

    return answer;
  }

  public static IntrospectedTable createIntrospectedTable(TableConfiguration tableConfiguration, FullyQualifiedTable table, Context context)
  {
    IntrospectedTable answer = createIntrospectedTableForValidation(context);
    answer.setFullyQualifiedTable(table);
    answer.setTableConfiguration(tableConfiguration);

    return answer;
  }

  public static IntrospectedTable createIntrospectedTableForValidation(Context context)
  {
    String type = context.getTargetRuntime();
    if (!StringUtility.stringHasValue(type))
      type = IntrospectedTableMyBatis3Impl.class.getName();
    else if ("Ibatis2Java2".equalsIgnoreCase(type))
      type = IntrospectedTableIbatis2Java2Impl.class.getName();
    else if ("Ibatis2Java5".equalsIgnoreCase(type))
      type = IntrospectedTableIbatis2Java5Impl.class.getName();
    else if ("Ibatis3".equalsIgnoreCase(type))
      type = IntrospectedTableMyBatis3Impl.class.getName();
    else if ("MyBatis3".equalsIgnoreCase(type))
      type = IntrospectedTableMyBatis3Impl.class.getName();
    else if ("MyBatis3Simple".equalsIgnoreCase(type)) {
      type = IntrospectedTableMyBatis3SimpleImpl.class.getName();
    }

    IntrospectedTable answer = (IntrospectedTable)createInternalObject(type);
    answer.setContext(context);

    return answer;
  }

  public static IntrospectedColumn createIntrospectedColumn(Context context) {
    String type = context.getIntrospectedColumnImpl();
    if (!StringUtility.stringHasValue(type)) {
      type = IntrospectedColumn.class.getName();
    }

    IntrospectedColumn answer = (IntrospectedColumn)createInternalObject(type);
    answer.setContext(context);

    return answer;
  }
}