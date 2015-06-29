package org.ednovo.gooru.core.api.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Transient;

import org.ednovo.gooru.core.application.util.BaseUtil;

public class AssessmentQuestion extends Resource {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5780952748437855414L;

	private static final String INDEX_TYPE = "question";

	public static enum TYPE {
		MULTIPLE_CHOICE("MC", "1"), SHORT_ANSWER("SA", "2"), 
		TRUE_OR_FALSE("T/F", "3"), FILL_IN_BLANKS("FIB", "4"), 
		MATCH_THE_FOLLOWING("MTF", "5"), OPEN_ENDED("OE", "6"), 
		MULTIPLE_ANSWERS("MA", "7"), HOT_TEXT_HL("HT_HL", "8"),
		HOT_TEXT_RO("HT_RO", "9"), HOT_SPOT_TEXT("HS_TXT", "10"),
		HOT_SPOT_IMAGES("HS_IMG", "11");

		private String name;
		private String id;

		TYPE(String name, String id) {
			this.name = name;
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public String getId() {
			return id;
		}
	}

	private String importCode;

	private String label;

	private String type;

	private String typeName;

	private Integer difficultyLevel;

	private String concept;

	private String questionText;

	private String helpContentLink;

	private String sourceContentInfo;

	private Integer scorePoints;

	private String instruction;

	private Integer timeToCompleteInSecs;

	private String explanation;

	private String description;

	private String source;

	private Set<AssessmentAnswer> answers;

	private Set<AssessmentQuestionAssetAssoc> assets;

	private Set<AssessmentHint> hints;

	private String assessmentCode;

	private Map<Integer, List<Code>> taxonomyMapByCode;

	private String groupedQuizNames;

	private String groupedQuizIds;

	private Integer userVote;

	private Integer voteDown;

	private Integer voteUp;

	private Double score;

	private String assessmentGooruId;

    private String quizNetwork;

    /*
     * These are the media files which are for answers only. They do not cater
     * to assets for questions and they are not stored in my sql. Hence they
     * are transient
     * This transient value signifies the media files which are added in
     * current operation
     */
    @Transient
	private List<String> mediaFiles;

    /*
     * These are the media files which are for answers only. They do not cater
     * to assets for questions and they are not stored in my sql. Hence they
     * are transient
     * This transient value signifies the media files which are to be deleted
     * in current operation
     */
    @Transient
    private List<String> deletedMediaFiles;

	public AssessmentQuestion() {
	}

	public String getInstruction() {
		return instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		/*
		 * TODO:
		 * AM: In this block, if the type is invalid, we 
		 * are just proceeding and not throwing back any
		 * thing. Not sure if I should change this to throw
		 * as some code may be working on this assumption.
		 * Should revisit later to verify and fix.
		 */
		if (type != null) {
			this.type = type;
			if (type.equalsIgnoreCase(TYPE.MULTIPLE_CHOICE.getId())) {
				typeName = TYPE.MULTIPLE_CHOICE.getName();
			} else if (type.equalsIgnoreCase(TYPE.SHORT_ANSWER.getId())) {
				typeName = TYPE.SHORT_ANSWER.getName();
			} else if (type.equalsIgnoreCase(TYPE.TRUE_OR_FALSE.getId())) {
				typeName = TYPE.TRUE_OR_FALSE.getName();
			} else if (type.equalsIgnoreCase(TYPE.FILL_IN_BLANKS.getId())) {
				typeName = TYPE.FILL_IN_BLANKS.getName();
			} else if (type.equalsIgnoreCase(TYPE.MATCH_THE_FOLLOWING.getId())) {
				typeName = TYPE.MATCH_THE_FOLLOWING.getName();
			} else if (type.equalsIgnoreCase(TYPE.OPEN_ENDED.getId())) {
				typeName = TYPE.OPEN_ENDED.getName();
			} else if (type.equalsIgnoreCase(TYPE.MULTIPLE_ANSWERS.getId())) {
				typeName = TYPE.MULTIPLE_ANSWERS.getName();
			} else if (type.equalsIgnoreCase(TYPE.HOT_TEXT_HL.getId())) {
				typeName = TYPE.HOT_TEXT_HL.getName();
			} else if (type.equalsIgnoreCase(TYPE.HOT_TEXT_RO.getId())) {
				typeName = TYPE.HOT_TEXT_RO.getName();
			} else if (type.equalsIgnoreCase(TYPE.HOT_SPOT_TEXT.getId())) {
				typeName = TYPE.HOT_SPOT_TEXT.getName();
			} else if (type.equalsIgnoreCase(TYPE.HOT_SPOT_IMAGES.getId())) {
				typeName = TYPE.HOT_SPOT_IMAGES.getName();
			}
		} else if (typeName == null) {
			this.type = type;
		}
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public String getHelpContentLink() {
		return helpContentLink;
	}

	public void setHelpContentLink(String helpContentLink) {
		this.helpContentLink = helpContentLink;
	}

	public String getSourceContentInfo() {
		return sourceContentInfo;
	}

	public void setSourceContentInfo(String sourceContentInfo) {
		this.sourceContentInfo = sourceContentInfo;
	}

	public Integer getScorePoints() {
		return scorePoints;
	}

	public void setScorePoints(Integer scorePoints) {
		this.scorePoints = scorePoints;
	}

	public Integer getTimeToCompleteInSecs() {
		return timeToCompleteInSecs;
	}

	public void setTimeToCompleteInSecs(Integer timeToCompleteInSecs) {
		this.timeToCompleteInSecs = timeToCompleteInSecs;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public Set<AssessmentAnswer> getAnswers() {
		return answers;
	}

	public void setAnswers(Set<AssessmentAnswer> answers) {
		this.answers = answers;
	}

	public Set<AssessmentHint> getHints() {
		return hints;
	}

	public void setHints(Set<AssessmentHint> hints) {
		this.hints = hints;
	}

	public String getConcept() {
		return concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}

	public String getTypeName() {
		return typeName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getDifficultyLevel() {
		return difficultyLevel;
	}

	public Set<AssessmentQuestionAssetAssoc> getAssets() {
		return assets;
	}

	public void setAssets(Set<AssessmentQuestionAssetAssoc> assets) {
		this.assets = assets;
	}

	public void setDifficultyLevel(Integer difficultyLevel) {
		this.difficultyLevel = difficultyLevel;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
		/*
		 * TODO:
		 * AM: In this block, if the type name is invalid, we 
		 * are just proceeding and not throwing back any
		 * thing. Not sure if I should change this to throw
		 * as some code may be working on this assumption.
		 * Should revisit later to verify and fix.
		 */

		if (typeName != null) {
			if (typeName.equals(TYPE.MULTIPLE_CHOICE.getName())) {
				type = TYPE.MULTIPLE_CHOICE.getId();
			} else if (typeName.equals(TYPE.SHORT_ANSWER.getName())) {
				type = TYPE.SHORT_ANSWER.getId();
			} else if (typeName.equals(TYPE.TRUE_OR_FALSE.getName())) {
				type = TYPE.TRUE_OR_FALSE.getId();
			} else if (typeName.equals(TYPE.FILL_IN_BLANKS.getName())) {
				type = TYPE.FILL_IN_BLANKS.getId();
			} else if (typeName.equals(TYPE.MATCH_THE_FOLLOWING.getName())) {
				type = TYPE.MATCH_THE_FOLLOWING.getId();
			} else if (typeName.equals(TYPE.OPEN_ENDED.getName())) {
				type = TYPE.OPEN_ENDED.getId();
			} else if (typeName.equals(TYPE.MULTIPLE_ANSWERS.getName())) {
				type = TYPE.MULTIPLE_ANSWERS.getId();
			} else if (typeName.equals(TYPE.HOT_SPOT_IMAGES.getName())) {
				type = TYPE.HOT_SPOT_IMAGES.getId();
			} else if (typeName.equals(TYPE.HOT_SPOT_TEXT.getName())) {
				type = TYPE.HOT_SPOT_TEXT.getId();
			} else if (typeName.equals(TYPE.HOT_TEXT_HL.getName())) {
				type = TYPE.HOT_TEXT_HL.getId();
			} else if (typeName.equals(TYPE.HOT_TEXT_RO.getName())) {
				type = TYPE.HOT_TEXT_RO.getId();

			}

		}
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Integer getUserVote() {
		return userVote;
	}

	public void setUserVote(Integer userVote) {
		this.userVote = userVote;
	}

	public Integer getVoteDown() {
		return voteDown;
	}

	public void setVoteDown(Integer voteDown) {
		this.voteDown = voteDown;
	}

	public Integer getVoteUp() {
		return voteUp;
	}

	public void setVoteUp(Integer voteUp) {
		this.voteUp = voteUp;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public String getImportCode() {
		return importCode;
	}

	public void setImportCode(String importCode) {
		this.importCode = importCode;
	}

	public String getAssessmentCode() {
		return assessmentCode;
	}

	public void setAssessmentCode(String assessmentCode) {
		this.assessmentCode = assessmentCode;
	}

	public Map<Integer, List<Code>> getTaxonomyMapByCode() {
		return taxonomyMapByCode;
	}

	public void setTaxonomyMapByCode(Map<Integer, List<Code>> taxonomyMapByCode) {
		this.taxonomyMapByCode = taxonomyMapByCode;
	}

	public String getGroupedQuizNames() {
		return groupedQuizNames;
	}

	public void setGroupedQuizNames(String groupedQuizNames) {
		this.groupedQuizNames = groupedQuizNames;
	}

	public String getGroupedQuizIds() {
		return groupedQuizIds;
	}

	public void setGroupedQuizIds(String groupedQuizIds) {
		this.groupedQuizIds = groupedQuizIds;
	}

	public String getAssessmentGooruId() {
		return assessmentGooruId;
	}

	public void setAssessmentGooruId(String assessmentGooruId) {
		this.assessmentGooruId = assessmentGooruId;
	}

	@Override
	public String toString() {
		return "question_id:" + getGooruOid();
	}

	public void setQuizNetwork(String quizNetwork) {
		this.quizNetwork = quizNetwork;
	}

	@Override
	public Thumbnail getThumbnails() {
		Thumbnail questionThumbnail = super.getThumbnails();
		if (getAssets() != null) {
			if (questionThumbnail == null) {
				questionThumbnail = new Thumbnail();
			}
			for (AssessmentQuestionAssetAssoc assests : getAssets()) {
				if (assests != null
						&& assests.getAsset() != null
						&& BaseUtil.getYoutubeVideoId(assests.getAsset()
								.getName()) != null
						|| assests.getAsset().getName()
								.contains("http://www.youtube.com")) {
					questionThumbnail.setUrl("img.youtube.com/vi/"
							+ BaseUtil.getYoutubeVideoId(assests.getAsset()
									.getUrl()) + "/1.jpg");
				} else {
					questionThumbnail
							.setUrl(getAssetURI()
									+ getFolder()
									+ (assests == null
											|| assests.getAsset() == null ? null
											: assests.getAsset().getName()));
				}
				break;
			}
		}
		return questionThumbnail;
	}

	public String getQuizNetwork() {
		return quizNetwork;
	}

	@Override
	public String getIndexType() {
		return INDEX_TYPE;
	}
	
	@Transient
	public boolean isQuestionNewGen() {

		if (typeName.equalsIgnoreCase(TYPE.MATCH_THE_FOLLOWING.getName()) ||
				typeName.equalsIgnoreCase(TYPE.OPEN_ENDED.getName()) ||
				typeName.equalsIgnoreCase(TYPE.MULTIPLE_ANSWERS.getName()) ||
				typeName.equalsIgnoreCase(TYPE.MULTIPLE_CHOICE.getName()) ||
				typeName.equalsIgnoreCase(TYPE.SHORT_ANSWER.getName()) ||
				typeName.equalsIgnoreCase(TYPE.TRUE_OR_FALSE.getName()) ||
				typeName.equalsIgnoreCase(TYPE.FILL_IN_BLANKS.getName())) {
			return false;
		}
		return true;
	}

    public List<String> getMediaFiles() {
        return mediaFiles;
    }

    public void setMediaFiles(List<String> mediaFiles) {
        this.mediaFiles = mediaFiles;
    }

    public List<String> getDeletedMediaFiles() {
        return deletedMediaFiles;
    }

    public void setDeletedMediaFiles(List<String> deletedMediaFiles) {
        this.deletedMediaFiles = deletedMediaFiles;
    }


}