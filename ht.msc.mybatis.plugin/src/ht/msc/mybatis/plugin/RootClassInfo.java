package ht.msc.mybatis.plugin;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RootClassInfo
{
  private static Map<String, RootClassInfo> rootClassInfoMap = Collections.synchronizedMap(new HashMap());
  private PropertyDescriptor[] propertyDescriptors;
  private String className;
  private List<String> warnings;

  public static RootClassInfo getInstance(String className, List<String> warnings)
  {
    RootClassInfo classInfo = (RootClassInfo)rootClassInfoMap.get(className);
    if (classInfo == null) {
      classInfo = new RootClassInfo(className, warnings);
      rootClassInfoMap.put(className, classInfo);
    }

    return classInfo;
  }

  private RootClassInfo(String className, List<String> warnings)
  {
    this.className = className;
    this.warnings = warnings;

    if (className == null) {
      return;
    }
    try
    {
      Class clazz = ObjectFactory.externalClassForName(className);
      BeanInfo bi = Introspector.getBeanInfo(clazz);
      this.propertyDescriptors = bi.getPropertyDescriptors();
    } catch (Exception localException) {
      this.propertyDescriptors = null;
      warnings.add(Messages.getString("Warning.20", className));
    }
  }

  public boolean containsProperty(IntrospectedColumn introspectedColumn) {
    if (this.propertyDescriptors == null) {
      return false;
    }

    boolean found = false;
    String propertyName = introspectedColumn.getJavaProperty();
    String propertyType = introspectedColumn.getFullyQualifiedJavaType()
      .getFullyQualifiedName();

    for (int i = 0; i < this.propertyDescriptors.length; i++) {
      PropertyDescriptor propertyDescriptor = this.propertyDescriptors[i];

      if (propertyDescriptor.getName().equals(propertyName))
      {
        if (!propertyDescriptor.getPropertyType().getName().equals(
          propertyType)) {
          this.warnings.add(Messages.getString("Warning.21", 
            propertyName, this.className, propertyType));
          break;
        }

        if (propertyDescriptor.getReadMethod() == null) {
          this.warnings.add(Messages.getString("Warning.22", 
            propertyName, this.className));
          break;
        }

        if (propertyDescriptor.getWriteMethod() == null) {
          this.warnings.add(Messages.getString("Warning.23", 
            propertyName, this.className));
          break;
        }

        found = true;
        break;
      }
    }

    return found;
  }
}