package ht.plugin.wizard;

import ht.msc.mybatis.plugin.CodeLayoutEnum;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class GeneratorThread implements IWorkspaceRunnable{
	
	private IFile inputFile;
	private List<String> warnings;
	private ClassLoader oldClassLoader;
	private List<String> tableList;
	private Map<CodeLayoutEnum, Boolean> codeLayout;
	private String codeVersion;
	
	public GeneratorThread(IFile inputFile, List<String> warnings, List<String> tableList, Map<CodeLayoutEnum, Boolean> codeLayout, String codeVersion){
		this.inputFile = inputFile;
	    this.warnings = warnings;
	    this.tableList = tableList;
	    this.codeLayout = codeLayout;
	    this.codeVersion = codeVersion;
	}
	
	@Override
	public void run(IProgressMonitor arg0) throws CoreException {
		
		
	}
	
}
