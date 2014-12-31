package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ednovo.gooru.core.application.util.BaseUtil;
import org.ednovo.gooru.core.application.util.ResourceMetaInfo;
import org.ednovo.gooru.core.security.AuthenticationDo;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import flexjson.JSON;

@JsonFilter("resource")
public class Resource extends Content implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9010445497258009775L;

	private static final String INDEX_TYPE = "resource";

	public static enum RecordSource {
		QUOTED("userquoted"), CRAWLED("goorucrawled"), COLLECTION("useradded"), GAT("adminadded"), DEFAULT("notadded");

		String source;

		RecordSource(String source) {
			this.source = source;
		}

		public String getRecordSource() {
			return this.source;
		}
	}

	private String url;

	private String title;

	private ResourceType resourceType;

	public License license;

	private String folder;

	private Integer numberOfSubcribers;

	private String thumbnail;

	private String fileHash;

	private String description;

	private ResourceSource resourceSource;

	private String parentUrl;

	private List<Code> codes;

	private String sourceReference;

	private Short distinguish;

	private String recordSource;

	private String tags;

	private String siteName;

	private String batchId;

	private String assetURI;

	private Date addDate;

	private String category;

	private Integer collaboratorCount;

	private String averageTime;

	private Integer averageTimeSpent;

	private Integer collectionCount;

	private Integer scollectionCount;

	private String scollectionTitles;

	private String scollectionIds;

	private Integer isFeatured = 0;

	private Set<Segment> resourceSegments;

	private byte[] fileData;

	private boolean isNew = false;

	private List<ResourceInstance> resourceInstances;

	private transient List<Learnguide> resourceLearnguides;

	private List<ContentMetaDTO> depthOfKnowledges;

	@JsonManagedReference
	private Set<ResourceMetaData> resourceMetaData;

	private ResourceInfo resourceInfo;

	private String social;

	private Map<String, String> customFieldValues;

	private Thumbnail thumbnails;

	private Boolean hasFrameBreaker;

	private Integer brokenStatus;

	private Long views = 0L;

	private String lessonsString;

	private String vocaularyString;

	private Integer s3UploadFlag = 0;

	private Integer viewCount;

	private Integer subscriptionCount;

	private Map<String, String> customFields;

	private String mediaType;

	private String taxonomyDataSet;

	private String durationInSec;

	private String numOfPages;

	private Integer votesUp;

	private Integer votesDown;

	private String vocabulary;

	private String grade;

	private Integer userRating;

	private String resultUId;

	private String copiedResourceId;

	private TrackActivity trackActivity;

	private Integer isOer;

	private String goals;

	private Boolean hasAdvertisement;

	private AttachDTO attach;

	private Map<Integer, List<Code>> taxonomyMapByCode;

	private CustomTableValue resourceFormat;

	private CustomTableValue instructional;

	private Map<String, Object> ratings;

	private List<ContentMetaDTO> educationalUse;

	private List<ContentMetaDTO> momentsOfLearning;

	private ResourceMetaInfo metaInfo;

	private List<String> publisher;

	private List<String> aggregator;

	private List<String> host;

	private List<Map<String, Object>> resourceTags;
	private String clusterUid;
	private Integer isRepresentative;
	private List<String> libraryNames;

	public Resource() {
		recordSource = RecordSource.DEFAULT.getRecordSource();
	}

	public void setIsNew(boolean isNew) {
		this.isNew = isNew;
	}

	public boolean getIsNew() {
		return isNew;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public ResourceType getResourceType() {
		return resourceType;
	}

	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}

	public void setResourceTypeByString(String resourceTypeName) {
		this.resourceType = new ResourceType();
		this.resourceType.setName(resourceTypeName);
	}

	public License getLicense() {
		return license;
	}

	public void setLicense(License license) {
		this.license = license;
	}

	public void setLicense(String licenseName) {
		this.license = new License();
		this.license.setName(licenseName);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public byte[] getFileData() {
		return fileData;
	}

	public String getFolder() {
		if ((folder == null || folder.length() < 10) && getContentId() != null) {
			folder = buildResourceFolder(getContentId());
		}
		return folder;
	}

	public static final String buildResourceFolder(Long contentId) {

		String prefix = "f00000000000";

		String contentFolder = prefix.substring(0, 12 - String.valueOf(contentId).length()) + contentId;
		contentFolder = contentFolder.substring(0, 4) + "/" + contentFolder.substring(4, 8) + "/" + contentFolder.substring(8, 12);

		return contentFolder + "/";
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public Long getViews() {
		return views;
	}

	public void setViews(Long views) {
		if (views == null) {
			views = 0L;
		} else {
			this.views = views;
		}
	}

	public Set<Segment> getResourceSegments() {
		return resourceSegments;
	}

	public void setResourceSegments(Set<Segment> resourceSegments) {
		this.resourceSegments = resourceSegments;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public Boolean getHasFrameBreaker() {
		return hasFrameBreaker;
	}

	public void setHasFrameBreaker(Boolean hasFrameBreaker) {
		this.hasFrameBreaker = hasFrameBreaker;
	}

	public Integer getBrokenStatus() {
		return brokenStatus;
	}

	public void setBrokenStatus(Integer brokenStatus) {
		this.brokenStatus = brokenStatus;
	}

	public String getAssetURI() {
		if (getOrganization() != null) {
			if (getS3UploadFlag() != null && getS3UploadFlag() == 1 && getOrganization().getS3StorageArea() != null) {
				assetURI = getOrganization().getS3StorageArea().getS3Path();
			} else if ((getS3UploadFlag() == null || getS3UploadFlag() == 0) && getOrganization().getNfsStorageArea() != null) {
				if (getOrganization().getNfsStorageArea().getCdnDirectPath() != null) {
					assetURI = getOrganization().getNfsStorageArea().getCdnDirectPath().split(",")[0];
				} else {
					assetURI = getOrganization().getNfsStorageArea().getAreaPath();
				}
			} else if (getOrganization().getS3StorageArea() != null) {
				if (getOrganization().getS3StorageArea().getCdnDirectPath() != null) {
					assetURI = getOrganization().getS3StorageArea().getCdnDirectPath().split(",")[0];
				} else {
					assetURI = getOrganization().getS3StorageArea().getAreaPath();
				}
			}
		}

		if (UserGroupSupport.getUserOrganizationCdnDirectPath() != null && (getS3UploadFlag() == null || getS3UploadFlag() == 0)) {
			assetURI = UserGroupSupport.getUserOrganizationCdnDirectPath();
		}
		assetURI = BaseUtil.changeHttpsProtocol(assetURI);
		return assetURI;
	}

	public String retrieveXml() {
		return "<resource id='" + this.getGooruOid() + "'></resource>";
	}

	public Integer getNumberOfSubcribers() {
		return numberOfSubcribers;
	}

	public void setNumberOfSubcribers(Integer numberOfSubcribers) {
		this.numberOfSubcribers = numberOfSubcribers;
	}

	public Integer getIsFeatured() {
		if (isFeatured != null && isFeatured > 1) {
			isFeatured = 1;
		}
		return isFeatured;
	}

	public boolean getIsFeaturedBoolean() {
		if (isFeatured == null || isFeatured == 0) {
			return false;
		}
		return true;
	}

	public void setIsFeatured(Integer isFeatured) {
		if (isFeatured == null) {
			isFeatured = 0;
		} else if (isFeatured > 1) {
			isFeatured = 1;
		}
		this.isFeatured = isFeatured;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getFileHash() {
		return fileHash;
	}

	public void setFileHash(String fileHash) {
		this.fileHash = fileHash;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ResourceSource getResourceSource() {
		return resourceSource;
	}

	public void setResourceSource(ResourceSource resourceSource) {
		this.resourceSource = resourceSource;
	}

	public String getParentUrl() {
		return parentUrl;
	}

	public void setParentUrl(String parentUrl) {
		this.parentUrl = parentUrl;
	}

	public List<Learnguide> getResourceLearnguides() {
		return resourceLearnguides;
	}

	public void setResourceLearnguides(List<Learnguide> resourceLearnguides) {
		this.resourceLearnguides = resourceLearnguides;
	}

	public List<Code> getCodes() {
		return codes;
	}

	public void setCodes(List<Code> codes) {
		this.codes = codes;
	}

	public String getLessonsString() {
		return lessonsString;
	}

	public String getVocaularyString() {
		return vocaularyString;
	}

	public void setLessonsString(String lessonsString) {
		this.lessonsString = lessonsString;
	}

	public void setVocaularyString(String vocaularyString) {
		this.vocaularyString = vocaularyString;
	}

	public void setSourceReference(String sourceReference) {
		this.sourceReference = sourceReference;
	}

	public String getSourceReference() {
		return sourceReference;
	}

	public Short getDistinguish() {
		return distinguish;
	}

	public void setDistinguish(Short distinguish) {
		if (distinguish == null) {
			distinguish = 0;
		}
		this.distinguish = distinguish;
	}

	public String getRecordSource() {
		return recordSource;
	}

	public void setRecordSource(String recordSource) {
		this.recordSource = recordSource;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	@JSON(include = false)
	public Set<ResourceMetaData> getResourceMetaData() {
		return resourceMetaData;
	}

	public void setResourceMetaData(Set<ResourceMetaData> resourceMetaData) {
		this.resourceMetaData = resourceMetaData;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public void setAssetURI(String assetURI) {
		this.assetURI = assetURI;
	}

	public ResourceInfo getResourceInfo() {
		return resourceInfo;
	}

	public void setResourceInfo(ResourceInfo resourceInfo) {
		this.resourceInfo = resourceInfo;
	}

	public void setSocial(String social) {
		this.social = social;
	}

	public String getSocial() {
		return social;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public Date getAddDate() {
		return addDate;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}

	public void setViewCount(Integer viewCount) {
		this.viewCount = viewCount;
	}

	public Integer getViewCount() {
		return viewCount;
	}

	public void setSubscriptionCount(Integer subscriptionCount) {
		this.subscriptionCount = subscriptionCount;
	}

	public Integer getSubscriptionCount() {
		return subscriptionCount;
	}

	public Thumbnail getThumbnails() {
		return new Thumbnail(this.getResourceType(), getUrl(), getThumbnail(), getAssetURI(), getFolder());
	}

	public void setCustomFields(Map<String, String> customFields) {
		this.customFields = customFields;
	}

	public Map<String, String> getCustomFields() {
		return customFields;
	}

	public Map<String, String> getCustomFieldValues() {
		return customFieldValues;
	}

	public void setCustomFieldValues(Map<String, String> customFieldValues) {
		this.customFieldValues = customFieldValues;
	}

	public List<ResourceInstance> getResourceInstances() {
		return resourceInstances;
	}

	public void setResourceInstances(List<ResourceInstance> resourceInstances) {
		this.resourceInstances = resourceInstances;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public Integer getCollaboratorCount() {
		return collaboratorCount;
	}

	public void setCollaboratorCount(Integer collaboratorCount) {
		this.collaboratorCount = collaboratorCount;
	}

	public String getAverageTime() {
		return averageTime;
	}

	public void setAverageTime(String averageTime) {
		this.averageTime = averageTime;
	}

	public void setTaxonomyDataSet(String taxonomyDataSet) {
		this.taxonomyDataSet = taxonomyDataSet;
	}

	public String getTaxonomyDataSet() {
		return taxonomyDataSet;
	}

	public void setDurationInSec(String durationInSec) {
		this.durationInSec = durationInSec;
	}

	public String getDurationInSec() {
		return durationInSec;
	}

	public void setNumOfPages(String numOfPages) {
		this.numOfPages = numOfPages;
	}

	public String getNumOfPages() {
		return numOfPages;
	}

	public void setVotesUp(Integer votesUp) {
		this.votesUp = votesUp;
	}

	public Integer getVotesUp() {
		return votesUp;
	}

	public void setVotesDown(Integer votesDown) {
		this.votesDown = votesDown;
	}

	public Integer getVotesDown() {
		return votesDown;
	}

	public void setVocabulary(String vocabulary) {
		this.vocabulary = vocabulary;
	}

	public String getVocabulary() {
		return vocabulary;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getGrade() {
		return grade;
	}

	public void setCollectionCount(Integer collectionCount) {
		this.collectionCount = collectionCount;
	}

	public Integer getCollectionCount() {
		return collectionCount;
	}

	public void setScollectionCount(Integer scollectionCount) {
		this.scollectionCount = scollectionCount;
	}

	public Integer getScollectionCount() {
		return scollectionCount;
	}

	public void setUserRating(Integer userRating) {
		this.userRating = userRating;
	}

	public Integer getUserRating() {
		return userRating;
	}

	public String getResultUId() {
		return resultUId;
	}

	public void setResultUId(String resultUId) {
		this.resultUId = resultUId;
	}

	public Map<Integer, List<Code>> getTaxonomyMapByCode() {
		return taxonomyMapByCode;
	}

	public void setTaxonomyMapByCode(Map<Integer, List<Code>> taxonomyMapByCode) {
		this.taxonomyMapByCode = taxonomyMapByCode;
	}

	public void setCopiedResourceId(String copiedResourceId) {
		this.copiedResourceId = copiedResourceId;
	}

	public String getCopiedResourceId() {
		return copiedResourceId;
	}

	public void setHasAdvertisement(Boolean hasAdvertisement) {
		this.hasAdvertisement = hasAdvertisement;
	}

	public Boolean getHasAdvertisement() {
		return hasAdvertisement;
	}

	@Override
	public String getIndexType() {
		return INDEX_TYPE;
	}

	public void setTrackActivity(TrackActivity trackActivity) {
		this.trackActivity = trackActivity;
	}

	public TrackActivity getTrackActivity() {
		return trackActivity;
	}

	public String getGoals() {
		return goals;
	}

	public void setGoals(String goals) {
		this.goals = goals;
	}

	public void setScollectionTitles(String scollectionTitles) {
		this.scollectionTitles = scollectionTitles;
	}

	public String getScollectionTitles() {
		return scollectionTitles;
	}

	public void setScollectionIds(String scollectionIds) {
		this.scollectionIds = scollectionIds;
	}

	public String getScollectionIds() {
		return scollectionIds;
	}

	public void setIsOer(Integer isOer) {
		this.isOer = isOer;
	}

	public Integer getIsOer() {
		return isOer;
	}

	public void setAttach(AttachDTO attach) {
		this.attach = attach;
	}

	public AttachDTO getAttach() {
		return attach;
	}

	public void setInstructional(CustomTableValue instructional) {
		this.instructional = instructional;
	}

	public CustomTableValue getInstructional() {
		return instructional;
	}

	public void setResourceFormat(CustomTableValue resourceFormat) {
		this.resourceFormat = resourceFormat;
	}

	public CustomTableValue getResourceFormat() {
		return resourceFormat;
	}

	public Map<String, Object> getRatings() {
		return ratings;
	}

	public void setRatings(Map<String, Object> ratings) {
		this.ratings = ratings;
	}

	public void setMomentsOfLearning(List<ContentMetaDTO> momentsOfLearning) {
		this.momentsOfLearning = momentsOfLearning;
	}

	public List<ContentMetaDTO> getMomentsOfLearning() {
		return momentsOfLearning;
	}

	public void setEducationalUse(List<ContentMetaDTO> educationalUse) {
		this.educationalUse = educationalUse;
	}

	public List<ContentMetaDTO> getEducationalUse() {
		return educationalUse;
	}

	public void setDepthOfKnowledges(List<ContentMetaDTO> depthOfKnowledges) {
		this.depthOfKnowledges = depthOfKnowledges;
	}

	public List<ContentMetaDTO> getDepthOfKnowledges() {
		return depthOfKnowledges;
	}

	public void setMetaInfo(ResourceMetaInfo metaInfo) {
		this.metaInfo = metaInfo;
	}

	public ResourceMetaInfo getMetaInfo() {
		return metaInfo;
	}

	public List<String> getPublisher() {
		return publisher;
	}

	public void setPublisher(List<String> publisher) {
		this.publisher = publisher;
	}

	public List<String> getAggregator() {
		return aggregator;
	}

	public void setAggregator(List<String> aggregator) {
		this.aggregator = aggregator;
	}

	public void setHost(List<String> host) {
		this.host = host;
	}

	public List<String> getHost() {
		return host;
	}

	public void setResourceTags(List<Map<String, Object>> resourceTags) {
		this.resourceTags = resourceTags;
	}

	public List<Map<String, Object>> getResourceTags() {
		return resourceTags;
	}

	public String getClusterUid() {
		return clusterUid;
	}

	public void setClusterUid(String clusterUid) {
		this.clusterUid = clusterUid;
	}

	public Integer getIsRepresentative() {
		return isRepresentative;
	}

	public void setIsRepresentative(Integer isRepresentative) {
		this.isRepresentative = isRepresentative;
	}

	public Integer getS3UploadFlag() {
		return s3UploadFlag;
	}

	public void setS3UploadFlag(Integer s3UploadFlag) {
		this.s3UploadFlag = s3UploadFlag;
	}

	public void setLibraryNames(List<String> libraryNames) {
		this.libraryNames = libraryNames;
	}

	public List<String> getLibraryNames() {
		return libraryNames;
	}

	public void setAverageTimeSpent(Integer averageTimeSpent) {
		this.averageTimeSpent = averageTimeSpent;
	}

	public Integer getAverageTimeSpent() {
		return averageTimeSpent;
	}
}
