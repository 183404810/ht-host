package ht.msc.mybatis.plugin;


public class ActualTableName
{
  private String tableName;
  private String catalog;
  private String schema;
  private String fullName;

  public ActualTableName(String catalog, String schema, String tableName)
  {
    this.catalog = catalog;
    this.schema = schema;
    this.tableName = tableName;
    this.fullName = StringUtility.composeFullyQualifiedTableName(catalog, 
      schema, tableName, '.');
  }

  public String getCatalog() {
    return this.catalog;
  }

  public String getSchema() {
    return this.schema;
  }

  public String getTableName() {
    return this.tableName;
  }

  public boolean equals(Object obj)
  {
    if ((obj == null) || (!(obj instanceof ActualTableName))) {
      return false;
    }

    return obj.toString().equals(toString());
  }

  public int hashCode()
  {
    return this.fullName.hashCode();
  }

  public String toString()
  {
    return this.fullName;
  }
}