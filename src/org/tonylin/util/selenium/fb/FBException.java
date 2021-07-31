package org.tonylin.util.selenium.fb;

public class FBException extends Exception {
	private static final long serialVersionUID = 1L;

	public FBException(String msg){
		super(msg);
	}
	
	public FBException(Throwable e){
		super(e);
	}
	
}
