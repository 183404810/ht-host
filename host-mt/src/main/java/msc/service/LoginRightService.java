package msc.service;

import ht.msc.util.SCException;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import msc.dao.LoginRightDao;
import msc.entity.SCOperation;
import msc.entity.SCResource;
import msc.entity.SCRole;
import msc.entity.SCRoleRight;
import msc.entity.SCUser;
import msc.entity.SCUserRole;
import msc.model.ModelJson;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;

@Service("loginRightService")
public class LoginRightService {
	
	@Resource
	private LoginRightDao loginRightDao;
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = SCException.class)
	public void saveUser(List<ModelJson> list){
		for(ModelJson mj:list){
			List<SCUser> uers=JSON.parseArray(mj.getData(),SCUser.class);
			for(SCUser user:uers){
				if("A".equals(mj.getOperate())) 
					loginRightDao.addUser(user);
				if("U".equals(mj.getOperate()))
					loginRightDao.modifyUser(user);
				if("D".equals(mj.getOperate())) 
					loginRightDao.modifyUser(user);
			}
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = SCException.class)
	public void saveRole(List<ModelJson> list){
		for(ModelJson mj:list){
			List<SCRole> roles=JSON.parseArray(mj.getData(),SCRole.class);
			for(SCRole role: roles){
				if("A".equals(mj.getOperate())) 
					loginRightDao.addRole(role);
				if("U".equals(mj.getOperate()))
					loginRightDao.modifyRole(role);
				if("D".equals(mj.getOperate())) 
					loginRightDao.removeRole(role);
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = SCException.class)
	public void saveResource(List<ModelJson> list){
		for(ModelJson mj:list){
			List<SCResource> sourcs=JSON.parseArray(mj.getData(),SCResource.class);
			for(SCResource source:sourcs){
				if("A".equals(mj.getOperate())) 
					loginRightDao.addResource(source);
				if("U".equals(mj.getOperate()))
					loginRightDao.modifyResource(source);
				if("D".equals(mj.getOperate())) 
					loginRightDao.removeResource(source);
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = SCException.class)
	public void saveOperate(List<ModelJson> list){
		for(ModelJson mj:list){
			List<SCOperation> operates=JSON.parseArray(mj.getData(),SCOperation.class);
			for(SCOperation oper:operates){
				if("A".equals(mj.getOperate())) 
					loginRightDao.addOperation(oper);
				if("U".equals(mj.getOperate()))
					loginRightDao.modifyOperation(oper);
				if("D".equals(mj.getOperate())) 
					loginRightDao.removeOperation(oper);
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = SCException.class)
	public void saveUserRole(List<ModelJson> list){
		for(ModelJson mj:list){
			List<SCUserRole> urs=JSON.parseArray(mj.getData(),SCUserRole.class);
			for(SCUserRole ur:urs){
				if("A".equals(mj.getOperate())) 
					loginRightDao.addUserRole(ur);
				if("U".equals(mj.getOperate()))
					loginRightDao.modifyUserRole(ur);
				if("D".equals(mj.getOperate())) 
					loginRightDao.removeUserRole(ur);
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = SCException.class)
	public void saveRoleRight(List<ModelJson> list){
		for(ModelJson mj:list){
			List<SCRoleRight> rrs=JSON.parseArray(mj.getData(),SCRoleRight.class);
			for(SCRoleRight rr:rrs){
				if("A".equals(mj.getOperate())) 
					loginRightDao.addRoleRight(rr);
				if("U".equals(mj.getOperate()))
					loginRightDao.modifyRoleRight(rr);
				if("D".equals(mj.getOperate())) 
					loginRightDao.removeRoleRight(rr);
			}
		}
	}
 
	
	public List<SCUser> selectUser(Map<String,Object> params){
		return loginRightDao.selectUser(params);
	}
    public List<SCRole> selectRole(Map<String,Object> params){
    	return loginRightDao.selectRole(params);
    }
    public List<SCResource> selectResource(Map<String,Object> params){
     	return loginRightDao.selectResource(params);
    }
    public List<SCOperation> selectOperation(Map<String,Object> params){
    	return loginRightDao.selectOperation(params);
    }
    public List<SCRoleRight> selectRoleRight(Map<String,Object> params){
    	return loginRightDao.selectRoleRight(params);
    }
    public List<SCUserRole> selectUserRole(Map<String,Object> params){
    	return loginRightDao.selectUserRole(params);
    }

    public void addUser(SCUser user){
    	loginRightDao.addUser(user);
    }
	public void removeUser(SCUser user){
    	loginRightDao.removeUser(user);
	}
	public void modifyUser(SCUser user){
    	loginRightDao.modifyUser(user);
	}
	
	public void addRole(SCRole role){
		loginRightDao.addRole(role);
	}
	public void removeRole(SCRole role){
		loginRightDao.removeRole(role);
	}
	public void modifyRole(SCRole role){
		loginRightDao.modifyRole(role);
	}
	
	public void addResource(SCResource resource){
		loginRightDao.addResource(resource);
	}
	public void removeResource(SCResource resource){
		loginRightDao.removeResource(resource);
	}
	public void modifyResource(SCResource resource){
		loginRightDao.modifyResource(resource);
	}
	
	public void addOperation(SCOperation operation){
		loginRightDao.addOperation(operation);
	}
	public void removeOperation(SCOperation operation){
		loginRightDao.removeOperation(operation);
	}
	public void modifyOperation(SCOperation operation){
		loginRightDao.modifyOperation(operation);
	}

	public void addRoleRight(SCRoleRight roleRight){
		loginRightDao.addRoleRight(roleRight);
	}
	public void removeRoleRight(SCRoleRight roleRight){
		loginRightDao.removeRoleRight(roleRight);
	}
	public void modifyRoleRight(SCRoleRight roleRight){
		loginRightDao.modifyRoleRight(roleRight);
	}

	public void addUserRole(SCUserRole userRole){
		loginRightDao.addUserRole(userRole);
	}
	public void removeUserRole(SCUserRole userRole){
		loginRightDao.removeUserRole(userRole);
	}
	public void modifyUserRole(SCUserRole userRole){
		loginRightDao.modifyUserRole(userRole);
	}
}
