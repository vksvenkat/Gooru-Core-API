package org.ednovo.gooru.core.exception;

import org.springframework.security.authentication.AccountStatusException;

public class UserNotConfirmedException extends AccountStatusException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserNotConfirmedException(String msg) {
		super(msg);
	}
}
