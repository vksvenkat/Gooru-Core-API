package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class EntityOperation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2007539524878855125L;
	
	private Integer entityOperationId;
	private String entityName;
	private String operationName;

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public String getEntityName() {
		return this.entityName;
	}

	public void setEntityName(final String entityName) {
		this.entityName = entityName;
	}

	public Integer getEntityOperationId() {
		return entityOperationId;
	}

	public void setEntityOperationId(Integer entityOperationId) {
		this.entityOperationId = entityOperationId;
	}

}