package org.ednovo.gooru.core.exception;

public class UnauthorizedException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4525654760966310979L;

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

}
