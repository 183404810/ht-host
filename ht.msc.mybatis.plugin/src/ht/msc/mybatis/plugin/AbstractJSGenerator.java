package ht.msc.mybatis.plugin;

import java.util.List;

public abstract class AbstractJSGenerator extends AbstractGenerator
{
  public abstract List<CompilationUnit> getCompilationUnits();
}