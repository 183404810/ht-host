package ht.msc.mybatis.plugin;

import java.util.List;

public class InvalidConfigurationException extends Exception
{
  static final long serialVersionUID = 4902307610148543411L;
  private List<String> errors;

  public InvalidConfigurationException(List<String> errors)
  {
    this.errors = errors;
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