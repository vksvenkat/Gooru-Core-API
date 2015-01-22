package org.ednovo.gooru.core.exception;

public class NotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 671804410291844746L;

	public NotFoundException()
	{
		super();
	}

	private String errorCode;
	
	

	//Overloaded Constructor for preserving the Message
	public NotFoundException(String msg) {
		super(msg);
	}

	//Overloaded Constructor for preserving the Message & cause
	public NotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public NotFoundException(String msg, String errorCode ) {
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
