package org.ednovo.gooru.core.api.model;

public enum SessionStatus {
	OPEN("open"), ARCHIVE("archive");

	private String sessionStatus;

	private SessionStatus(String sessionStatus) {
		this.sessionStatus = sessionStatus;
	}

	public String getSessionStatus() {
		return this.sessionStatus;
	}
	
}
