package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;

public class Classpage extends Collection implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4360758388302848819L;

	private String classpageCode;
	private Date associationDate;


	public Date getAssociationDate() {
		return associationDate;
	}

	public void setAssociationDate(Date associationDate) {
		this.associationDate = associationDate;
	}

	public void setClasspageCode(String classpageCode) {
		this.classpageCode = classpageCode;
	}

	public String getClasspageCode() {
		return classpageCode;
	}

}
