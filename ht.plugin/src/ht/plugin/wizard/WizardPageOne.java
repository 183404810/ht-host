package ht.plugin.wizard;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
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
		setComplete(false);
		
		Composite composite=new Composite(container,0);
		composite.setLayoutData(new BorderLayout.BorderData(0));
		FormLayout fl_composite=new FormLayout();
		fl_composite.marginTop=5;
		fl_composite.marginHeight=3;
		composite.setLayout(fl_composite);

	    Label lblNewLabel = new Label(composite, 0);
	    FormData fd_lblNewLabel = new FormData();
	    fd_lblNewLabel.top = new FormAttachment(0, 10);
	    fd_lblNewLabel.left = new FormAttachment(0, 3);
	    lblNewLabel.setLayoutData(fd_lblNewLabel);
	    lblNewLabel.setText("请输入表名:");
	    this.combo = new Combo(composite, 8);
	    this.combo.setTouchEnabled(true);
	    this.combo.setToolTipText("请选择表空间");
	    FormData fd_combo = new FormData();
	    fd_combo.top = new FormAttachment(lblNewLabel, -5, 128);
	    fd_combo.left = new FormAttachment(lblNewLabel, 10, 131072);
	    this.combo.setLayoutData(fd_combo);
	    ·
	}
	private void setComplete(boolean flag){
		setPageComplete(flag);	
	}
}
