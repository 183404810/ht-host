package ht.msc.biny;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractFactory<T> implements Factory<T>,Pool<T> {
	protected Integer maxSize;
	protected Integer size;
	protected Integer currSize;
	protected List<T> pools;
	protected List<T> executePools;
	
	public AbstractFactory(Integer size){
		this.size=size;
		if(pools==null)
			pools=new ArrayList<T>(size);
		executePools=new ArrayList<T>(size);
		this.Init();
	}
	
	public T getInstance(){
		T o=null;
		if(pools.size()<=size && pools.size()>0){
			synchronized(pools){
				o=pools.get(0);
				pools.remove(o);
			}
		}else{
			o=createInstance();	
		}
		if(o!=null){
			synchronized(executePools){
				executePools.add(o);
			}
		}
		return o;
	}
 
	public void release(T o){
		if(o==null) return;
		synchronized(executePools){
			executePools.remove(o);
		}
		synchronized(pools){
			pools.add(o);
		}
		
	}
	
	public void destory(){
		pools.clear();
		if(executePools.size()>0){
			stop(executePools);
			executePools.clear();
		}
		maxSize=0;
		size=0;
		currSize=0;
		System.gc();
	}
	
	public void stop(List<T> pools){ };
	public T createInstance(){ return null;};
	
	public Integer getAllSize(){
		return currSize;
	}
	
	public Integer getExeSize(){
		return executePools.size();
	}
	
	public Integer getSize(){
		return pools.size();
	}
}
