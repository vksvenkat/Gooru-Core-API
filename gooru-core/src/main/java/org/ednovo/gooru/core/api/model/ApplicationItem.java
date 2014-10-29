package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class ApplicationItem implements Serializable {
	
	private static final long serialVersionUID = 7173591300165688575L;
	
	private String applicationItemUid;
	private String displayName;
	private String displaySequence;
	private String url;
	
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

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getApplicationItemUid() {
		return applicationItemUid;
	}

	public void setApplicationItemUid(String applicationItemUid) {
		this.applicationItemUid = applicationItemUid;
	}

}
