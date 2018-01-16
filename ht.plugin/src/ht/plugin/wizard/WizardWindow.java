package ht.plugin.wizard;


import ht.plugin.actions.Activator;
import ht.plugin.configration.Configration;
import ht.plugin.context.PluginContext;
import ht.plugin.properties.LayoutEnum;
import ht.plugin.util.HtClassLoader;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Shell;

public class WizardWindow extends Wizard{
	public WizardPageOne pageOne;
	public WizardPageTwo pageTwo;
	private Map<String, List<String>> tableMap = null;
	private IFile selectedFile;
	private boolean validate;
	private String message;
	
	public WizardWindow(IFile selectedFile, PluginContext context){
		setWindowTitle("向导");
		setNeedsProgressMonitor(true);
	    setTitleBarColor(new RGB(255, 0, 0));
	    setWindowTitle("选择表信息");
	    this.selectedFile = selectedFile;
	    this.validate = loadConn(context);
	}

	public boolean isValidate() {
		return this.validate;
	}

	public String getMessage() {
		return this.message;
	}

	public boolean canFinish(){
		if (getContainer().getCurrentPage() == this.pageTwo) {
			return true;
		}
		return false;
	}

	public IWizardPage getNextPage(IWizardPage page){
		return super.getNextPage(page);
	}

	public boolean performCancel(){
		return super.performCancel();
	}

	public void addPages(){
	    this.pageOne = new WizardPageOne(this.tableMap);
	    addPage(this.pageOne);
	    this.pageTwo = new WizardPageTwo();
	    addPage(this.pageTwo);
	}

	private boolean loadConn(PluginContext context) {
		Connection conn=null;
		try {
			Configration config=context.getConfig();
			Driver driver=(Driver)HtClassLoader.loadClass(config).newInstance();
			conn=config.getDbConfig().getConn(driver);
			DatabaseMetaData meta=conn.getMetaData();
			ResultSet rs=meta.getTables(null, null, null, new String[]{"TABLE"});
			tableMap=new HashMap<>();
			while(rs.next()){
				if(rs.getString(1)!=null){
					if(!this.tableMap.containsKey(rs.getString(1))){
						List<String> tableList = new ArrayList<>();
						tableList.add(rs.getString(3));
						tableMap.put(rs.getString(1), tableList);
					}else{
						tableMap.get(rs.getString(1)).add(rs.getString(3));
					}
				}
				if(rs.getString(2)!=null){
					if(!this.tableMap.containsKey(rs.getString(2))){
						List<String> tableList = new ArrayList<>();
						tableList.add(rs.getString(3));
						tableMap.put(rs.getString(2), tableList);
					}else{
						tableMap.get(rs.getString(2)).add(rs.getString(3));
					}
				}
			}
			conn.close();
			return true;
		}catch(Exception e){
			try {
				if(conn!=null && !conn.isClosed())
					conn.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return false;
	}

	public boolean performFinish(){
		Shell shell=getShell();
		try{
			List<String> warnings=new ArrayList<>();
			ProgressMonitorDialog dialog=new ProgressMonitorDialog(shell);
			Map<LayoutEnum,Boolean> map=this.pageTwo.getCodeLayoutMap();
			String codeVersion = this.pageTwo.getCodeVersion();
			if(map.size()==0){
				map.put(LayoutEnum.SERVICE_LAYOUT, false);
				map.put(LayoutEnum.DAO_LAYOUT, false);
				map.put(LayoutEnum.CONTROLLER_LAYOUT, false);
				map.put(LayoutEnum.JSMVC_LAYOUT,false);
			}
			
			IRunnableWithProgress thread = new GeneratorRunner(warnings,this.pageOne.getTableList(), map, codeVersion);
			dialog.run(true, false, thread);
			if(warnings.size() > 0){
				MultiStatus ms = new MultiStatus("org.mybatis.generator.eclipse.ui",2, "Generation Warnings Occured", null);
				Iterator<String> iter = warnings.iterator();
				while (iter.hasNext()) {
					Status status = new Status(2,"org.mybatis.generator.eclipse.ui", 2, iter.next(),null);
					ms.add(status);
				}
				ErrorDialog.openError(shell, "MyBatis Generator","Run Complete With Warnings", ms, 2);
			}
		}catch(Exception e){
			handleException(e, shell);
		}
		return true;
	}
	
	private void handleException(Exception e,Shell shell){
		Throwable exception;
		if(e instanceof InvocationTargetException)
			exception=((InvocationTargetException) e).getCause();
		else
			exception=e;
		IStatus status;
		if(exception instanceof InterruptedException){
			status=new Status(8,"org.mybatis.generator.eclipse.ui", 8, "Cancelled by User", exception);
		}else{
			if(exception instanceof CoreException){
				status = ((CoreException)exception).getStatus();
			}else{
				String message = "Unexpected error while running MyBatis Generator.";
				status = new Status(4, "org.mybatis.generator.eclipse.ui",4, message, exception);
				Activator.getDefault().getLog().log(status);
			}
		}
	}
	 
	private class GeneratorRunner implements IRunnableWithProgress{
		private List<String> warnings;
		private List<String> tableList;
		private Map<LayoutEnum,Boolean> codeLayout;
		private String codeVersion;
		public GeneratorRunner(List<String> warnings,List<String> tableList,Map<LayoutEnum,Boolean> codeLayout,String codeVersion){
			this.warnings=warnings;
			this.tableList=tableList;
			this.codeLayout=codeLayout;
			this.codeVersion=codeVersion;
		}
		public void run(IProgressMonitor monitor) throws InvocationTargetException,InterruptedException{
			try{
				GeneratorThread thread=new GeneratorThread(WizardWindow.this.selectedFile,
						this.warnings,this.tableList,this.codeLayout,this.codeVersion);
				ResourcesPlugin.getWorkspace().run(thread, monitor);
			}catch(CoreException e){
				throw new InvocationTargetException(e);
			}
		}
	}
}
