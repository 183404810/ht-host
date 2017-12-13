package ht.msc.mybatis.plugin;


public abstract class AbstractDAOElementGenerator extends AbstractGenerator
{
  protected AbstractDAOTemplate daoTemplate;
  private DAOMethodNameCalculator dAOMethodNameCalculator;
  private JavaVisibility exampleMethodVisibility;

  public abstract void addInterfaceElements(Interface paramInterface);

  public abstract void addImplementationElements(TopLevelClass paramTopLevelClass);

  public void setDAOTemplate(AbstractDAOTemplate abstractDAOTemplate)
  {
    this.daoTemplate = abstractDAOTemplate;
  }

  public DAOMethodNameCalculator getDAOMethodNameCalculator() {
    if (this.dAOMethodNameCalculator == null) {
      String type = this.context.getJavaClientGeneratorConfiguration()
        .getProperty("methodNameCalculator");
      if (StringUtility.stringHasValue(type)) {
        if ("extended".equalsIgnoreCase(type))
          type = ExtendedDAOMethodNameCalculator.class.getName();
        else if ("default".equalsIgnoreCase(type))
          type = DefaultDAOMethodNameCalculator.class.getName();
      }
      else {
        type = DefaultDAOMethodNameCalculator.class.getName();
      }
      try
      {
        this.dAOMethodNameCalculator = ((DAOMethodNameCalculator)
          ObjectFactory.createInternalObject(type));
      } catch (Exception e) {
        this.dAOMethodNameCalculator = new DefaultDAOMethodNameCalculator();
        this.warnings.add(Messages.getString(
          "Warning.17", type, e.getMessage()));
      }
    }

    return this.dAOMethodNameCalculator;
  }

  public JavaVisibility getExampleMethodVisibility() {
    if (this.exampleMethodVisibility == null) {
      String type = this.context
        .getJavaClientGeneratorConfiguration()
        .getProperty("exampleMethodVisibility");
      if (StringUtility.stringHasValue(type)) {
        if ("public".equalsIgnoreCase(type)) {
          this.exampleMethodVisibility = JavaVisibility.PUBLIC;
        } else if ("private".equalsIgnoreCase(type)) {
          this.exampleMethodVisibility = JavaVisibility.PRIVATE;
        } else if ("protected".equalsIgnoreCase(type)) {
          this.exampleMethodVisibility = JavaVisibility.PROTECTED;
        } else if ("default".equalsIgnoreCase(type)) {
          this.exampleMethodVisibility = JavaVisibility.DEFAULT;
        } else {
          this.exampleMethodVisibility = JavaVisibility.PUBLIC;
          this.warnings.add(Messages.getString("Warning.16", type));
        }
      }
      else this.exampleMethodVisibility = JavaVisibility.PUBLIC;

    }

    return this.exampleMethodVisibility;
  }
}