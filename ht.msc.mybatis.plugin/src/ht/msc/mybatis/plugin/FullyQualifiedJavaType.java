package ht.msc.mybatis.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class FullyQualifiedJavaType implements Comparable<FullyQualifiedJavaType>
{
	private static final String JAVA_LANG = "java.lang";
	private static FullyQualifiedJavaType intInstance = null;
	private static FullyQualifiedJavaType stringInstance = null;
	private static FullyQualifiedJavaType booleanPrimitiveInstance = null;
	private static FullyQualifiedJavaType objectInstance = null;
	private static FullyQualifiedJavaType dateInstance = null;
	private static FullyQualifiedJavaType criteriaInstance = null;
	private static FullyQualifiedJavaType generatedCriteriaInstance = null;
	private String baseShortName;
	private String baseQualifiedName;
	private boolean explicitlyImported;
	private String packageName;
	private boolean primitive;
	private PrimitiveTypeWrapper primitiveTypeWrapper;
	private List<FullyQualifiedJavaType> typeArguments;
	private boolean wildcardType;
	private boolean boundedWildcard;
	private boolean extendsBoundedWildcard;
	
	public FullyQualifiedJavaType(String fullTypeSpecification)
	{
	  this.typeArguments = new ArrayList();
	  parse(fullTypeSpecification);
	}
	
	public boolean isExplicitlyImported()
	{
	  return this.explicitlyImported;
	}
	
	public String getFullyQualifiedName()
	{
	  StringBuilder sb = new StringBuilder();
	  if (this.wildcardType) {
	    sb.append('?');
	    if (this.boundedWildcard) {
	      if (this.extendsBoundedWildcard)
	        sb.append(" extends ");
	      else {
	        sb.append(" super ");
	      }
	
	      sb.append(this.baseQualifiedName);
	    }
	  } else {
	    sb.append(this.baseQualifiedName);
	  }
	
	  if (this.typeArguments.size() > 0) {
	    boolean first = true;
	    sb.append('<');
	    for (FullyQualifiedJavaType fqjt : this.typeArguments) {
	      if (first)
	        first = false;
	      else {
	        sb.append(", ");
	      }
	      sb.append(fqjt.getFullyQualifiedName());
	    }
	
	    sb.append('>');
	  }
	
	  return sb.toString();
	}
	
	public List<String> getImportList()
	{
	  List answer = new ArrayList();
	  StringBuilder sb;
	  if (isExplicitlyImported()) {
	    int index = this.baseShortName.indexOf('.');
	    if (index == -1) {
	      answer.add(this.baseQualifiedName);
	    }
	    else
	    {
	      sb = new StringBuilder();
	      sb.append(this.packageName);
	      sb.append('.');
	      sb.append(this.baseShortName.substring(0, index));
	      answer.add(sb.toString());
	    }
	  }
	
	  for (FullyQualifiedJavaType fqjt : this.typeArguments) {
	    answer.addAll(fqjt.getImportList());
	  }
	
	  return answer;
	}
	
	public String getPackageName()
	{
	  return this.packageName;
	}
	
	public String getShortName()
	{
	  StringBuilder sb = new StringBuilder();
	  if (this.wildcardType) {
	    sb.append('?');
	    if (this.boundedWildcard) {
	      if (this.extendsBoundedWildcard)
	        sb.append(" extends ");
	      else {
	        sb.append(" super ");
	      }
	
	      sb.append(this.baseShortName);
	    }
	  } else {
	    sb.append(this.baseShortName);
	  }
	
	  if (this.typeArguments.size() > 0) {
	    boolean first = true;
	    sb.append('<');
	    for (FullyQualifiedJavaType fqjt : this.typeArguments) {
	      if (first)
	        first = false;
	      else {
	        sb.append(", ");
	      }
	      sb.append(fqjt.getShortName());
	    }
	
	    sb.append('>');
	  }
	
	  return sb.toString();
	}
	
	public boolean equals(Object obj)
	{
	  if (this == obj) {
	    return true;
	  }
	
	  if (!(obj instanceof FullyQualifiedJavaType)) {
	    return false;
	  }
	
	  FullyQualifiedJavaType other = (FullyQualifiedJavaType)obj;
	
	  return getFullyQualifiedName().equals(other.getFullyQualifiedName());
	}
	
	public int hashCode()
	{
	  return getFullyQualifiedName().hashCode();
	}
	
	public String toString()
	{
	  return getFullyQualifiedName();
	}
	
	public boolean isPrimitive()
	{
	  return this.primitive;
	}
	
	public PrimitiveTypeWrapper getPrimitiveTypeWrapper()
	{
	  return this.primitiveTypeWrapper;
	}
	
	public static final FullyQualifiedJavaType getIntInstance() {
	  if (intInstance == null) {
	    intInstance = new FullyQualifiedJavaType("int");
	  }
	
	  return intInstance;
	}
	
	public static final FullyQualifiedJavaType getNewMapInstance()
	{
	  return new FullyQualifiedJavaType("java.util.Map");
	}
	
	public static final FullyQualifiedJavaType getNewListInstance()
	{
	  return new FullyQualifiedJavaType("java.util.List");
	}
	
	public static final FullyQualifiedJavaType getNewHashMapInstance()
	{
	  return new FullyQualifiedJavaType("java.util.HashMap");
	}
	
	public static final FullyQualifiedJavaType getNewArrayListInstance()
	{
	  return new FullyQualifiedJavaType("java.util.ArrayList");
	}
	
	public static final FullyQualifiedJavaType getNewIteratorInstance()
	{
	  return new FullyQualifiedJavaType("java.util.Iterator");
	}
	
	public static final FullyQualifiedJavaType getAnnotateParam()
	{
	  return new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param");
	}
	
	public static final FullyQualifiedJavaType getStringInstance() {
	  if (stringInstance == null) {
	    stringInstance = new FullyQualifiedJavaType("java.lang.String");
	  }
	
	  return stringInstance;
	}
	
	public static final FullyQualifiedJavaType getBooleanPrimitiveInstance() {
	  if (booleanPrimitiveInstance == null) {
	    booleanPrimitiveInstance = new FullyQualifiedJavaType("boolean");
	  }
	
	  return booleanPrimitiveInstance;
	}
	
	public static final FullyQualifiedJavaType getObjectInstance() {
	  if (objectInstance == null) {
	    objectInstance = new FullyQualifiedJavaType("java.lang.Object");
	  }
	
	  return objectInstance;
	}
	
	public static final FullyQualifiedJavaType getDateInstance() {
	  if (dateInstance == null) {
	    dateInstance = new FullyQualifiedJavaType("java.util.Date");
	  }
	
	  return dateInstance;
	}
	
	public static final FullyQualifiedJavaType getCriteriaInstance() {
	  if (criteriaInstance == null) {
	    criteriaInstance = new FullyQualifiedJavaType("Criteria");
	  }
	
	  return criteriaInstance;
	}
	
	public static final FullyQualifiedJavaType getGeneratedCriteriaInstance() {
	  if (generatedCriteriaInstance == null) {
	    generatedCriteriaInstance = new FullyQualifiedJavaType(
	      "GeneratedCriteria");
	  }
	
	  return generatedCriteriaInstance;
	}
	
	public int compareTo(FullyQualifiedJavaType other)
	{
	  return getFullyQualifiedName().compareTo(other.getFullyQualifiedName());
	}
	
	public void addTypeArgument(FullyQualifiedJavaType type) {
	  this.typeArguments.add(type);
	}
	
	private void parse(String fullTypeSpecification) {
	  String spec = fullTypeSpecification.trim();
	
	  if (spec.startsWith("?")) {
	    this.wildcardType = true;
	    spec = spec.substring(1).trim();
	    if (spec.startsWith("extends ")) {
	      this.boundedWildcard = true;
	      this.extendsBoundedWildcard = true;
	      spec = spec.substring(8);
	    } else if (spec.startsWith("super ")) {
	      this.boundedWildcard = true;
	      this.extendsBoundedWildcard = false;
	      spec = spec.substring(6);
	    } else {
	      this.boundedWildcard = false;
	    }
	    parse(spec);
	  } else {
	    int index = fullTypeSpecification.indexOf('<');
	    if (index == -1) {
	      simpleParse(fullTypeSpecification);
	    } else {
	      simpleParse(fullTypeSpecification.substring(0, index));
	      genericParse(fullTypeSpecification.substring(index));
	    }
	  }
	}
	
	private void simpleParse(String typeSpecification) {
	  this.baseQualifiedName = typeSpecification.trim();
	  if (this.baseQualifiedName.contains(".")) {
	    this.packageName = getPackage(this.baseQualifiedName);
	    this.baseShortName = this.baseQualifiedName
	      .substring(this.packageName.length() + 1);
	    int index = this.baseShortName.lastIndexOf('.');
	    if (index != -1) {
	      this.baseShortName = this.baseShortName.substring(index + 1);
	    }
	
	    if ("java.lang".equals(this.packageName))
	      this.explicitlyImported = false;
	    else
	      this.explicitlyImported = true;
	  }
	  else {
	    this.baseShortName = this.baseQualifiedName;
	    this.explicitlyImported = false;
	    this.packageName = "";
	
	    if ("byte".equals(this.baseQualifiedName)) {
	      this.primitive = true;
	      this.primitiveTypeWrapper = PrimitiveTypeWrapper.getByteInstance();
	    } else if ("short".equals(this.baseQualifiedName)) {
	      this.primitive = true;
	      this.primitiveTypeWrapper = PrimitiveTypeWrapper.getShortInstance();
	    } else if ("int".equals(this.baseQualifiedName)) {
	      this.primitive = true;
	      this.primitiveTypeWrapper = 
	        PrimitiveTypeWrapper.getIntegerInstance();
	    } else if ("long".equals(this.baseQualifiedName)) {
	      this.primitive = true;
	      this.primitiveTypeWrapper = PrimitiveTypeWrapper.getLongInstance();
	    } else if ("char".equals(this.baseQualifiedName)) {
	      this.primitive = true;
	      this.primitiveTypeWrapper = 
	        PrimitiveTypeWrapper.getCharacterInstance();
	    } else if ("float".equals(this.baseQualifiedName)) {
	      this.primitive = true;
	      this.primitiveTypeWrapper = PrimitiveTypeWrapper.getFloatInstance();
	    } else if ("double".equals(this.baseQualifiedName)) {
	      this.primitive = true;
	      this.primitiveTypeWrapper = PrimitiveTypeWrapper.getDoubleInstance();
	    } else if ("boolean".equals(this.baseQualifiedName)) {
	      this.primitive = true;
	      this.primitiveTypeWrapper = 
	        PrimitiveTypeWrapper.getBooleanInstance();
	    } else {
	      this.primitive = false;
	      this.primitiveTypeWrapper = null;
	    }
	  }
	}
	
	private void genericParse(String genericSpecification) {
	  int lastIndex = genericSpecification.lastIndexOf('>');
	  if (lastIndex == -1) {
	    throw new RuntimeException(Messages.getString(
	      "RuntimeError.22", genericSpecification));
	  }
	  String argumentString = genericSpecification.substring(1, lastIndex);
	
	  StringTokenizer st = new StringTokenizer(argumentString, ",<>", true);
	  int openCount = 0;
	  StringBuilder sb = new StringBuilder();
	  while (st.hasMoreTokens()) {
	    String token = st.nextToken();
	    if ("<".equals(token)) {
	      sb.append(token);
	      openCount++;
	    } else if (">".equals(token)) {
	      sb.append(token);
	      openCount--;
	    } else if (",".equals(token)) {
	      if (openCount == 0) {
	        this.typeArguments
	          .add(new FullyQualifiedJavaType(sb.toString()));
	        sb.setLength(0);
	      } else {
	        sb.append(token);
	      }
	    } else {
	      sb.append(token);
	    }
	  }
	
	  if (openCount != 0) {
	    throw new RuntimeException(Messages.getString(
	      "RuntimeError.22", genericSpecification));
	  }
	
	  String finalType = sb.toString();
	  if (StringUtility.stringHasValue(finalType))
	    this.typeArguments.add(new FullyQualifiedJavaType(finalType));
	}
	
	private static String getPackage(String baseQualifiedName)
	{
	  int index = baseQualifiedName.lastIndexOf('.');
	  return baseQualifiedName.substring(0, index);
	}
}