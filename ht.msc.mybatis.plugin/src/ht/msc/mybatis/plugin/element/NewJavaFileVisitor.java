package ht.msc.mybatis.plugin.element;



import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;


public class NewJavaFileVisitor extends ASTVisitor
{
  private List<ASTNode> newNodes;
  private List<ImportDeclaration> imports;
  private Type superclass;
  private boolean isInterface;
  private List<Type> superInterfaceTypes;

  public NewJavaFileVisitor()
  {
    this.newNodes = new ArrayList();
  }

  public boolean visit(FieldDeclaration node) {
    this.newNodes.add(node);

    return false;
  }

  public boolean visit(EnumDeclaration node) {
    this.newNodes.add(node);

    return false;
  }

  public boolean visit(MethodDeclaration node) {
    this.newNodes.add(node);

    return false;
  }

  public boolean visit(TypeDeclaration node)
  {
    if (node.getParent().getNodeType() == 15) {
      this.isInterface = node.isInterface();

      this.superclass = node.getSuperclassType();

      this.superInterfaceTypes = node.superInterfaceTypes();

      return true;
    }
    this.newNodes.add(node);
    return false;
  }

  public List<ASTNode> getNewNodes()
  {
    return this.newNodes;
  }

  public boolean visit(CompilationUnit node)
  {
    this.imports = node.imports();

    return true;
  }

  public List<ImportDeclaration> getImports() {
    return this.imports;
  }

  public Type getSuperclass() {
    return this.superclass;
  }

  public boolean isInterface() {
    return this.isInterface;
  }

  public List<Type> getSuperInterfaceTypes() {
    return this.superInterfaceTypes;
  }
}