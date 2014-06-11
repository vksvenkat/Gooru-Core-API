package org.ednovo.gooru.core.api.model;


public class Question extends Resource {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8905865471147037534L;

	public static final String SHORT_ANSWER = "SA";
	public static final String MULTIPLE_CHOICE = "MC";

	private String questionType;
	private String correctOption;
	private Integer duration;
	private String optionA;
	private String optionB;
	private String optionC;
	private String optionD;
	private String optionE;
	private String optionF;
	private String questionXml;
	private String index;
	private String hashValue;

	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	public String getCorrectOption() {
		return correctOption;
	}

	public void setCorrectOption(String correctOption) {
		this.correctOption = correctOption;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public String getOptionA() {
		return optionA;
	}

	public void setOptionA(String optionA) {
		this.optionA = optionA;
	}

	public String getOptionB() {
		return optionB;
	}

	public void setOptionB(String optionB) {
		this.optionB = optionB;
	}

	public String getOptionC() {
		return optionC;
	}

	public void setOptionC(String optionC) {
		this.optionC = optionC;
	}

	public String getOptionD() {
		return optionD;
	}

	public void setOptionD(String optionD) {
		this.optionD = optionD;
	}

	public String getOptionE() {
		return optionE;
	}

	public void setOptionE(String optionE) {
		this.optionE = optionE;
	}

	public String getOptionF() {
		return optionF;
	}

	public void setOptionF(String optionF) {
		this.optionF = optionF;
	}

	public String getQuestionXml() {
		return questionXml;
	}

	public void setQuestionXml(String questionXml) {
		this.questionXml = questionXml;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getHashValue() {
		return hashValue;
	}

	public void setHashValue(String hashValue) {
		this.hashValue = hashValue;
	}

	public static Integer calculateDuration(String minutes, String sec) {
		int mins = 0;
		int secs = 0;
		if (minutes != null && !minutes.equalsIgnoreCase("")){
			mins = new Integer(minutes) * 60;
		}
		if (sec != null && !sec.equalsIgnoreCase("")){
			secs = new Integer(sec);
		}

		return mins + secs;
	}

}