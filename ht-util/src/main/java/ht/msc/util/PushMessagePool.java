package ht.msc.util;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;


public class PushMessagePool {
	/**
	 *  每个用户异步消息最大条娄
	 */
	private static Integer UserRedisMessageMaxCount=100;
	
	@Resource
	private RedisClient redisClient;

	private String getUserRedisKey(String key){
		return SysConstans.REDIS_CACHE_UserMessageKey+key;
	}
	
	/**
	 * 增加消息到队列
	 * @param key
	 * @param val
	 */
	@SuppressWarnings("unchecked")
	public void addMsg(String key,Object val) {
		ArrayList<Object> list = new ArrayList<Object>();
		key=getUserRedisKey(key);
		if(redisClient.get(key)!=null){
			list = (ArrayList<Object>) redisClient.get(key);
			int list_size=list.size();
			if(list !=null && list_size > 0){
				val=setMsgId(val, (long)list.size());
				//超过最大条数先清空
				if(list_size>UserRedisMessageMaxCount){
					list.clear();
				}
				list.add(val);
			}else{
				val=setMsgId(val, (long)0);
				list.add(val);
			}
		}else{
			val=setMsgId(val, (long)0);
			list.add(val);
		}
		redisClient.set(key, list,(int)CommonUtil.getCacheAliveTime());//缓存一天
	}
	
	/**
	 *  返回msg list
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Object> getMsg(String key) {
		return (ArrayList<Object>) redisClient.get(getUserRedisKey(key));
	}
	
	/**
	 * 发送系统消息通知用户
	 * @param userId
	 * @param val
	 */
	public void sendMsg(String userId,Object val) {
		addMsg(userId, val);
	}
	
	/**
	 * 设置msg的id
	 * @param obj
	 * @param id
	 * @return
	 */
	public Object setMsgId(Object obj,Long id) {
		if (obj instanceof PushMessage) {
			((PushMessage) obj).setId(id);
		}
		return obj;
	}
	
	
	@SuppressWarnings("unused")
	@PostConstruct
	private void afterInit(){
	}
	
	@SuppressWarnings("unused")
	@PreDestroy
	private void beforeDestroy(){
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//System.out.println(getCacheAliveTime());
	}
}
