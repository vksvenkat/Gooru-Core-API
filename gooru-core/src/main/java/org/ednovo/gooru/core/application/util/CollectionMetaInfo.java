/*******************************************************************************
 * CollectionMetaInfo.java
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
package org.ednovo.gooru.core.application.util;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.ednovo.gooru.core.api.model.Rating;
import org.ednovo.gooru.core.api.model.StandardFo;

public class CollectionMetaInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2670811444584590511L;
	
	private List<String> vocabulary;
	private Set<String> course;
	private List<StandardFo> standards;
	private Rating rating;
	private Set<String> acknowledgement;
	
	/**
	 * 
	 */
	public CollectionMetaInfo() {
	}

	public List<String> getVocabulary() {
		return vocabulary;
	}

	public void setVocabulary(List<String> vocabulary) {
		this.vocabulary = vocabulary;
	}

	public Set<String> getCourse() {
		return course;
	}

	public void setCourse(Set<String> course) {
		this.course = course;
	}

	public void setRating(Rating rating) {
		this.rating = rating;
	}

	public Rating getRating() {
		return rating;
	}

	public void setStandards(List<StandardFo> standards) {
		this.standards = standards;
	}

	public List<StandardFo> getStandards() {
		return standards;
	}

	public void setAcknowledgement(Set<String> acknowledgement) {
		this.acknowledgement = acknowledgement;
	}

	public Set<String> getAcknowledgement() {
		return acknowledgement;
	}
}
