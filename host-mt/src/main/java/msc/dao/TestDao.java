package msc.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import msc.entiry.Test;
import msc.mapper.TestMapper;

@Repository("testDao") 
public class TestDao {
	@Resource
	private TestMapper testMapper;
	
	public List<Test> list(){
		return testMapper.list();
	}
}
