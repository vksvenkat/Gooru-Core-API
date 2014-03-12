package org.ednovo.gooru.core.api.model;

import org.dom4j.CDATA;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class ClassplanInfo {
	
	private String lesson ="";
	private String duration = "";
	private String instruction = "";
	private String curriculum = "";
	private String code ="";
	private String notes ="";
	private String goals ="";
	private String vocabulary ="";
	private String suggestedStudy ="";
	private String homework ="";
	private String assessment ="";
	private String studentQuestions ="";
	private String folder ="";
	
	
	
	public ClassplanInfo() {
		super();
	}
	
	public ClassplanInfo(Document infoDoc) {
		super();
		//this.grade = infoDoc.selectSingleNode("//grade") == null ? "" : infoDoc.selectSingleNode("//grade").getText();
		this.lesson = infoDoc.selectSingleNode("//lesson").getText();		
		//this.subject = infoDoc.selectSingleNode("//subject") == null ? "" : infoDoc.selectSingleNode("//subject").getText();		
		//this.topic = infoDoc.selectSingleNode("//topic") == null ? "" : infoDoc.selectSingleNode("//topic").getText();		
		//this.unit = infoDoc.selectSingleNode("//unit") == null ? "" : infoDoc.selectSingleNode("//unit").getText();
		this.duration = infoDoc.selectSingleNode("//duration").getText();
		this.instruction = infoDoc.selectSingleNode("//instruction") == null ? "" : infoDoc.selectSingleNode("//instruction").getText();		
		this.curriculum = infoDoc.selectSingleNode("//curriculum") == null ? "" : infoDoc.selectSingleNode("//curriculum").getText();		
		this.code = infoDoc.selectSingleNode("//code") == null ? "" : infoDoc.selectSingleNode("//code").getText();
		this.notes = infoDoc.selectSingleNode("//notes") == null ? "" : infoDoc.selectSingleNode("//notes").getText();		
		this.goals = infoDoc.selectSingleNode("//lessonobjectives").getText();
		this.vocabulary = infoDoc.selectSingleNode("//vocabulary") == null ? "" : infoDoc.selectSingleNode("//vocabulary").getText();		
		this.suggestedStudy = infoDoc.selectSingleNode("//suggestedreading") == null ? "" : infoDoc.selectSingleNode("//suggestedreading").getText();	
		this.homework = infoDoc.selectSingleNode("//homework") == null ? "" : infoDoc.selectSingleNode("//homework").getText();		
		this.assessment = infoDoc.selectSingleNode("//assessment") == null ? "" : infoDoc.selectSingleNode("//assessment").getText();		
		this.studentQuestions = infoDoc.selectSingleNode("//studentquestions") == null ? "" : infoDoc.selectSingleNode("//studentquestions").getText();		
	}
	
	public String getHomework() {
		return homework;
	}
	public void setHomework(String homework) {
		this.homework = homework;
	}
	public String getAssessment() {
		return assessment;
	}
	public void setAssessment(String assessment) {
		this.assessment = assessment;
	}
	
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getVocabulary() {
		return vocabulary;
	}
	public void setVocabulary(String vocabulary) {
		this.vocabulary = vocabulary;
	}
	public String getSuggestedStudy() {
		return suggestedStudy;
	}
	public void setSuggestedStudy(String suggestedStudy) {
		this.suggestedStudy = suggestedStudy;
	}
	/*public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}*/
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
	public String getFolder() {
		return folder;
	}
	public void setFolder(String folder) {
		this.folder = folder;
	}
	public String getInstruction() {
		return instruction;
	}
	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCurriculum() {
		return curriculum;
	}
	public void setCurriculum(String curriculum) {
		this.curriculum = curriculum;
	}
	public String getStudentQuestions() {
		return studentQuestions;
	}
	public void setStudentQuestions(String studentQuestions) {
		this.studentQuestions = studentQuestions;
	}
	
	public String getXML(boolean escape){
		
		Element info = DocumentHelper.createElement("info");
		
		Element elmLesson = info.addElement("lesson");
		elmLesson.addText(lesson);

		Element duration = info.addElement("duration");
		duration.addText("0");

		Element elmInstruction = info.addElement("instruction");
		elmInstruction.addText(instruction);

		Element elmCurriculums = info.addElement("curriculums");
		String[] strCurriculum = curriculum.split(",");

		for (String curr : strCurriculum) {
			Element curriculumNode = elmCurriculums.addElement("curriculum");
			curriculumNode.addText(curr);
		}

		Element elmCode = info.addElement("code");
		elmCode.addText(code);

		Element elmNotes = info.addElement("notes");
		CDATA notesText = null;
		notesText= DocumentHelper.createCDATA(notes);
		elmNotes.add(notesText);

		Element lessonObjectives = info.addElement("lessonobjectives");
		CDATA lessonObjectiveText = null;
		lessonObjectiveText= DocumentHelper.createCDATA(goals.trim());
		lessonObjectives.add(lessonObjectiveText);

		Element vocab = info.addElement("vocabulary");
		CDATA vocabText = null;
		vocabText= DocumentHelper.createCDATA(vocabulary);
		vocab.add(vocabText);

		Element studentques = info.addElement("studentquestions");
		CDATA studentQuesText = null;
		studentQuesText= DocumentHelper.createCDATA(studentQuestions.trim());
		studentques.add(studentQuesText);

	
		return info.asXML();	
	}
}
