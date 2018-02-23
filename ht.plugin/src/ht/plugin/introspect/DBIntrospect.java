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
		Connection conn=null;
		try {
			conn = context.getConfig().getDbConfig().getConn(context.getConfig());
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
				calColumn(rs,table);
			}
			
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
	
	public void calColumn(DatabaseMetaData dmd,ITable table){
		try{
			List<IColumn> column=new ArrayList<>();
			table.setColumns(column);
			ResultSet rs=dmd.getColumns(table.getCatalog(),table.getSchema(), table.getTableName(),null);
			while(rs.next()){
				IColumn col=new IColumn(table);
				column.add(col);
				col.setColumnName(rs.getString("COLUMN_NAME"));
				col.setLength(rs.getInt("COLUMN_SIZE"));
				col.setTypeName(rs.getString("TYPE_NAME"));
				col.setRemarks(rs.getString("REMARKS"));
				col.setNullAble(rs.getInt("NULLABLE"));
				col.setDefaultValue(rs.getString("COLUMN_DEF"));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public void calPrimaryKey(DatabaseMetaData dmd,ITable table){
		try {
			List<IColumn> column=new ArrayList<>();
			table.setKeyColumns(column);
			ResultSet rs=dmd.getPrimaryKeys(table.getCatalog(),table.getSchema(), table.getTableName());
			while(rs.next()){
				String columnName=rs.getString("COLUMN_NAME");
				IColumn col=new IColumn(table);
				column.add(col);
				col.setColumnName(columnName);
		 	}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
