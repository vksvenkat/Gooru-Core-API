/*******************************************************************************
 * ResourceDTO.java
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

public class ResourceDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1963763391072122777L;

	public static final String BLANK_JSON = "{\"description\":\"\",\"folder\":\"\",\"id\":\"\",\"instruction\":\"\",\"instructorNotes\":{\"stop\":1,\"start\":1,\"instruction\":{}},\"label\":\"\",\"nativeURL\":\"\",\"totalSlides\":\"\",\"type\":\"\"}";
	
	private String id = "";
	private String type = "";
	private String folder = "";
	private String nativeURL = "";
	private String label = "";
	private String totalSlides = "";
	private String description = "";
	private String instructorNotes = "";
	private String stop = "";
	private String start = "";
	private String instruction = "";
	private byte[] fileData = null;
	private String documentid = null;
	private String documentkey = null;
	
	@Override
	public String toString()
	{
		return "resource_id:"+id;
		
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getNativeURL() {
		return nativeURL;
	}

	public void setNativeURL(String nativeURL) {
		this.nativeURL = nativeURL;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getTotalSlides() {
		return totalSlides;
	}

	public void setTotalSlides(String totalSlides) {
		this.totalSlides = totalSlides;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getInstructorNotes() {
		return instructorNotes;
	}

	public void setInstructorNotes(String instructorNotes) {
		this.instructorNotes = instructorNotes;
	}

	public String getStop() {
		return stop;
	}

	public void setStop(String stop) {
		this.stop = stop;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getInstruction() {
		return instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

	public byte[] getFileData() {
		return fileData;
	}

	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}
	
	public String getDocumentid() {
		return documentid;
	}
	public void setDocumentid(String documentid) {
		this.documentid = documentid;
	}
	public String getDocumentkey() {
		return documentkey;
	}
	public void setDocumentkey(String documentkey) {
		this.documentkey = documentkey;
	}


}
