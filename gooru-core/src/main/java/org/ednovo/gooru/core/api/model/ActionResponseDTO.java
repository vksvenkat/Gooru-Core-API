package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

import org.springframework.validation.Errors;

public class ActionResponseDTO<M extends Serializable> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7383616087257131292L;
	

	public ActionResponseDTO() {

	}

	public ActionResponseDTO(M model, Errors errors) {
		this.model = model;
		this.errors = errors;
	}

	private M model;

	private Errors errors;

	public M getModel() {
		return model;
	}

	public void setModel(M model) {
		this.model = model;
	}

	public Errors getErrors() {
		return errors;
	}

	public void setErrors(Errors errors) {
		this.errors = errors;
	}

	public Object getModelData() {
		return (getErrors().hasErrors()) ? getErrors() : getModel();
	}

}
