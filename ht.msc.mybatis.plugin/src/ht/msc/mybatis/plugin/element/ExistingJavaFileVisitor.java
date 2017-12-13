package ht.msc.mybatis.plugin.element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;


public class ExistingJavaFileVisitor extends ASTVisitor
{
  private TypeDeclaration typeDeclaration;
  private String[] javadocTags;
  private List<String> generatedInnerClassesToKeep;
  private Map<String, List<Annotation>> fieldAnnotations;
  private Map<String, List<Annotation>> methodAnnotations;

  public ExistingJavaFileVisitor(String[] javadocTags)
  {
    this.javadocTags = javadocTags;
    this.generatedInnerClassesToKeep = new ArrayList();
    this.fieldAnnotations = new HashMap();
    this.methodAnnotations = new HashMap();
  }

  public boolean visit(FieldDeclaration node)
  {
    if (isGenerated(node)) {
      List annotations = retrieveAnnotations(node);
      if (!annotations.isEmpty()) {
        VariableDeclarationFragment variable = 
          (VariableDeclarationFragment)node
          .fragments().get(0);
        this.fieldAnnotations.put(variable.getName().getIdentifier(), 
          annotations);
      }
      node.delete();
    }

    return false;
  }

  public boolean visit(MethodDeclaration node)
  {
    if (isGenerated(node)) {
      List annotations = retrieveAnnotations(node);
      if (!annotations.isEmpty()) {
        String methodSignature = 
          EclipseDomUtils.getMethodSignature(node);
        this.methodAnnotations.put(methodSignature, annotations);
      }
      node.delete();
    }

    return false;
  }

  public boolean visit(TypeDeclaration node)
  {
    if (node.getParent().getNodeType() == 15) {
      this.typeDeclaration = node;
      return true;
    }

    if (isGenerated(node)) {
      node.delete();
    }

    return false;
  }

  public boolean visit(EnumDeclaration node)
  {
    if (isGenerated(node)) {
      node.delete();
    }

    return false;
  }

  public TypeDeclaration getTypeDeclaration() {
    return this.typeDeclaration;
  }

  private boolean isGenerated(BodyDeclaration node)
  {
    boolean rc = false;
    Javadoc jd = node.getJavadoc();
    if (jd != null) {
      List<TagElement> tags = jd.tags();
      for (TagElement tag : tags) {
        String tagName = tag.getTagName();
        if (tagName != null)
        {
          for (String javadocTag : this.javadocTags) {
            if (tagName.equals(javadocTag)) {
              String string = tag.toString();
              if (string.contains("do_not_delete_during_merge")) {
                if (node.getNodeType() != 55) break;
                String name = ((TypeDeclaration)node)
                  .getName().getFullyQualifiedName();
                this.generatedInnerClassesToKeep.add(name); break;
              }

              rc = true;

              break;
            }
          }
        }
      }
    }
    return rc;
  }

  public boolean containsInnerClass(String name) {
    return this.generatedInnerClassesToKeep.contains(name);
  }

  private List<Annotation> retrieveAnnotations(BodyDeclaration node) {
    List modifiers = node.modifiers();
    List annotations = new ArrayList();
    for (Iterator localIterator = modifiers.iterator(); localIterator.hasNext(); ) { Object modifier = localIterator.next();
      if ((modifier instanceof Annotation)) {
        annotations.add((Annotation)modifier);
      }
    }
    return annotations;
  }

  public List<Annotation> getFieldAnnotations(FieldDeclaration fieldDeclaration)
  {
    VariableDeclarationFragment variable = 
      (VariableDeclarationFragment)fieldDeclaration
      .fragments().get(0);
    return (List)this.fieldAnnotations.get(variable.getName().getIdentifier());
  }

  public List<Annotation> getMethodAnnotations(MethodDeclaration methodDeclaration)
  {
    return (List)this.methodAnnotations.get(EclipseDomUtils.getMethodSignature(methodDeclaration));
  }
}