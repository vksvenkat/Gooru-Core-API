package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@JsonFilter("segment")
public class Segment implements Serializable, Comparable<Segment> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5027132707226934579L;



	public static final String BLANK_JSON = "{\"segment\": {\"title\": \"\", \"rendition\": \"\",\"description\": \"\",\"duration\": \"\"}}";

	private String segmentId;
	private String title;
	private String description;
	private String renditionUrl;
	private String duration;
	private String type;

	private Integer sequence;
		
	private String xmlSegmentId;
	
	private Integer isMeta;
	
	private String keyPoints;
	
	private String narrationLink; 
		
	private Integer noOfQuestions;
		
	private Integer attemptedQuestions;
    
	private Long percentage;

	@JsonManagedReference
	private Set<ResourceInstance> resourceInstances;
	
	private String concept;
	
	private String segmentImage;
	
	public Segment() {
	}

	public String getType() {
		return type;
	}

	public Integer getIsMeta() {
		return isMeta;
	}

	public void setIsMeta(Integer isMeta) {
		this.isMeta = isMeta;
	}

	@Override
	public String toString() {
		return "segment_id:" + segmentId;

	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSegmentId() {
		return segmentId;
	}

	public void setSegmentId(String segmentId) {
		this.segmentId = segmentId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		if (description != null && description.length() > 0) {
			description = description.replaceAll("<p>", "<li>");
			description = description.replaceAll("</p>", "</li>");
			description = description.replaceAll("<ul>", "");
			description = description.replaceAll("</ul>", "");

			description = "<ul>" + description + "</ul>";
		}
		this.description = description;
	}
	public String getRenditionUrl() {
		return renditionUrl;
	}

	public void setRenditionUrl(String renditionUrl) {
		this.renditionUrl = renditionUrl;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Set<ResourceInstance> getResourceInstances() {
		return resourceInstances;
	}

	public void setResourceInstances(Set<ResourceInstance> resourceInstances) {
		this.resourceInstances = resourceInstances;
	}

	@Override
	public int compareTo(Segment segment) {
		if (segment != null && this.getSegmentId() != null) {
			return this.getSegmentId().compareTo(segment.getSegmentId());
		}
		return 0;
	}

	public String getXmlSegmentId() {
		return xmlSegmentId;
	}

	public void setXmlSegmentId(String xmlSegmentId) {
		this.xmlSegmentId = xmlSegmentId;
	}

	public String getConcept() {
		return concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}

	public void setSegmentImage(String segmentImage) {
		this.segmentImage = segmentImage;
	}

	public String getSegmentImage() {
		return segmentImage;
	}

	public void setKeyPoints(String keyPoints) {
		this.keyPoints = keyPoints;
	}

	public String getKeyPoints() {
		return keyPoints;
	}

	public void setNoOfQuestions(Integer noOfQuestions) {
		noOfQuestions = noOfQuestions;
	}

	public Integer getNoOfQuestions() {
		return noOfQuestions;
	}

	public void setAttemptedQuestions(Integer attemptedQuestions) {
		this.attemptedQuestions = attemptedQuestions;
	}

	public Integer getAttemptedQuestions() {
		return attemptedQuestions;
	}

	public void setPercentage(Long percentage) {
		this.percentage = percentage;
	}

	public Long getPercentage() {
		return percentage;
	}

	public String getNarrationLink() {
		return narrationLink;
	}

	public void setNarrationLink(String narrationLink) {
		this.narrationLink = narrationLink;
	}	
	
	
}
