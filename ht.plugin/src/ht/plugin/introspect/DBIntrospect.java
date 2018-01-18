package ht.plugin.introspect;

import ht.plugin.configration.config.TableSettingConfig;
import ht.plugin.context.PluginContext;
import ht.plugin.util.StringUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBIntrospect {
	private PluginContext context;
	
	public DBIntrospect(PluginContext context){
		this.context=context;
	}
	
	public void introspect(List<String> tableList){
		try {
			Connection conn = context.getConn();
			TableSettingConfig tsconfig=context.getConfig().getTbConfig();
			DatabaseMetaData rs=conn.getMetaData();
			for(String tb:tableList){
				List<IColumn> column=new ArrayList<>();
				ITable table=new ITable(column);
				context.getTables().add(table);
				String[] tbinfo=tb.split("\\.");
				if(tbinfo.length<2) continue;
				if(!tsconfig.isSchema()){
					table.setCatalog(tbinfo[0]);
				}
				if(tsconfig.isSchema()){
					table.setSchema(tbinfo[0]);
				}
				if(!StringUtils.isEmpty(tbinfo[1]))
					table.setTableName(tbinfo[1]);
				
				calPrimaryKey(rs,table);
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void calPrimaryKey(DatabaseMetaData dmd,ITable table){
		try {
			List<IColumn> column=new ArrayList<>();
			ResultSet rs=dmd.getPrimaryKeys(table.getCatalog(),table.getSchema(), table.getTableName());
			while(rs.next()){
				String columnName=rs.getString("COLUMN_NAME");
				IColumn col=new IColumn(table);
				col.setTable(table);
				col.setColumnName(columnName);
				column.add(col);
				table.setColumns(column);
				//col.setLength(length)
				//col.setRemarks(remarks);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
}
