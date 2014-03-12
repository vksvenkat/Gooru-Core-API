package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.ednovo.gooru.core.cassandra.model.IsCassandraIndexable;

public class Code extends OrganizationModel implements Comparable<Code> , IndexableEntry, IsCassandraIndexable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5114132847386670528L;

	public static final String GOORU_TAXONOMY_CODE_ID = "20000";
	
	public static final String TAXONOMY_CODE_IMAGE_SIZES = "80x60,160x120";

	
	private static final String INDEX_TYPE = "taxonomy";
	
	private Integer codeId;
	private String code;
	private int displayOrder;
	private String label;
	private Integer parentId;
	private Code parent;
	private String description;
	private Short depth;
	private Integer rootNodeId;	
	private CodeType codeType;
	private List<Code> parentsList;
	private Set<Content> taxonomySet;
	private Set<Code> associatedCodes;
	private String codeImage;
	private String taxonomyImageUrl;
	private int s3UploadFlag;
	private String assetURI;
	private String codeUid;
	private Integer grade;
	private Integer activeFlag;
	private String displayCode;
	private Thumbnail thumbnails;
	private User creator;
	private Integer libraryFlag;
	private Set<Code> codeOrganizationAssoc;
	
    
	public Code() {
		thumbnails = new Thumbnail();
		taxonomySet = new HashSet<Content>();
		parentsList = new ArrayList<Code>();
	}
	
	@Override
	public boolean equals(Object obj) {

		if(!(obj instanceof Code)) {
			return false;
		}
		final Code other = (Code) obj;

		if (this.codeId != null && this.codeId.equals(other.codeId)){
			return true;
		}
		else{ 
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.codeId != null ? this.codeId.hashCode() : 0;
	}

	@Override
	public int compareTo(Code code) {

		if(this.codeType.getTypeId() > code.codeType.getTypeId()){
			return 1;
		}
		else if(this.codeType.getTypeId() < code.codeType.getTypeId()){
			return -1;
		}
		else{ 
			return 0;
		}
	}

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public int getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
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

	public Integer getCodeId() {
		return codeId;
	}
	public void setCodeId(Integer codeId) {
		this.codeId = codeId;
	}

	public CodeType getCodeType() {
		return codeType;
	}
	public void setCodeType(CodeType codeType) {
		this.codeType = codeType;
	}

	public Set<Content> getTaxonomySet() {
		return taxonomySet;
	}
	public void setTaxonomySet(Set<Content> taxonomySet) {
		this.taxonomySet = taxonomySet;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<Code> getAssociatedCodes() {
		return associatedCodes;
	}

	public void setAssociatedCodes(Set<Code> associatedCodes) {
		this.associatedCodes = associatedCodes;
	}

	public Short getDepth() {
		return depth;
	}

	public void setDepth(Short depth) {
		this.depth = depth;
	}

	public Integer getRootNodeId() {
		return rootNodeId;
	}

	public void setRootNodeId(Integer rootNodeId) {
		this.rootNodeId = rootNodeId;
	}

	public Code getParent() {
		return parent;
	}

	public void setParent(Code parent) {
		this.parent = parent;
	}

	public List<Code> getParentsList() {
		return parentsList;
	}

	public void setParentsList(List<Code> parentsList) {
		this.parentsList = parentsList;
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

	public void setCodeUid(String codeUid) {
		this.codeUid = codeUid;
	}

	public String getCodeUid() {
	
		if(codeUid == null){
			codeUid = UUID.randomUUID().toString();
		}
		return codeUid;
	}

	public void setTaxonomyImageUrl(String taxonomyImageUrl) {
		this.taxonomyImageUrl = taxonomyImageUrl;
	}

	public String getTaxonomyImageUrl() {
		if (getCodeImage() != null) { 
			this.taxonomyImageUrl = this.getAssetURI() +  "/"  + getCodeImage();
		}
		return taxonomyImageUrl;
	}

	@Override
	public String getEntryId() {
		return getCodeId()+"";
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public Integer getGrade() {
		return grade;
	}

	public Integer getActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(Integer activeFlag) {
		this.activeFlag = activeFlag;
	}
	public String getdisplayCode() {
		return displayCode;
	}

	public void setdisplayCode(String displayCode) {
		this.displayCode = displayCode;
	}
	
	@Override
	public String getIndexId() {
		return getCodeId()+"";
	}

	@Override
	public String getIndexType() {
		return INDEX_TYPE;
	}
	
	public void setThumbnails(Thumbnail thumbnails) {
		this.thumbnails = thumbnails;
	}

	public Thumbnail getThumbnails() {
		return thumbnails;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public User getCreator() {
		return creator;
	}

	public void setLibraryFlag(Integer libraryFlag) {
		this.libraryFlag = libraryFlag;
	}

	public Integer getLibraryFlag() {
		return libraryFlag;
	}


	public void setCodeOrganizationAssoc(Set<Code> codeOrganizationAssoc) {
		this.codeOrganizationAssoc = codeOrganizationAssoc;
	}

	public Set<Code> getCodeOrganizationAssoc() {
		return codeOrganizationAssoc;
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
