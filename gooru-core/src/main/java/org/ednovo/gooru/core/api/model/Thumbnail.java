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
	private ResourceType thumbnailResourceType;
	private String resourceUrl;
	private String thumbnailName;
	private String thumbnailAssetURI;
	private String thumbnailFolder;
	public static final String COLLECTION_THUMBNAIL_SIZES = "160x120,75x56,120x90,80x60,50x40,310x258,800x600";

	public static final String QUIZ_THUMBNAIL_SIZES = "160x120,75x56,120x90,d80x60,50x40,800x600";

	public static final String RESOURCE_THUMBNAIL_SIZES = "80x60,160x120";

	public Thumbnail() {

	}

	public Thumbnail(ResourceType resourceType, String resourceUrl, String thumbnail, String assetURI, String folder) {
		this.setThumbnailAssetURI(assetURI);
		this.setThumbnailResourceType(resourceType);
		this.setResourceUrl(resourceUrl);
		this.setThumbnailName(thumbnail);
		this.setThumbnailFolder(folder);
		this.setUrl(getUrl());
		this.setDefaultImage(isDefaultImage());
		this.setDimensions(this.getDimensions());
	}

	public String getUrl() {
		if (getThumbnailResourceType() != null) {
			if (!getThumbnailResourceType().getName().equalsIgnoreCase("assessment-question")) {
				if (StringUtils.isBlank(getThumbnailName()) && getThumbnailResourceType().getName().equalsIgnoreCase(ResourceType.Type.VIDEO.getType())) {
					this.url = this.getYoutubeVideoId(getResourceUrl()) == null ? null : "img.youtube.com/vi/" + this.getYoutubeVideoId(getResourceUrl()) + "/1.jpg";
				} else {
					if (getThumbnailName() != null && getThumbnailName().contains("gooru-default")) {
						this.url = getThumbnailAssetURI() + getThumbnailName();
					} else if (getThumbnailName() != null && !getThumbnailName().isEmpty()) {
						this.url = getThumbnailAssetURI() + getThumbnailFolder() + getThumbnailName();
					} else {
						this.url = "";
					}
				}
			}
			else if(getThumbnailName() != null && !getThumbnailName().isEmpty()){
				this.url = getThumbnailAssetURI() + getThumbnailFolder() + getThumbnailName();
			}
		}
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDimensions() {
		if (getThumbnailResourceType() != null) {
			String resourceTypeName = getThumbnailResourceType().getName();
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
		if (getThumbnailResourceType() != null) {
			String resourceTypeName = getThumbnailResourceType().getName();
			if (getThumbnailName() == null && !(resourceTypeName.equalsIgnoreCase(ResourceType.Type.VIDEO.getType()))) {
				this.isDefaultImage = true;
			} else if (((resourceTypeName.equalsIgnoreCase(ResourceType.Type.CLASSPLAN.getType()) || resourceTypeName.equalsIgnoreCase(ResourceType.Type.CLASSBOOK.getType()) || resourceTypeName.equalsIgnoreCase(ResourceType.Type.ASSESSMENT_EXAM.getType()) || resourceTypeName
					.equalsIgnoreCase(ResourceType.Type.ASSESSMENT_QUIZ.getType())) && getThumbnailName().contains("gooru-default"))) {
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

	public String getResourceUrl() {
		return resourceUrl;
	}

	public void setResourceUrl(String resourceUrl) {
		this.resourceUrl = resourceUrl;
	}

	public ResourceType getThumbnailResourceType() {
		return thumbnailResourceType;
	}

	public void setThumbnailResourceType(ResourceType thumbnailResourceType) {
		this.thumbnailResourceType = thumbnailResourceType;
	}

	public String getThumbnailName() {
		return thumbnailName;
	}

	public void setThumbnailName(String thumbnailName) {
		this.thumbnailName = thumbnailName;
	}

	public String getThumbnailAssetURI() {
		return thumbnailAssetURI;
	}

	public void setThumbnailAssetURI(String thumbnailAssetURI) {
		this.thumbnailAssetURI = thumbnailAssetURI;
	}

	public String getThumbnailFolder() {
		return thumbnailFolder;
	}

	public void setThumbnailFolder(String thumbnailFolder) {
		this.thumbnailFolder = thumbnailFolder;
	}

}