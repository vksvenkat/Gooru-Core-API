package org.ednovo.gooru.core.exception;

public class BadRequestException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4525654760966310979L;

	public BadRequestException()
	{
		super();
	}

	//Overloaded Constructor for preserving the Message
	public BadRequestException(String msg) {
		super(msg);
	}

	//Overloaded Constructor for preserving the Message & cause
	public BadRequestException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
