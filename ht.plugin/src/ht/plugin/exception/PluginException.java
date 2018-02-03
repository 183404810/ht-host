package ht.plugin.exception;

public class PluginException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public PluginException(String message){
		super(message);
	}
	
	public PluginException(Exception e){
		super(e);
	}
	
	public PluginException(Exception e,String message){
		super(message+e.getMessage());
	}
}

