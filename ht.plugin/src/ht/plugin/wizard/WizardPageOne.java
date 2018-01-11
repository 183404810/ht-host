package ht.plugin.wizard;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

public class WizardPageOne extends WizardPage{
	private Text text;
	private Table table;
	private Combo combo=null;
	private Map<String,List<String>> tableMap=null;
	private List<String> tableList=null;
	
	public WizardPageOne(Map<String,List<String>> tableMap){
		super("page1");
		setTitle("请选择表信息");
		setDescription("请输入表信息");
		this.tableMap=tableMap;
	}
	
	public List<String> getTableList(){
		return this.tableList;
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite container=new Composite(parent,2144);
		setControl(container);
		container.setLayout(new BorderLayout());
		
		
	}

}
