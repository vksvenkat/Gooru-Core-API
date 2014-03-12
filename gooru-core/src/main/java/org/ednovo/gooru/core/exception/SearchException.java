/**
 * 
 */
package org.ednovo.gooru.core.exception;

import org.springframework.http.HttpStatus;

/**
 * @author SearchTeam
 * 
 */
public class SearchException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1212631626429455799L;
	
	private HttpStatus status;

	public SearchException(HttpStatus status,
			String message) {
		super(message);
		setStatus(status);
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

}
