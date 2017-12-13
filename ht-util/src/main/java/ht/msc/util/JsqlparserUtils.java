package ht.msc.util;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.util.TablesNamesFinder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsqlparserUtils {
	
	private JsqlparserUtils(){
		
	}
	
	protected  static Logger logger = LoggerFactory.getLogger(JsqlparserUtils.class);
	
	/**
	 * 从sql中获取全部的表名
	 * @param sql
	 * @return
	 */
	public static List<String> getTableNames(String sql){
		List<String> tableList = null;
		try {
			Statement statement = CCJSqlParserUtil.parse(sql);
			Select selectStatement = (Select) statement;
			TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
			tableList = tablesNamesFinder.getTableList(selectStatement);
		} catch (JSQLParserException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logger.error("",e);
		}
		
		return tableList;
	}
	
	/**
	 * 获取sql中的全部查询字段
	 * @param sql
	 * @return
	 */
	public static List<String> getColumnNames(String sql){
		List<String> columnList = new ArrayList<String>();
		try {
			Statement statement = CCJSqlParserUtil.parse(sql);
			Select selectStatement = (Select) statement;
			if(selectStatement != null){
				SelectBody selectBody = selectStatement.getSelectBody();
				if (selectBody instanceof PlainSelect) {
					PlainSelect plainSelect = (PlainSelect) selectBody;
					List<SelectItem> list = plainSelect.getSelectItems();
					if(list != null && list.size() > 0){
						for(SelectItem item : list){
							columnList.add(item.toString());
						}
					}
			    } else if (selectBody instanceof SetOperationList) {
			    	SetOperationList operationList = (SetOperationList) selectBody;
			        if (operationList.getPlainSelects() != null && operationList.getPlainSelects().size() > 0) {
			            List<PlainSelect> plainSelects = operationList.getPlainSelects();
			            if(plainSelects != null && plainSelects.size() > 0){
			            	PlainSelect plainSelect = plainSelects.get(0);
			            	List<SelectItem> list = plainSelect.getSelectItems();
							if(list != null && list.size() > 0){
								for(SelectItem item : list){
									if(item != null){
										columnList.add(item.toString());
									}
									
								}
							}
			            }
			        }
			    }
			}
		} catch (JSQLParserException e) {
			//e.printStackTrace();
			logger.error("",e);
		}
		
		return columnList;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//String sql = "SELECT c1,c2,c3 FROM A a left join B b on a.id = b.id right join C c on b.name = c.name where 1 = 1 UNION ALL select c1,c2,c3 from D , E ,F where 2=2";
		//String sql="select a.id,a.name,b.x,b.y,c.prdname from table1 a inner join table2 b on a.orderno=b.orderno  inner join table3 c on a.prdname=c.prdname where a.name='崔永远'";
		String sql="select a.id,a.name,b.x,b.y,c.prdname from table1 a ,table2 b,table3 c where a.orderno=b.orderno and a.prdname=c.prdname and a.name='崔永远'";

		List<String> list = getTableNames(sql);
		if(list !=null && list.size() > 0){
			for(String name : list){
				System.out.println("table name: " + name);
			}
		}
		
		List<String> list2 = getColumnNames(sql);
		if(list2 !=null && list2.size() > 0){
			for(String name : list2){
				System.out.println("column name: " + name);
			}
		}
	}

}