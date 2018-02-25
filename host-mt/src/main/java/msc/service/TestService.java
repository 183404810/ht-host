package msc.service;

import ht.msc.util.MemcachedUtils;
import ht.msc.util.MongoDBUtil;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import msc.dao.TestDao;
import msc.entity.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

@Repository  
@Service("testService")
public class TestService {
	
	@Autowired
	private ShardedJedisPool jedisPool;
	
	@Autowired
	private RedisTemplate jedisTemplate;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	private final static String COLLECTION_NAME = "bas_division"; 
	
	@Resource
	private TestDao testDao;
	 
	public List<Test> list() throws IllegalArgumentException, IllegalAccessException{
		ListOperations opsForList = jedisTemplate.opsForList();
		ShardedJedis redis=jedisPool.getResource();
		List<Test> res=new ArrayList<>();
		res=testDao.list();
		if(MemcachedUtils.get("list1")!=null)		
			System.out.println(MemcachedUtils.get("list1"));
		else
			MemcachedUtils.set("list1", res);
		
		List<Test> range = opsForList.range("list", 0, -1);
		if(range.size()>0)		
			System.out.println(range);
		else
			opsForList.rightPush("list" ,res);
		
		DBCollection collection = mongoTemplate.getCollection(COLLECTION_NAME);
        int result = 0;
        DBObject iteminfoObj = MongoDBUtil.bean2DBObject(res.get(0));
        WriteResult writeResult = collection.save(iteminfoObj);
        result = writeResult.getN();
        System.out.println(collection.findOne());

		return res;
	}
}
