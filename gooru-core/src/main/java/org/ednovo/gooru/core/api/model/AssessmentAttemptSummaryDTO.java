package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class AssessmentAttemptSummaryDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1630954979050974383L;

	private Integer score;

	private Integer correctAnswerCount;

	private Integer totalQuestions;

	private Integer correctAnswersPercentage;

	private List<AttemptQuestionDTO> questionData;

	private Map<String,Object> socialAttemptScore;
	
	private Map<String, Integer> conceptsScore;
	
	public Map<String, Object> getSocialAttemptScore() {
		return socialAttemptScore;
	}

	public void setSocialAttemptScore(Map<String, Object> socialAttemptScore) {
		this.socialAttemptScore = socialAttemptScore;
	}

	public List<AttemptQuestionDTO> getQuestionData() {
		return questionData;
	}

	public void setQuestionData(List<AttemptQuestionDTO> questionData) {
		this.questionData = questionData;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Integer getCorrectAnswerCount() {
		return correctAnswerCount;
	}

	public void setCorrectAnswerCount(Integer correctAnswerCount) {
		this.correctAnswerCount = correctAnswerCount;
	}

	public Integer getTotalQuestions() {
		return totalQuestions;
	}

	public void setTotalQuestions(Integer totalQuestions) {
		this.totalQuestions = totalQuestions;
	}

	public Integer getCorrectAnswersPercentage() {
		return correctAnswersPercentage;
	}

	public void setCorrectAnswersPercentage(Integer correctAnswersPercentage) {
		this.correctAnswersPercentage = correctAnswersPercentage;
	}

	public Map<String, Integer> getConceptsScore() {
		return conceptsScore;
	}

	public void setConceptsScore(Map<String, Integer> conceptsScore) {
		this.conceptsScore = conceptsScore;
	}
}
