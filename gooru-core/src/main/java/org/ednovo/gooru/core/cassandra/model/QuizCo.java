/*******************************************************************************
 * QuizCo.java
 *  gooru-core
 *  Created by Gooru on 2014
 *  Copyright (c) 2014 Gooru. All rights reserved.
 *  http://www.goorulearning.org/
 *       
 *  Permission is hereby granted, free of charge, to any 
 *  person obtaining a copy of this software and associated 
 *  documentation. Any one can use this software without any 
 *  restriction and can use without any limitation rights 
 *  like copy,modify,merge,publish,distribute,sub-license or 
 *  sell copies of the software.
 *  
 *  The seller can sell based on the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be   
 *  included in all copies or substantial portions of the Software. 
 * 
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY    
 *   KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE  
 *   WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR   
 *   PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE       AUTHORS 
 *   OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
 *   OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *   OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 *   WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 *   THE SOFTWARE.
 ******************************************************************************/
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
