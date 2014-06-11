package org.ednovo.gooru.core.api.model;


public class Textbook extends Resource {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9015263472821069373L;


	private String documentId;
	private String documentKey;

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getDocumentKey() {
		return documentKey;
	}

	public void setDocumentKey(String documentKey) {
		this.documentKey = documentKey;
	}

}
