package org.ednovo.gooru.core.api.model;

import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.TrackActivity;

public class Assignment extends Collection {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9077185395746880833L;

	private Long assignmentContentId;
	
	private TrackActivity trackActivity;

	public Long getAssignmentContentId() {
		return assignmentContentId;
	}

	public void setAssignmentContentId(Long assignmentContentId) {
		this.assignmentContentId = assignmentContentId;
	}

	public void setTrackActivity(TrackActivity trackActivity) {
		this.trackActivity = trackActivity;
	}

	public TrackActivity getTrackActivity() {
		return trackActivity;
	}

	
	
}
