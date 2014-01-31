package org.ednovo.gooru.core.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.CodeType;
import org.ednovo.gooru.core.api.model.Content;

public class CodeTransModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8018331187153885319L;
	public static final String GOORU_TAXONOMY_CODE_ID = "20000";
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
	private List<Code> parentsList = new ArrayList<Code>();
	private Set<Content> taxonomySet = new HashSet<Content>();
	private Set<Code> associatedCodes;
	private String codeImage;
	private String taxonomyImageUrl;
	private int s3UploadFlag;
	private String assetURI;
	private String codeUid;
	private Integer grade;
	private Integer activeFlag;
	

	@Override
	public int hashCode() {
		return this.codeId.hashCode();
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
	
}
