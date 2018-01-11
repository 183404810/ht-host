package ht.plugin.wizard;

import ht.msc.mybatis.plugin.element.WizardPageOne;
import ht.msc.mybatis.plugin.element.WizardPageTwo;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.wizard.Wizard;

public class WizardWindow extends Wizard{
	public WizardPageOne pageOne;
	public WizardPageTwo pageTwo;
	private Map<String, List<String>> tableMap = null;
	private IFile selectedFile;
	private boolean validate;
	private String message;
	
	@Override
	public boolean performFinish() {
		// TODO 自动生成的方法存根
		return false;
	}

}
