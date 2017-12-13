package msc.mapper;

import java.util.List;
import java.util.Map;

import msc.entiry.SCOperation;
import msc.entiry.SCResource;
import msc.entiry.SCRole;
import msc.entiry.SCRoleRight;
import msc.entiry.SCUser;
import msc.entiry.SCUserRole;

import org.apache.ibatis.annotations.Param;

public interface LoginRightMapper {

    public List<SCUser> selectUser(@Param("params")Map<String,Object> params);
    public List<SCRole> selectRole(@Param("params")Map<String,Object> params);
    public List<SCResource> selectResource(@Param("params")Map<String,Object> params);
    public List<SCOperation> selectOperation(@Param("params")Map<String,Object> params);
    public List<SCRoleRight> selectRoleRight(@Param("params")Map<String,Object> params);
    public List<SCUserRole> selectUserRole(@Param("params")Map<String,Object> params);

    public void insertUser(SCUser user);
	public void deleteUser(SCUser user);
	public void updateUser(SCUser user);
	
	public void insertRole(SCRole role);
	public void deleteRole(SCRole role);
	public void updateRole(SCRole role);
	
	public void insertResource(SCResource resource);
	public void deleteResource(SCResource resource);
	public void updateResource(SCResource resource);
	
	public void insertOperation(SCOperation operation);
	public void deleteOperation(SCOperation operation);
	public void updateOperation(SCOperation operation);

	public void insertRoleRight(SCRoleRight roleRight);
	public void deleteRoleRight(SCRoleRight roleRight);
	public void updateRoleRight(SCRoleRight roleRight);

	public void insertUserRole(SCUserRole userRole);
	public void deleteUserRole(SCUserRole userRole);
	public void updateUserRole(SCUserRole userRole);
}
