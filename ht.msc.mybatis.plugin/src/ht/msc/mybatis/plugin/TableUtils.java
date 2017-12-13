package ht.msc.mybatis.plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TableUtils
{
  public static String convertJaveBeanStrToUnderLine(String javeBeanStr)
  {
    StringBuffer buf = new StringBuffer();
    Pattern p = Pattern.compile("[A-Z]");
    Matcher m = p.matcher(javeBeanStr);
    while (m.find()) {
      m.appendReplacement(buf, "_" + m.group(0));
    }
    m.appendTail(buf);
    return buf.toString().toLowerCase();
  }

  public static String convertUnderLineStrToJaveBean(String underLineStr)
  {
    StringBuffer buf = new StringBuffer(underLineStr);
    Matcher mc = Pattern.compile("_").matcher(underLineStr);
    int i = 0;
    while (mc.find()) {
      int position = mc.end() - i++;
      buf.replace(position - 1, position + 1, 
        buf.substring(position, position + 1).toUpperCase());
    }
    return buf.toString();
  }

  public static List<TableColumModel> getTableNameCOLUMNS(String tablename, Connection conn)
  {
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    List list = null;
    try
    {
      StringBuffer sbf = new StringBuffer();
      sbf.append("");
      sbf.append("Select table_name as `name`, Table_TYPE as `type`,TABLE_COMMENT  as `comment`,'' isNullAble,'' maxLength,'' columnKey ");
      sbf.append("from INFORMATION_SCHEMA.TABLES Where TABLE_NAME = ? ");
      sbf.append("UNION ");
      sbf.append("SELECT COLUMN_NAME as `name`, DATA_TYPE as `type`, COLUMN_COMMENT as `comment`,IS_NULLABLE as isNullAble, ");
      sbf.append("  case DATA_TYPE ");
      sbf.append("          when 'varchar' then CHARACTER_MAXIMUM_LENGTH ");
      sbf.append("          when 'datetime' then 19 ");
      sbf.append("          else NUMERIC_PRECISION ");
      sbf.append("  END AS maxLength,COLUMN_KEY as columnKey  ");
      sbf.append("FROM INFORMATION_SCHEMA.COLUMNS  WHERE TABLE_NAME = ?");

      pstmt = conn.prepareStatement(sbf.toString());
      pstmt.setString(1, tablename);
      pstmt.setString(2, tablename);
      rs = pstmt.executeQuery();
      list = new ArrayList();
      TableColumModel tmp = new TableColumModel();
      while (rs.next()) {
        tmp = new TableColumModel();
        tmp.setName(rs.getString("name"));
        tmp.setType(rs.getString("type"));
        tmp.setComment(rs.getString("comment") != null ? rs.getString("comment") : rs.getString("name"));
        tmp.setIsNullAble(rs.getString("isNullAble"));
        tmp.setMaxLength(Integer.valueOf((rs.getString("maxLength") == null) || ("".equals(rs.getString("maxLength"))) ? 50 : Integer.parseInt(rs.getString("maxLength"))));
        tmp.setColumnKey(rs.getString("columnKey"));
        list.add(tmp);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      ConnectionUtils.closeConnection(conn, pstmt, rs);
    }
    return list;
  }
}