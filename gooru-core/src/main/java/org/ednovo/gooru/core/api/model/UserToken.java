package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;


public class UserToken  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9030494179188905938L;

	public static final String DEFAULT_TOKEN = "NA";

	private String sessionId;
	private String token;
	private String scope;
	private Application application;
	private Date createdOn;
	private User user;
	private boolean firstLogin;
	private String restEndPoint;
	private String dateOfBirth;
	private String userRole;
	
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public void setFirstLogin(boolean firstLogin) {
		this.firstLogin = firstLogin;
	}

	public boolean isFirstLogin() {
		return firstLogin;
	}

	public String getRestEndPoint() {
		return restEndPoint;
	}

	public void setRestEndPoint(String restEndPoint) {
		this.restEndPoint = restEndPoint;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public void setUserRole(String userType) {
		this.userRole = userType;
	}

	public String getUserRole() {
		return userRole;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}
}
