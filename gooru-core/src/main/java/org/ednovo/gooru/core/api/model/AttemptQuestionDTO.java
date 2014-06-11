package org.ednovo.gooru.core.api.model;

import java.util.List;

public class AttemptQuestionDTO {

	private String questionText;

	private String answer;

	private Integer type;

	private String correctAnswer;

	private Integer isCorrect;

	private String questionStatus;

	private String concept;

	private String assetURI;

	private String folder;

	private String correctlyAnsweredPercentage;

	private List<AssessmentAnswer> answers;

	private AssessmentQuestionAssetAssoc asset;

	private String explanation;
	
	private String gooruOid;
	
	private List<AssessmentAttemptTry> assessmentAttemptsTry;
	
	private Integer correctTrySequence;

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public String getQuestionStatus() {
		return questionStatus;
	}

	public void setQuestionStatus(String questionStatus) {
		if (questionStatus.equals("1")) {
			questionStatus = "Correct";
		} else if (questionStatus.equals("2")) {
			questionStatus = "Incorrect";
		} else if (questionStatus.equals("0")) {
			questionStatus = "Not Attempted";
		}
		this.questionStatus = questionStatus;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getCorrectAnswer() {
		return correctAnswer;
	}

	public void setCorrectAnswer(String correctAnswer) {
		this.correctAnswer = correctAnswer;
	}

	public int getIsCorrect() {
		return isCorrect;
	}

	public void setIsCorrect(Integer isCorrect) {
		if (isCorrect == null) {
			isCorrect = 0;
		}
		this.isCorrect = isCorrect;
	}

	public String getConcept() {
		return concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}

	public String getCorrectlyAnsweredPercentage() {
		return correctlyAnsweredPercentage;
	}

	public void setCorrectlyAnsweredPercentage(String correctlyAnsweredPercentage) {
		this.correctlyAnsweredPercentage = correctlyAnsweredPercentage;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setAnswers(List<AssessmentAnswer> answers) {
		this.answers = answers;
	}

	public List<AssessmentAnswer> getAnswers() {
		return answers;
	}

	public void setAsset(AssessmentQuestionAssetAssoc asset) {
		this.asset = asset;
	}

	public AssessmentQuestionAssetAssoc getAsset() {
		return asset;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
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

	public void setGooruOid(String gooruOid) {
		this.gooruOid = gooruOid;
	}

	public String getGooruOid() {
		return gooruOid;
	}

	public List<AssessmentAttemptTry> getAssessmentAttemptsTry() {
		return assessmentAttemptsTry;
	}

	public void setAssessmentAttemptsTry(List<AssessmentAttemptTry> assessmentAttemptsTry) {
		this.assessmentAttemptsTry = assessmentAttemptsTry;
	}

	public Integer getCorrectTrySequence() {
		return correctTrySequence;
	}

	public void setCorrectTrySequence(Integer correctTrySequence) {
		this.correctTrySequence = correctTrySequence;
	}

}
