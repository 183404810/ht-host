package ht.plugin.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.core.PackageFragment;

public class FindJar {
	public static final String DOT=".";
	
	public static List<IJavaProject> findProject(IWorkspace workspace){
		IProject[] projects=workspace.getRoot().getProjects();
		if(projects==null) return null;
		List<IJavaProject> retList=new ArrayList<>(projects.length);
		for(IProject p: projects){
			IJavaProject javap=JavaCore.create(p);
			if(javap!=null && javap.exists()){
				retList.add(javap);
			}
		}
		return retList;
	}
	
	public static List<JarPackageFragmentRoot> findAllJar(IJavaProject project){
		try {
			IPackageFragmentRoot[] ipackages=project.getAllPackageFragmentRoots();
			if(ipackages==null) return null;
			List<JarPackageFragmentRoot> rlist=new ArrayList<>();
			for(IPackageFragmentRoot ipackage : ipackages){
				if(ipackage instanceof JarPackageFragmentRoot){
					JarPackageFragmentRoot jp=(JarPackageFragmentRoot)ipackage;
					IClasspathEntry classpath=jp.getRawClasspathEntry();
					IClasspathContainer classpathContainer=JavaCore.getClasspathContainer(classpath.getPath(), project);
					if(classpathContainer.getKind()!=3){
						rlist.add(jp);
					}
				}
			}
			return rlist;
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static JarPackageFragmentRoot findDriver(IWorkspace workspace,String driverName){
		List<IJavaProject> projects=findProject(workspace);
		if(projects==null) return null;
		List<JarPackageFragmentRoot> jarList = new ArrayList<>();
		for(IJavaProject jProject: projects){
			jarList.addAll(findAllJar(jProject));
		} 
		
		if(jarList.size()<=0) return null;
		boolean flag=false;
		for(JarPackageFragmentRoot jPackage:jarList){
			flag=exists(jPackage,driverName);
			if(flag){
				String msg=MessageFormat.format("已找到驱动,驱动包路径:{0}",new Object[]{jPackage.getPath()});
				Tools.println(msg);
				return jPackage;
			}
		}
		return null;
	}
	
	public static boolean exists(JarPackageFragmentRoot jPackage,String driverName){
		if(StringUtils.isEmpty(driverName)) return false;
		try {
			IJavaElement[] eles=jPackage.getChildren();
			if(eles==null || eles.length<=0) return false;
			String packageName=driverName.substring(0,driverName.indexOf("."));
			for(IJavaElement ele: eles){
				if(ele instanceof PackageFragment){
					if(ele.getElementName().equals(packageName)){
						IJavaElement[] jeles=((PackageFragment) ele).getChildren();
						for(IJavaElement jele: jeles){
							if(jele instanceof IClassFile){
								String cname=jele.getElementName();
								String className=ele.getElementName()+"."+cname.substring(0,cname.indexOf("."));
								if(className.equals(driverName)) return true;
							}
						}
					}
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return false;
	}
}
