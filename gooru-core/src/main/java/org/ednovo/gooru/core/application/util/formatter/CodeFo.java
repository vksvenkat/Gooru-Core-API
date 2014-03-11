/*******************************************************************************
 * CodeFo.java
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
package org.ednovo.gooru.core.application.util.formatter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.ednovo.gooru.core.api.model.Code.Thumbnail;
import org.ednovo.gooru.core.api.model.OrganizationModel;

public class CodeFo  extends OrganizationModel implements Serializable {
	private static final long serialVersionUID = -2962558353764012311L;
	private Integer codeId;
	private String label;
	private Integer parentId;	
	private List<CodeFo> node = new ArrayList<CodeFo>();
	private Integer grade;
	private Integer firstUnitId;
	private String displayCode;
	private Thumbnail thumbnails;
	private String assetURI;
	private String codeImage;
	private int s3UploadFlag;
	public static final String TAXONOMY_CODE_IMAGE_SIZES = "80x60,160x120";
	
	public CodeFo() {
		setThumbnails(new Thumbnail());
	}
	public Integer getCodeId() {
		return codeId;
	}
	public void setCodeId(Integer codeId) {
		this.codeId = codeId;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	public Integer getGrade() {
		return grade;
	}
	public void setGrade(Integer grade) {
		this.grade = grade;
	}
	public void setNode(List<CodeFo> node) {
		this.node = node;
	}
	public List<CodeFo> getNode() {
		return node;
	}
	public void setFirstUnitId(Integer firstUnitId) {
		this.firstUnitId = firstUnitId;
	}
	public Integer getFirstUnitId() {
		return firstUnitId;
	}
	public String getdisplayCode() {
		return displayCode;
	}
	public void setdisplayCode(String displayCode) {
		this.displayCode = displayCode;
	}
	
	public void setAssetURI(String assetURI) {
		this.assetURI = assetURI;
	}
	public String getAssetURI() {
		if ( getOrganization() != null) {
			if (getS3UploadFlag() == 0) {
				assetURI = getOrganization().getNfsStorageArea().getAreaPath();
			} else {
				assetURI = getOrganization().getS3StorageArea().getAreaPath();
			}
		} 
		return assetURI;
	}

	public void setThumbnails(Thumbnail thumbnails) {
		this.thumbnails = thumbnails;
	}
	public Thumbnail getThumbnails() {
		return thumbnails;
	}

	public void setCodeImage(String codeImage) {
		this.codeImage = codeImage;
	}
	public String getCodeImage() {
		return codeImage;
	}

	public void setS3UploadFlag(int s3UploadFlag) {
		this.s3UploadFlag = s3UploadFlag;
	}
	public int getS3UploadFlag() {
		return s3UploadFlag;
	}

	public class Thumbnail implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String url;
		private String dimensions;
		private boolean isDefaultImage;

	

		public boolean isDefaultImage() {
			return isDefaultImage;
		}

		public void setDefaultImage(boolean isDefaultImage) {
			this.isDefaultImage = isDefaultImage;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getUrl() {
			this.url= getAssetURI() + getCodeImage();
			return url;
		}

		public void setDimensions(String dimensions) {
			this.dimensions = dimensions;
		}

		public String getDimensions() {
			this.dimensions = TAXONOMY_CODE_IMAGE_SIZES;
			return dimensions;
		}

	}
}
