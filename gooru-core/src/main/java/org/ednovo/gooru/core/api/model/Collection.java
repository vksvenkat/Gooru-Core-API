package org.ednovo.gooru.core.api.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ednovo.gooru.core.application.util.ResourceMetaInfo;

public class Collection extends Content implements Versionable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3271310636333972691L;

	private static final String INDEX_TYPE = "scollection";

	private String collectionType;

	private String narrationLink;

	private String notes;

	private String keyPoints;

	private String language;

	private String goals;

	private String estimatedTime;

	private Set<CollectionItem> collectionItems;

	private List<User> collaborators;

	private ContentAssociation contentAssociation;

	private Map<Integer, List<Map<String, Object>>> taxonomySetMapping;

	private String network;

	private CollectionItem collectionItem;

	private Short buildType;

	private Short publishStatus;

	private Boolean mailNotification;

	private Map<String, Object> lastModifiedUser;

	private String ideas;

	private String questions;

	private String performanceTasks;

	private String languageObjective;

	private List<ContentMetaDTO> audience;

	private List<ContentMetaDTO> learningSkills;

	private List<ContentMetaDTO> instructionalMethod;

	private Integer itemCount;

	private String collectionItemId;

	private String clusterUid;

	private Integer isRepresentative;

	private String title;

	private String description;

	private String grade;

	private String copiedCollectionId;

	private List<ContentMetaDTO> depthOfKnowledges;

	private String url;

	private ResourceMetaInfo metaInfo;

	private String imagePath;

	private String lastAccessedTime;

	private static final String URL = "url";

	private String uri;


	public String getIdeas() {
		return ideas;
	}

	public void setIdeas(String ideas) {
		this.ideas = ideas;
	}

	public String getQuestions() {
		return questions;
	}

	public void setQuestions(String questions) {
		this.questions = questions;
	}

	public String getPerformanceTasks() {
		return performanceTasks;
	}

	public void setPerformanceTasks(String performanceTasks) {
		this.performanceTasks = performanceTasks;
	}

	public String getLanguageObjective() {
		return languageObjective;
	}

	public void setLanguageObjective(String languageObjective) {
		this.languageObjective = languageObjective;
	}

	public Map<String, Object> getLastModifiedUser() {
		return lastModifiedUser;
	}

	public void setLastModifiedUser(Map<String, Object> lastModifiedUser) {
		this.lastModifiedUser = lastModifiedUser;
	}

	public Collection() {
		super();
	}

	public String getCollectionType() {
		return collectionType;
	}

	public void setCollectionType(String collectionType) {
		this.collectionType = collectionType;
	}

	public String getNarrationLink() {
		return narrationLink;
	}

	public void setNarrationLink(String narrationLink) {
		this.narrationLink = narrationLink;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getKeyPoints() {
		return keyPoints;
	}

	public void setKeyPoints(String keyPoints) {
		this.keyPoints = keyPoints;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getGoals() {
		return goals;
	}

	public void setGoals(String goals) {
		this.goals = goals;
	}

	public String getEstimatedTime() {
		return estimatedTime;
	}

	public void setEstimatedTime(String estimatedTime) {
		this.estimatedTime = estimatedTime;
	}

	public Set<CollectionItem> getCollectionItems() {
		return collectionItems;
	}

	public void setCollectionItems(Set<CollectionItem> collectionItems) {
		this.collectionItems = collectionItems;
	}

	public void setCollaborators(List<User> collaborators) {
		this.collaborators = collaborators;
	}

	public List<User> getCollaborators() {
		return collaborators;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public ContentAssociation getContentAssociation() {
		return contentAssociation;
	}

	public void setContentAssociation(ContentAssociation contentAssociation) {
		this.contentAssociation = contentAssociation;
	}

	public void setTaxonomySetMapping(Map<Integer, List<Map<String, Object>>> taxonomySetMapping) {
		this.taxonomySetMapping = taxonomySetMapping;
	}

	public Map<Integer, List<Map<String, Object>>> getTaxonomySetMapping() {
		return taxonomySetMapping;
	}

	@Override
	public String getEntityId() {
		return getGooruOid();
	}

	@Override
	public String getIndexType() {
		return INDEX_TYPE;
	}

	public void setCollectionItem(CollectionItem collectionItem) {
		this.collectionItem = collectionItem;
	}

	public CollectionItem getCollectionItem() {
		return collectionItem;
	}

	public Boolean getMailNotification() {
		return mailNotification;
	}

	public void setMailNotification(Boolean mailNotification) {
		if (mailNotification == null) {
			mailNotification = true;
		}
		this.mailNotification = mailNotification;
	}

	public void setAudience(List<ContentMetaDTO> audience) {
		this.audience = audience;
	}

	public List<ContentMetaDTO> getAudience() {
		return audience;
	}

	public void setLearningSkills(List<ContentMetaDTO> learningSkills) {
		this.learningSkills = learningSkills;
	}

	public List<ContentMetaDTO> getLearningSkills() {
		return learningSkills;
	}

	public void setInstructionalMethod(List<ContentMetaDTO> instructionalMethod) {
		this.instructionalMethod = instructionalMethod;
	}

	public List<ContentMetaDTO> getInstructionalMethod() {
		return instructionalMethod;
	}

	public Integer getItemCount() {
		return itemCount;
	}

	public void setItemCount(Integer itemCount) {
		this.itemCount = itemCount;
	}

	public String getCollectionItemId() {
		return collectionItemId;
	}

	public void setCollectionItemId(String collectionItemId) {
		this.collectionItemId = collectionItemId;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getCopiedCollectionId() {
		return copiedCollectionId;
	}

	public void setCopiedCollectionId(String copiedCollectionId) {
		this.copiedCollectionId = copiedCollectionId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ContentMetaDTO> getDepthOfKnowledges() {
		return depthOfKnowledges;
	}

	public void setDepthOfKnowledges(List<ContentMetaDTO> depthOfKnowledges) {
		this.depthOfKnowledges = depthOfKnowledges;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public ResourceMetaInfo getMetaInfo() {
		return metaInfo;
	}

	public void setMetaInfo(ResourceMetaInfo metaInfo) {
		this.metaInfo = metaInfo;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getLastAccessedTime() {
		return lastAccessedTime;
	}

	public void setLastAccessedTime(String lastAccessedTime) {
		this.lastAccessedTime = lastAccessedTime;
	}

	public Map<String, String> getThumbnails() {
		Map<String, String> thumbnails = null;
		if (getImagePath() != null) {
			thumbnails = new HashMap<String, String>();
			StringBuilder url = new StringBuilder(getOrganization().getNfsStorageArea().getCdnDirectPath());
			url.append(getImagePath());
			thumbnails.put(URL, url.toString());
		}
		return thumbnails;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Short getBuildType() {
		return buildType;
	}

	public void setBuildType(Short buildType) {
		this.buildType = buildType;
	}

	public Short getPublishStatus() {
		return publishStatus;
	}

	public void setPublishStatus(Short publishStatus) {
		this.publishStatus = publishStatus;
	}

	public String getType() {
		return getCollectionType();
	}

}
