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
	public static final String COLLECTION_THUMBNAIL_SIZES = "160x120,75x56,120x90,80x60,50x40,310x258,800x600";

	public static final String QUIZ_THUMBNAIL_SIZES = "160x120,75x56,120x90,d80x60,50x40,800x600";

	public static final String RESOURCE_THUMBNAIL_SIZES = "80x60,160x120";

	public Thumbnail() {

	}

	public Thumbnail(ResourceType resourceType, String resourceUrl, String thumbnail, String assetURI, String folder) {
		this.setUrl(getUrl(resourceType, resourceUrl, thumbnail, assetURI, folder));
		this.setDimensions(getDimensions(resourceType));
		this.setDefaultImage(isDefaultImage(resourceType, thumbnail));
	}

	public String getUrl(ResourceType resourceType, String resourceUrl, String thumbnail, String assetURI, String folder) {
		if (resourceType != null) {
			if (!resourceType.getName().equalsIgnoreCase("assessment-question")) {
				if (StringUtils.isBlank(thumbnail) && resourceType.getName().equalsIgnoreCase(ResourceType.Type.VIDEO.getType())) {
					this.url = this.getYoutubeVideoId(resourceUrl) == null ? null : "img.youtube.com/vi/" + this.getYoutubeVideoId(resourceUrl) + "/1.jpg";
				} else {
					if (thumbnail != null && thumbnail.contains("gooru-default")) {
						this.url = assetURI + thumbnail;
					} else if (StringUtils.isBlank(thumbnail)) {
						this.url = assetURI + folder + thumbnail;
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

	public String getDimensions(ResourceType resourceType) {
		if (resourceType != null) {
			String resourceTypeName = resourceType.getName();
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

	public boolean isDefaultImage(ResourceType resourceType, String thumbnail) {
		if (resourceType != null) {
			String resourceTypeName = resourceType.getName();
			if (thumbnail == null && !(resourceTypeName.equalsIgnoreCase(ResourceType.Type.VIDEO.getType()))) {
				this.isDefaultImage = true;
			} else if (((resourceTypeName.equalsIgnoreCase(ResourceType.Type.CLASSPLAN.getType()) || resourceTypeName.equalsIgnoreCase(ResourceType.Type.CLASSBOOK.getType()) || resourceTypeName.equalsIgnoreCase(ResourceType.Type.ASSESSMENT_EXAM.getType()) || resourceTypeName
					.equalsIgnoreCase(ResourceType.Type.ASSESSMENT_QUIZ.getType())) && thumbnail.contains("gooru-default"))) {
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
}