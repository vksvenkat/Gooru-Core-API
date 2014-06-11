package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Map;

public class UserImportDO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4360820015398397211L;

	
	private String importCode;
	
	private String firstName;
	
	private String lastName;
	
	private String userName;
	
	private String emailId;
	
	private String organizationCode;
	
	private String sendMail;
	
	private User user;
	
	private Map<String, String> errorMessage;

	public String getImportCode() {
		return importCode;
	}

	public void setImportCode(String importCode) {
		this.importCode = importCode;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getOrganizationCode() {
		return organizationCode;
	}

	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}

	public String getSendMail() {
		return sendMail;
	}

	public void setSendMail(String sendMail) {
		this.sendMail = sendMail;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Map<String, String> getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(Map<String, String> errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	

}
