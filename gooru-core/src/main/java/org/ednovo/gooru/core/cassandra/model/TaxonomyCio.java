package org.ednovo.gooru.core.cassandra.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.ednovo.gooru.core.constant.ColumnFamilyConstant;

@Entity(name = ColumnFamilyConstant.TAXONOMY)
public class TaxonomyCio implements IsEntityCassandraIndexable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5270935891808582697L;

	private static final String ORG_PARTY_UID = "organization.partyUid";

	private static final String PARTY_UID = "partyUid";

	@Id
	private String codeId;

	@Column
	private String codeUid;

	@Column
	private String code;

	@Column
	private String label;

	@Column
	private Map<String, String> organization;

	@Column
	private String displayCode;

	@Column
	private int s3UploadFlag;

	@Column
	private String assetURI;

	@Column
	private String parentTaxonomy;

	@Column
	private String parentTaxonomyXml;

	@Column
	private int isAssociateToCode;

	@Column
	private List<Integer> sourceCodeId;

	@Column
	private int displayOrder;

	@Column
	private Integer parentId;

	@Column
	private CodeTypeCo codeType;

	@Column
	private String description;

	@Column
	private Short depth;

	@Column
	private Integer rootNodeId;

	@Column
	private String codeImage;

	@Column
	private String versionUid;
	
	@Column
	private String nextLevelLabels;

	public void setCodeId(String codeId) {
		this.codeId = codeId;
	}

	public String getCodeId() {
		return codeId;
	}

	public TaxonomyCio() {

	}

	public void setCodeUid(String codeUid) {
		this.codeUid = codeUid;
	}

	public String getCodeUid() {
		return codeUid;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	public int getDisplayOrder() {
		return displayOrder;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setDepth(Short depth) {
		this.depth = depth;
	}

	public Short getDepth() {
		return depth;
	}

	public void setRootNodeId(Integer rootNodeId) {
		this.rootNodeId = rootNodeId;
	}

	public Integer getRootNodeId() {
		return rootNodeId;
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
		return assetURI;
	}

	public void setIsAssociateToCode(int isAssociateToCode) {
		this.isAssociateToCode = isAssociateToCode;
	}

	public int getIsAssociateToCode() {
		return isAssociateToCode;
	}

	public void setParentTaxonomyXml(String parentTaxonomyXml) {
		this.parentTaxonomyXml = parentTaxonomyXml;
	}

	public String getParentTaxonomyXml() {
		return parentTaxonomyXml;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setDisplayCode(String displayCode) {
		this.displayCode = displayCode;
	}

	public String getDisplayCode() {
		return displayCode;
	}

	public void setSourceCodeId(List<Integer> sourceCodeId) {
		this.sourceCodeId = sourceCodeId;
	}

	public List<Integer> getSourceCodeId() {
		return sourceCodeId;
	}

	public void setParentTaxonomy(String parentTaxonomy) {
		this.parentTaxonomy = parentTaxonomy;
	}

	public String getParentTaxonomy() {
		return parentTaxonomy;
	}

	public void setOrganization(Map<String, String> organization) {
		this.organization = organization;
	}

	public Map<String, String> getOrganization() {
		return organization;
	}

	public void setCodeType(CodeTypeCo codeType) {
		this.codeType = codeType;
	}

	public CodeTypeCo getCodeType() {
		return codeType;
	}

	@Override
	public String getIndexId() {
		return getCodeId();
	}

	@Override
	public String getIndexType() {
		return ColumnFamilyConstant.TAXONOMY;
	}

	public void setVersionUid(String versionUid) {
		this.versionUid = versionUid;
	}

	public String getVersionUid() {
		return versionUid;
	}

	@Override
	public Map<String, String> getRiFields() {
		Map<String, String> riFields = new HashMap<String, String>(1);
		riFields.put(ORG_PARTY_UID, getOrganization().get(PARTY_UID));
		return riFields;
	}

	public String getNextLevelLabels() {
		return nextLevelLabels;
	}

	public void setNextLevelLabels(String nextLevelLabels) {
		this.nextLevelLabels = nextLevelLabels;
	}
}
