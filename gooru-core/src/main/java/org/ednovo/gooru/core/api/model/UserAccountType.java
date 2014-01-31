package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class UserAccountType implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9043764054337836256L;


	private Short accountTypeId;
	
	private String accountType;
	
	
	public static final Integer ACCOUNT_PARENT = 1;
	public static final Integer ACCOUNT_CHILD = 2;
	public static final Integer ACCOUNT_NON_PARENT = 3;
	
	public static enum userAccount{
		PARENT("Parent"),
		CHILD("Child"),
		NON_PARENT("NonParent");
				
		private String type;
		userAccount(String type){
			this.type=type;
		}

		public String getType() {
			return type;
		}
		
	}
	
	public static enum accountCreatedType {
		NORMAL("normal"),
		CHILD("child"),
		GOOGLE_APP("google"),
		SSO("sso");
				
		private String type;
		accountCreatedType(String type){
			this.type=type;
		}

		public String getType() {
			return type;
		}
		
	}

	public Short getAccountTypeId() {
		return accountTypeId;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountTypeId(Short accountTypeId) {
		this.accountTypeId = accountTypeId;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

}
