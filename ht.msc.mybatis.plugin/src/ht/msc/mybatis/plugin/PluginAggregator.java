package ht.msc.mybatis.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class PluginAggregator implements Plugin
{
	private List<Plugin> plugins;
	
	public PluginAggregator()
	{
	  this.plugins = new ArrayList();
	}
	
	public void addPlugin(Plugin plugin) {
	  this.plugins.add(plugin);
	}
	
	public void setContext(Context context) {
	  throw new UnsupportedOperationException();
	}
	
	public void setProperties(Properties properties) {
	  throw new UnsupportedOperationException();
	}
	
	public boolean validate(List<String> warnings) {
	  throw new UnsupportedOperationException();
	}
	
	public boolean modelBaseRecordClassGenerated(TopLevelClass tlc, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.modelBaseRecordClassGenerated(tlc, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass tlc, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.modelRecordWithBLOBsClassGenerated(tlc, 
	      introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean sqlMapCountByExampleElementGenerated(XmlElement element, IntrospectedTable table)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.sqlMapCountByExampleElementGenerated(element, table)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean sqlMapDeleteByExampleElementGenerated(XmlElement element, IntrospectedTable table)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.sqlMapDeleteByExampleElementGenerated(element, table)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean sqlMapDeleteByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable table)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins)
	  {
	    if (!plugin
	      .sqlMapDeleteByPrimaryKeyElementGenerated(element, table)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean modelExampleClassGenerated(TopLevelClass tlc, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.modelExampleClassGenerated(tlc, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable)
	{
	  List answer = new ArrayList();
	  for (Plugin plugin : this.plugins) {
	    List temp = plugin
	      .contextGenerateAdditionalJavaFiles(introspectedTable);
	    if (temp != null) {
	      answer.addAll(temp);
	    }
	  }
	  return answer;
	}
	
	public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles(IntrospectedTable introspectedTable)
	{
	  List answer = new ArrayList();
	  for (Plugin plugin : this.plugins) {
	    List temp = plugin
	      .contextGenerateAdditionalXmlFiles(introspectedTable);
	    if (temp != null) {
	      answer.addAll(temp);
	    }
	  }
	  return answer;
	}
	
	public boolean modelPrimaryKeyClassGenerated(TopLevelClass tlc, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.modelPrimaryKeyClassGenerated(tlc, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean sqlMapResultMapWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.sqlMapResultMapWithoutBLOBsElementGenerated(element, 
	      introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean sqlMapExampleWhereClauseElementGenerated(XmlElement element, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.sqlMapExampleWhereClauseElementGenerated(element, 
	      introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean sqlMapInsertElementGenerated(XmlElement element, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins)
	  {
	    if (!plugin
	      .sqlMapInsertElementGenerated(element, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean sqlMapResultMapWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.sqlMapResultMapWithBLOBsElementGenerated(element, 
	      introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.sqlMapSelectByExampleWithoutBLOBsElementGenerated(
	      element, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean sqlMapSelectByExampleWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.sqlMapSelectByExampleWithBLOBsElementGenerated(element, 
	      introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean sqlMapSelectByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.sqlMapSelectByPrimaryKeyElementGenerated(element, 
	      introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.sqlMapGenerated(sqlMap, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean sqlMapUpdateByExampleSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.sqlMapUpdateByExampleSelectiveElementGenerated(element, 
	      introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean sqlMapUpdateByExampleWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.sqlMapUpdateByExampleWithBLOBsElementGenerated(element, 
	      introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean sqlMapUpdateByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.sqlMapUpdateByExampleWithoutBLOBsElementGenerated(
	      element, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.sqlMapUpdateByPrimaryKeySelectiveElementGenerated(
	      element, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(
	      element, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(
	      element, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean clientCountByExampleMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.clientCountByExampleMethodGenerated(method, interfaze, 
	      introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean clientCountByExampleMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.clientCountByExampleMethodGenerated(method, topLevelClass, 
	      introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean clientDeleteByExampleMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.clientDeleteByExampleMethodGenerated(method, interfaze, 
	      introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean clientDeleteByExampleMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.clientDeleteByExampleMethodGenerated(method, 
	      topLevelClass, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.clientDeleteByPrimaryKeyMethodGenerated(method, interfaze, 
	      introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.clientDeleteByPrimaryKeyMethodGenerated(method, 
	      topLevelClass, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean clientInsertMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.clientInsertMethodGenerated(method, interfaze, 
	      introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean clientInsertMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.clientInsertMethodGenerated(method, topLevelClass, 
	      introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.clientGenerated(interfaze, topLevelClass, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean clientSelectAllMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.clientSelectAllMethodGenerated(method, 
	      topLevelClass, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean clientSelectAllMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.clientSelectAllMethodGenerated(method, 
	      interfaze, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean clientSelectByExampleWithBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.clientSelectByExampleWithBLOBsMethodGenerated(method, 
	      interfaze, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean clientSelectByExampleWithBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.clientSelectByExampleWithBLOBsMethodGenerated(method, 
	      topLevelClass, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean clientSelectByExampleWithoutBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.clientSelectByExampleWithoutBLOBsMethodGenerated(method, 
	      interfaze, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean clientSelectByExampleWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.clientSelectByExampleWithoutBLOBsMethodGenerated(method, 
	      topLevelClass, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.clientSelectByPrimaryKeyMethodGenerated(method, interfaze, 
	      introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean clientSelectCountMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.clientSelectCountMethodGenerated(method, interfaze, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.clientSelectByPrimaryKeyMethodGenerated(method, 
	      topLevelClass, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean clientUpdateByExampleSelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.clientUpdateByExampleSelectiveMethodGenerated(method, 
	      interfaze, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean clientUpdateByExampleSelectiveMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.clientUpdateByExampleSelectiveMethodGenerated(method, 
	      topLevelClass, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean clientUpdateByExampleWithBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.clientUpdateByExampleWithBLOBsMethodGenerated(method, 
	      interfaze, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean clientUpdateByExampleWithBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.clientUpdateByExampleWithBLOBsMethodGenerated(method, 
	      topLevelClass, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean clientUpdateByExampleWithoutBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.clientUpdateByExampleWithoutBLOBsMethodGenerated(method, 
	      interfaze, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean clientUpdateByExampleWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.clientUpdateByExampleWithoutBLOBsMethodGenerated(method, 
	      topLevelClass, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.clientUpdateByPrimaryKeySelectiveMethodGenerated(method, 
	      interfaze, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.clientUpdateByPrimaryKeySelectiveMethodGenerated(method, 
	      topLevelClass, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(method, 
	      interfaze, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(method, 
	      topLevelClass, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(
	      method, interfaze, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(
	      method, topLevelClass, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles() {
	  List answer = new ArrayList();
	  for (Plugin plugin : this.plugins) {
	    List temp = plugin
	      .contextGenerateAdditionalJavaFiles();
	    if (temp != null) {
	      answer.addAll(temp);
	    }
	  }
	  return answer;
	}
	
	public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles() {
	  List answer = new ArrayList();
	  for (Plugin plugin : this.plugins) {
	    List temp = plugin
	      .contextGenerateAdditionalXmlFiles();
	    if (temp != null) {
	      answer.addAll(temp);
	    }
	  }
	  return answer;
	}
	
	public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.sqlMapDocumentGenerated(document, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, Plugin.ModelClassType modelClassType)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.modelFieldGenerated(field, topLevelClass, 
	      introspectedColumn, introspectedTable, modelClassType)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, Plugin.ModelClassType modelClassType)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.modelGetterMethodGenerated(method, topLevelClass, 
	      introspectedColumn, introspectedTable, modelClassType)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, Plugin.ModelClassType modelClassType)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.modelSetterMethodGenerated(method, topLevelClass, 
	      introspectedColumn, introspectedTable, modelClassType)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.sqlMapInsertSelectiveElementGenerated(element, 
	      introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean clientInsertSelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.clientInsertSelectiveMethodGenerated(method, interfaze, 
	      introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean clientInsertSelectiveMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.clientInsertSelectiveMethodGenerated(method, 
	      topLevelClass, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public void initialized(IntrospectedTable introspectedTable) {
	  for (Plugin plugin : this.plugins)
	    plugin.initialized(introspectedTable);
	}
	
	public boolean sqlMapBaseColumnListElementGenerated(XmlElement element, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.sqlMapBaseColumnListElementGenerated(element, 
	      introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean sqlMapBlobColumnListElementGenerated(XmlElement element, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.sqlMapBlobColumnListElementGenerated(element, 
	      introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean providerGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.providerGenerated(topLevelClass, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean providerApplyWhereMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.providerApplyWhereMethodGenerated(method, 
	      topLevelClass, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean providerCountByExampleMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.providerCountByExampleMethodGenerated(method, 
	      topLevelClass, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean providerDeleteByExampleMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.providerDeleteByExampleMethodGenerated(method, 
	      topLevelClass, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean providerInsertSelectiveMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.providerInsertSelectiveMethodGenerated(method, 
	      topLevelClass, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean providerSelectByExampleWithBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.providerSelectByExampleWithBLOBsMethodGenerated(method, 
	      topLevelClass, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean providerSelectByExampleWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.providerSelectByExampleWithoutBLOBsMethodGenerated(method, 
	      topLevelClass, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean providerUpdateByExampleSelectiveMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.providerUpdateByExampleSelectiveMethodGenerated(method, 
	      topLevelClass, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean providerUpdateByExampleWithBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.providerUpdateByExampleWithBLOBsMethodGenerated(method, 
	      topLevelClass, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean providerUpdateByExampleWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.providerUpdateByExampleWithoutBLOBsMethodGenerated(method, 
	      topLevelClass, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean providerUpdateByPrimaryKeySelectiveMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.providerUpdateByPrimaryKeySelectiveMethodGenerated(method, 
	      topLevelClass, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
	
	public boolean sqlMapSelectAllElementGenerated(XmlElement element, IntrospectedTable introspectedTable)
	{
	  boolean rc = true;
	
	  for (Plugin plugin : this.plugins) {
	    if (!plugin.sqlMapSelectAllElementGenerated(element, introspectedTable)) {
	      rc = false;
	      break;
	    }
	  }
	
	  return rc;
	}
}