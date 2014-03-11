/*******************************************************************************
 * UserAccountType.java
 *  gooru-core
 *  Created by Gooru on 2014
 *  Copyright (c) 2014 Gooru. All rights reserved.
 *  http://www.goorulearning.org/
 *       
 *  Permission is hereby granted, free of charge, to any 
 *  person obtaining a copy of this software and associated 
 *  documentation. Any one can use this software without any 
 *  restriction and can use without any limitation rights 
 *  like copy,modify,merge,publish,distribute,sub-license or 
 *  sell copies of the software.
 *  
 *  The seller can sell based on the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be   
 *  included in all copies or substantial portions of the Software. 
 * 
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY    
 *   KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE  
 *   WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR   
 *   PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE       AUTHORS 
 *   OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
 *   OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *   OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 *   WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 *   THE SOFTWARE.
 ******************************************************************************/
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
