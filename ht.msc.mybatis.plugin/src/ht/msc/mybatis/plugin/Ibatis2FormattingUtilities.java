package ht.msc.mybatis.plugin;

import java.util.StringTokenizer;


public class Ibatis2FormattingUtilities
{
  public static String getEscapedColumnName(IntrospectedColumn introspectedColumn)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(escapeStringForIbatis2(introspectedColumn
      .getActualColumnName()));

    if (introspectedColumn.isColumnNameDelimited()) {
      sb.insert(0, introspectedColumn.getContext()
        .getBeginningDelimiter());
      sb.append(introspectedColumn.getContext().getEndingDelimiter());
    }

    return sb.toString();
  }

  public static String getAliasedEscapedColumnName(IntrospectedColumn introspectedColumn)
  {
    if (StringUtility.stringHasValue(introspectedColumn.getTableAlias())) {
      StringBuilder sb = new StringBuilder();

      sb.append(introspectedColumn.getTableAlias());
      sb.append('.');
      sb.append(getEscapedColumnName(introspectedColumn));
      return sb.toString();
    }
    return getEscapedColumnName(introspectedColumn);
  }

  public static String getParameterClause(IntrospectedColumn introspectedColumn)
  {
    return getParameterClause(introspectedColumn, null);
  }

  public static String getParameterClause(IntrospectedColumn introspectedColumn, String prefix)
  {
    StringBuilder sb = new StringBuilder();

    sb.append('#');
    sb.append(introspectedColumn.getJavaProperty(prefix));

    if (StringUtility.stringHasValue(introspectedColumn.getTypeHandler())) {
      sb.append(",jdbcType=");
      sb.append(introspectedColumn.getJdbcTypeName());
      sb.append(",handler=");
      sb.append(introspectedColumn.getTypeHandler());
    } else {
      sb.append(':');
      sb.append(introspectedColumn.getJdbcTypeName());
    }

    sb.append('#');

    return sb.toString();
  }

  public static String getSelectListPhrase(IntrospectedColumn introspectedColumn)
  {
    if (StringUtility.stringHasValue(introspectedColumn.getTableAlias())) {
      StringBuilder sb = new StringBuilder();

      sb.append(getAliasedEscapedColumnName(introspectedColumn));
      sb.append(" as ");
      if (introspectedColumn.isColumnNameDelimited()) {
        sb.append(introspectedColumn.getContext()
          .getBeginningDelimiter());
      }
      sb.append(introspectedColumn.getTableAlias());
      sb.append('_');
      sb.append(escapeStringForIbatis2(introspectedColumn
        .getActualColumnName()));
      if (introspectedColumn.isColumnNameDelimited()) {
        sb.append(introspectedColumn.getContext().getEndingDelimiter());
      }
      return sb.toString();
    }
    return getEscapedColumnName(introspectedColumn);
  }

  public static String escapeStringForIbatis2(String s)
  {
    StringTokenizer st = new StringTokenizer(s, "$#", true);
    StringBuilder sb = new StringBuilder();
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      if ("$".equals(token))
        sb.append("$$");
      else if ("#".equals(token))
        sb.append("##");
      else {
        sb.append(token);
      }
    }

    return sb.toString();
  }

  public static String getAliasedActualColumnName(IntrospectedColumn introspectedColumn)
  {
    StringBuilder sb = new StringBuilder();
    if (StringUtility.stringHasValue(introspectedColumn.getTableAlias())) {
      sb.append(introspectedColumn.getTableAlias());
      sb.append('.');
    }

    if (introspectedColumn.isColumnNameDelimited()) {
      sb.append(StringUtility.escapeStringForJava(introspectedColumn
        .getContext().getBeginningDelimiter()));
    }

    sb.append(introspectedColumn.getActualColumnName());

    if (introspectedColumn.isColumnNameDelimited()) {
      sb.append(StringUtility.escapeStringForJava(introspectedColumn
        .getContext().getEndingDelimiter()));
    }

    return sb.toString();
  }

  public static String getRenamedColumnNameForResultMap(IntrospectedColumn introspectedColumn)
  {
    if (StringUtility.stringHasValue(introspectedColumn.getTableAlias())) {
      StringBuilder sb = new StringBuilder();

      sb.append(introspectedColumn.getTableAlias());
      sb.append('_');
      sb.append(introspectedColumn.getActualColumnName());
      return sb.toString();
    }
    return introspectedColumn.getActualColumnName();
  }
}