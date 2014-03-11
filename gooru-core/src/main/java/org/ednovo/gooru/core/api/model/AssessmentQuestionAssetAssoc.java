/*******************************************************************************
 * AssessmentQuestionAssetAssoc.java
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
