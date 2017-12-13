package ht.msc.mybatis.plugin.element;

import java.util.Iterator;

import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.UnionType;
import org.eclipse.jdt.core.dom.WildcardType;


public class EclipseDomUtils
{
  public static boolean importDeclarationsMatch(ImportDeclaration import1, ImportDeclaration import2)
  {
    if ((import1 == null) || (import2 == null)) {
      return (import1 == null) && (import2 == null);
    }

    boolean rc = import1.isStatic() == import2.isStatic();

    if (rc) {
      if ((import1.isOnDemand()) && (import2.isOnDemand()))
        rc = import1.getName().getFullyQualifiedName()
          .equals(import2.getName().getFullyQualifiedName());
      else if ((!import1.isOnDemand()) && (!import2.isOnDemand()))
        rc = import1.getName().getFullyQualifiedName()
          .equals(import2.getName().getFullyQualifiedName());
      else if (import1.isOnDemand()) {
        rc = checkOndemandImport(import1, import2);
      }
      else {
        rc = checkOndemandImport(import2, import1);
      }
    }

    return rc;
  }

  private static boolean checkOndemandImport(ImportDeclaration onDemandImport, ImportDeclaration singleImport)
  {
    String fullName = singleImport.getName().getFullyQualifiedName();
    int index = fullName.lastIndexOf('.');
    if (index == -1)
    {
      return onDemandImport.getName().getFullyQualifiedName().length() == 0;
    }

    return onDemandImport.getName().getFullyQualifiedName()
      .equals(fullName.substring(0, index));
  }

  public static boolean typesMatch(Type type1, Type type2)
  {
    if ((type1 == null) || (type2 == null)) {
      return (type1 == null) && (type2 == null);
    }

    boolean rc = false;

    if ((type1.isSimpleType()) && (type2.isSimpleType()))
      return simpleTypesMatch((SimpleType)type1, (SimpleType)type2);
    if ((type1.isParameterizedType()) && (type2.isParameterizedType()))
      return parameterizedTypesMatch((ParameterizedType)type1, 
        (ParameterizedType)type2);
    if ((type1.isPrimitiveType()) && (type2.isPrimitiveType()))
      return primitiveTypesMatch((PrimitiveType)type1, 
        (PrimitiveType)type2);
    if ((type1.isArrayType()) && (type2.isArrayType()))
      return arrayTypesMatch((ArrayType)type1, (ArrayType)type2);
    if ((type1.isUnionType()) && (type2.isUnionType()))
      return unionTypesMatch((UnionType)type1, (UnionType)type2);
    if ((type1.isQualifiedType()) && (type2.isQualifiedType()))
      return qualifiedTypesMatch((QualifiedType)type1, 
        (QualifiedType)type2);
    if ((type1.isWildcardType()) && (type2.isWildcardType())) {
      return wildcardTypesMatch((WildcardType)type1, 
        (WildcardType)type2);
    }

    return rc;
  }

  public static boolean wildcardTypesMatch(WildcardType type1, WildcardType type2)
  {
    boolean rc = type1.isUpperBound() == type2.isUpperBound();

    if (rc) {
      rc = typesMatch(type1.getBound(), type2.getBound());
    }

    return rc;
  }

  public static boolean simpleTypesMatch(SimpleType type1, SimpleType type2) {
    return type1.getName().getFullyQualifiedName()
      .equals(type2.getName().getFullyQualifiedName());
  }

  public static boolean primitiveTypesMatch(PrimitiveType type1, PrimitiveType type2)
  {
    return type1.getPrimitiveTypeCode().toString()
      .equals(type2.getPrimitiveTypeCode().toString());
  }

  public static boolean arrayTypesMatch(ArrayType type1, ArrayType type2) {
    boolean rc = type1.getDimensions() == type2.getDimensions();

    if (rc) {
      rc = typesMatch(type1.getComponentType(), type2.getComponentType());
    }

    return rc;
  }

  public static boolean unionTypesMatch(UnionType type1, UnionType type2)
  {
    boolean rc = type1.types().size() == type2.types().size();

    if (rc) {
      for (int i = 0; i < type1.types().size(); i++) {
        rc = typesMatch((Type)type1.types().get(i), 
          (Type)type2
          .types().get(i));
        if (!rc)
        {
          break;
        }
      }
    }
    return rc;
  }

  public static boolean qualifiedTypesMatch(QualifiedType type1, QualifiedType type2)
  {
    boolean rc = type1.getName().getFullyQualifiedName()
      .equals(type2.getName().getFullyQualifiedName());

    if (rc) {
      rc = typesMatch(type1.getQualifier(), type2.getQualifier());
    }

    return rc;
  }

  public static boolean parameterizedTypesMatch(ParameterizedType type1, ParameterizedType type2)
  {
    boolean rc = typesMatch(type1.getType(), type2.getType());

    if (rc)
    {
      rc = type1.typeArguments().size() == type2.typeArguments().size();

      if (rc) {
        for (int i = 0; i < type1.typeArguments().size(); i++) {
          rc = typesMatch((Type)type1.typeArguments().get(i), 
            (Type)type2.typeArguments().get(i));
          if (!rc)
          {
            break;
          }
        }
      }
    }
    return rc;
  }

  public static String getTypeName(Type type)
  {
    if (type == null) {
      return "";
    }

    if (type.isSimpleType())
      return getSimpleTypeName((SimpleType)type);
    if (type.isParameterizedType())
      return getParameterizedTypeName((ParameterizedType)type);
    if (type.isPrimitiveType())
      return getPrimitiveTypeName((PrimitiveType)type);
    if (type.isArrayType())
      return getArrayTypeName((ArrayType)type);
    if (type.isUnionType())
      return getUnionTypeName((UnionType)type);
    if (type.isQualifiedType())
      return getQualifiedTypeName((QualifiedType)type);
    if (type.isWildcardType()) {
      return getWildcardTypeName((WildcardType)type);
    }
    return "";
  }

  public static String getWildcardTypeName(WildcardType type)
  {
    StringBuilder sb = new StringBuilder();

    sb.append('?');
    if (type.getBound() != null) {
      if (type.isUpperBound())
        sb.append(" extends ");
      else {
        sb.append(" super ");
      }
      sb.append(getTypeName(type.getBound()));
    }

    return sb.toString();
  }

  public static String getQualifiedTypeName(QualifiedType type) {
    StringBuilder sb = new StringBuilder();
    sb.append(getTypeName(type.getQualifier()));
    sb.append('.');
    sb.append(type.getName().getFullyQualifiedName());

    return sb.toString();
  }

  public static String getUnionTypeName(UnionType type)
  {
    StringBuilder sb = new StringBuilder();

    boolean or = false;
    for (Object type2 : type.types()) {
      if (or)
        sb.append('|');
      else {
        or = true;
      }

      sb.append(getTypeName((Type)type2));
    }

    return sb.toString();
  }

  public static String getArrayTypeName(ArrayType type) {
    StringBuilder sb = new StringBuilder();
    sb.append(getTypeName(type.getComponentType()));
    for (int i = 0; i < type.getDimensions(); i++) {
      sb.append("[]");
    }
    return sb.toString();
  }

  public static String getPrimitiveTypeName(PrimitiveType type) {
    return type.getPrimitiveTypeCode().toString();
  }

  public static String getParameterizedTypeName(ParameterizedType type)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(getTypeName(type.getType()));
    sb.append('<');

    boolean comma = false;
    for (Object typeArgument : type.typeArguments()) {
      if (comma)
        sb.append(',');
      else {
        comma = true;
      }

      sb.append(getTypeName((Type)typeArgument));
    }
    sb.append('>');

    return sb.toString();
  }

  public static String getSimpleTypeName(SimpleType type) {
    return type.getName().getFullyQualifiedName();
  }

  public static String getMethodSignature(MethodDeclaration node)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(node.getName().getIdentifier());
    sb.append('(');
    boolean comma = false;

    Iterator localIterator = node
      .parameters().iterator();

    while (localIterator.hasNext()) {
      SingleVariableDeclaration parameter = (SingleVariableDeclaration)localIterator.next();
      if (comma)
        sb.append(',');
      else {
        comma = true;
      }

      sb.append(getTypeName(parameter.getType()));
    }
    sb.append(')');

    return sb.toString();
  }
}