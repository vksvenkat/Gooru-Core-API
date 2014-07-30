package org.ednovo.gooru.core.exception;

public class MethodFailureException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5700393057572264447L;
	
	public MethodFailureException()
	{
		super();
	}

	//Overloaded Constructor for preserving the Message
	public MethodFailureException(String msg) {
		super(msg);
	}

	//Overloaded Constructor for preserving the Message & cause
	public MethodFailureException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
