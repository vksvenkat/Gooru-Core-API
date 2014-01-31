package org.ednovo.gooru.core.exception;

public class CollaboratorException extends RuntimeException {

	public CollaboratorException()
	{
		super();
	}

	//Overloaded Constructor for preserving the Message
	public CollaboratorException(String msg) {
		super(msg);
	}

	//Overloaded Constructor for preserving the Message & cause
	public CollaboratorException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
