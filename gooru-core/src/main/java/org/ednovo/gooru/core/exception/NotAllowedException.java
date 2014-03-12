package org.ednovo.gooru.core.exception;

public class NotAllowedException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4525654760966310979L;

	public NotAllowedException()
	{
		super();
	}

	//Overloaded Constructor for preserving the Message
	public NotAllowedException(String msg) {
		super(msg);
	}

	//Overloaded Constructor for preserving the Message & cause
	public NotAllowedException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
