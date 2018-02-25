package msc.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import msc.entity.TreeModel;
import msc.model.ModelJson;
import msc.service.LoginRightService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;


@Controller
@RequestMapping("/login")
public class LoginRightController {
	private Logger logger = LoggerFactory.getLogger(LoginRightController.class);
	
	@Resource
	private LoginRightService loginRightService;
	
	@RequestMapping("/save.json")
    @ResponseBody
    public Map<String,Object> save(HttpServletRequest req) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
        String tbname=req.getParameter("tbname");
	    String dataJsonStr=req.getParameter("data").toString();
	    List<ModelJson> data=JSON.parseArray(dataJsonStr,ModelJson.class);
		resultMap.put("code", "1");
	    
	    try{
	    	for(ModelJson mj:data){
		    	if("user".equals(tbname))
		    		loginRightService.saveUser(data);
		    	if("role".equals(tbname))
		    		loginRightService.saveRole(data);
		    	if("source".equals(tbname))
		    		loginRightService.saveResource(data);
		    	if("operation".equals(tbname))
		    		loginRightService.saveOperate(data);
		    	if("userrole".equals(tbname))
		    		loginRightService.saveUserRole(data);
		    	if("roleright".equals(tbname))
		    		loginRightService.saveRoleRight(data);
		    }
	    }catch(Exception e){
			resultMap.put("code", "-1");
	    	resultMap.put("msg", e.getMessage());
	    }
		return resultMap;
	}

	@RequestMapping("/login.json")
    @ResponseBody
    public Map<String,Object> login(HttpServletRequest req, Model model) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
			
		
		resultMap.put("msg", "hhhh");
		return resultMap;
	}
	
	@RequestMapping(value = "/sysmenu.json")
	@ResponseBody
    public Map<String,Object> getMenuTree(HttpServletRequest request) {
		Map<String,Object> result=new HashMap<String,Object>();
		result.put("success", "true");
		List<TreeModel> list=new ArrayList<TreeModel>();
		result.put("success", "true");
		TreeModel sys=new TreeModel();
		sys.setText("系统资源");
		sys.setId(1);
		TreeModel user=new TreeModel();
		user.setLeaf(true);
		user.setText("用户");
		user.setId(11);
		user.setView("user.User");
		TreeModel role=new TreeModel();
		role.setLeaf(true);
		role.setText("角色");
		role.setId(12);
		role.setView("user.User");
		TreeModel right=new TreeModel();
		right.setLeaf(true);
		right.setText("权限");
		right.setId(13);
		right.setView("user.User");
		TreeModel source=new TreeModel();
		source.setLeaf(true);
		source.setText("资源");
		source.setId(14);
		source.setView("user.User");
		list.add(user);
		list.add(role);
		list.add(right);
		list.add(source);
		sys.setChildren(list);
		result.put("data", sys);
        return result;
        
		//...
        //此处为了测试，将json的内容直接拼出，按照树形结构拼出菜单栏的分层结构内容，要注意的是约定view字段的内容是视图的相对路径（相对于view目录的）
        /*String systemconfig = "{ text:'root', expanded: true, id:'sys',text:'系统设置',leaf:false,children:["
                + "{id:'sys1',text:'系统菜单管理',view:'systemconfig.SysMenuMgmView',leaf:true}," +
                "{id:'sys1',text:'系统菜单管理',view:'systemconfig.SysMenuMgmView',leaf:true}," +
                "{id:'sys1',text:'系统菜单管理',view:'systemconfig.SysMenuMgmView',leaf:true}]}";*/  
        //...
    }
}
