
package org.ednovo.gooru.core.cassandra.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.constant.ColumnFamilyConstant;

@Entity(name = ColumnFamilyConstant.RESOURCE)
public class ResourceCio implements IsEntityCassandraIndexable {

	private static final long serialVersionUID = 724105455979462567L;

	private static final String RESOURCE_TYPE = "resourceType";

	private static final String BATCH_ID = "batchId";

	private static final String CATEGORY = "category";

	private static final String RESOURCE_FORMAT = "resourceFormat";

	private static final String INSTRUCTIONAL = "instructional";

	@Id
	private String id;

	@Column
	private String indexType;

	@Column
	private String segmentConcepts;

	@Column
	private String title;

	@Column
	private String segmentTitles;

	@Column
	private String description;

	@Column
	private String text;

	@Column
	private String tags;

	@Column
	private Integer numOfPages;

	@Column
	private String url;

	@Column
	private Long contentId;

	@Column
	private String isFeatured;

	@Column
	private String distinguish;

	@Column
	private String siteName;

	@Column
	private String attribution;

	@Column
	private Integer resourceSourceId;

	@Column
	private String domainName;

	@Column
	private Integer activeStatus;

	@Column
	private Integer frameBreaker;

	@Column
	private String sourceType;

	@Column
	private String thumbnail;

	@Column
	private String folder;

	@Column
	private String resourceType;

	@Column
	private String category;

	@Column
	private String mediaType;

	@Column
	private String recordSource;

	@Column
	private String typeEscaped;

	@Column
	private String sharing;

	@Column
	private String resourceDomainName;

	@Column
	private String assetURI;

	@Column
	private Integer collaboratorCount;

	@Column
	private String grade;

	@Column
	private String averageTime;

	@Column
	private String taxonomyGrade;

	@Column
	private String collectionGooruOIds;

	@Column
	private String scollectionTitles;

	@Column
	private FeedCo feed;

	@Column
	private TextbookCo textbook;

	@Column
	private CollectionCo collection;

	@Column
	private String instanceJson;

	@Column
	private QuestionCo question;

	@Column
	private StatisticsCo statistics;

	@Column
	private ResourceStasCo stas;

	@Column
	private UserCo creator;

	@Column
	private UserCo owner;

	@Column
	private String batchId;

	@Column
	private Date lastModified;

	@Column
	private Date addDate;

	@Column
	private String customFieldsJson;

	@Column
	private Map<String, String> customFields;

	@Column
	private LicenseCo license;

	@Column
	private String tagSetJson;

	@Column
	private String updatedCustomFields;

	@Column
	private String taxonomyJson;

	@Column
	private QuizCo quizCo;

	@Column
	private SCollectionCo sCollectionCo;

	@Column
	private ResourceMetadataCo resourceMetadata;

	@Column
	private String segmentCount;

	@Column
	private String lastUpdatedUserUid;

	@Column
	private Date createdOn;

	@Column
	private Map<String, String> organization;

	@Column
	private String partyPermissionsJson;

	@Column
	private String versionUid;

	@Column
	private String copiedResourceId;

	@Column
	private String isOer;

	@Column
	private String scollectionItemGooruOIds;

	@Column
	private String resourceFormat;

	@Column
	private String instructional;

	@Column
	private String protocolSupported;

	@Column
	private Map<String,Object> ratings;

	@Column
	private  String audience;

	@Column
	private String depthOfknowledge;

	@Column
	private List<CustomTableValue> customTables;

	@Column
	private String learningAndInovation;

	@Column
	private String instructionalMethod;

	@Column
	private String educationalUse;

	@Column
	private String standards;

	@Column
	private String momentsofLearning;

	@Column
	private String publisher;

	@Column
	private String aggregator;

	@Column
	private String clusterUid;

	@Column
	private Integer isCanonical;

	@Column
	private String rootNodeId;

	@Column
	private String rootCodes;

	@Column
	private Map<String,String> codeAndDescription;


	public Map<String, String> getCodeAndDescription() {
		return codeAndDescription;
	}

	public void setCodeAndDescription(Map<String, String> codeAndDescription) {
		this.codeAndDescription = codeAndDescription;
	}

	public String getRootCodes() {
		return rootCodes;
	}

	public void setRootCodeId(String rootCodeId) {
		this.rootCodes = rootCodeId;
	}

	public String getRootNodeId() {
		return rootNodeId;
	}

	public void setRootNodeId(String rootNodeId) {
		this.rootNodeId = rootNodeId;
	}

	public String getMomentsofLearning() {
		return momentsofLearning;
	}

	public void setMomentsofLearning(String momentsofLearning) {
		this.momentsofLearning = momentsofLearning;
	}

	public String getEducationalUse() {
		return educationalUse;
	}

	public void setEducationalUse(String educationalUse) {
		this.educationalUse = educationalUse;
	}

	public String getStandards() {
		return standards;
	}

	public void setStandards(String standards) {
		this.standards = standards;
	}

	public String getLearningAndInovation() {
		return learningAndInovation;
	}

	public void setLearningAndInovation(String learningAndInovation) {
		this.learningAndInovation = learningAndInovation;
	}

	public String getInstructionalMethod() {
		return instructionalMethod;
	}

	public void setInstructionalMethod(String instructionalMethod) {
		this.instructionalMethod = instructionalMethod;
	}

	public String getDepthOfknowledge() {
		return depthOfknowledge;
	}

	public void setDepthOfknowledge(String depthOfknowledge) {
		this.depthOfknowledge = depthOfknowledge;
	}

	public List<CustomTableValue> getCustomTables() {
		return customTables;
	}

	public void setCustomTables(List<CustomTableValue> customTables) {
		this.customTables = customTables;
	}

	public String getSegmentConcepts() {
		return segmentConcepts;
	}

	public void setSegmentConcepts(String segmentConcepts) {
		this.segmentConcepts = segmentConcepts;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSegmentTitles() {
		return segmentTitles;
	}

	public void setSegmentTitles(String segmentTitles) {
		this.segmentTitles = segmentTitles;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public Integer getNumOfPages() {
		return numOfPages;
	}

	public void setNumOfPages(Integer numOfPages) {
		this.numOfPages = numOfPages;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getContentId() {
		return contentId;
	}

	public void setContentId(Long contentId) {
		this.contentId = contentId;
	}

	public String getIsFeatured() {
		return isFeatured;
	}

	public String getAudience() {
		return audience;
	}

	public void setAudience(String audience) {
		this.audience = audience;
	}

	public void setIsFeatured(String isFeatured) {
		this.isFeatured = isFeatured;
	}

	public String getDistinguish() {
		return distinguish;
	}

	public void setDistinguish(String distinguish) {
		this.distinguish = distinguish;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getAttribution() {
		return attribution;
	}

	public void setAttribution(String attribution) {
		this.attribution = attribution;
	}

	public Integer getResourceSourceId() {
		return resourceSourceId;
	}

	public void setResourceSourceId(Integer resourceSourceId) {
		this.resourceSourceId = resourceSourceId;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public Integer getActiveStatus() {
		return activeStatus;
	}

	public void setActiveStatus(Integer activeStatus) {
		this.activeStatus = activeStatus;
	}

	public Integer getFrameBreaker() {
		return frameBreaker;
	}

	public void setFrameBreaker(Integer frameBreaker) {
		this.frameBreaker = frameBreaker;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public String getRecordSource() {
		return recordSource;
	}

	public void setRecordSource(String recordSource) {
		this.recordSource = recordSource;
	}

	public String getTypeEscaped() {
		return typeEscaped;
	}

	public void setTypeEscaped(String typeEscaped) {
		this.typeEscaped = typeEscaped;
	}

	public String getSharing() {
		return sharing;
	}

	public void setSharing(String sharing) {
		this.sharing = sharing;
	}

	public String getResourceDomainName() {
		return resourceDomainName;
	}

	public void setResourceDomainName(String resourceDomainName) {
		this.resourceDomainName = resourceDomainName;
	}

	public String getAssetURI() {
		return assetURI;
	}

	public void setAssetURI(String assetURI) {
		this.assetURI = assetURI;
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

	public String getTaxonomyGrade() {
		return taxonomyGrade;
	}

	public void setTaxonomyGrade(String taxonomyGrade) {
		this.taxonomyGrade = taxonomyGrade;
	}

	public String getCollectionGooruOIds() {
		return collectionGooruOIds;
	}

	public void setCollectionGooruOIds(String collectionGooruOIds) {
		this.collectionGooruOIds = collectionGooruOIds;
	}

	public FeedCo getFeed() {
		return feed;
	}

	public void setFeed(FeedCo feed) {
		this.feed = feed;
	}

	public TextbookCo getTextbook() {
		return textbook;
	}

	public void setTextbook(TextbookCo textbook) {
		this.textbook = textbook;
	}

	public CollectionCo getCollection() {
		return collection;
	}

	public void setCollection(CollectionCo collection) {
		this.collection = collection;
	}

	public QuestionCo getQuestion() {
		return question;
	}

	public void setQuestion(QuestionCo question) {
		this.question = question;
	}

	public StatisticsCo getStatistics() {
		return statistics;
	}

	public void setStatistics(StatisticsCo statistics) {
		this.statistics = statistics;
	}

	public UserCo getCreator() {
		return creator;
	}

	public void setCreator(UserCo creator) {
		this.creator = creator;
	}

	public UserCo getOwner() {
		return owner;
	}

	public void setOwner(UserCo owner) {
		this.owner = owner;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public Date getAddDate() {
		return addDate;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}

	public LicenseCo getLicense() {
		return license;
	}

	public void setLicense(LicenseCo license) {
		this.license = license;
	}

	public String getUpdatedCustomFields() {
		return updatedCustomFields;
	}

	public void setUpdatedCustomFields(String updatedCustomFields) {
		this.updatedCustomFields = updatedCustomFields;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getCustomFieldsJson() {
		return customFieldsJson;
	}

	public void setCustomFieldsJson(String customFieldsJson) {
		this.customFieldsJson = customFieldsJson;
	}

	public Map<String, String> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(Map<String, String> customFields) {
		this.customFields = customFields;
	}

	public QuizCo getQuizCo() {
		return quizCo;
	}

	public void setQuizCo(QuizCo quizCo) {
		this.quizCo = quizCo;
	}

	public void setsCollectionCo(SCollectionCo sCollectionCo) {
		this.sCollectionCo = sCollectionCo;
	}

	public SCollectionCo getsCollectionCo() {
		return sCollectionCo;
	}

	public void setSegmentCount(String segmentCount) {
		this.segmentCount = segmentCount;
	}

	public String getSegmentCount() {
		return segmentCount;
	}

	public String getLastUpdatedUserUid() {
		return lastUpdatedUserUid;
	}

	public void setLastUpdatedUserUid(String lastUpdatedUserUid) {
		this.lastUpdatedUserUid = lastUpdatedUserUid;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Map<String, String> getOrganization() {
		return organization;
	}

	public void setOrganization(Map<String, String> organization) {
		this.organization = organization;
	}

	public String getTaxonomyJson() {
		return taxonomyJson;
	}

	public void setTaxonomyJson(String taxonomyJson) {
		this.taxonomyJson = taxonomyJson;
	}

	public String getInstanceJson() {
		return instanceJson;
	}

	public void setInstanceJson(String instanceJson) {
		this.instanceJson = instanceJson;
	}

	public String getPartyPermissionsJson() {
		return partyPermissionsJson;
	}

	public void setPartyPermissionsJson(String partyPermissionsJson) {
		this.partyPermissionsJson = partyPermissionsJson;
	}

	public String getTagSetJson() {
		return tagSetJson;
	}

	public void setTagSetJson(String tagSetJson) {
		this.tagSetJson = tagSetJson;
	}

	@Override
	public String getIndexId() {
		return getId();
	}

	@Override
	public String getIndexType() {
		return indexType;
	}

	public void setIndexType(String indexType) {
		this.indexType = indexType;
	}

	public void setVersionUid(String versionUid) {
		this.versionUid = versionUid;
	}

	public String getVersionUid() {
		return versionUid;
	}

	public ResourceMetadataCo getResourceMetadata() {
		return resourceMetadata;
	}

	public void setResourceMetadata(ResourceMetadataCo resourceMetadata) {
		this.resourceMetadata = resourceMetadata;
	}

	@Override
	public Map<String, String> getRiFields() {
		Map<String, String> riFields = new HashMap<String, String>(5);
		riFields.put(RESOURCE_TYPE, resourceType);
		riFields.put(BATCH_ID, batchId);
		riFields.put(CATEGORY, category);
		riFields.put(RESOURCE_FORMAT, resourceFormat);
		riFields.put(INSTRUCTIONAL, instructional);
		return riFields;
	}

	public String getScollectionTitles() {
		return scollectionTitles;
	}

	public void setScollectionTitles(String scollectionTitles) {
		this.scollectionTitles = scollectionTitles;
	}

	public String getCopiedResourceId() {
		return copiedResourceId;
	}

	public void setCopiedResourceId(String copiedResourceId) {
		this.copiedResourceId = copiedResourceId;
	}

	public String getIsOer() {
		return isOer;
	}

	public void setIsOer(String isOer) {
		this.isOer = isOer;
	}

	public String getScollectionItemGooruOIds() {
		return scollectionItemGooruOIds;
	}

	public void setScollectionItemGooruOIds(String scollectionItemGooruOIds) {
		this.scollectionItemGooruOIds = scollectionItemGooruOIds;
	}

	public String getResourceFormat() {
		return resourceFormat;
	}

	public void setResourceFormat(String resourceFormat) {
		this.resourceFormat = resourceFormat;
	}

	public String getInstructional() {
		return instructional;
	}

	public void setInstructional(String instructional) {
		this.instructional = instructional;
	}

	public String getProtocolSupported() {
		return protocolSupported;
	}

	public void setProtocolSupported(String protocolSupported) {
		this.protocolSupported = protocolSupported;
	}

	public Map<String, Object> getRatings() {
		return ratings;
	}

	public void setRatings(Map<String, Object> ratings) {
		this.ratings = ratings;
	}

	public String getPublisher(){
		return publisher;
	}

	public void setPublisher(String publisher){
		this.publisher = publisher;
	}

	public String getAggregator(){
		return aggregator;
	}

	public void setAggregator(String aggregator){
		this.aggregator = aggregator;
	}

	public void setStas(ResourceStasCo stas) {
		this.stas = stas;
	}

	public ResourceStasCo getStas() {
		return stas;
	}

	public String getClusterUid() {
		return clusterUid;
	}

	public void setClusterUid(String clusterUid) {
		this.clusterUid = clusterUid;
	}

	public Integer getIsCanonical() {
		return isCanonical;
	}

	public void setIsCanonical(Integer isCanonical) {
		this.isCanonical = isCanonical;
	}
}
