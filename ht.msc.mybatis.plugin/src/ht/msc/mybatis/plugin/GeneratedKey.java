package ht.msc.mybatis.plugin;

import java.util.List;


public class GeneratedKey
{
  private String column;
  private String configuredSqlStatement;
  private String runtimeSqlStatement;
  private boolean isIdentity;
  private String type;

  public GeneratedKey(String column, String configuredSqlStatement, boolean isIdentity, String type)
  {
    this.column = column;
    this.type = type;
    this.isIdentity = isIdentity;
    this.configuredSqlStatement = configuredSqlStatement;

    DatabaseDialects dialect = DatabaseDialects.getDatabaseDialect(configuredSqlStatement);
    if (dialect == null)
      this.runtimeSqlStatement = configuredSqlStatement;
    else
      this.runtimeSqlStatement = dialect.getIdentityRetrievalStatement();
  }

  public String getColumn()
  {
    return this.column;
  }

  public boolean isIdentity() {
    return this.isIdentity;
  }

  public String getRuntimeSqlStatement() {
    return this.runtimeSqlStatement;
  }

  public String getType() {
    return this.type;
  }

  public boolean isPlacedBeforeInsertInIbatis2()
  {
    boolean rc;
    if (StringUtility.stringHasValue(this.type))
      rc = true;
    else {
      rc = !this.isIdentity;
    }

    return rc;
  }

  public String getMyBatis3Order() {
    return this.isIdentity ? "AFTER" : "BEFORE";
  }

  public XmlElement toXmlElement() {
    XmlElement xmlElement = new XmlElement("generatedKey");
    xmlElement.addAttribute(new Attribute("column", this.column));
    xmlElement.addAttribute(new Attribute(
      "sqlStatement", this.configuredSqlStatement));
    if (StringUtility.stringHasValue(this.type)) {
      xmlElement.addAttribute(new Attribute("type", this.type));
    }
    xmlElement.addAttribute(new Attribute("identity", 
      this.isIdentity ? "true" : "false"));

    return xmlElement;
  }

  public void validate(List<String> errors, String tableName) {
    if (!StringUtility.stringHasValue(this.runtimeSqlStatement)) {
      errors.add(Messages.getString("ValidationError.7", 
        tableName));
    }

    if ((StringUtility.stringHasValue(this.type)) && 
      (!"pre".equals(this.type)) && (!"post".equals(this.type))) {
      errors.add(Messages.getString("ValidationError.15", 
        tableName));
    }

    if (("pre".equals(this.type)) && (this.isIdentity)) {
      errors.add(Messages.getString("ValidationError.23", 
        tableName));
    }

    if (("post".equals(this.type)) && (!this.isIdentity))
      errors.add(Messages.getString("ValidationError.24", 
        tableName));
  }

  public boolean isJdbcStandard()
  {
    return "JDBC".equals(this.runtimeSqlStatement);
  }
}