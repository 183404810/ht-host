package ht.msc.mybatis.plugin.element;

import ht.msc.mybatis.plugin.AbstractGenerator;
import ht.msc.mybatis.plugin.TopLevelClass;

public abstract class AbstractJavaProviderMethodGenerator extends AbstractGenerator
{
  public abstract void addClassElements(TopLevelClass paramTopLevelClass);
}