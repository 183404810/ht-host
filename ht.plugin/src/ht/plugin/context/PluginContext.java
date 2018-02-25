package ht.plugin.context;

import ht.plugin.adapter.PropertiesAdapter;
import ht.plugin.configration.Configration;
import ht.plugin.configration.ConfigrationParser;
import ht.plugin.generate.GeneratedFile;
import ht.plugin.introspect.ITable;
import ht.plugin.properties.LayoutEnum;
import ht.plugin.util.HtClassLoader;
import ht.plugin.util.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class PluginContext extends PropertiesAdapter{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<ITable> tables;
	private Configration config;
	private ConfigrationParser parser;
	private ClassLoader loader;
	private Generator generator;
	private List<GeneratedFile> generatedFiles;
	private FilePathGenerator filePathGenerator;

	public PluginContext(){
		if(config==null)
			config=new Configration(this);
		if(loader==null)
			loader=new HtClassLoader("",System.class.getClassLoader());
		if(parser==null)
			parser=new ConfigrationParser(null,null);
		if(generator==null)
			generator=new MybatisGenerator(this);
		if(tables==null)
			tables=new ArrayList<>();
		if(filePathGenerator==null)
			filePathGenerator=new FilePathGenerator(this);
	}

	public void generat(List<String> tableList,Map<LayoutEnum, Boolean> codeLayout) throws Exception{
		if(generatedFiles==null) generatedFiles=new ArrayList<>();
		this.generator.generate(generatedFiles,tableList);
		File targetFile=null;
		String source=null;
		Shell shell = new Shell(new Display(), 16793600);
		for(GeneratedFile files:this.generatedFiles){
			if(!codeLayout.get(files.getLayout()))continue;
			
			targetFile=new File(filePathGenerator.getDirector(files.getTargetProject(),files.getTargetPackage(),files.getFileLocal()),files.getFileName());
			if(targetFile.exists()){
				boolean flag = MessageDialog.openConfirm(shell, "确认", "是否覆盖" + targetFile.getName() + "文件");
				if(!flag) continue;
			}
			source=files.generator();	
			if(StringUtils.isEmpty(source));
				writeFile(targetFile, source, "UTF-8");
		}
	}
	
	private void writeFile(File file,String context,String encoding) throws Exception{
		FileOutputStream fos=new FileOutputStream(file,false);
		OutputStreamWriter osw;
		if(StringUtils.isEmpty(encoding))
			osw=new OutputStreamWriter(fos);
		else
			osw=new OutputStreamWriter(fos,encoding);
		
		BufferedWriter bw=new BufferedWriter(osw);
		bw.write(context);
		bw.close();
	}
	
	public void destory(){
		this.clear();
		tables.clear();
		config.destory();
		config=null;
		parser=null;
		loader=null;
		generator=null;
		generatedFiles.clear();
		generatedFiles=null;
		filePathGenerator=null;
	}
	
	public List<ITable> getTables() {
		return tables;
	}
	public void setTables(List<ITable> tables) {
		this.tables = tables;
	}
	public Configration getConfig() {
		return config;
	}
	public void setConfig(Configration config) {
		this.config = config;
	}
	public ConfigrationParser getParser() {
		return parser;
	}
	public void setParser(ConfigrationParser parser) {
		this.parser = parser;
	}
	public ClassLoader getLoader() {
		return loader;
	}
	public void setLoader(ClassLoader loader) {
		this.loader = loader;
	}
	public Generator getGenerator() {
		return generator;
	}
	public void setGenerator(Generator generator) {
		this.generator = generator;
	}
}
