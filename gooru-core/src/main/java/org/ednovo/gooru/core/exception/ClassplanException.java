package org.ednovo.gooru.core.exception;

public class ClassplanException extends RuntimeException {

	public ClassplanException()
	{
		super();
	}

	//Overloaded Constructor for preserving the Message
	public ClassplanException(String msg) {
		super(msg);
	}

	//Overloaded Constructor for preserving the Message & cause
	public ClassplanException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
