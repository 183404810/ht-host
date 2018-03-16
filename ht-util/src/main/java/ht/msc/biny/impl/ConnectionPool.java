package ht.msc.biny.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import ht.msc.biny.AbstractFactory;

public class ConnectionPool extends AbstractFactory<Connection>{
	private static ConnectionPool pool;
	private String driver;
	private String url;
	private String user;
	private String password;
	
	private ConnectionPool(int size){
		super(size);
	}
	
	private void loadConfig(String path){
		File config=new File(path);
		InputStream is;
		try {
			is = new FileInputStream(config);
			System.getProperties().load(is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static ConnectionPool getInstance(int size){
		if(pool==null)
			pool=new ConnectionPool(size);
		return pool;
	}
 
	public void Init() {
		currSize=0;
		Connection conn;
		loadConfig("D:/jdbc.properties");
		driver=System.getProperty("driverClassName");
		url=System.getProperty("url");
		user=System.getProperty("user");
		password=System.getProperty("driverClass");
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		for(int i=0;i<size;i++){
			conn = createInstance();
			this.pools.add(conn);
		}
	}
	public Connection createInstance(){ 
		if(currSize<size){
			try {
				currSize++;
				return DriverManager.getConnection(url, user, password);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	};
	
	public void stop(List<Connection> pools){ 
		for(Connection conn:pools)
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	};
	
	public boolean execute(Connection e) {
		// TODO Auto-generated method stub
		return false;
	}
}