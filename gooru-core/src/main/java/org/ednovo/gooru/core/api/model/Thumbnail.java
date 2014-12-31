package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class Thumbnail implements Serializable {

	private static final long serialVersionUID = 7789051171506439642L;
	private String url;
	private String dimensions;
	private boolean isDefaultImage;
	private ResourceType resourceType;
	private String resourceUrl;
	private String thumbnail;
	private String assetURI;
	private String folder;
	public static final String COLLECTION_THUMBNAIL_SIZES = "160x120,75x56,120x90,80x60,50x40,310x258,800x600";

	public static final String QUIZ_THUMBNAIL_SIZES = "160x120,75x56,120x90,d80x60,50x40,800x600";

	public static final String RESOURCE_THUMBNAIL_SIZES = "80x60,160x120";

	public Thumbnail() {

	}

	public Thumbnail(ResourceType resourceType, String resourceUrl, String thumbnail, String assetURI, String folder) {
		this.setAssetURI(assetURI);
		this.setResourceType(resourceType);
		this.setResourceUrl(resourceUrl);
		this.setThumbnail(thumbnail);
		this.setFolder(folder);
	}

	public String getUrl() {
		if (getResourceType() != null) {
			if (!getResourceType().getName().equalsIgnoreCase("assessment-question")) {
				if (StringUtils.isBlank(getThumbnail()) && getResourceType().getName().equalsIgnoreCase(ResourceType.Type.VIDEO.getType())) {
					this.url = this.getYoutubeVideoId(getResourceUrl()) == null ? null : "img.youtube.com/vi/" + this.getYoutubeVideoId(getResourceUrl()) + "/1.jpg";
				} else {
					if (getThumbnail() != null && getThumbnail().contains("gooru-default")) {
						this.url = getAssetURI() + getThumbnail();
					} else if (getThumbnail() != null && !getThumbnail().isEmpty()) {
						this.url = getAssetURI() + getFolder() + getThumbnail();
					} else {
						this.url = "";
					}
				}
			}
		}
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDimensions() {
		if (getResourceType() != null) {
			String resourceTypeName = getResourceType().getName();
			if (resourceTypeName.equalsIgnoreCase(ResourceType.Type.CLASSPLAN.getType()) || resourceTypeName.equalsIgnoreCase(ResourceType.Type.CLASSBOOK.getType())) {
				this.dimensions = COLLECTION_THUMBNAIL_SIZES;
			} else if (resourceTypeName.equalsIgnoreCase(ResourceType.Type.ASSESSMENT_QUIZ.getType()) || resourceTypeName.equalsIgnoreCase(ResourceType.Type.ASSESSMENT_EXAM.getType())) {
				this.dimensions = QUIZ_THUMBNAIL_SIZES;
			} else {
				this.dimensions = RESOURCE_THUMBNAIL_SIZES;
			}
			return dimensions;
		} else {
			return null;
		}
	}

	public void setDimensions(String dimensions) {
		this.dimensions = dimensions;
	}

	public boolean isDefaultImage() {
		if (getResourceType() != null) {
			String resourceTypeName = getResourceType().getName();
			if (getThumbnail() == null && !(resourceTypeName.equalsIgnoreCase(ResourceType.Type.VIDEO.getType()))) {
				this.isDefaultImage = true;
			} else if (((resourceTypeName.equalsIgnoreCase(ResourceType.Type.CLASSPLAN.getType()) || resourceTypeName.equalsIgnoreCase(ResourceType.Type.CLASSBOOK.getType()) || resourceTypeName.equalsIgnoreCase(ResourceType.Type.ASSESSMENT_EXAM.getType()) || resourceTypeName
					.equalsIgnoreCase(ResourceType.Type.ASSESSMENT_QUIZ.getType())) && getThumbnail().contains("gooru-default"))) {
				this.isDefaultImage = true;
			} else {
				this.isDefaultImage = false;
			}
		}

		return isDefaultImage;
	}

	public void setDefaultImage(boolean isDefaultImage) {
		this.isDefaultImage = isDefaultImage;
	}

	private String getYoutubeVideoId(String url) {
		String pattern = "youtu(?:\\.be|be\\.com)/(?:.*v(?:/|=)|(?:.*/)?)([a-zA-Z0-9-_]{11}+)";
		String videoId = null;
		Pattern compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = compiledPattern.matcher(url);
		if (matcher != null) {
			while (matcher.find()) {
				videoId = matcher.group(1);
			}
		}
		return videoId;
	}

	public ResourceType getResourceType() {
		return resourceType;
	}

	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}

	public String getResourceUrl() {
		return resourceUrl;
	}

	public void setResourceUrl(String resourceUrl) {
		this.resourceUrl = resourceUrl;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getAssetURI() {
		return assetURI;
	}

	public void setAssetURI(String assetURI) {
		this.assetURI = assetURI;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}
}