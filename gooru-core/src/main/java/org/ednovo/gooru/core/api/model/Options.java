package org.ednovo.gooru.core.api.model;

import java.io.Serializable;


public class Options implements Serializable {

	private Boolean isRandomize;
	
	private Boolean isRandomizeChoice;
	
	private Boolean showHints;
	
	private Boolean showScore;
	
	private Boolean showCorrectAnswer;

	public Boolean getIsRandomize() {
		return isRandomize;
	}

	public void setIsRandomize(Boolean isRandomize) {
		this.isRandomize = isRandomize;
	}

	public Boolean getIsRandomizeChoice() {
		return isRandomizeChoice;
	}

	public void setIsRandomizeChoice(Boolean isRandomizeChoice) {
		this.isRandomizeChoice = isRandomizeChoice;
	}

	public Boolean getShowHints() {
		return showHints;
	}

	public void setShowHints(Boolean showHints) {
		this.showHints = showHints;
	}

	public Boolean getShowScore() {
		return showScore;
	}

	public void setShowScore(Boolean showScore) {
		this.showScore = showScore;
	}

	public Boolean getShowCorrectAnswer() {
		return showCorrectAnswer;
	}

	public void setShowCorrectAnswer(Boolean showCorrectAnswer) {
		this.showCorrectAnswer = showCorrectAnswer;
	}
}
