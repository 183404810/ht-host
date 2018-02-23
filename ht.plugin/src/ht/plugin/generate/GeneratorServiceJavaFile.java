package ht.plugin.generate;

import java.util.ArrayList;
import java.util.List;

import ht.plugin.configration.Configration;
import ht.plugin.configration.config.JavaFileConfig;
import ht.plugin.introspect.IJavaType;
import ht.plugin.introspect.IMethod;
import ht.plugin.properties.LayoutEnum;

public class GeneratorServiceJavaFile extends GeneratedFile{
	 
	private boolean isInterface=false;
 	private boolean isService=false;
 	private boolean isDao=false;
 	private boolean isController=false;
	private String interfaceName;
	private String entrityName;
	private JavaFileConfig javaFileConfig;
	private Configration config;
	
	public GeneratorServiceJavaFile(String targetProject,String targetPakage,LayoutEnum layout){
		super(targetProject,targetPakage,layout);
	}

	public GeneratorServiceJavaFile(String fileName,String entrityName,Configration config,String key,LayoutEnum layout){
		super(config.getConfig().get(key).getTargetProject(),config.getConfig().get(key).getTargetPackage(),layout);
		this.fileName=fileName;
		if(fileName.endsWith("service") || fileName.endsWith("dao")){
			isInterface=true;
		}else{
			interfaceName=fileName.replace("impl", "");
		}
		if("javaServiceGenerator".equals(key))
			isService=true;
		if("javaDaoGenerator".equals(key))
			isDao=true;
		if("javaControllerGenerator".equals(key))
			isController=true;
		 
		this.entrityName=entrityName;
		this.config=config;
		this.javaFileConfig=config.getConfig().get(key);
	}

	@Override
	public String generator() {
		StringBuilder sb=getFileHeader();
		if(isInterface)
			sb.append(" extends ").append(javaFileConfig.getInterfaceExtendSupInterface());
		else if(isDao)
			sb.append(" extends ").append(javaFileConfig.getExtendSupClass()).append(" implements ").append(interfaceName);
		else if(isController)
			sb.append(" extends ").append(javaFileConfig.getExtendSupClass());
		sb.append(" { ").append(newLine);
		if(!isInterface){
			sb.append("@Resource");
			if(isService){
				sb.append(interfaceName.replace("serviceImpl", "Dao"));
				sb.append(interfaceName.substring(0, 1).toLowerCase()).append(interfaceName.substring(1).replace("serviceImpl", "Dao"));
			}
			if(isController){
				sb.append(entrityName.substring(0, 1).toLowerCase()).append(entrityName.substring(1)).append("service");
			}
		}
		sb.append(getMethod().getMethodContext());
		sb.append(" } ");
		return sb.toString();
	}
	
	private IMethod getMethod(){
		IMethod m=null;
		List<IJavaType> params=new ArrayList<>();
		if(isDao){
			String mapstr=config.getXfconfig().getProperty("interfaceExtendSupInterface");
			IJavaType type=new IJavaType(mapstr.substring(0, mapstr.lastIndexOf(".")),mapstr.substring(mapstr.lastIndexOf(".")));
			m=new IMethod(" ","init",type,"public","Mapper",params);
			m.setMethodContext("return "+entrityName+"Mapper");
		}else if(isService){
			String mapstr=javaFileConfig.getExtendSupClass();
			IJavaType type=new IJavaType(mapstr.substring(0, mapstr.lastIndexOf(".")),mapstr.substring(mapstr.lastIndexOf(".")));
			m=new IMethod(" ","init",type,"public","Dao",params);
			m.setMethodContext("return "+entrityName+"Dao");
		}else if(isController){
			String mapstr=config.getConfig().get("javaServiceGenerator").getInterfaceExtendSupInterface();
			IJavaType type=new IJavaType(entrityName+"Service",mapstr);
			m=new IMethod(" ","init",type,"public","Service",params);
			m.setMethodContext("return "+entrityName+"Service");
		}
		return m;
	}
	
	
	@Override
	public StringBuilder getFileHeader() {
		StringBuilder sb=new StringBuilder();
		sb.append("package ").append(targetProject).append(newLine).append("import ");
		if(!isInterface){
			sb.append("@Repository").append("(");
			if(isService)
				sb.append(interfaceName);
			sb.append(")");
		}
		if(isInterface && javaFileConfig.isEnableInterfaceSupInterfaceGenericity())
			sb.append(javaFileConfig.getInterfaceExtendSupInterface());
		if(!isInterface && javaFileConfig.isEnableSupClassGenericity())
			sb.append(javaFileConfig.getExtendSupClass());
		sb.append(newLine);
		sb.append(annotation).append(newLine).append(modifier);
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
