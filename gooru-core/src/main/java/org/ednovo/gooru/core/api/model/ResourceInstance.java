package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("resourceInstance")
public class ResourceInstance implements Serializable, Comparable<ResourceInstance> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5152466220602302890L;

	/**
	 * 
	 */


	private String resourceInstanceId;
	
	@JsonBackReference
	private Segment segment;

	private String title;

	private Resource resource;

	private Integer sequence;

	private String start;

	private String stop;

	private String narrative;

	private String description;

	private String documentid;
	
	private String documentkey;
	
	private Boolean shortenedUrlStatus;
	
	private Set<StandardFo> standards = new  HashSet<StandardFo>();
	
	private String keyPoints;
	
	private String narrationLink; 
	
	public ResourceInstance() {
		resource = new Resource();
		
	}

	public ResourceInstance(String segmentId) {
		resource = new Resource();
		segment = new Segment();
		segment.setSegmentId(segmentId);
	}

	public ResourceInstance(String segmentId, Resource resource) {
		segment = new Segment();
		segment.setSegmentId(segmentId);
		this.resource = resource;
	}

	public ResourceInstance(Segment segment) {
		this.segment = segment;
	}

	public ResourceInstance(Segment segment, Resource resource) {
		this.segment = segment;
		this.resource = resource;
	}

	public Segment getSegment() {
		return segment;
	}

	public void setSegment(Segment segment) {
		this.segment = segment;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public String getInstructionInfo() {
		return (narrative == null) ? "" : narrative;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getStop() {
		return stop;
	}

	public void setStop(String stop) {
		this.stop = stop;
	}

	public String getNarrative() {
		return narrative;
	}

	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public int compareTo(ResourceInstance arg0) {
		if (segment != null && arg0.getSegment() != null && this.getResource() != null && arg0.getResource() != null && arg0.getResource().getContentId() != null) {
			String key = this.getResourceInstanceId()+" "+this.getSegment().getSegmentId() + " " + this.getResource().getContentId();
			String compare = arg0.getResourceInstanceId()+" "+ arg0.getSegment().getSegmentId() + " " + arg0.getResource().getContentId();
			int compareValue = 0;
			if(key.equalsIgnoreCase(compare)) {
			    compareValue = 0;
			} else if( this.getSegment().getSequence() < arg0.getSegment().getSequence()) {
			    compareValue = -1;
			} else if( this.getSegment().getSequence() > arg0.getSegment().getSequence()) {
			    compareValue = 1;
			}else if( this.getSegment().getSequence() == arg0.getSegment().getSequence()) {
			    compareValue = 0;
			    if(this.getSequence() < arg0.getSequence()) {
				compareValue = -1;
			    }else if(this.getSequence() > arg0.getSequence()) {
				compareValue = 1;
			    }
			}
			return compareValue;
		}
		return 0;
	}

	public String getResourceInstanceId() {
		return resourceInstanceId;
	}

	public void setResourceInstanceId(String resourceInstanceId) {
		this.resourceInstanceId = resourceInstanceId;
	}

	public void setDocumentid(String documentid) {
		this.documentid = documentid;
	}

	public String getDocumentid() {
		return documentid;
	}

	public void setDocumentkey(String documentkey) {
		this.documentkey = documentkey;
	}

	public String getDocumentkey() {
		return documentkey;
	}

	public Boolean getShortenedUrlStatus() {
		return shortenedUrlStatus;
	}

	public void setShortenedUrlStatus(Boolean shortenedUrlStatus) {
		this.shortenedUrlStatus = shortenedUrlStatus;
	}

	public void setStandards(Set<StandardFo> standards) {
		this.standards = standards;
	}

	public Set<StandardFo> getStandards() {
		return standards;
	}
	

	public void setKeyPoints(String keyPoints) {
		this.keyPoints = keyPoints;
	}

	public String getKeyPoints() {
		return keyPoints;
	}

	public void setNarrationLink(String narrationLink) {
		this.narrationLink = narrationLink;
	}

	public String getNarrationLink() {
		return narrationLink;
	}
	
}