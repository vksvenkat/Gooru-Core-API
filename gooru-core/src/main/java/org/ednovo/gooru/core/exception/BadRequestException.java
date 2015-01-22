package org.ednovo.gooru.core.exception;

public class BadRequestException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4525654760966310979L;

	private String errorCode;
	
	public BadRequestException() {
		super();
	}

	// Overloaded Constructor for preserving the Message
	public BadRequestException(String msg) {
		super(msg);
	}

	// Overloaded Constructor for preserving the Message & cause
	public BadRequestException(String msg, Throwable cause) {
		super(msg, cause);
	}

	// Overloaded Constructor for preserving the Message & cause
	public BadRequestException(String msg, String errorCode) {
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
