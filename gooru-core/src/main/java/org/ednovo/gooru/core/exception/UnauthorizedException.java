package org.ednovo.gooru.core.exception;

public class UnauthorizedException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4525654760966310979L;

	private String errorCode;
	
	public UnauthorizedException()
	{
		super();
	}

	//Overloaded Constructor for preserving the Message
	public UnauthorizedException(String msg) {
		super(msg);
	}

	//Overloaded Constructor for preserving the Message & cause
	public UnauthorizedException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public UnauthorizedException(String msg, String  errorCode) {
		super(msg);
		this.setErrorCode(errorCode);
		
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	

}
