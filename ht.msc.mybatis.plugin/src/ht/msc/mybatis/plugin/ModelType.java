package ht.msc.mybatis.plugin;

public enum ModelType
{
  HIERARCHICAL("hierarchical"), 
  FLAT("flat"), 
  CONDITIONAL("conditional");

  private final String modelType;

  private ModelType(String modelType)
  {
    this.modelType = modelType;
  }

  public String getModelType() {
    return this.modelType;
  }

  public static ModelType getModelType(String type) {
    if (HIERARCHICAL.getModelType().equalsIgnoreCase(type))
      return HIERARCHICAL;
    if (FLAT.getModelType().equalsIgnoreCase(type))
      return FLAT;
    if (CONDITIONAL.getModelType().equalsIgnoreCase(type)) {
      return CONDITIONAL;
    }
    throw new RuntimeException(Messages.getString(
      "RuntimeError.13", type));
  }
}