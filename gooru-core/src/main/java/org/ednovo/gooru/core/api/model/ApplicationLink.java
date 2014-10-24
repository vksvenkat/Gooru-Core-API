package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class ApplicationLink implements Serializable {
	
	private static final long serialVersionUID = 7173591300165688575L;
	
	private String linkId;
	private String displayName;
	private String displaySequence;
	private String applicationLinkUrl;
	
	private Application application;


	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplaySequence() {
		return displaySequence;
	}

	public void setDisplaySequence(String displaySequence) {
		this.displaySequence = displaySequence;
	}

	public String getApplicationLinkUrl() {
		return applicationLinkUrl;
	}

	public void setApplicationLinkUrl(String applicationLinkUrl) {
		this.applicationLinkUrl = applicationLinkUrl;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public String getLinkId() {
		return linkId;
	}

	public void setLinkId(String linkId) {
		this.linkId = linkId;
	}

}
