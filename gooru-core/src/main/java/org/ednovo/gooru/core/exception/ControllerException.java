package org.ednovo.gooru.core.exception;

public class ControllerException  extends RuntimeException{

	public ControllerException()
	{
		super();
	}

	//Overloaded Constructor for preserving the Message
	public ControllerException(String msg) {
		super(msg);
	}

	//Overloaded Constructor for preserving the Message & cause
	public ControllerException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
