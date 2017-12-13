package msc.dao;

import ht.msc.base.BaseDao;
import ht.msc.base.BaseMapper;

import javax.annotation.Resource;

import msc.mapper.UserMapper;

import org.springframework.stereotype.Repository;

@Repository("userDao") 
public class UserDao extends BaseDao{
	@Resource
	private UserMapper userMapper;
	
	@Override
	public BaseMapper init() {
		return userMapper;
	}
}
