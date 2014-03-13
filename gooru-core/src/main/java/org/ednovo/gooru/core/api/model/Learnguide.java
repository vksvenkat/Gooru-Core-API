package org.ednovo.gooru.core.api.model;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFilter;


@JsonFilter("collection")
public class Learnguide extends Resource implements Versionable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7975444873140110102L;

	private static final String INDEX_TYPE = "collection";


	public static String CLASSPLAN_BASE_URI;
	public static final String MANIFEST_XML = "<gooruclassplan><info><id>NEW</id>" + "<lesson></lesson><unit></unit><duration></duration><lessonobjectives><![CDATA[]]></lessonobjectives>" + "<vocabulary><![CDATA[]]></vocabulary>"
			+ "<studentquestions><![CDATA[]]></studentquestions><notes><![CDATA[]]></notes></info>" + "</gooruclassplan>";

	private String lesson = "";
	private String goals = "";
	private String duration;
	private String medium;
	private String curriculum;
    private String narration;

	// Will be removed when migration is successful
    private List<User> collaborators;
    private String notes;
	private Map<Integer, List<Code>> taxonomyMapByCode;
	private String collectionGooruOid;
	private String assessmentGooruOid;
	private String type;
	private String collectionLink;
	private String assessmentLink;
	private String source;
	private String linkedCollectionTitle;
	private String linkedAssessmentTitle;
	private String taxonomyContentData;
	private transient String social;
	private Integer requestPending;
	private String narrationLink;
	private Integer chapterCount;
	private Integer resourceCount;

	public Integer getChapterCount() {
		return chapterCount;
	}

	public void setChapterCount(Integer chapterCount) {
		this.chapterCount = chapterCount;
	}

	public Integer getResourceCount() {
		return resourceCount;
	}

	public void setResourceCount(Integer resourceCount) {
		this.resourceCount = resourceCount;
	}


	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<Integer, List<Code>> getTaxonomyMapByCode() {
		return taxonomyMapByCode;
	}

	public void setTaxonomyMapByCode(Map<Integer, List<Code>> taxonomyMapByCode) {
		this.taxonomyMapByCode = taxonomyMapByCode;
	}

	public Learnguide() {

	}

	@Override
	public String toString() {
		return "learnguide_id:" + getGooruOid();
	}

	public String getCurriculum() {
		return curriculum;
	}

	public void setCurriculum(String curriculum) {
		this.curriculum = curriculum;
	}

	public List<User> getCollaborators() {
		return collaborators;
	}

	public void setCollaborators(List<User> collaborators) {
		this.collaborators = collaborators;
	}

	public String getLesson() {
		return lesson;
	}

	public void setLesson(String lesson) {
		this.lesson = lesson;
	}

	public String getGoals() {
		return goals;
	}

	public void setGoals(String goals) {
		this.goals = goals;
	}

	public static Set<Segment> getSegmentsSkeleton() {
		Set<Segment> segments = new TreeSet<Segment>();
		segments.add(createSegment(1, "suggestedstudy", "Suggested Study"));
		segments.add(createSegment(2, "homework", "Homework"));
		segments.add(createSegment(3, "assessment", "Lesson Plan"));
		return segments;
	}

	public static Segment createSegment(int sequence, String type, String title) {
		Segment segment = new Segment();
		segment.setTitle(title);
		segment.setSegmentId(UUID.randomUUID().toString());
		segment.setSequence(sequence);
		segment.setIsMeta(1);
		segment.setType(type);
		return segment;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getInstructionInfo() {
		return (medium == null) ? "eng" : medium;
	}

	public String getDuration() {
		return duration;
	}

	public String getMedium() {
		return medium;
	}

	public void setMedium(String medium) {
		this.medium = medium;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getNarration() {
		return narration;
	}

	public void setNarration(String narration) {
		this.narration = narration;
	}

	public String getCollectionGooruOid() {
		return collectionGooruOid;
	}

	public void setCollectionGooruOid(String collectionGooruOid) {
		this.collectionGooruOid = collectionGooruOid;
	}

	public String getAssessmentGooruOid() {
		return assessmentGooruOid;
	}

	public void setAssessmentGooruOid(String assessmentGooruOid) {
		this.assessmentGooruOid = assessmentGooruOid;
	}

	public String getCollectionLink() {
		return collectionLink;
	}

	public void setCollectionLink(String collectionLink) {
		this.collectionLink = collectionLink;
	}

	public String getAssessmentLink() {
		return assessmentLink;
	}

	public void setAssessmentLink(String assessmentLink) {
		this.assessmentLink = assessmentLink;
	}

	public String getLinkedAssessmentTitle() {
		return linkedAssessmentTitle;
	}

	public void setLinkedAssessmentTitle(String linkedAssessmentTitle) {
		this.linkedAssessmentTitle = linkedAssessmentTitle;
	}

	public String getLinkedCollectionTitle() {
		return linkedCollectionTitle;
	}

	public void setLinkedCollectionTitle(String linkedCollectionTitle) {
		this.linkedCollectionTitle = linkedCollectionTitle;
	}

	public String getTaxonomyContentData() {
		return taxonomyContentData;
	}

	public void setTaxonomyContentData(String taxonomyContentData) {
		this.taxonomyContentData = taxonomyContentData;
	}

	public String getSocialData() {
		return social;
	}

	public void setSocialData(String social) {
		this.social = social;
	}

	public void setRequestPending(Integer requestPending) {
		this.requestPending = requestPending;
	}

	public Integer getRequestPending() {
		return requestPending;
	}
	
	@Override
	public String getEntityId() {
		return getGooruOid();
	}

	public void setNarrationLink(String narrationLink) {
		this.narrationLink = narrationLink;
	}

	public String getNarrationLink() {
		return narrationLink;
	}

	@Override
	public String getIndexType() {
		return INDEX_TYPE;
	}

}
