package ht.plugin.generate;

import java.util.ArrayList;
import java.util.List;

import ht.plugin.configration.Configration;
import ht.plugin.configration.config.JavaFileConfig;
import ht.plugin.introspect.IJavaType;
import ht.plugin.introspect.IMethod;
import ht.plugin.properties.LayoutEnum;
import ht.plugin.util.StringUtils;

public class GeneratorServiceJavaFile extends GeneratedFile{
	 
	private boolean isInterface=false;
 	private boolean isService=false;
 	private boolean isDao=false;
 	private boolean isController=false;
 	private boolean isMapper=false;
 	
	private String interfaceName;
	private String entrityName;
	private String lowEntrityName;
	private JavaFileConfig javaFileConfig;
	private Configration config;
	private String tableName;
	
	public GeneratorServiceJavaFile(String fileName,String entrityName,String targetProject,String targetPakage,LayoutEnum layout,String tableName,Configration config){
		super(targetProject,targetPakage,layout);
		this.fileName=fileName;
		isInterface=true;
		isMapper=true;
		this.entrityName=entrityName.substring(0, 1).toUpperCase() + entrityName.substring(1);
		this.lowEntrityName=entrityName.substring(0, 1).toLowerCase() + entrityName.substring(1);
		this.modifier="public";
		this.tableName=tableName;
		this.config=config;
	}

	public GeneratorServiceJavaFile(String fileName,String entrityName,Configration config,String key,LayoutEnum layout,String tableName){
		super(config.getConfig().get(key).getTargetProject(),config.getConfig().get(key).getTargetPackage(),layout);
		this.fileName=fileName;
		if(fileName.endsWith("service") || fileName.endsWith("dao") || fileName.endsWith("Mapper")){
			isInterface=true;
		}else{
			interfaceName=fileName.replace("Impl", "");
		}
		if("javaServiceGenerator".equals(key))
			isService=true;
		if("javaDaoGenerator".equals(key))
			isDao=true;
		if("javaControllerGenerator".equals(key))
			isController=true;
		if("sqlMapGenerator".equals(key)){
			this.targetProject=config.getXfconfig().getProperty("targetProject");
			this.targetPackage=config.getXfconfig().getProperty("targetPackage");
			isMapper=true;
		}
		this.entrityName=entrityName.substring(0, 1).toUpperCase() + entrityName.substring(1);
		this.lowEntrityName=entrityName.substring(0, 1).toLowerCase() + entrityName.substring(1);
		this.config=config;
		this.javaFileConfig=config.getConfig().get(key);
		this.modifier="public";
		this.tableName=tableName;
	 
	}

	@Override
	public String generator() {
		StringBuilder sb=getFileHeader();
		if(javaFileConfig!=null && javaFileConfig.isEnableInterfaceSupInterfaceGenericity())
			sb.append(" implements ").append(interfaceName);
		if(javaFileConfig!=null && javaFileConfig.isEnableSupClassGenericity())
			sb.append(" extends ").append(javaFileConfig.getExtendSupClass().substring(javaFileConfig.getExtendSupClass().lastIndexOf(".")+1));
		if(isMapper)
			sb.append(" extends ").append(config.getXfconfig().getProperty("interfaceExtendSupInterface").substring(config.getXfconfig().getProperty("interfaceExtendSupInterface").lastIndexOf(".")+1));
		sb.append("  ").append("{").append(newLine);
		if(!isInterface){
			sb.append(newLine).append("  ").append("@Resource").append(newLine);
			sb.append("  ").append("private ");
			if(isDao){
				sb.append(entrityName).append("Mapper").append(" ");
				sb.append(lowEntrityName).append("Mapper").append(";").append(newLine);
			}
			if(isService){
				sb.append(entrityName).append("Dao").append(" ");
				sb.append(lowEntrityName).append("Dao").append(";").append(newLine);
			}
			if(isController){
				sb.append(entrityName).append("Service").append(" ");
				sb.append(lowEntrityName).append("Service").append(";").append(newLine);
			}
		}
		sb.append("  ").append(getMethod()).append(newLine);
		sb.append("}");
		return sb.toString();
	}
	
	private String getMethod(){
		IMethod m=null;
		List<IJavaType> params=new ArrayList<>();
		if(isDao){
			String mapstr=config.getXfconfig().getProperty("interfaceExtendSupInterface");
			IJavaType type=new IJavaType(mapstr.substring(mapstr.lastIndexOf(".")+1),mapstr.substring(0, mapstr.lastIndexOf(".")));
			m=new IMethod("  ","init",type,"public","Mapper",params);
			m.setMethodContext("  return "+lowEntrityName+"Mapper;");
		}
		if(isService){
			String mapstr=config.getConfig().get("javaDaoGenerator").getExtendSupClass();
			IJavaType type=new IJavaType(mapstr.substring(mapstr.lastIndexOf(".")+1),mapstr.substring(0, mapstr.lastIndexOf(".")));
			m=new IMethod("  ","init",type,"public","Dao",params);
			m.setMethodContext("  return "+lowEntrityName+"Dao;");
		}
		if(isController){
			String mapstr=config.getConfig().get("javaServiceGenerator").getExtendSupClass();
			//IJavaType type=new IJavaType(entrityName+"Service",mapstr);
			IJavaType type=new IJavaType(mapstr.substring(mapstr.lastIndexOf(".")+1),mapstr.substring(0, mapstr.lastIndexOf(".")));
			m=new IMethod("  ","init",type,"public","Service",params);
			m.setMethodContext("  return "+lowEntrityName+"Service;");
		}
		return m==null?"":m.getMethodContext();
	}
	
	
	@Override
	public StringBuilder getFileHeader() {
		StringBuilder sb=new StringBuilder();
		sb.append("package ").append(targetPackage).append(newLine);
	
		if(javaFileConfig!=null && javaFileConfig.isEnableInterfaceSupInterfaceGenericity())
			sb.append("import ").append(javaFileConfig.getInterfaceExtendSupInterface()).append(";").append(newLine);
		if(javaFileConfig!=null && javaFileConfig.isEnableSupClassGenericity())
			sb.append("import ").append(javaFileConfig.getExtendSupClass()).append(";").append(newLine);
		if(isMapper)
			sb.append("import ").append(config.getXfconfig().getProperty("interfaceExtendSupInterface")).append(";").append(newLine);
		sb.append(newLine);
		
		if(!StringUtils.isEmpty(annotation))
			sb.append(annotation).append(newLine);
		if(!isInterface && !isController && !isMapper){
			sb.append("@Repository").append("(");
		    sb.append("\""+interfaceName+"\"");
			sb.append(")").append(newLine);
		}
		if(isController){
			sb.append("@Controller").append(newLine);
			sb.append("@RequestMapping(\"/").append(tableName).append("\")").append(newLine);
		}
		sb.append(modifier);
		if(isInterface)
			sb.append(" interface ");
		else
			sb.append(" class ");
		sb.append(fileName);
		return sb;
	}
	
	public String getImplementationPackage(){
		return this.javaFileConfig.getImplementationPackage();
	}
}
