package org.ednovo.gooru.core.application.util.formatter;

import java.util.List;

public class SegmentFo {

	private String id;
	private List<ResourceFo> resources;
	private String concept;
	private String title;
	private String duration;
	private String description;
	private String nativeurl;
	private String type;
	private String segmentImage;
	private Integer resourceCount;

	public SegmentFo() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<ResourceFo> getResources() {
		return resources;
	}

	public void setResources(List<ResourceFo> resources) {
		this.resources = resources;
	}

	public String getConcept() {
		return concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNativeurl() {
		return nativeurl;
	}

	public void setNativeurl(String nativeurl) {
		this.nativeurl = nativeurl;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setSegmentImage(String segmentImage) {
		this.segmentImage = segmentImage;
	}

	public String getSegmentImage() {
		return segmentImage;
	}

	public Integer getResourceCount() {
		return resourceCount;
	}

	public void setResourceCount(Integer resourceCount) {
		this.resourceCount = resourceCount;
	}
	
	

}
