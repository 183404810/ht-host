package ht.msc.mybatis.plugin.element;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.text.BadLocationException;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;


public class JavaFileMerger
{
  private String newJavaSource;
  private String existingFilePath;
  private String[] javaDocTags;
  private String fileEncoding;

  public JavaFileMerger(String newJavaSource, String existingFilePath, String[] javaDocTags, String fileEncoding)
  {
    this.newJavaSource = newJavaSource;
    this.existingFilePath = existingFilePath;
    this.javaDocTags = javaDocTags;
    this.fileEncoding = fileEncoding;
  }

  public String getMergedSource() throws ShellException
  {
    ASTParser astParser = ASTParser.newParser(3);
    NewJavaFileVisitor newJavaFileVisitor = visitNewJavaFile(astParser);

    String existingFile = getExistingFileContents();
    IDocument document = new Document(existingFile);

    ExistingJavaFileVisitor visitor = new ExistingJavaFileVisitor(
      this.javaDocTags);

    astParser.setSource(existingFile.toCharArray());
    CompilationUnit cu = (CompilationUnit)astParser.createAST(null);
    AST ast = cu.getAST();
    cu.recordModifications();
    cu.accept(visitor);

    TypeDeclaration typeDeclaration = visitor.getTypeDeclaration();
    if (typeDeclaration == null) {
      StringBuffer sb = new StringBuffer();
      sb.append("No types defined in the file ");
      sb.append(this.existingFilePath);

      throw new ShellException(sb.toString());
    }

    List<Type> newSuperInterfaces = getNewSuperInterfaces(
      typeDeclaration.superInterfaceTypes(), newJavaFileVisitor);
    for (Type newSuperInterface : newSuperInterfaces) {
      typeDeclaration.superInterfaceTypes().add(
        ASTNode.copySubtree(ast, newSuperInterface));
    }

    if (newJavaFileVisitor.getSuperclass() != null)
      typeDeclaration.setSuperclassType((Type)ASTNode.copySubtree(ast, 
        newJavaFileVisitor.getSuperclass()));
    else {
      typeDeclaration.setSuperclassType(null);
    }

    if (newJavaFileVisitor.isInterface())
      typeDeclaration.setInterface(true);
    else {
      typeDeclaration.setInterface(false);
    }

    List<ImportDeclaration> newImports = getNewImports(cu.imports(), 
      newJavaFileVisitor);
    for (ImportDeclaration newImport : newImports) {
      Name name = ast
        .newName(newImport.getName().getFullyQualifiedName());
      ImportDeclaration newId = ast.newImportDeclaration();
      newId.setName(name);
      cu.imports().add(newId);
    }

    TextEdit textEdit = cu.rewrite(document, null);
    try {
      textEdit.apply(document);
    } catch (MalformedTreeException e) {
    	 throw new ShellException(
    		        "BadLocationException removing prior fields and methods");
	} catch (org.eclipse.jface.text.BadLocationException e) {
		 throw new ShellException(
			        "BadLocationException removing prior fields and methods");
	}

    astParser.setSource(document.get().toCharArray());
    CompilationUnit strippedCu = (CompilationUnit)astParser
      .createAST(null);

    TypeDeclaration topLevelType = null;
    Iterator iter = strippedCu.types().iterator();
    while (iter.hasNext()) {
      TypeDeclaration td = (TypeDeclaration)iter.next();
      if ((td.getParent().equals(strippedCu)) && 
        ((td.getModifiers() & 0x1) > 0)) {
        topLevelType = td;
        break;
      }

    }

    if (topLevelType == null)
      return "";
    ASTRewrite rewrite = ASTRewrite.create(topLevelType.getRoot().getAST());
    ListRewrite listRewrite = rewrite.getListRewrite(topLevelType, 
      TypeDeclaration.BODY_DECLARATIONS_PROPERTY);

    Iterator astIter = newJavaFileVisitor.getNewNodes().iterator();
    int i = 0;
    while (astIter.hasNext()) {
      ASTNode node = (ASTNode)astIter.next();

      if (node.getNodeType() == 55) {
        String name = ((TypeDeclaration)node).getName()
          .getFullyQualifiedName();
        if (visitor.containsInnerClass(name))
          continue;
      }
      else if ((node instanceof FieldDeclaration)) {
        addExistsAnnotations((BodyDeclaration)node, 
          visitor.getFieldAnnotations((FieldDeclaration)node));
      } else if ((node instanceof MethodDeclaration)) {
        addExistsAnnotations((BodyDeclaration)node, 
          visitor.getMethodAnnotations((MethodDeclaration)node));
      }

      listRewrite.insertAt(node, i++, null);
    }

    textEdit = rewrite.rewriteAST(document, JavaCore.getOptions());
    try {
      textEdit.apply(document);
    } catch (MalformedTreeException e) {
    	throw new ShellException(
    	        "BadLocationException adding new fields and methods");
	} catch (org.eclipse.jface.text.BadLocationException e) {
		throw new ShellException(
		        "BadLocationException adding new fields and methods");
	}

    String newSource = document.get();
    return newSource;
  }

  private List<Type> getNewSuperInterfaces(List<Type> existingSuperInterfaces, NewJavaFileVisitor newJavaFileVisitor)
  {
    List answer = new ArrayList();

    Iterator localIterator1 = newJavaFileVisitor
      .getSuperInterfaceTypes().iterator();

    while (localIterator1.hasNext()) {
      Type newSuperInterface = (Type)localIterator1.next();
      boolean found = false;
      for (Type existingSuperInterface : existingSuperInterfaces) {
        found = EclipseDomUtils.typesMatch(newSuperInterface, 
          existingSuperInterface);
        if (found)
        {
          break;
        }
      }
      if (!found) {
        answer.add(newSuperInterface);
      }
    }

    return answer;
  }

  private List<ImportDeclaration> getNewImports(List<ImportDeclaration> existingImports, NewJavaFileVisitor newJavaFileVisitor)
  {
    List answer = new ArrayList();

    for (ImportDeclaration newImport : newJavaFileVisitor.getImports()) {
      boolean found = false;
      for (ImportDeclaration existingImport : existingImports) {
        found = EclipseDomUtils.importDeclarationsMatch(newImport, 
          existingImport);
        if (found)
        {
          break;
        }
      }
      if (!found) {
        answer.add(newImport);
      }
    }

    return answer;
  }

  private NewJavaFileVisitor visitNewJavaFile(ASTParser astParser)
  {
    astParser.setSource(this.newJavaSource.toCharArray());
    CompilationUnit cu = (CompilationUnit)astParser.createAST(null);
    NewJavaFileVisitor newVisitor = new NewJavaFileVisitor();
    cu.accept(newVisitor);

    return newVisitor;
  }

  private String getExistingFileContents() throws ShellException {
    File file = new File(this.existingFilePath);

    if (!file.exists())
    {
      StringBuilder sb = new StringBuilder();
      sb.append("The file ");
      sb.append(this.existingFilePath);
      sb.append(" does not exist");
      throw new ShellException(sb.toString());
    }
    try
    {
      StringBuilder sb = new StringBuilder();
      FileInputStream fis = new FileInputStream(file);
      InputStreamReader isr;
      if (this.fileEncoding == null)
        isr = new InputStreamReader(fis);
      else {
        isr = new InputStreamReader(fis, this.fileEncoding);
      }
      BufferedReader br = new BufferedReader(isr);
      char[] buffer = new char[1024];
      int returnedBytes = br.read(buffer);
      while (returnedBytes != -1) {
        sb.append(buffer, 0, returnedBytes);
        returnedBytes = br.read(buffer);
      }

      br.close();
      return sb.toString();
    } catch (IOException e) {
      StringBuilder sb = new StringBuilder();
      sb.append("IOException reading the file ");
      sb.append(this.existingFilePath);
      throw new ShellException(sb.toString(), e);
    }
  }

  private void addExistsAnnotations(BodyDeclaration node, List<Annotation> oldAnnotations)
  {
    Set newAnnotationTypes = new HashSet();
    int lastAnnotationIndex = 0;
    int idx = 0;
    for (Iterator localIterator = node.modifiers().iterator(); localIterator.hasNext(); ) { Object modifier = localIterator.next();
      if ((modifier instanceof Annotation)) {
        Annotation newAnnotation = (Annotation)modifier;
        newAnnotationTypes.add(newAnnotation.getTypeName()
          .getFullyQualifiedName());
        lastAnnotationIndex = idx;
      }
      idx++;
    }

    if (oldAnnotations != null)
      for (Annotation oldAnnotation : oldAnnotations)
        if (!newAnnotationTypes.contains(oldAnnotation.getTypeName()
          .getFullyQualifiedName()))
        {
          AST nodeAst = node.getAST();
          node.modifiers().add(lastAnnotationIndex++, 
            ASTNode.copySubtree(nodeAst, oldAnnotation));
        }
  }
}