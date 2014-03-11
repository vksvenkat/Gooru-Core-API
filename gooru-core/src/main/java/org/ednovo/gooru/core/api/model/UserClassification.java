/*******************************************************************************
 * UserClassification.java
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

import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.User;

public class UserClassification implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -349689314340064916L;
	private User user;
	private CustomTableValue type;
	private Code code;
	private String classificationId;
	private User creator;
	private Integer activeFlag;
	private String grade;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public CustomTableValue getType() {
		return type;
	}

	public void setType(CustomTableValue type) {
		this.type = type;
	}

	public Code getCode() {
		return code;
	}

	public void setCode(Code code) {
		this.code = code;
	}

	public void setClassificationId(String classificationId) {
		this.classificationId = classificationId;
	}

	public String getClassificationId() {
		return classificationId;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public User getCreator() {
		return creator;
	}

	public void setActiveFlag(Integer activeFlag) {
		this.activeFlag = activeFlag;
	}

	public Integer getActiveFlag() {
		return activeFlag;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getGrade() {
		return grade;
	}

}
