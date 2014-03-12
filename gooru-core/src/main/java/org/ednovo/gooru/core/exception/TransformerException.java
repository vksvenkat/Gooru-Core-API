package org.ednovo.gooru.core.exception;

public class TransformerException extends RuntimeException{


	public TransformerException()
	{
		super();
	}

	//Overloaded Constructor for preserving the Message
	public TransformerException(String msg) {
		super(msg);
	}

	//Overloaded Constructor for preserving the Message & cause
	public TransformerException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
