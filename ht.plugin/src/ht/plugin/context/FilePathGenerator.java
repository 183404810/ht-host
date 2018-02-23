package ht.plugin.context;

import ht.plugin.exception.PluginException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;

public class FilePathGenerator {
	Map<String,IFolder> folders;
	Map<String,IPackageFragmentRoot> sourceFolders;
	Map<String,IJavaProject> projects;
	private PluginContext context;
	public FilePathGenerator(PluginContext context){
		this.context=context;
		this.folders=new HashMap<>();
		this.sourceFolders=new HashMap<>();
		this.projects=new HashMap<>();
	}
	
	public File getDirector(String targetProject, String targetPackage) throws PluginException{
		 IFolder folder = getFolder(targetProject, targetPackage);
		 return folder.getRawLocation().toFile();
	}
	
	private IFolder getFolder(String targetProject,String targetPackage) throws PluginException{
		String key=targetProject+targetPackage;
		if(this.folders.containsKey(key)) 
			return this.folders.get(key);
		IFolder folder;
		IPackageFragmentRoot root = getSourceFolder(targetProject);
		IPackageFragment packageFragment = getPackage(root, targetPackage);
		try{
			folder = (IFolder)packageFragment.getCorrespondingResource();
			this.folders.put(key, folder);
		} catch (CoreException e) {
			throw new PluginException(e,e.getStatus().getMessage());
		}
		return folder;
	}
	
	private IPackageFragmentRoot getSourceFolder(String targetProject) throws PluginException{
	    if(sourceFolders.containsKey(targetProject)) 
	    	return sourceFolders.get(targetProject);
	    
	    IPackageFragmentRoot answer;
	    int index = targetProject.indexOf('/');
	    if (index == -1) {
	    	index = targetProject.indexOf('\\');
	    }

	    if (index == -1){
	        IJavaProject javaProject = getJavaProject(targetProject);
	        answer = getFirstSourceFolder(javaProject);
	    } else {
	    	IJavaProject javaProject = getJavaProject(targetProject.substring(0, index));
	        answer = getSpecificSourceFolder(javaProject, targetProject);
	    }
	    this.sourceFolders.put(targetProject, answer);
	    return answer;
	}
	
	private IPackageFragment getPackage(IPackageFragmentRoot srcFolder, String packageName)throws PluginException {
		IPackageFragment fragment = srcFolder.getPackageFragment(packageName);
	    try{
	    	if (!fragment.exists()) {
	    		fragment = srcFolder.createPackageFragment(packageName, true, null);
	    	}
	    	fragment.getCorrespondingResource().refreshLocal(1, null);
	    } catch (CoreException e) {
	    	throw new PluginException(e,e.getStatus().getMessage());
	    }
	    return fragment;
	}
	
	private IJavaProject getJavaProject(String javaProjectName) throws PluginException{
		IJavaProject javaProject = this.projects.get(javaProjectName);
		if (javaProject == null) {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IProject project = root.getProject(javaProjectName);
			boolean isJavaProject;
			if (project.exists()){
				try {
					isJavaProject = project.hasNature("org.eclipse.jdt.core.javanature");
				}catch (CoreException e){
					throw new PluginException(e,e.getStatus().getMessage());
				}
				if (isJavaProject) {
					javaProject = JavaCore.create(project);
				} else {
					StringBuffer sb = new StringBuffer();
					sb.append("Project ");
					sb.append(javaProjectName);
					sb.append(" is not a Java project");
					throw new PluginException(sb.toString());
				}
			} else {
				StringBuffer sb = new StringBuffer();
				sb.append("Project ");
				sb.append(javaProjectName);
				sb.append(" does not exist");
				throw new PluginException(sb.toString());
			}
			this.projects.put(javaProjectName, javaProject);
		}
		return javaProject;
	}
	
	private IPackageFragmentRoot getFirstSourceFolder(IJavaProject javaProject) throws PluginException{
		IPackageFragmentRoot[] roots;
		try{
			roots = javaProject.getPackageFragmentRoots();
		}catch (CoreException e){
			throw new PluginException(e,e.getStatus().getMessage());
		}
		IPackageFragmentRoot srcFolder = null;
		for (int i = 0; i < roots.length; i++) {
			if ((!roots[i].isArchive()) && (!roots[i].isReadOnly()) && (!roots[i].isExternal())){
				srcFolder = roots[i];
				break;
			}
		}
		if (srcFolder == null) {
			StringBuffer sb = new StringBuffer();
			sb.append("Cannot find source folder for project ");
			sb.append(javaProject.getElementName());
			throw new PluginException(sb.toString());
		}
		return srcFolder;
	}
	
	private IPackageFragmentRoot getSpecificSourceFolder(IJavaProject javaProject, String targetProject)throws PluginException{
		try{
			Path path = new Path("/" + targetProject);
			IPackageFragmentRoot pfr = javaProject.findPackageFragmentRoot(path);
			if (pfr == null) {
				StringBuffer sb = new StringBuffer();
		        sb.append("Cannot find source folder ");
		        sb.append(targetProject);
		        throw new PluginException(sb.toString());
			}
			return pfr;
		} catch (CoreException e) {
			throw new PluginException(e,e.getStatus().getMessage());
		}
	}
}
