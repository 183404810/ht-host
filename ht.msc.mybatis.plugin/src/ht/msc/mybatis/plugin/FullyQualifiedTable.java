package ht.msc.mybatis.plugin;

import java.util.HashMap;
import java.util.Map;

public class FullyQualifiedTable
{
  private String introspectedCatalog;
  private String introspectedSchema;
  private String introspectedTableName;
  private String runtimeCatalog;
  private String runtimeSchema;
  private String runtimeTableName;
  private String domainObjectName;
  private String domainObjectSubPackage;
  private String alias;
  private boolean ignoreQualifiersAtRuntime;
  private String beginningDelimiter;
  private String endingDelimiter;
  private boolean disableMapperGeneratorSchema;
  private HashMap<String, String> replaceTablePrefixMap;

  public FullyQualifiedTable(String introspectedCatalog, String introspectedSchema, String introspectedTableName, String domainObjectName, String alias, boolean ignoreQualifiersAtRuntime, String runtimeCatalog, String runtimeSchema, String runtimeTableName, boolean delimitIdentifiers, Context context)
  {
    this.introspectedCatalog = introspectedCatalog;
    this.introspectedSchema = introspectedSchema;
    this.introspectedTableName = introspectedTableName;
    this.domainObjectName = domainObjectName;
    this.ignoreQualifiersAtRuntime = ignoreQualifiersAtRuntime;
    this.runtimeCatalog = runtimeCatalog;
    this.runtimeSchema = runtimeSchema;
    this.runtimeTableName = runtimeTableName;

    if (StringUtility.stringHasValue(domainObjectName)) {
      int index = domainObjectName.lastIndexOf('.');
      if (index == -1) {
        this.domainObjectName = domainObjectName;
      } else {
        this.domainObjectName = domainObjectName.substring(index + 1);
        this.domainObjectSubPackage = domainObjectName.substring(0, index);
      }
    }

    if (alias == null)
      this.alias = null;
    else {
      this.alias = alias.trim();
    }

    this.beginningDelimiter = (delimitIdentifiers ? context
      .getBeginningDelimiter() : "");
    this.endingDelimiter = (delimitIdentifiers ? context.getEndingDelimiter() : 
      "");

    YouGouTableSettingConfiguration tableSettingConf = context.getYouGouTableSettingConfiguration();
    this.disableMapperGeneratorSchema = tableSettingConf.isIgnoreGeneratorSchema();
    this.replaceTablePrefixMap = tableSettingConf.getReplaceTablePrefixMap();
  }

  public String getIntrospectedCatalog() {
    return this.introspectedCatalog;
  }

  public String getIntrospectedSchema() {
    return this.introspectedSchema;
  }

  public String getIntrospectedTableName() {
    return this.introspectedTableName;
  }

  public String getFullyQualifiedTableNameAtRuntime()
  {
    StringBuilder localCatalog = new StringBuilder();
    if (!this.ignoreQualifiersAtRuntime) {
      if (StringUtility.stringHasValue(this.runtimeCatalog))
        localCatalog.append(this.runtimeCatalog);
      else if (StringUtility.stringHasValue(this.introspectedCatalog)) {
        localCatalog.append(this.introspectedCatalog);
      }
    }
    if (localCatalog.length() > 0) {
      addDelimiters(localCatalog);
    }

    StringBuilder localSchema = new StringBuilder();
    if (!this.ignoreQualifiersAtRuntime) {
      if (StringUtility.stringHasValue(this.runtimeSchema))
        localSchema.append(this.runtimeSchema);
      else if (StringUtility.stringHasValue(this.introspectedSchema)) {
        localSchema.append(this.introspectedSchema);
      }
    }
    if (localSchema.length() > 0) {
      addDelimiters(localSchema);
    }

    StringBuilder localTableName = new StringBuilder();
    if (StringUtility.stringHasValue(this.runtimeTableName))
      localTableName.append(this.runtimeTableName);
    else {
      localTableName.append(this.introspectedTableName);
    }
    addDelimiters(localTableName);

    if (this.disableMapperGeneratorSchema) {
      return localTableName.toString();
    }
    return StringUtility.composeFullyQualifiedTableName(localCatalog
      .toString(), localSchema.toString(), localTableName.toString(), 
      '.');
  }

  public String getYouGouFullyQualifiedTableNameAtRuntime()
  {
    StringBuilder localCatalog = new StringBuilder();
    if (!this.ignoreQualifiersAtRuntime) {
      if (StringUtility.stringHasValue(this.runtimeCatalog))
        localCatalog.append(this.runtimeCatalog);
      else if (StringUtility.stringHasValue(this.introspectedCatalog)) {
        localCatalog.append(this.introspectedCatalog);
      }
    }
    if (localCatalog.length() > 0) {
      addDelimiters(localCatalog);
    }

    StringBuilder localSchema = new StringBuilder();
    if (!this.ignoreQualifiersAtRuntime) {
      if (StringUtility.stringHasValue(this.runtimeSchema))
        localSchema.append(this.runtimeSchema);
      else if (StringUtility.stringHasValue(this.introspectedSchema)) {
        localSchema.append(this.introspectedSchema);
      }
    }
    if (localSchema.length() > 0) {
      addDelimiters(localSchema);
    }

    StringBuilder localTableName = new StringBuilder();
    localTableName.append(this.introspectedTableName);
    addDelimiters(localTableName);

    if (this.disableMapperGeneratorSchema) {
      return localTableName.toString();
    }
    return StringUtility.composeFullyQualifiedTableName(localCatalog
      .toString(), localSchema.toString(), localTableName.toString(), 
      '.');
  }

  public String getAliasedFullyQualifiedTableNameAtRuntime()
  {
    String name = getYouGouFullyQualifiedTableNameAtRuntime();
    StringBuilder sb1 = new StringBuilder();
    sb1.append(name);
    if (StringUtility.stringHasValue(this.alias)) {
      sb1.append(' ');
      sb1.append(this.alias);
    }

    return sb1.toString();
  }

  public String getIbatis2SqlMapNamespace()
  {
    String localCatalog = StringUtility.stringHasValue(this.runtimeCatalog) ? this.runtimeCatalog : 
      this.introspectedCatalog;
    String localSchema = StringUtility.stringHasValue(this.runtimeSchema) ? this.runtimeSchema : 
      this.introspectedSchema;
    String localTable = StringUtility.stringHasValue(this.runtimeTableName) ? this.runtimeTableName : 
      this.introspectedTableName;

    return StringUtility.composeFullyQualifiedTableName(
      this.ignoreQualifiersAtRuntime ? null : localCatalog, 
      this.ignoreQualifiersAtRuntime ? null : localSchema, 
      localTable, '_');
  }

  public String getDomainObjectName() {
    if (StringUtility.stringHasValue(this.domainObjectName))
      return this.domainObjectName;
    if (StringUtility.stringHasValue(this.runtimeTableName)) {
      return JavaBeansUtil.getCamelCaseString(this.runtimeTableName, true);
    }
    return JavaBeansUtil.getCamelCaseString(this.introspectedTableName, true);
  }

  public String getTableName4Controller()
  {
    String temp = this.introspectedTableName.toLowerCase();
    for (Map.Entry te : this.replaceTablePrefixMap.entrySet()) {
      if (temp.indexOf(((String)te.getKey()).toLowerCase()) != -1) {
        temp = temp.replaceAll(((String)te.getKey()).toLowerCase(), ((String)te.getValue()).toLowerCase());
        break;
      }
    }
    return temp;
  }

  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof FullyQualifiedTable)) {
      return false;
    }

    FullyQualifiedTable other = (FullyQualifiedTable)obj;

    if (EqualsUtil.areEqual(this.introspectedTableName, 
      other.introspectedTableName))
      if (EqualsUtil.areEqual(this.introspectedCatalog, 
        other.introspectedCatalog))
        if (EqualsUtil.areEqual(this.introspectedSchema, 
          other.introspectedSchema))
          return true;
    return 
      false;
  }

  public int hashCode()
  {
    int result = 23;
    result = HashCodeUtil.hash(result, this.introspectedTableName);
    result = HashCodeUtil.hash(result, this.introspectedCatalog);
    result = HashCodeUtil.hash(result, this.introspectedSchema);

    return result;
  }

  public String toString()
  {
    return StringUtility.composeFullyQualifiedTableName(
      this.introspectedCatalog, this.introspectedSchema, this.introspectedTableName, 
      '.');
  }

  public String getAlias() {
    return this.alias;
  }

  public String getSubPackage(boolean isSubPackagesEnabled)
  {
    StringBuilder sb = new StringBuilder();
    if ((!this.ignoreQualifiersAtRuntime) && (isSubPackagesEnabled)) {
      if (StringUtility.stringHasValue(this.runtimeCatalog)) {
        sb.append('.');
        sb.append(this.runtimeCatalog.toLowerCase());
      } else if (StringUtility.stringHasValue(this.introspectedCatalog)) {
        sb.append('.');
        sb.append(this.introspectedCatalog.toLowerCase());
      }

      if (StringUtility.stringHasValue(this.runtimeSchema)) {
        sb.append('.');
        sb.append(this.runtimeSchema.toLowerCase());
      } else if (StringUtility.stringHasValue(this.introspectedSchema)) {
        sb.append('.');
        sb.append(this.introspectedSchema.toLowerCase());
      }
    }

    if (StringUtility.stringHasValue(this.domainObjectSubPackage)) {
      sb.append('.');
      sb.append(this.domainObjectSubPackage);
    }

    return sb.toString();
  }

  private void addDelimiters(StringBuilder sb) {
    if (StringUtility.stringHasValue(this.beginningDelimiter)) {
      sb.insert(0, this.beginningDelimiter);
    }

    if (StringUtility.stringHasValue(this.endingDelimiter))
      sb.append(this.endingDelimiter);
  }
}