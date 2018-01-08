package ht.msc.mybatis.plugin;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class Context extends PropertyHolder
{
  private String id;
  private static String codeVersion;
  private JDBCConnectionConfiguration jdbcConnectionConfiguration;
  private SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration;
  private JavaTypeResolverConfiguration javaTypeResolverConfiguration;
  private JavaModelGeneratorConfiguration javaModelGeneratorConfiguration;
  private JavaClientGeneratorConfiguration javaClientGeneratorConfiguration;
  private YouGouServiceGeneratorConfiguration youGouServiceGeneratorConfiguration;
  private YouGouManagerGeneratorConfiguration youGouManagerGeneratorConfiguration;
  private YouGouDaoGeneratorConfiguration youGouDaoGeneratorConfiguration;
  private YouGouJSMVCGeneratorConfiguration youGouJSMVCGeneratorConfiguration;
  private YouGouControllerGeneratorConfiguration youGouControllerGeneratorConfiguration;
  private YouGouTableSettingConfiguration youGouTableSettingConfiguration;
  private static YouGouSqlMapConfigConfiguration youGouSqlMapConfigConfiguration;
  private ArrayList<TableConfiguration> tableConfigurations;
  private ModelType defaultModelType;
  private String beginningDelimiter = "\"";

  private String endingDelimiter = "\"";
  private CommentGeneratorConfiguration commentGeneratorConfiguration;
  private CommentGenerator commentGenerator;
  private PluginAggregator pluginAggregator;
  private List<PluginConfiguration> pluginConfigurations;
  private String targetRuntime;
  private String introspectedColumnImpl;
  private Boolean autoDelimitKeywords;
  private JavaFormatter javaFormatter;
  private JSFormatter jsFormatter;
  private XmlFormatter xmlFormatter;
  private List<IntrospectedTable> introspectedTables;

  public Context(ModelType defaultModelType)
  {
    if (defaultModelType == null)
      this.defaultModelType = ModelType.CONDITIONAL;
    else {
      this.defaultModelType = defaultModelType;
    }

    this.tableConfigurations = new ArrayList();
    this.pluginConfigurations = new ArrayList();
  }

  public void addTableConfiguration(TableConfiguration tc) {
    this.tableConfigurations.add(tc);
  }

  public JDBCConnectionConfiguration getJdbcConnectionConfiguration() {
    return this.jdbcConnectionConfiguration;
  }

  public JavaClientGeneratorConfiguration getJavaClientGeneratorConfiguration() {
    return this.javaClientGeneratorConfiguration;
  }

  public JavaModelGeneratorConfiguration getJavaModelGeneratorConfiguration() {
    return this.javaModelGeneratorConfiguration;
  }

  public JavaTypeResolverConfiguration getJavaTypeResolverConfiguration() {
    return this.javaTypeResolverConfiguration;
  }

  public SqlMapGeneratorConfiguration getSqlMapGeneratorConfiguration() {
    return this.sqlMapGeneratorConfiguration;
  }

  public void addPluginConfiguration(PluginConfiguration pluginConfiguration)
  {
    this.pluginConfigurations.add(pluginConfiguration);
  }

  public void validate(List<String> errors)
  {
    if (!StringUtility.stringHasValue(this.id)) {
      errors.add(Messages.getString("ValidationError.16"));
    }

    if (this.jdbcConnectionConfiguration == null)
      errors.add(Messages.getString("ValidationError.10", this.id));
    else {
      this.jdbcConnectionConfiguration.validate(errors);
    }

    if (this.javaModelGeneratorConfiguration == null)
      errors.add(Messages.getString("ValidationError.8", this.id));
    else {
      this.javaModelGeneratorConfiguration.validate(errors, this.id);
    }

    if (this.javaClientGeneratorConfiguration != null) {
      this.javaClientGeneratorConfiguration.validate(errors, this.id);
    }

    if (this.youGouServiceGeneratorConfiguration != null) {
      this.youGouServiceGeneratorConfiguration.validate(errors, this.id);
    }

    if (this.youGouDaoGeneratorConfiguration != null) {
      this.youGouDaoGeneratorConfiguration.validate(errors, this.id);
    }

    if (this.youGouJSMVCGeneratorConfiguration != null) {
      this.youGouJSMVCGeneratorConfiguration.validate(errors, this.id);
    }

    if (this.youGouManagerGeneratorConfiguration != null) {
      this.youGouManagerGeneratorConfiguration.validate(errors, this.id);
    }

    if (this.youGouControllerGeneratorConfiguration != null) {
      this.youGouControllerGeneratorConfiguration.validate(errors, this.id);
    }

    IntrospectedTable it = null;
    try {
      it = ObjectFactory.createIntrospectedTableForValidation(this);
    } catch (Exception localException) {
      errors.add(Messages.getString("ValidationError.25", this.id));
    }

    if ((it != null) && (it.requiresXMLGenerator()))
      if (this.sqlMapGeneratorConfiguration == null)
        errors.add(Messages.getString("ValidationError.9", this.id));
      else
        this.sqlMapGeneratorConfiguration.validate(errors, this.id);
    TableConfiguration tc;
    if (this.tableConfigurations.size() == 0)
      errors.add(Messages.getString("ValidationError.3", this.id));
    else {
      for (int i = 0; i < this.tableConfigurations.size(); i++) {
        tc = (TableConfiguration)this.tableConfigurations.get(i);

        tc.validate(errors, i);
      }
    }

    for (PluginConfiguration pluginConfiguration : this.pluginConfigurations)
      pluginConfiguration.validate(errors, this.id);
  }

  public String getId()
  {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public static String getCodeVersion()
  {
    return codeVersion;
  }

  public void setCodeVersion(String codeVersion)
  {
    codeVersion = codeVersion;
  }

  public void setJavaClientGeneratorConfiguration(JavaClientGeneratorConfiguration javaClientGeneratorConfiguration)
  {
    this.javaClientGeneratorConfiguration = javaClientGeneratorConfiguration;
  }

  public YouGouServiceGeneratorConfiguration getYouGouServiceGeneratorConfiguration()
  {
    return this.youGouServiceGeneratorConfiguration;
  }

  public void setYouGouServiceGeneratorConfiguration(YouGouServiceGeneratorConfiguration youGouServiceGeneratorConfiguration)
  {
    this.youGouServiceGeneratorConfiguration = youGouServiceGeneratorConfiguration;
  }

  public YouGouManagerGeneratorConfiguration getYouGouManagerGeneratorConfiguration()
  {
    return this.youGouManagerGeneratorConfiguration;
  }

  public void setYouGouManagerGeneratorConfiguration(YouGouManagerGeneratorConfiguration youGouManagerGeneratorConfiguration)
  {
    this.youGouManagerGeneratorConfiguration = youGouManagerGeneratorConfiguration;
  }

  public YouGouDaoGeneratorConfiguration getYouGouDaoGeneratorConfiguration()
  {
    return this.youGouDaoGeneratorConfiguration;
  }

  public void setYouGouDaoGeneratorConfiguration(YouGouDaoGeneratorConfiguration youGouDaoGeneratorConfiguration)
  {
    this.youGouDaoGeneratorConfiguration = youGouDaoGeneratorConfiguration;
  }

  public YouGouJSMVCGeneratorConfiguration getYouGouJSMVCGeneratorConfiguration()
  {
    return this.youGouJSMVCGeneratorConfiguration;
  }

  public void setYouGouJSMVCGeneratorConfiguration(YouGouJSMVCGeneratorConfiguration youGouJSMVCGeneratorConfiguration)
  {
    this.youGouJSMVCGeneratorConfiguration = youGouJSMVCGeneratorConfiguration;
  }

  public YouGouControllerGeneratorConfiguration getYouGouControllerGeneratorConfiguration()
  {
    return this.youGouControllerGeneratorConfiguration;
  }

  public void setYouGouControllerGeneratorConfiguration(YouGouControllerGeneratorConfiguration youGouControllerGeneratorConfiguration)
  {
    this.youGouControllerGeneratorConfiguration = youGouControllerGeneratorConfiguration;
  }

  public YouGouTableSettingConfiguration getYouGouTableSettingConfiguration()
  {
    return this.youGouTableSettingConfiguration;
  }

  public void setYouGouTableSettingConfiguration(YouGouTableSettingConfiguration youGouTableSettingConfiguration)
  {
    this.youGouTableSettingConfiguration = youGouTableSettingConfiguration;
  }

  public static YouGouSqlMapConfigConfiguration getYouGouSqlMapConfigConfiguration()
  {
    return youGouSqlMapConfigConfiguration;
  }

  public static void setYouGouSqlMapConfigConfiguration(YouGouSqlMapConfigConfiguration youGouSqlMapConfigConfiguration)
  {
    youGouSqlMapConfigConfiguration = youGouSqlMapConfigConfiguration;
  }

  public void setJavaModelGeneratorConfiguration(JavaModelGeneratorConfiguration javaModelGeneratorConfiguration)
  {
    this.javaModelGeneratorConfiguration = javaModelGeneratorConfiguration;
  }

  public void setJavaTypeResolverConfiguration(JavaTypeResolverConfiguration javaTypeResolverConfiguration)
  {
    this.javaTypeResolverConfiguration = javaTypeResolverConfiguration;
  }

  public void setJdbcConnectionConfiguration(JDBCConnectionConfiguration jdbcConnectionConfiguration)
  {
    this.jdbcConnectionConfiguration = jdbcConnectionConfiguration;
  }

  public void setSqlMapGeneratorConfiguration(SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration)
  {
    this.sqlMapGeneratorConfiguration = sqlMapGeneratorConfiguration;
  }

  public ModelType getDefaultModelType() {
    return this.defaultModelType;
  }

  public XmlElement toXmlElement()
  {
    XmlElement xmlElement = new XmlElement("context");

    xmlElement.addAttribute(new Attribute("id", this.id));

    if (this.defaultModelType != ModelType.CONDITIONAL) {
      xmlElement.addAttribute(new Attribute(
        "defaultModelType", this.defaultModelType.getModelType()));
    }

    if (StringUtility.stringHasValue(this.introspectedColumnImpl)) {
      xmlElement.addAttribute(new Attribute(
        "introspectedColumnImpl", this.introspectedColumnImpl));
    }

    if (StringUtility.stringHasValue(this.targetRuntime)) {
      xmlElement.addAttribute(new Attribute(
        "targetRuntime", this.targetRuntime));
    }

    addPropertyXmlElements(xmlElement);

    for (PluginConfiguration pluginConfiguration : this.pluginConfigurations) {
      xmlElement.addElement(pluginConfiguration.toXmlElement());
    }

    if (this.commentGeneratorConfiguration != null) {
      xmlElement.addElement(this.commentGeneratorConfiguration.toXmlElement());
    }

    if (this.jdbcConnectionConfiguration != null) {
      xmlElement.addElement(this.jdbcConnectionConfiguration.toXmlElement());
    }

    if (this.javaTypeResolverConfiguration != null) {
      xmlElement.addElement(this.javaTypeResolverConfiguration.toXmlElement());
    }

    if (this.javaModelGeneratorConfiguration != null) {
      xmlElement.addElement(this.javaModelGeneratorConfiguration
        .toXmlElement());
    }

    if (this.sqlMapGeneratorConfiguration != null) {
      xmlElement.addElement(this.sqlMapGeneratorConfiguration.toXmlElement());
    }

    if (this.javaClientGeneratorConfiguration != null) {
      xmlElement.addElement(this.javaClientGeneratorConfiguration.toXmlElement());
    }

    if (this.youGouServiceGeneratorConfiguration != null) {
      xmlElement.addElement(this.youGouServiceGeneratorConfiguration.toXmlElement());
    }

    for (TableConfiguration tableConfiguration : this.tableConfigurations) {
      xmlElement.addElement(tableConfiguration.toXmlElement());
    }

    return xmlElement;
  }

  public List<TableConfiguration> getTableConfigurations() {
    return this.tableConfigurations;
  }

  public String getBeginningDelimiter() {
    return this.beginningDelimiter;
  }

  public String getEndingDelimiter() {
    return this.endingDelimiter;
  }

  public void addProperty(String name, String value)
  {
    super.addProperty(name, value);

    if ("beginningDelimiter".equals(name))
      this.beginningDelimiter = value;
    else if ("endingDelimiter".equals(name))
      this.endingDelimiter = value;
    else if (("autoDelimitKeywords".equals(name)) && 
      (StringUtility.stringHasValue(value)))
      this.autoDelimitKeywords = new Boolean(StringUtility.isTrue(value));
  }

  public CommentGenerator getCommentGenerator()
  {
    if (this.commentGenerator == null) {
      this.commentGenerator = ObjectFactory.createCommentGenerator(this);
    }

    return this.commentGenerator;
  }

  public JavaFormatter getJavaFormatter() {
    if (this.javaFormatter == null) {
      this.javaFormatter = ObjectFactory.createJavaFormatter(this);
    }

    return this.javaFormatter;
  }
  public JSFormatter getJSFormatter() {
    if (this.jsFormatter == null) {
      this.jsFormatter = ObjectFactory.createJSFormatter(this);
    }

    return this.jsFormatter;
  }

  public XmlFormatter getXmlFormatter() {
    if (this.xmlFormatter == null) {
      this.xmlFormatter = ObjectFactory.createXmlFormatter(this);
    }

    return this.xmlFormatter;
  }

  public CommentGeneratorConfiguration getCommentGeneratorConfiguration() {
    return this.commentGeneratorConfiguration;
  }

  public void setCommentGeneratorConfiguration(CommentGeneratorConfiguration commentGeneratorConfiguration)
  {
    this.commentGeneratorConfiguration = commentGeneratorConfiguration;
  }

  public Plugin getPlugins() {
    return this.pluginAggregator;
  }

  public String getTargetRuntime() {
    return this.targetRuntime;
  }

  public void setTargetRuntime(String targetRuntime) {
    this.targetRuntime = targetRuntime;
  }

  public String getIntrospectedColumnImpl() {
    return this.introspectedColumnImpl;
  }

  public void setIntrospectedColumnImpl(String introspectedColumnImpl) {
    this.introspectedColumnImpl = introspectedColumnImpl;
  }

  public int getIntrospectionSteps()
  {
    int steps = 0;

    steps++;

    steps += this.tableConfigurations.size() * 1;

    return steps;
  }

  public void introspectTables(ProgressCallback callback, List<String> warnings, Set<String> fullyQualifiedTableNames)
    throws SQLException, InterruptedException
  {
    this.introspectedTables = new ArrayList();
    JavaTypeResolver javaTypeResolver = 
      ObjectFactory.createJavaTypeResolver(this, warnings);

    Connection connection = null;
    try
    {
      callback.startTask(Messages.getString("Progress.0"));
      connection = getConnection();

      DatabaseIntrospector databaseIntrospector = new DatabaseIntrospector(
        this, connection.getMetaData(), javaTypeResolver, warnings);

      for (TableConfiguration tc : this.tableConfigurations) {
        String tableName = StringUtility.composeFullyQualifiedTableName(tc.getCatalog(), tc
          .getSchema(), tc.getTableName(), '.');

        if ((fullyQualifiedTableNames == null) || 
          (fullyQualifiedTableNames.size() <= 0) || 
          (fullyQualifiedTableNames.contains(tableName)))
        {
          if (!tc.areAnyStatementsEnabled()) {
            warnings.add(Messages.getString("Warning.0", tableName));
          }
          else
          {
            //callback.startTask(Messages.getString("Progress.1", tableName));
            List tables = databaseIntrospector
              .introspectTables(tc);

            if (tables != null) {
              this.introspectedTables.addAll(tables);
            }

            callback.checkCancel();
          }
        }
      } } finally { closeConnection(connection); }
  }

  public int getGenerationSteps()
  {
    int steps = 0;

    if (this.introspectedTables != null) {
      for (IntrospectedTable introspectedTable : this.introspectedTables) {
        steps += introspectedTable.getGenerationSteps();
      }
    }

    return steps;
  }

  public void generateFiles(ProgressCallback callback, List<GeneratedJavaFile> generatedJavaFiles, List<GeneratedXmlFile> generatedXmlFiles, List<GeneratedJSFile> generatedJSFiles, List<String> warnings)
    throws InterruptedException
  {
    this.pluginAggregator = new PluginAggregator();
    for (PluginConfiguration pluginConfiguration : this.pluginConfigurations) {
      Plugin plugin = ObjectFactory.createPlugin(this, 
        pluginConfiguration);
      if (plugin.validate(warnings))
        this.pluginAggregator.addPlugin(plugin);
      else {
        warnings.add(Messages.getString("Warning.24", 
          pluginConfiguration.getConfigurationType(), this.id));
      }
    }

    if (this.introspectedTables != null) {
      for (IntrospectedTable introspectedTable : this.introspectedTables) {
        callback.checkCancel();

        introspectedTable.initialize();
        introspectedTable.calculateGenerators(warnings, callback);
        generatedJavaFiles.addAll(introspectedTable.getGeneratedJavaFiles());
        generatedXmlFiles.addAll(introspectedTable.getGeneratedXmlFiles());
        generatedJSFiles.addAll(introspectedTable.getGeneratedJSFiles());

        generatedJavaFiles.addAll(this.pluginAggregator
          .contextGenerateAdditionalJavaFiles(introspectedTable));
        generatedXmlFiles.addAll(this.pluginAggregator
          .contextGenerateAdditionalXmlFiles(introspectedTable));
      }
    }

    generatedJavaFiles.addAll(this.pluginAggregator
      .contextGenerateAdditionalJavaFiles());
    generatedXmlFiles.addAll(this.pluginAggregator
      .contextGenerateAdditionalXmlFiles());
  }

  private Connection getConnection() throws SQLException {
    Connection connection = ConnectionFactory.getInstance().getConnection(
      this.jdbcConnectionConfiguration);

    return connection;
  }

  private void closeConnection(Connection connection) {
    if (connection != null)
      try {
        connection.close();
      }
      catch (SQLException localSQLException)
      {
      }
  }

  public boolean autoDelimitKeywords()
  {
    return (this.autoDelimitKeywords != null) && 
      (this.autoDelimitKeywords.booleanValue());
  }

  public String getDbmsType()
  {
    Connection connection = null;
    try
    {
      String str;
      if ((this.jdbcConnectionConfiguration.getDbmsType() == null) || ("".equals(this.jdbcConnectionConfiguration.getDbmsType()))) {
        connection = getConnection();
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        return databaseMetaData.getDatabaseProductName();
      }
      return this.jdbcConnectionConfiguration.getDbmsType();
    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    } finally {
      closeConnection(connection);
    }
  }
}