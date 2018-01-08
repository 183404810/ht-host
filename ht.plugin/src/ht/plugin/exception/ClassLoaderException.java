package ht.plugin.exception;

public class ClassLoaderException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ClassLoaderException(String message){
		super(message);
	}
	
	public ClassLoaderException(Exception e){
		super(e);
	}
	
	public ClassLoaderException(Exception e,String message){
		super(message+e.getMessage());
	}
}
