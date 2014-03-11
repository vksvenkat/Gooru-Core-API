/*******************************************************************************
 * ResourceType.java
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

import javax.persistence.Entity;

@Entity(name="resourceType")
public class ResourceType implements Serializable{
	

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3764335602004806426L;
	private String name;
	private String description;
	
	public static enum Type{
		PRESENTATION("ppt/pptx")
		, VIDEO("video/youtube")
		, QUIZ("question")
		, ANIMATION_SWF("animation/swf")
		, ANIMATION_KMZ("animation/kmz")
		, IMAGE("image/png")
		, RESOURCE("resource/url")
		, HANDOUTS("handouts")
		, CLASSPLAN("gooru/classplan")
		, TEXTBOOK("textbook/scribd")
		, STUDYSHELF("gooru/studyshelf")
		, EXAM("exam/pdf")
		, CLASSBOOK("gooru/classbook")
		, NOTEBOOK("gooru/notebook")
		, QB_QUESTION("qb/question")
		, QB_RESPONSE("qb/response")
		, ASSESSMENT_QUIZ("assessment-quiz")
		, ASSESSMENT_EXAM("assessment-exam")
		, ASSESSMENT_QUESTION("assessment-question")
		,SCOLLECTION("scollection")
		,FOLDER("folder")
		,ASSIGNMENT("assignment")
		,CLASSPAGE("classpage")
		, ALL("all")
		,Quiz("quiz")
		, DOCUMENTS("documents")
		, AUDIO("audio")
		, READINGS("readings")
		, MAPS("maps")
		, CASES("cases")
		;
		
		
		private String type;
		
		Type(String type){
			this.type = type;
		}
		
		public String getType() {
			return this.type;
		}
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

}
