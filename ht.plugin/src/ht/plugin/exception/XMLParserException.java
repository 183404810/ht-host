package ht.plugin.exception;

import java.util.ArrayList;
import java.util.List;

public class XMLParserException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<String> errors;
	
	public XMLParserException(List<String> errors){
		this.errors=errors;
	}
	
	public XMLParserException(String message){
		this.errors=new ArrayList<>();
		this.errors.add(message);
	}
	
	@Override
	public String getMessage(){
		StringBuilder sb=new StringBuilder();
		for(String str:errors){
			sb.append(str);
		}
		return sb+":"+super.getMessage();
	}

}
