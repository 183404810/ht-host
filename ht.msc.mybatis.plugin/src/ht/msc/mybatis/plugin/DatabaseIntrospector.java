package ht.msc.mybatis.plugin;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class DatabaseIntrospector
{
  private DatabaseMetaData databaseMetaData;
  private JavaTypeResolver javaTypeResolver;
  private List<String> warnings;
  private Context context;
  private Log logger;

  public DatabaseIntrospector(Context context, DatabaseMetaData databaseMetaData, JavaTypeResolver javaTypeResolver, List<String> warnings)
  {
    this.context = context;
    this.databaseMetaData = databaseMetaData;
    this.javaTypeResolver = javaTypeResolver;
    this.warnings = warnings;
    this.logger = (Log) LogFactory.getLog(getClass());
  }

  private void calculatePrimaryKey(FullyQualifiedTable table, IntrospectedTable introspectedTable)
  {
    ResultSet rs = null;
    try
    {
      rs = this.databaseMetaData.getPrimaryKeys(
        table.getIntrospectedCatalog(), table
        .getIntrospectedSchema(), table
        .getIntrospectedTableName());
    } catch (SQLException localSQLException1) {
      closeResultSet(rs);
      this.warnings.add(Messages.getString("Warning.15"));
      return;
    }

    try
    {
      Map<Short,String> keyColumns = new TreeMap();
      short keySeq;
      while (rs.next()) {
        String columnName = rs.getString("COLUMN_NAME");
        keySeq = rs.getShort("KEY_SEQ");
        keyColumns.put(Short.valueOf(keySeq), columnName);
      }

      for (String columnName : keyColumns.values())
        introspectedTable.addPrimaryKeyColumn(columnName);
    }
    catch (SQLException localSQLException2) {
    }
    finally {
      closeResultSet(rs);
    }
  }

  private void closeResultSet(ResultSet rs) {
    if (rs != null)
      try {
        rs.close();
      }
      catch (SQLException localSQLException)
      {
      }
  }

  private void reportIntrospectionWarnings(IntrospectedTable introspectedTable, TableConfiguration tableConfiguration, FullyQualifiedTable table)
  {
    Iterator localIterator = tableConfiguration
      .getColumnOverrides().iterator();

    while (localIterator.hasNext()) {
      ColumnOverride columnOverride = (ColumnOverride)localIterator.next();
      if (introspectedTable.getColumn(columnOverride.getColumnName()) == null) {
        this.warnings.add(Messages.getString("Warning.3", 
          columnOverride.getColumnName(), table.toString()));
      }

    }

    for (String string : tableConfiguration.getIgnoredColumnsInError()) {
      this.warnings.add(Messages.getString("Warning.4", 
        string, table.toString()));
    }

    GeneratedKey generatedKey = tableConfiguration.getGeneratedKey();
    if ((generatedKey != null) && 
      (introspectedTable.getColumn(generatedKey.getColumn()) == null))
      if (generatedKey.isIdentity())
        this.warnings.add(Messages.getString("Warning.5", 
          generatedKey.getColumn(), table.toString()));
      else
        this.warnings.add(Messages.getString("Warning.6", 
          generatedKey.getColumn(), table.toString()));
  }

  public List<IntrospectedTable> introspectTables(TableConfiguration tc)
    throws SQLException
  {
    Map columns = getColumns(tc);

    if (columns.isEmpty()) {
     this.warnings.add(Messages.getString("Warning.19", tc.getCatalog(), 
        tc.getSchema(), tc.getTableName()));
      return null;
    }

    removeIgnoredColumns(tc, columns);
    calculateExtraColumnInformation(tc, columns);
    applyColumnOverrides(tc, columns);
    calculateIdentityColumns(tc, columns);

    List introspectedTables = calculateIntrospectedTables(
      tc, columns);

    Iterator iter = introspectedTables.iterator();
    while (iter.hasNext()) {
      IntrospectedTable introspectedTable = (IntrospectedTable)iter.next();

      if (!introspectedTable.hasAnyColumns())
      {
        String warning = Messages.getString(
          "Warning.1", introspectedTable.getFullyQualifiedTable().toString());
        this.warnings.add(warning);
        iter.remove();
      } else if ((!introspectedTable.hasPrimaryKeyColumns()) && 
        (!introspectedTable.hasBaseColumns()))
      {
        String warning = Messages.getString(
          "Warning.18", introspectedTable.getFullyQualifiedTable().toString());
        this.warnings.add(warning);
        iter.remove();
      }
      else
      {
        reportIntrospectionWarnings(introspectedTable, tc, 
          introspectedTable.getFullyQualifiedTable());
      }
    }

    return introspectedTables;
  }

  private void removeIgnoredColumns(TableConfiguration tc, Map<ActualTableName, List<IntrospectedColumn>> columns)
  {
    Iterator localIterator1 = columns
      .entrySet().iterator();
    Iterator tableColumns;
    for (;localIterator1.hasNext();)
    {
      Map.Entry entry = (Map.Entry)localIterator1.next();
      if(entry==null) continue;
      tableColumns = ((List)entry.getValue()).iterator();
      for(;tableColumns.hasNext();){
	      IntrospectedColumn introspectedColumn = (IntrospectedColumn)tableColumns.next();
	      if (tc
	        .isColumnIgnored(introspectedColumn
	        .getActualColumnName())) {
	        tableColumns.remove();
	        if (this.logger.isDebugEnabled())
	          this.logger.debug(Messages.getString("Tracing.3", 
	            introspectedColumn.getActualColumnName(), 
	            ((ActualTableName)entry
	            .getKey()).toString()));
	      }
      }
    }
  }

  private void calculateExtraColumnInformation(TableConfiguration tc, Map<ActualTableName, List<IntrospectedColumn>> columns)
  {
    StringBuilder sb = new StringBuilder();
    Pattern pattern = null;
    String replaceString = null;
    if (tc.getColumnRenamingRule() != null) {
      pattern = Pattern.compile(tc.getColumnRenamingRule()
        .getSearchString());
      replaceString = tc.getColumnRenamingRule().getReplaceString();
      replaceString = replaceString == null ? "" : replaceString;
    }

    Iterator localIterator1 = columns
      .entrySet().iterator();
    Iterator localIterator2;
    for (; localIterator1.hasNext();)
    {
      Map.Entry entry = (Map.Entry)localIterator1.next();
      if(entry==null) continue;
      localIterator2 = ((List)entry.getValue()).iterator(); 
      for( ;localIterator2.hasNext();){
	      IntrospectedColumn introspectedColumn = (IntrospectedColumn)localIterator2.next();
	      String calculatedColumnName;
	      if (pattern == null) {
	        calculatedColumnName = introspectedColumn
	          .getActualColumnName();
	      } else {
	        Matcher matcher = pattern.matcher(introspectedColumn
	          .getActualColumnName());
	        calculatedColumnName = matcher.replaceAll(replaceString);
	      }
	
	      if (StringUtility.isTrue(tc
	        .getProperty("useActualColumnNames"))) {
	        introspectedColumn.setJavaProperty(
	          JavaBeansUtil.getValidPropertyName(calculatedColumnName));
	      } else if (StringUtility.isTrue(tc
	        .getProperty("useCompoundPropertyNames"))) {
	        sb.setLength(0);
	        sb.append(calculatedColumnName);
	        sb.append('_');
	        sb.append(JavaBeansUtil.getCamelCaseString(
	          introspectedColumn.getRemarks(), true));
	        introspectedColumn.setJavaProperty(
	          JavaBeansUtil.getValidPropertyName(sb.toString()));
	      } else {
	        introspectedColumn.setJavaProperty(
	          JavaBeansUtil.getCamelCaseString(calculatedColumnName, false));
	      }
	
	      FullyQualifiedJavaType fullyQualifiedJavaType = this.javaTypeResolver
	        .calculateJavaType(introspectedColumn);
	
	      if (fullyQualifiedJavaType != null) {
	        introspectedColumn
	          .setFullyQualifiedJavaType(fullyQualifiedJavaType);
	        introspectedColumn.setJdbcTypeName(this.javaTypeResolver
	          .calculateJdbcTypeName(introspectedColumn));
	      }
	      else {
	        boolean warn = true;
	        if (tc.isColumnIgnored(introspectedColumn
	          .getActualColumnName())) {
	          warn = false;
	        }
	
	        ColumnOverride co = tc.getColumnOverride(introspectedColumn
	          .getActualColumnName());
	        if ((co != null) && 
	          (StringUtility.stringHasValue(co.getJavaType())) && 
	          (StringUtility.stringHasValue(co.getJavaType()))) {
	          warn = false;
	        }
	
	        if (warn) {
	          introspectedColumn
	            .setFullyQualifiedJavaType(
	            FullyQualifiedJavaType.getObjectInstance());
	          introspectedColumn.setJdbcTypeName("OTHER");
	
	          String warning = Messages.getString("Warning.14", 
	            Integer.toString(introspectedColumn.getJdbcType()), 
	            ((ActualTableName)entry.getKey()).toString(), 
	            introspectedColumn.getActualColumnName());
	
	          this.warnings.add(warning);
	        }
	      }
	
	      if ((this.context.autoDelimitKeywords()) && 
	        (SqlReservedWords.containsWord(introspectedColumn
	        .getActualColumnName()))) {
	        introspectedColumn.setColumnNameDelimited(true);
	      }
	
	      if (tc.isAllColumnDelimitingEnabled())
	        introspectedColumn.setColumnNameDelimited(true);
      }
    }
  }

  private void calculateIdentityColumns(TableConfiguration tc, Map<ActualTableName, List<IntrospectedColumn>> columns)
  {
    GeneratedKey gk = tc.getGeneratedKey();
    if (gk == null)
    {
      return;
    }

    Iterator localIterator1 = columns
      .entrySet().iterator();
    Iterator localIterator2;
    for (; localIterator1.hasNext();)
    {
      Map.Entry entry = (Map.Entry)localIterator1.next();
      if(entry==null)continue;
      localIterator2 = ((List)entry.getValue()).iterator(); 
      for(;localIterator2.hasNext();){
	      IntrospectedColumn introspectedColumn = (IntrospectedColumn)localIterator2.next();
	      if (isMatchedColumn(introspectedColumn, gk))
	        if ((gk.isIdentity()) || (gk.isJdbcStandard())) {
	          introspectedColumn.setIdentity(true);
	          introspectedColumn.setSequenceColumn(false);
	        } else {
	          introspectedColumn.setIdentity(false);
	          introspectedColumn.setSequenceColumn(true);
	        }
	    }
  	}
  }

  private boolean isMatchedColumn(IntrospectedColumn introspectedColumn, GeneratedKey gk)
  {
    if (introspectedColumn.isColumnNameDelimited()) {
      return introspectedColumn.getActualColumnName().equals(gk.getColumn());
    }
    return introspectedColumn.getActualColumnName().equalsIgnoreCase(gk.getColumn());
  }

  private void applyColumnOverrides(TableConfiguration tc, Map<ActualTableName, List<IntrospectedColumn>> columns)
  {
    Iterator localIterator1 = columns
      .entrySet().iterator();
    Iterator localIterator2;
    for (; localIterator1.hasNext();)
    {
      Map.Entry entry = (Map.Entry)localIterator1.next();
      if(entry==null) continue;
      localIterator2 = ((List)entry.getValue()).iterator(); 
      for(;localIterator2.hasNext();){
	      IntrospectedColumn introspectedColumn = (IntrospectedColumn)localIterator2.next();
	      ColumnOverride columnOverride = tc
	        .getColumnOverride(introspectedColumn
	        .getActualColumnName());
	
	      if (columnOverride != null) {
	        if (this.logger.isDebugEnabled()) {
	          this.logger.debug(Messages.getString("Tracing.4", 
	            introspectedColumn.getActualColumnName(), 
	            ((ActualTableName)entry
	            .getKey()).toString()));
	        }
	
	        if (StringUtility.stringHasValue(columnOverride
	          .getJavaProperty())) {
	          introspectedColumn.setJavaProperty(columnOverride
	            .getJavaProperty());
	        }
	
	        if (StringUtility.stringHasValue(columnOverride
	          .getJavaType())) {
	          introspectedColumn
	            .setFullyQualifiedJavaType(new FullyQualifiedJavaType(
	            columnOverride.getJavaType()));
	        }
	
	        if (StringUtility.stringHasValue(columnOverride
	          .getJdbcType())) {
	          introspectedColumn.setJdbcTypeName(columnOverride
	            .getJdbcType());
	        }
	
	        if (StringUtility.stringHasValue(columnOverride
	          .getTypeHandler())) {
	          introspectedColumn.setTypeHandler(columnOverride
	            .getTypeHandler());
	        }
	
	        if (columnOverride.isColumnNameDelimited()) {
	          introspectedColumn.setColumnNameDelimited(true);
	        }
	
	        introspectedColumn.setProperties(columnOverride
	          .getProperties());
	      }
      }
    }
  }

  private Map<ActualTableName, List<IntrospectedColumn>> getColumns(TableConfiguration tc)
    throws SQLException
  {
    boolean delimitIdentifiers = (tc.isDelimitIdentifiers()) || 
      (StringUtility.stringContainsSpace(tc.getCatalog())) || 
      (StringUtility.stringContainsSpace(tc.getSchema())) || 
      (StringUtility.stringContainsSpace(tc.getTableName()));
    String localTableName;
    String localCatalog;
    String localSchema;
    if (delimitIdentifiers) {
      localCatalog = tc.getCatalog();
      localSchema = tc.getSchema();
      localTableName = tc.getTableName();
    }
    else
    {
      if (this.databaseMetaData.storesLowerCaseIdentifiers()) {
        localCatalog = tc.getCatalog() == null ? null : tc.getCatalog()
          .toLowerCase();
        localSchema = tc.getSchema() == null ? null : tc.getSchema()
          .toLowerCase();
        localTableName = tc.getTableName() == null ? null : tc
          .getTableName().toLowerCase();
      }
      else
      {
        if (this.databaseMetaData.storesUpperCaseIdentifiers()) {
          localCatalog = tc.getCatalog() == null ? null : tc.getCatalog()
            .toUpperCase();
          localSchema = tc.getSchema() == null ? null : tc.getSchema()
            .toUpperCase();
          localTableName = tc.getTableName() == null ? null : tc
            .getTableName().toUpperCase();
        } else {
          localCatalog = tc.getCatalog();
          localSchema = tc.getSchema();
          localTableName = tc.getTableName();
        }
      }
    }
    if (tc.isWildcardEscapingEnabled()) {
      String escapeString = this.databaseMetaData.getSearchStringEscape();

      StringBuilder sb = new StringBuilder();

      if (localSchema != null) {
        StringTokenizer st = new StringTokenizer(localSchema, "_%", true);
        while (st.hasMoreTokens()) {
          String token = st.nextToken();
          if ((token.equals("_")) || 
            (token.equals("%"))) {
            sb.append(escapeString);
          }
          sb.append(token);
        }
        localSchema = sb.toString();
      }

      sb.setLength(0);
      StringTokenizer st = new StringTokenizer(localTableName, "_%", true);
      while (st.hasMoreTokens()) {
        String token = st.nextToken();
        if ((token.equals("_")) || 
          (token.equals("%"))) {
          sb.append(escapeString);
        }
        sb.append(token);
      }
      localTableName = sb.toString();
    }

    Map<ActualTableName,List<IntrospectedColumn>> answer = new HashMap();

    if (this.logger.isDebugEnabled()) {
      String fullTableName = StringUtility.composeFullyQualifiedTableName(localCatalog, localSchema, 
        localTableName, '.');
        this.logger.debug(Messages.getString("Tracing.1", fullTableName));
    }

    ResultSet rs = this.databaseMetaData.getColumns(localCatalog, localSchema, 
      localTableName, null);

    while (rs.next()) {
      IntrospectedColumn introspectedColumn = 
        ObjectFactory.createIntrospectedColumn(this.context);

      introspectedColumn.setTableAlias(tc.getAlias());
      introspectedColumn.setJdbcType(rs.getInt("DATA_TYPE"));
      introspectedColumn.setLength(rs.getInt("COLUMN_SIZE"));
      introspectedColumn.setActualColumnName(rs.getString("COLUMN_NAME"));
      introspectedColumn
        .setNullable(rs.getInt("NULLABLE") == 1);
      introspectedColumn.setScale(rs.getInt("DECIMAL_DIGITS"));
      introspectedColumn.setRemarks(rs.getString("REMARKS"));
      introspectedColumn.setDefaultValue(rs.getString("COLUMN_DEF"));

      ActualTableName atn = new ActualTableName(
        rs.getString("TABLE_CAT"), 
        rs.getString("TABLE_SCHEM"), 
        rs.getString("TABLE_NAME"));

      List columns = (List)answer.get(atn);
      if (columns == null) 
        columns = new ArrayList();
      columns.add(introspectedColumn);
      answer.put(atn, columns);

      if (this.logger.isDebugEnabled()) {
        this.logger.debug(Messages.getString(
          "Tracing.2", 
          introspectedColumn.getActualColumnName(), 
          Integer.toString(introspectedColumn.getJdbcType()), 
          atn.toString()));
      }
    }

    closeResultSet(rs);

    if ((answer.size() > 1) && 
      (!StringUtility.stringContainsSQLWildcard(localSchema)) && 
      (!StringUtility.stringContainsSQLWildcard(localTableName)))
    {
      ActualTableName inputAtn = new ActualTableName(tc.getCatalog(), tc
        .getSchema(), tc.getTableName());

      StringBuilder sb = new StringBuilder();
      boolean comma = false;
      for (ActualTableName atn : answer.keySet()) {
        if (comma)
          sb.append(',');
        else {
          comma = true;
        }
        sb.append(atn.toString());
      }

      this.warnings.add(Messages.getString("Warning.25", 
        inputAtn.toString(), sb.toString()));
    }

    return answer;
  }

  private List<IntrospectedTable> calculateIntrospectedTables(TableConfiguration tc, Map<ActualTableName, List<IntrospectedColumn>> columns)
  {
    boolean delimitIdentifiers = (tc.isDelimitIdentifiers()) || 
      (StringUtility.stringContainsSpace(tc.getCatalog())) || 
      (StringUtility.stringContainsSpace(tc.getSchema())) || 
      (StringUtility.stringContainsSpace(tc.getTableName()));

    List answer = new ArrayList();

    Iterator localIterator1 = columns
      .entrySet().iterator();

    while (localIterator1.hasNext()) {
      Map.Entry entry = (Map.Entry)localIterator1.next();
      ActualTableName atn = (ActualTableName)entry.getKey();

      String domainObjectName = tc.getDomainObjectName();
      YouGouTableSettingConfiguration settingConfig = this.context.getYouGouTableSettingConfiguration();
      String temp;
      if (settingConfig != null) {
        for (Map.Entry e : settingConfig.getReplaceTablePrefixMap().entrySet()) {
          if (domainObjectName != null) {
            temp = domainObjectName.replaceFirst((String)e.getKey(), (String)e.getValue());
            if (!temp.equals(domainObjectName)) {
              domainObjectName = temp;
              break;
            }

          }

        }

      }

      String runtimeTableName = tc.getTableName().toLowerCase();
      if (settingConfig != null) {
        for (Map.Entry e : settingConfig.getReplaceTablePrefixMap().entrySet()) {
          if (runtimeTableName != null) {
            temp = runtimeTableName.replaceFirst((String)e.getKey(), (String)e.getValue());
            if (!temp.equals(runtimeTableName)) {
              runtimeTableName = temp;
              break;
            }
          }
        }

      }

      FullyQualifiedTable table = new FullyQualifiedTable(
        StringUtility.stringHasValue(tc.getCatalog()) ? atn
        .getCatalog() : null, 
        StringUtility.stringHasValue(tc.getSchema()) ? atn
        .getSchema() : null, 
        tc.getTableName(), 
        domainObjectName, 
        tc.getAlias(), 
        StringUtility.isTrue(tc.getProperty("ignoreQualifiersAtRuntime")), 
        tc.getProperty("runtimeCatalog"), 
        tc.getProperty("runtimeSchema"), 
        runtimeTableName, 
        delimitIdentifiers, this.context);

      IntrospectedTable introspectedTable = 
        ObjectFactory.createIntrospectedTable(tc, table, this.context);

      for (IntrospectedColumn introspectedColumn : (List<IntrospectedColumn>)entry.getValue()) {
        introspectedTable.addColumn(introspectedColumn);
      }

      calculatePrimaryKey(table, introspectedTable);

      answer.add(introspectedTable);
    }

    return answer;
  }
}