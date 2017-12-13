package msc.service;

import ht.msc.base.BaseDao;
import ht.msc.base.BaseService;

import javax.annotation.Resource;

import msc.dao.UserDao;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository  
@Service("userService")
public class UserService extends BaseService{
	
	@Resource
	private UserDao userDao;
	
	@Override
	public BaseDao init() {
		return userDao;
	}
}
