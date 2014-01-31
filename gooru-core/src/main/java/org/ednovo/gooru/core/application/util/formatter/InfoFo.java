package org.ednovo.gooru.core.application.util.formatter;

import org.ednovo.gooru.core.api.model.Learnguide;

public class InfoFo {

	private String lesson;
	private String duration;
	private String vocabulary;
	private String lessonobjectives;
	private String curriculum;
	private String instruction;
	private String notes;
	private String grade;

	public InfoFo(Learnguide learnguide) {
		setLesson(learnguide.getLesson());
		String duration = learnguide.getDuration();
		duration = (duration == null || duration.trim().equals("")) ? "0" : duration;
		setDuration(learnguide.getDuration());
		setVocabulary(learnguide.getVocabulary());
		setLessonobjectives(learnguide.getGoals());
		setCurriculum(learnguide.getCurriculum());
		setGrade(learnguide.getGrade());
		setInstruction(learnguide.getMedium());
		setNotes(learnguide.getNotes());
	}

	public String getLesson() {
		return lesson;
	}

	public void setLesson(String lesson) {
		this.lesson = lesson;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getVocabulary() {
		return vocabulary;
	}

	public void setVocabulary(String vocabulary) {
		this.vocabulary = vocabulary;
	}

	public String getLessonobjectives() {
		return lessonobjectives;
	}

	public void setLessonobjectives(String lessonobjectives) {
		this.lessonobjectives = lessonobjectives;
	}

	public String getCurriculum() {
		return curriculum;
	}

	public void setCurriculum(String curriculum) {
		this.curriculum = curriculum;
	}

	public String getInstruction() {
		return instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

}
