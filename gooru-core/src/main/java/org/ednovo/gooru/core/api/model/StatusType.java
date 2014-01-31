package org.ednovo.gooru.core.api.model;


import java.io.Serializable;

public class StatusType implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 3623327004657484905L;
	private Integer statusId;
	private String name;

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	public Integer getStatusId() {
		return statusId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
