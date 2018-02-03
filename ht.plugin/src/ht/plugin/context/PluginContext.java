package ht.plugin.context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import ht.plugin.adapter.PropertiesAdapter;
import ht.plugin.configration.Configration;
import ht.plugin.configration.ConfigrationParser;
import ht.plugin.exception.PluginException;
import ht.plugin.generate.GeneratedFile;
import ht.plugin.introspect.ITable;
import ht.plugin.properties.LayoutEnum;
import ht.plugin.util.HtClassLoader;
import ht.plugin.util.StringUtils;

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
	}
	
	public Connection getConn() throws InstantiationException, IllegalAccessException, SQLException{
		Driver driver=(Driver)HtClassLoader.loadClass(config).newInstance();
		Connection conn=config.getDbConfig().getConn(driver);
		return  conn;
	}
	
	public void generat(List<String> tableList,Map<LayoutEnum, Boolean> codeLayout) throws Exception{
		this.generator.generate(this.generatedFiles,tableList);
		File targetFile=null;
		String source=null;
		Shell shell = new Shell(new Display(), 16793600);
		for(GeneratedFile files:this.generatedFiles){
			if(!codeLayout.get(files.getLayout()))continue;
			
			targetFile=new File(getDirected(files.getTargetProject(),files.getTargetPackage()),files.getFileName());
			if(targetFile.exists()){
				boolean flag = MessageDialog.openConfirm(shell, "确认", "是否覆盖" + targetFile.getName() + "文件");
				if(!flag) continue;
				source=files.generator();	
			}
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
	
	private File getDirected(String targetProject,String targetPackage) throws PluginException{
		File f=new File(targetProject);
		if(!f.isDirectory()){
			throw new PluginException("项目["+targetProject+"]不存在");
		}
		StringBuilder sb=new StringBuilder();
		StringTokenizer st=new StringTokenizer(targetPackage,".");
		while(st.hasMoreTokens()){
			sb.append(st.nextToken());
			sb.append(File.separator);
		}
		File direct=new File(targetProject,sb.toString());
		if(!direct.isDirectory()){
			if(!direct.mkdirs())
				throw new PluginException("创建路径["+sb.toString()+"]错误");
		}
		return direct;
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
