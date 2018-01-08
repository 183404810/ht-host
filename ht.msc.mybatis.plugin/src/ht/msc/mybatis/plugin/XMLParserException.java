package ht.msc.mybatis.plugin;

import java.util.ArrayList;
import java.util.List;

public class XMLParserException extends Exception
{
  private static final long serialVersionUID = 5172525430401340573L;
  private List<String> errors;

  public XMLParserException(List<String> errors)
  {
    this.errors = errors;
  }

  public XMLParserException(String error) {
    super(error);
    this.errors = new ArrayList();
    this.errors.add(error);
  }

  public List<String> getErrors() {
    return this.errors;
  }

  public String getMessage()
  {
    if ((this.errors != null) && (this.errors.size() > 0)) {
      return (String)this.errors.get(0);
    }

    return super.getMessage();
  }
}