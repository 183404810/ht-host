package ht.msc.biny.impl;

import java.util.ArrayList;
import java.util.List;

import ht.msc.biny.AbstractFactory;

public class ThreadPool extends AbstractFactory<Runnable>{
 
	private List<ExceThread> executePools;
	private boolean isFinished;
	private boolean start;
	private Thread exe;
	
	private static ThreadPool instance;
	
	public static ThreadPool getInstance(int size){
		if(instance==null)
			instance=new ThreadPool(size);
		return instance;
	}
	
	private ThreadPool(Integer size) {
		super(size);
		executePools=new ArrayList<ExceThread>(size);
	}
	
	public void Init() {
		currSize=0;
		start=true;
		
		if(exe==null)
			exe=new Thread(new Runnable(){
				public void run() {
					Runnable t=null;
					Thread e;
					while(start){
						if(pools.size()<=0){
							if(executePools.size()<=0)
								isFinished=true; 
							continue;
						}
						
						//synchronized(pools){
							t=pools.get(0);
						//}	
						e=getExecutor(t);
						if(e!=null){
							e.start();
							synchronized(pools){
								pools.remove(t);
							}
						}
					}
				}
			});
		exe.start();
	}
	
	private Thread getExecutor(Runnable t){
		ExceThread o=null;
 
		if(t==null) return null;
		if(executePools.size()<size){
			o=new ExceThread(t);
			synchronized(executePools){
				executePools.add(o);
			}
		}
		return o;
	}
	
 	public void interrupt(){
		this.start=false;
	}
	
	public boolean execute(Runnable task) {
		Thread t=new Thread(task);
		isFinished=false; 
		synchronized(pools){
			pools.add(t);
		}
		return true;
	}
 
	public boolean isFinished(){
		return ((pools.size()==0)&&isFinished);
	}
	
	private class ExceThread extends Thread{
		private Runnable task;
		public ExceThread(Runnable task){
			this.task=task;
		}
		@Override
		public void run(){
			task.run();
			synchronized(executePools){
				executePools.remove(this);
			}
			try {
				this.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
