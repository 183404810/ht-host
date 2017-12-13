package ht.msc.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import org.apache.commons.beanutils.BeanUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class MongoDBUtil {

    /**
     * 把实体bean对象转换成DBObject
     * @param bean
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static <T> DBObject bean2DBObject(T bean) throws IllegalArgumentException,
        IllegalAccessException {
      if (bean == null) {
        return null;
      }
      DBObject dbObject = new BasicDBObject();
      // 获取对象对应类中的所有属性域
      Field[] fields = bean.getClass().getDeclaredFields();
      
      // 获取所有注解
      for (Field field : fields) {
          org.springframework.data.mongodb.core.mapping.Field anno = field.getAnnotation(org.springframework.data.mongodb.core.mapping.Field.class);
        // 获取属性名
        String varName = field.getName();
        
        if("serialVersionUID".equals(varName) || "_id".equals(varName)){
            continue;
        }
        
        // 获取注解的值
        varName = anno.value();
        // 修改访问控制权限
        boolean accessFlag = field.isAccessible();
        if (!accessFlag) {
          field.setAccessible(true);
        }
        Object param = field.get(bean);
        if (param == null) {
          continue;
        } else if (param instanceof Integer) {//判断变量的类型
          int value = ((Integer) param).intValue();
          dbObject.put(varName, value);
        } else if (param instanceof String) {
          String value = (String) param;
          dbObject.put(varName, value);
        } else if (param instanceof Double) {
          double value = ((Double) param).doubleValue();
          dbObject.put(varName, value);
        } else if (param instanceof Float) {
          float value = ((Float) param).floatValue();
          dbObject.put(varName, value);
        } else if (param instanceof Long) {
          long value = ((Long) param).longValue();
          dbObject.put(varName, value);
        } else if (param instanceof Boolean) {
          boolean value = ((Boolean) param).booleanValue();
          dbObject.put(varName, value);
        } else if (param instanceof Date) {
          Date value = (Date) param;
          dbObject.put(varName, value);
        }
        // 恢复访问控制权限
        field.setAccessible(accessFlag);
      }
      
      return dbObject;
    }

    /**
     * 把DBObject转换成bean对象
     * @param dbObject
     * @param bean
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    public static <T> T dbObject2Bean(DBObject dbObject, T bean) throws IllegalAccessException,
        InvocationTargetException, NoSuchMethodException {
      if (bean == null) {
        return null;
      }
      Field[] fields = bean.getClass().getDeclaredFields();
      for (Field field : fields) {
        String varName = field.getName();
        org.springframework.data.mongodb.core.mapping.Field anno = field.getAnnotation(org.springframework.data.mongodb.core.mapping.Field.class);
        if("serialVersionUID".equals(varName) || "_id".equals(varName)){
            continue;
        }
        
        String fieldName = anno.value();
        Object object = dbObject.get(fieldName);
        if (object != null) {
          BeanUtils.setProperty(bean, varName, object);
        }
      }
      return bean;
    }
}
