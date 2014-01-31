
package org.ednovo.gooru.core.cassandra.model;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name="quiz")
public class QuizCo {

	@Column
	private String vocabulary;
	
	@Column
	private String learningObjectives;
	
	@Column
	private String name;
	
	@Column
	private Map<String,String> segmentNames;
	
	@Column
	private String source;
	
	@Column
	private String collectionGooruOid;
	
	@Column
	private String quizGooruOid;
	
	@Column
	private String quizCollectionName;
	
	@Column
	private String importCode;
	

	public String getVocabulary() {
		return vocabulary;
	}

	public void setVocabulary(String vocabulary) {
		this.vocabulary = vocabulary;
	}

	public String getLearningObjectives() {
		return learningObjectives;
	}

	public void setLearningObjectives(String learningObjectives) {
		this.learningObjectives = learningObjectives;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String,String> getSegmentNames() {
		return segmentNames;
	}

	public void setSegmentNames(Map<String,String> segmentNames) {
		this.segmentNames = segmentNames;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getCollectionGooruOid() {
		return collectionGooruOid;
	}

	public void setCollectionGooruOid(String collectionGooruOid) {
		this.collectionGooruOid = collectionGooruOid;
	}

	public String getQuizGooruOid() {
		return quizGooruOid;
	}

	public void setQuizGooruOid(String quizGooruOid) {
		this.quizGooruOid = quizGooruOid;
	}

	public String getQuizCollectionName() {
		return quizCollectionName;
	}

	public void setQuizCollectionName(String quizCollectionName) {
		this.quizCollectionName = quizCollectionName;
	}

	public String getImportCode() {
		return importCode;
	}

	public void setImportCode(String importCode) {
		this.importCode = importCode;
	}
}
