package ht.plugin.actions;

import ht.plugin.adapter.ConfigFileAdapter;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class CodeAction implements IObjectActionDelegate {

	private Shell shell;
	private IFile selectedFile;
	
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
		
		
		
		
		
		
		MessageDialog.openInformation(shell,"Plugin","New Action was executed.");
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
