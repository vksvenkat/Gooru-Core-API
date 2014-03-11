/*******************************************************************************
 * Options.java
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
