package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class AssessmentQuestionAssetAssoc implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8236313515263936062L;

	/**
	 * 
	 */
	

	private Asset asset;

	//TODO  This need to be made generic for resource .
	//This is not yet implemented since resource doesn't have folder field.
	private AssessmentQuestion question;

	private String assetKey;

	public AssessmentQuestionAssetAssoc() {
		asset = new Asset();
		question = new AssessmentQuestion();
	}

	public String getAssetKey() {
		return assetKey;
	}

	public void setAssetKey(String assetKey) {
		this.assetKey = assetKey;
	}

	public Asset getAsset() {
		return asset;
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
	}

	public AssessmentQuestion getQuestion() {
		return question;
	}

	public void setQuestion(AssessmentQuestion question) {
		this.question = question;
	}

}