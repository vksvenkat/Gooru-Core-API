package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class UserAvailability implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2260693037606056395L;

	public static enum CheckUser {
		BYUSERNAME("byUsername"), BYEMAILID("byEmailid");

		String checkUser;

		CheckUser(String checkUser) {
			this.checkUser = checkUser;
		}
		public String getCheckUser(){
			return this.checkUser;
		}

	}
}
