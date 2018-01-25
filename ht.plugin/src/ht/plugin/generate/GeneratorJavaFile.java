package ht.plugin.generate;

import ht.plugin.introspect.IField;
import ht.plugin.introspect.IMethod;

import java.util.List;


public class GeneratorJavaFile extends GeneratedFile{
	List<IField> fields;
	List<IMethod> methods;
	
	
	
	public GeneratorJavaFile(String targetProject) {
		super(targetProject);
	}
	
}
