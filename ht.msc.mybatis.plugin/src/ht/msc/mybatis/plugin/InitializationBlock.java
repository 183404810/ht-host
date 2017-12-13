package ht.msc.mybatis.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public class InitializationBlock
{
  private boolean isStatic;
  private List<String> bodyLines;
  private List<String> javaDocLines;

  public InitializationBlock()
  {
    this(false);
  }

  public InitializationBlock(boolean isStatic) {
    this.isStatic = isStatic;
    this.bodyLines = new ArrayList();
    this.javaDocLines = new ArrayList();
  }

  public boolean isStatic() {
    return this.isStatic;
  }

  public void setStatic(boolean isStatic) {
    this.isStatic = isStatic;
  }

  public List<String> getBodyLines() {
    return this.bodyLines;
  }

  public void addBodyLine(String line) {
    this.bodyLines.add(line);
  }

  public void addBodyLine(int index, String line) {
    this.bodyLines.add(index, line);
  }

  public void addBodyLines(Collection<String> lines) {
    this.bodyLines.addAll(lines);
  }

  public void addBodyLines(int index, Collection<String> lines) {
    this.bodyLines.addAll(index, lines);
  }

  public List<String> getJavaDocLines() {
    return this.javaDocLines;
  }

  public void addJavaDocLine(String javaDocLine) {
    this.javaDocLines.add(javaDocLine);
  }

  public String getFormattedContent(int indentLevel) {
    StringBuilder sb = new StringBuilder();

    for (String javaDocLine : this.javaDocLines) {
      OutputUtilities.javaIndent(sb, indentLevel);
      sb.append(javaDocLine);
      OutputUtilities.newLine(sb);
    }

    OutputUtilities.javaIndent(sb, indentLevel);

    if (this.isStatic) {
      sb.append("static ");
    }

    sb.append('{');
    indentLevel++;

    ListIterator listIter = this.bodyLines.listIterator();
    while (listIter.hasNext()) {
      String line = (String)listIter.next();
      if (line.startsWith("}")) {
        indentLevel--;
      }

      OutputUtilities.newLine(sb);
      OutputUtilities.javaIndent(sb, indentLevel);
      sb.append(line);

      if (((line.endsWith("{")) && (!line.startsWith("switch"))) || 
        (line.endsWith(":"))) {
        indentLevel++;
      }

      if (line.startsWith("break"))
      {
        if (listIter.hasNext()) {
          String nextLine = (String)listIter.next();
          if (nextLine.startsWith("}")) {
            indentLevel++;
          }

          listIter.previous();
        }
        indentLevel--;
      }
    }

    indentLevel--;
    OutputUtilities.newLine(sb);
    OutputUtilities.javaIndent(sb, indentLevel);
    sb.append('}');

    return sb.toString();
  }
}