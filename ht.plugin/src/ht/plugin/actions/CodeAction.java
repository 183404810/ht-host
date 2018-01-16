package ht.plugin.actions;

import ht.plugin.adapter.ConfigFileAdapter;
import ht.plugin.configration.Configration;
import ht.plugin.configration.XMLConfigrationParser;
import ht.plugin.configration.config.JDBCConfig;
import ht.plugin.context.PluginContext;
import ht.plugin.exception.XMLParserException;
import ht.plugin.util.FindJar;
import ht.plugin.util.Tools;
import ht.plugin.wizard.WizardWindow;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class CodeAction implements IObjectActionDelegate {

	private Shell shell;
	private IFile selectedFile;
	private Map<String,IPath> cache;
	
	/**
	 * Constructor for Action1.
	 */
	public CodeAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		String message = "The xml file is incorrect";
		IPath rawPath = this.selectedFile.getRawLocation();
		File configurationFile = rawPath.toFile();
		Tools.println("config file:" + configurationFile.getAbsolutePath());

		PluginContext context=new PluginContext();
		Configration config=context.getConfig();
		try {
			context.getParser().parser(new XMLConfigrationParser(), configurationFile, config);
			JDBCConfig dbconfig=config.getDbConfig();
			if(cache==null)
				cache=new HashMap<>();
				
			if(dbconfig!=null){
				String driverClassName=	dbconfig.getDriverClass();
				IPath path=null;
				if(cache!=null) 
					path=cache.get(driverClassName);
				if(path==null || !path.toFile().exists()) 
					path=findDriver(driverClassName);
				if(path==null){
					MessageDialog.openError(this.shell, "提醒", "未找到驱动包");
				}
				cache.put(driverClassName, path);
				config.getClassEntry().add(driverClassName);
				createWizard(context);
			}
		} catch (XMLParserException e) {
			message=message+e.getMessage();
			Tools.println(message);
			e.printStackTrace();
		}
		MessageDialog.openInformation(shell,"Plugin","New Action was executed.");
	}

	private void createWizard(PluginContext context){
		JDBCConfig dbconfig=context.getConfig().getDbConfig();
		if(dbconfig==null) return;
		WizardWindow w=new WizardWindow(this.selectedFile,context);                          
		
		
	}
	
	private IPath findDriver(String driverName){
		IWorkspace workspace=ResourcesPlugin.getWorkspace();
		JarPackageFragmentRoot driverJar=FindJar.findDriver(workspace, driverName);
		return driverJar==null?null:driverJar.getPath();
	}
	
	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		StructuredSelection ss = (StructuredSelection)selection;
		ConfigFileAdapter adapter = new ConfigFileAdapter((IFile)ss.getFirstElement());
	    if (adapter != null)
	      this.selectedFile = adapter.getConfigFile();
	}

}
