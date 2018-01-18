package ht.plugin.configration.config;

import ht.plugin.adapter.PropertiesAdapter;

public class TableSettingConfig extends PropertiesAdapter{
	private boolean isSchema;
	private boolean enableCountByExample;
	private boolean enableUpdateByExample;
	private boolean enableDeleteByExample;
	private boolean enableSelectByExample;
	private boolean selectByExampleQueryId;
	private boolean ignoreGeneratorSchema;
	
	public boolean isSchema() {
		return isSchema;
	}
	public void setSchema(boolean isSchema) {
		this.isSchema = isSchema;
	}
	public boolean isEnableCountByExample() {
		return enableCountByExample;
	}
	public void setEnableCountByExample(boolean enableCountByExample) {
		this.enableCountByExample = enableCountByExample;
	}
	public boolean isEnableUpdateByExample() {
		return enableUpdateByExample;
	}
	public void setEnableUpdateByExample(boolean enableUpdateByExample) {
		this.enableUpdateByExample = enableUpdateByExample;
	}
	public boolean isEnableDeleteByExample() {
		return enableDeleteByExample;
	}
	public void setEnableDeleteByExample(boolean enableDeleteByExample) {
		this.enableDeleteByExample = enableDeleteByExample;
	}
	public boolean isEnableSelectByExample() {
		return enableSelectByExample;
	}
	public void setEnableSelectByExample(boolean enableSelectByExample) {
		this.enableSelectByExample = enableSelectByExample;
	}
	public boolean isSelectByExampleQueryId() {
		return selectByExampleQueryId;
	}
	public void setSelectByExampleQueryId(boolean selectByExampleQueryId) {
		this.selectByExampleQueryId = selectByExampleQueryId;
	}
	public boolean isIgnoreGeneratorSchema() {
		return ignoreGeneratorSchema;
	}
	public void setIgnoreGeneratorSchema(boolean ignoreGeneratorSchema) {
		this.ignoreGeneratorSchema = ignoreGeneratorSchema;
	}
}
