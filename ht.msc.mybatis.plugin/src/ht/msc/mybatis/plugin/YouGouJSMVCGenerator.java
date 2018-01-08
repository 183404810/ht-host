package ht.msc.mybatis.plugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class YouGouJSMVCGenerator extends AbstractJSGenerator
{
  private List<TableColumModel> tableColumModelList = new ArrayList();
  private String tableComment = "";
  private String projectName = "";

  public List<CompilationUnit> getCompilationUnits()
  {
    FullyQualifiedJavaType type = new FullyQualifiedJavaType(this.introspectedTable.getMyBatis3JSMVCType());

    Interface interfazemodel = new Interface(type);
    this.projectName = getProjectName(interfazemodel.getType().toString());
    String modelName = interfazemodel.getType().getShortName().replace("Model", "");
    String tableName = TableUtils.convertJaveBeanStrToUnderLine(firstCharToLower(modelName));

    JDBCConnectionConfiguration jdbcConfig = this.context.getJdbcConnectionConfiguration();
    try {
      Connection conn = ConnectionFactory.getInstance().getConnection(jdbcConfig);
      interfazemodel.addFileCommentLine("interfazemodel");
      this.tableColumModelList = TableUtils.getTableNameCOLUMNS(tableName, conn);
      this.tableComment = ((TableColumModel)this.tableColumModelList.get(0)).getComment();
      interfazemodel.addJSFileContentLine(createModelFile(modelName, tableName, conn));
    } catch (SQLException e) {
      e.printStackTrace();
    }

    Interface interfaze_view_default = new Interface(type);
    interfaze_view_default.addFileCommentLine("view_default");
    interfaze_view_default.addJSFileContentLine(createViewDefaultFile(modelName, tableName, null));

    Interface interfaze_view_model = new Interface(type);
    interfaze_view_model.addFileCommentLine("view_model");
    interfaze_view_model.addJSFileContentLine(createViewModelFile(modelName, tableName, null));

    Interface interfaze_view_controller = new Interface(type);
    interfaze_view_controller.addFileCommentLine("view_controller");
    interfaze_view_controller.addJSFileContentLine(createViewControllerFile(modelName, tableName, null));

    List answer = new ArrayList();
    answer.add(interfazemodel);
    answer.add(interfaze_view_default);
    answer.add(interfaze_view_model);
    answer.add(interfaze_view_controller);
    return answer;
  }

  private String createModelFile(String modelName, String tablename, Connection conn)
  {
    StringBuffer sfields = new StringBuffer();
    int listsize = this.tableColumModelList.size();
    TableColumModel tmp = new TableColumModel();
    String type = "";
    for (int i = 1; i < listsize; i++) {
      tmp = (TableColumModel)this.tableColumModelList.get(i);
      if (tmp.getType().contains("char")) {
        type = "string";
      }
      else if (tmp.getType().contains("datetime")) {
        type = "date";
      }
      else {
        type = tmp.getType();
      }
      sfields.append("         {name: '" + TableUtils.convertUnderLineStrToJaveBean(tmp.getName()) + "', text: '" + tmp.getComment() + "', type: '" + type + "'}," + "\n");
    }
    StringBuffer sbf = new StringBuffer();
    sbf.append(getJsFileComment("(app/model)") + "\n");
    sbf.append("Ext.define('" + this.projectName + ".model." + modelName + "', {" + "\n");
    sbf.append("    extend: 'Ext.data.Model',\n");
    sbf.append("    alias: 'model." + modelName.toLowerCase() + "'," + "\n");
    sbf.append("     fields: [\n");
    sbf.append(sfields.toString());
    sbf.append("         ],\n");
    sbf.append("    idProperty: 'appNo',\n");
    sbf.append("    identifier: 'negative'\n");
    sbf.append("  });");
    return sbf.toString();
  }

  private String createViewDefaultFile(String modelName, String tablename, Connection conn)
  {
    StringBuffer sfields = new StringBuffer();
    int listsize = this.tableColumModelList.size();
    TableColumModel tmp = new TableColumModel();
    for (int i = 1; i < listsize; i++) {
      tmp = (TableColumModel)this.tableColumModelList.get(i);
      sfields.append("\t\t\t\t{dataIndex: '" + TableUtils.convertUnderLineStrToJaveBean(tmp.getName()) + "', text: '" + tmp.getComment() + "', editor: {allowBlank: false}}," + "\n");
    }
    StringBuffer sbf = new StringBuffer();
    sbf.append(getJsFileComment("(default)") + "\n");
    sbf.append("Ext.define('" + this.projectName + ".view." + modelName.toLowerCase() + "." + modelName + "', {" + "\n");
    sbf.append("\textend: 'Belle_Common.view.BaseSimplePage',\n");
    sbf.append("\talias: 'widget." + modelName.toLowerCase() + "'," + "\n");
    sbf.append("\trequires: [\n");
    sbf.append("\t\t'" + this.projectName + ".model." + modelName + "'," + "\n");
    sbf.append("\t\t'" + this.projectName + ".view." + modelName.toLowerCase() + "." + modelName + "Controller'," + "\n");
    sbf.append("\t\t'" + this.projectName + ".view." + modelName.toLowerCase() + "." + modelName + "Model'" + "\n");
    sbf.append("\t],\n");
    sbf.append("\tcontroller: '" + modelName.toLowerCase() + "'," + "\n");
    sbf.append("\tviewModel: {\n");
    sbf.append("\t\ttype: '" + modelName.toLowerCase() + "'" + "\n");
    sbf.append("\t},\n");
    sbf.append("\tinitComponent: function () {\n");
    sbf.append("\t\tvar me= this;\n");
    sbf.append("\t\tExt.apply(me.searchPanel, {\n");
    sbf.append("\t\t\titems: []\n");
    sbf.append("\t\t});\n");
    sbf.append("\t\tExt.apply(me.grid, {\n");
    sbf.append("\t\t\tcolumns: [\n");

    sbf.append(sfields.toString());
    sbf.append("\t\t\t],\n");
    sbf.append("\t\t\tbatchUrl: Belle.IntegPath + 'rest/" + tablename + "/listsave.json'" + "\n");
    sbf.append("\t\t});\n");
    sbf.append("\t\tme.callParent();\n");
    sbf.append("\t}\n");
    sbf.append("});\n");
    return sbf.toString();
  }

  private String createViewControllerFile(String modelName, String tablename, Connection conn)
  {
    StringBuffer sbf = new StringBuffer();
    sbf.append(getJsFileComment("Controller") + "\n");
    sbf.append("Ext.define('" + this.projectName + ".view." + modelName.toLowerCase() + "." + modelName + "Controller', {" + "\n");
    sbf.append("    extend: 'Belle_Common.view.BaseSimplePageController',\n");
    sbf.append("    alias: 'controller." + modelName.toLowerCase() + "'" + "\n");
    sbf.append("  });");
    return sbf.toString();
  }

  private String createViewModelFile(String modelName, String tablename, Connection conn)
  {
    StringBuffer sbf = new StringBuffer();
    sbf.append(getJsFileComment("Model") + " \n");
    sbf.append("Ext.define('" + this.projectName + ".view." + modelName.toLowerCase() + "." + modelName + "Model', {" + "\n");
    sbf.append("    extend: 'Belle_Common.view.BaseSimplePageModel',\n");
    sbf.append("    alias: 'viewmodel." + modelName.toLowerCase() + "'," + "\n");
    sbf.append("    stores: {\n");
    sbf.append("    \tcommonstore:{\n");
    sbf.append("   \t\t\tmodel:'" + this.projectName + ".model." + modelName + "'," + "\n");
    sbf.append("    \t\tproxy: {\n");
    sbf.append("    \t\turl: Belle.IntegPath + 'rest/" + tablename + "/list.json'" + "\n");
    sbf.append("    \t\t}\n");
    sbf.append("   \t\t }\n");
    sbf.append("    }\n");
    sbf.append("  });");
    return sbf.toString();
  }

  private String getJsFileComment(String layout)
  {
    String comment = YougouCommentGenerator.addJSFileComment().replace("${d}", this.tableComment + layout);

    return comment;
  }

  private String getProjectName(String fullpackage)
  {
    String projectName = fullpackage.substring(fullpackage.indexOf("scm.") + 4, fullpackage.indexOf(".web"));
    if ("uc".equals(projectName)) {
      projectName = "Belle_Framework";
    }
    else {
      projectName = "Belle_" + projectName;
    }
    Tools.writeLine("fullpackage:" + fullpackage + " projectName:" + projectName);
    return projectName;
  }

  private String firstCharToLower(String modelName)
  {
    if (modelName.length() > 0)
      modelName = new StringBuilder(String.valueOf(modelName.charAt(0))).toString().toLowerCase() + modelName.substring(1);
    else {
      Character.toLowerCase(modelName.charAt(0));
    }
    return modelName;
  }
}