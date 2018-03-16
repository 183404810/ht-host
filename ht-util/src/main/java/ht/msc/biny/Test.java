package ht.msc.biny;

import java.sql.Connection;

import ht.msc.biny.impl.ConnectionPool;

public class Test {
	public static void main(String args[]){
		final ConnectionPool t=ConnectionPool.getInstance(5);
	 
		
		for(int i=0;i<20;i++){
			Connection con=t.getInstance();
			
			System.out.println(t.getInstance());
			t.release(con);
		}
		t.destory();
		while(true){
			System.out.print("all size = "+t.getAllSize());
			System.out.print("; exe size="+t.getExeSize());
			System.out.println("; relase size=" + t.getSize());
		}
	}	
}

class myThread implements Runnable{
	private String name;
	
	public myThread(String name){
		this.name=name;
	}
	@Override
	public String toString(){
		return this+"@"+name;
	}
	public void run() {
		    System.out.println("线程--"+name+"--Started");
		    int i=0;
		    while(i<10000000) i++;
			System.out.println("线程--"+name+"--Ended");
	}	
}