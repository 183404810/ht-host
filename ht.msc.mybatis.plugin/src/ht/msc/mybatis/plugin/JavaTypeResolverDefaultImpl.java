package ht.msc.mybatis.plugin;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class JavaTypeResolverDefaultImpl implements JavaTypeResolver
{
	protected List<String> warnings;
	protected Properties properties;
	protected Context context;
	protected boolean forceBigDecimals;
	protected Map<Integer, JdbcTypeInformation> typeMap;
	
	public JavaTypeResolverDefaultImpl()
	{
	  this.properties = new Properties();
	  this.typeMap = new HashMap();
	
	  this.typeMap.put(Integer.valueOf(2003), new JdbcTypeInformation("ARRAY", 
	    new FullyQualifiedJavaType(Object.class.getName())));
	  this.typeMap.put(Integer.valueOf(-5), new JdbcTypeInformation("BIGINT", 
	    new FullyQualifiedJavaType(Long.class.getName())));
	  this.typeMap.put(Integer.valueOf(-2), new JdbcTypeInformation("BINARY", 
	    new FullyQualifiedJavaType("byte[]")));
	  this.typeMap.put(Integer.valueOf(-7), new JdbcTypeInformation("BIT", 
	    new FullyQualifiedJavaType(Boolean.class.getName())));
	  this.typeMap.put(Integer.valueOf(2004), new JdbcTypeInformation("BLOB", 
	    new FullyQualifiedJavaType("byte[]")));
	  this.typeMap.put(Integer.valueOf(16), new JdbcTypeInformation("BOOLEAN", 
	    new FullyQualifiedJavaType(Boolean.class.getName())));
	  this.typeMap.put(Integer.valueOf(1), new JdbcTypeInformation("CHAR", 
	    new FullyQualifiedJavaType(String.class.getName())));
	  this.typeMap.put(Integer.valueOf(2005), new JdbcTypeInformation("CLOB", 
	    new FullyQualifiedJavaType(String.class.getName())));
	  this.typeMap.put(Integer.valueOf(70), new JdbcTypeInformation("DATALINK", 
	    new FullyQualifiedJavaType(Object.class.getName())));
	  this.typeMap.put(Integer.valueOf(91), new JdbcTypeInformation("DATE", 
	    new FullyQualifiedJavaType(Date.class.getName())));
	  this.typeMap.put(Integer.valueOf(2001), new JdbcTypeInformation("DISTINCT", 
	    new FullyQualifiedJavaType(Object.class.getName())));
	  this.typeMap.put(Integer.valueOf(8), new JdbcTypeInformation("DOUBLE", 
	    new FullyQualifiedJavaType(Double.class.getName())));
	  this.typeMap.put(Integer.valueOf(6), new JdbcTypeInformation("FLOAT", 
	    new FullyQualifiedJavaType(Double.class.getName())));
	  this.typeMap.put(Integer.valueOf(4), new JdbcTypeInformation("INTEGER", 
	    new FullyQualifiedJavaType(Integer.class.getName())));
	  this.typeMap.put(Integer.valueOf(2000), new JdbcTypeInformation("JAVA_OBJECT", 
	    new FullyQualifiedJavaType(Object.class.getName())));
	  this.typeMap.put(Integer.valueOf(-16), new JdbcTypeInformation("LONGNVARCHAR", 
	    new FullyQualifiedJavaType(String.class.getName())));
	  this.typeMap.put(Integer.valueOf(-4), new JdbcTypeInformation(
	    "LONGVARBINARY", 
	    new FullyQualifiedJavaType("byte[]")));
	  this.typeMap.put(Integer.valueOf(-1), new JdbcTypeInformation("LONGVARCHAR", 
	    new FullyQualifiedJavaType(String.class.getName())));
	  this.typeMap.put(Integer.valueOf(-15), new JdbcTypeInformation("NCHAR", 
	    new FullyQualifiedJavaType(String.class.getName())));
	  this.typeMap.put(Integer.valueOf(2011), new JdbcTypeInformation("NCLOB", 
	    new FullyQualifiedJavaType(String.class.getName())));
	  this.typeMap.put(Integer.valueOf(-9), new JdbcTypeInformation("NVARCHAR", 
	    new FullyQualifiedJavaType(String.class.getName())));
	  this.typeMap.put(Integer.valueOf(0), new JdbcTypeInformation("NULL", 
	    new FullyQualifiedJavaType(Object.class.getName())));
	  this.typeMap.put(Integer.valueOf(1111), new JdbcTypeInformation("OTHER", 
	    new FullyQualifiedJavaType(Object.class.getName())));
	  this.typeMap.put(Integer.valueOf(7), new JdbcTypeInformation("REAL", 
	    new FullyQualifiedJavaType(Float.class.getName())));
	  this.typeMap.put(Integer.valueOf(2006), new JdbcTypeInformation("REF", 
	    new FullyQualifiedJavaType(Object.class.getName())));
	  this.typeMap.put(Integer.valueOf(5), new JdbcTypeInformation("SMALLINT", 
	    new FullyQualifiedJavaType(Short.class.getName())));
	  this.typeMap.put(Integer.valueOf(2002), new JdbcTypeInformation("STRUCT", 
	    new FullyQualifiedJavaType(Object.class.getName())));
	  this.typeMap.put(Integer.valueOf(92), new JdbcTypeInformation("TIME", 
	    new FullyQualifiedJavaType(Date.class.getName())));
	  this.typeMap.put(Integer.valueOf(93), new JdbcTypeInformation("TIMESTAMP", 
	    new FullyQualifiedJavaType(Date.class.getName())));
	  this.typeMap.put(Integer.valueOf(-6), new JdbcTypeInformation("TINYINT", 
	    new FullyQualifiedJavaType(Byte.class.getName())));
	  this.typeMap.put(Integer.valueOf(-3), new JdbcTypeInformation("VARBINARY", 
	    new FullyQualifiedJavaType("byte[]")));
	  this.typeMap.put(Integer.valueOf(12), new JdbcTypeInformation("VARCHAR", 
	    new FullyQualifiedJavaType(String.class.getName())));
	}
	
	public void addConfigurationProperties(Properties properties)
	{
	  this.properties.putAll(properties);
	  this.forceBigDecimals = 
	    StringUtility.isTrue(properties
	    .getProperty("forceBigDecimals"));
	}
	
	public FullyQualifiedJavaType calculateJavaType(IntrospectedColumn introspectedColumn)
	{
	  JdbcTypeInformation jdbcTypeInformation = 
	    (JdbcTypeInformation)this.typeMap
	    .get(Integer.valueOf(introspectedColumn.getJdbcType()));
	  FullyQualifiedJavaType answer;
	  if (jdbcTypeInformation == null)
	  {
	    switch (introspectedColumn.getJdbcType())
	    {
	    case 2:
	    case 3:
	      if ((introspectedColumn.getScale() > 0) || 
	        (introspectedColumn.getLength() > 18) || 
	        (this.forceBigDecimals)) {
	        answer = new FullyQualifiedJavaType(BigDecimal.class
	          .getName());
	      }
	      else
	      {
	        if (introspectedColumn.getLength() > 9) {
	          answer = new FullyQualifiedJavaType(Long.class.getName());
	        }
	        else
	        {
	          if (introspectedColumn.getLength() > 4)
	            answer = new FullyQualifiedJavaType(Integer.class.getName());
	          else
	            answer = new FullyQualifiedJavaType(Short.class.getName()); 
	        }
	      }
	      break;
	    default:
	      answer = null;
	      break;
	    }
	  } else {
	    answer = jdbcTypeInformation.getFullyQualifiedJavaType();
	  }
	
	  return answer;
	}
	
	public String calculateJdbcTypeName(IntrospectedColumn introspectedColumn)
	{
	  JdbcTypeInformation jdbcTypeInformation = 
	    (JdbcTypeInformation)this.typeMap
	    .get(Integer.valueOf(introspectedColumn.getJdbcType()));
	  String answer;
	  if (jdbcTypeInformation == null)
	  {
	    switch (introspectedColumn.getJdbcType()) {
	    case 3:
	      answer = "DECIMAL";
	      break;
	    case 2:
	      answer = "NUMERIC";
	      break;
	    default:
	      answer = null;
	      break;
	    }
	  } else {
	    answer = jdbcTypeInformation.getJdbcTypeName();
	  }
	
	  return answer;
	}
	
	public void setWarnings(List<String> warnings) {
	  this.warnings = warnings;
	}
	
	public void setContext(Context context) {
	  this.context = context;
	}
	
	public static class JdbcTypeInformation
	{
	  private String jdbcTypeName;
	  private FullyQualifiedJavaType fullyQualifiedJavaType;
	
	  public JdbcTypeInformation(String jdbcTypeName, FullyQualifiedJavaType fullyQualifiedJavaType) {
	    this.jdbcTypeName = jdbcTypeName;
	    this.fullyQualifiedJavaType = fullyQualifiedJavaType;
	  }
	
	  public String getJdbcTypeName() {
	    return this.jdbcTypeName;
	  }
	
	  public FullyQualifiedJavaType getFullyQualifiedJavaType() {
	    return this.fullyQualifiedJavaType;
	  }
	}
}