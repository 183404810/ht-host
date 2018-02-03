package ht.plugin.wizard;


import ht.plugin.context.PluginContext;
import ht.plugin.properties.LayoutEnum;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public class GeneratorThread implements IWorkspaceRunnable{
	
	private IFile inputFile;
	private List<String> warnings;
	private ClassLoader oldClassLoader;
	private List<String> tableList;
	private Map<LayoutEnum, Boolean> codeLayout;
	private String codeVersion;
	private PluginContext context;
	
	public GeneratorThread(IFile inputFile, List<String> warnings, List<String> tableList, Map<LayoutEnum, Boolean> codeLayout, String codeVersion,PluginContext context){
		this.inputFile = inputFile;
	    this.warnings = warnings;
	    this.tableList = tableList;
	    this.codeLayout = codeLayout;
	    this.codeVersion = codeVersion;
	    this.context=context;
	}
	
	@Override
	public void run(IProgressMonitor monitor) throws CoreException {
		SubMonitor subMonitor = SubMonitor.convert(monitor, 1000);
	    subMonitor.beginTask("Generating MyBatis/iBATIS Artifacts:", 1000);
	    setClassLoader();
	    try{
	    	subMonitor.subTask("Parsing Configuration");
	    	subMonitor.worked(50);
	    	monitor.subTask("Generating Files from Database Tables");
	    	SubMonitor spm = subMonitor.newChild(950);
	    	this.context.generat(tableList,codeLayout);
	    }catch(Exception e){
	    	System.out.println(e.getMessage());
	    }
	}
	
	private void setClassLoader(){
		IWorkspaceRoot root=ResourcesPlugin.getWorkspace().getRoot();
		IJavaProject javaProject = getJavaProject();
		try{
			if (javaProject != null) {
				List<URL> entries = new ArrayList<>();
				IPath path = javaProject.getOutputLocation();
		        IResource iResource = root.findMember(path);
		        path = iResource.getLocation();
		        path = path.addTrailingSeparator();
		        entries.add(path.toFile().toURI().toURL());

		        IClasspathEntry[] cpEntries = javaProject.getRawClasspath();
		        for (IClasspathEntry cpEntry : cpEntries) {
		        	switch (cpEntry.getEntryKind()) {
		        		case 3:
		        			path = cpEntry.getOutputLocation();
		        			if (path != null) {
		        				iResource = root.findMember(path);
		        				path = iResource.getLocation();
		        				path = path.addTrailingSeparator();
		        				entries.add(path.toFile().toURI().toURL());
		        			}
		        			break;
		        		case 1:
		        			iResource = root.findMember(cpEntry.getPath());
		        			if (iResource == null){
		        				path = cpEntry.getPath();
		        			}else 
		        				path = iResource.getLocation();
		        			entries.add(path.toFile().toURI().toURL());
		        		case 2:
		        	}
		        }
		        ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
		        URL[] entryArray = new URL[entries.size()];
		        entries.toArray(entryArray);
		        Object newCl = new URLClassLoader(entryArray, oldCl);
		        Thread.currentThread().setContextClassLoader((ClassLoader)newCl);
		        this.oldClassLoader = oldCl;
		      }
			
		}catch(Exception e){
		}
	}
	
	private IJavaProject getJavaProject(){
		IJavaProject answer = null;
		IProject project = this.inputFile.getProject();
	    try {
	    	if (project.hasNature("org.eclipse.jdt.core.javanature")) {
	    		answer = JavaCore.create(project);
	    	}
	    }catch (CoreException localCoreException){
	    }
	    return answer;
	}
}
