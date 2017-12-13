package msc.dao;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import msc.entiry.SCOperation;
import msc.entiry.SCResource;
import msc.entiry.SCRole;
import msc.entiry.SCRoleRight;
import msc.entiry.SCUser;
import msc.entiry.SCUserRole;
import msc.mapper.LoginRightMapper;

import org.springframework.stereotype.Repository;

@Repository("loginRightDao") 
public class LoginRightDao {
	
	@Resource
	private LoginRightMapper loginRightMapper;
	
	public List<SCUser> selectUser(Map<String,Object> params){
		return loginRightMapper.selectUser(params);
	}
    public List<SCRole> selectRole(Map<String,Object> params){
    	return loginRightMapper.selectRole(params);
    }
    public List<SCResource> selectResource(Map<String,Object> params){
     	return loginRightMapper.selectResource(params);
    }
    public List<SCOperation> selectOperation(Map<String,Object> params){
    	return loginRightMapper.selectOperation(params);
    }
    public List<SCRoleRight> selectRoleRight(Map<String,Object> params){
    	return loginRightMapper.selectRoleRight(params);
    }
    public List<SCUserRole> selectUserRole(Map<String,Object> params){
    	return loginRightMapper.selectUserRole(params);
    }

    public void addUser(SCUser user){
    	loginRightMapper.insertUser(user);
    }
	public void removeUser(SCUser user){
    	loginRightMapper.deleteUser(user);
	}
	public void modifyUser(SCUser user){
    	loginRightMapper.updateUser(user);
	}
	
	public void addRole(SCRole role){
		loginRightMapper.insertRole(role);
	}
	public void removeRole(SCRole role){
		loginRightMapper.deleteRole(role);
	}
	public void modifyRole(SCRole role){
		loginRightMapper.updateRole(role);
	}
	
	public void addResource(SCResource resource){
		loginRightMapper.insertResource(resource);
	}
	public void removeResource(SCResource resource){
		loginRightMapper.deleteResource(resource);
	}
	public void modifyResource(SCResource resource){
		loginRightMapper.updateResource(resource);
	}
	
	public void addOperation(SCOperation operation){
		loginRightMapper.insertOperation(operation);
	}
	public void removeOperation(SCOperation operation){
		loginRightMapper.deleteOperation(operation);
	}
	public void modifyOperation(SCOperation operation){
		loginRightMapper.updateOperation(operation);
	}

	public void addRoleRight(SCRoleRight roleRight){
		loginRightMapper.insertRoleRight(roleRight);
	}
	public void removeRoleRight(SCRoleRight roleRight){
		loginRightMapper.deleteRoleRight(roleRight);
	}
	public void modifyRoleRight(SCRoleRight roleRight){
		loginRightMapper.updateRoleRight(roleRight);
	}

	public void addUserRole(SCUserRole userRole){
		loginRightMapper.insertUserRole(userRole);
	}
	public void removeUserRole(SCUserRole userRole){
		loginRightMapper.updateUserRole(userRole);
	}
	public void modifyUserRole(SCUserRole userRole){
		loginRightMapper.updateUserRole(userRole);
	}
}
