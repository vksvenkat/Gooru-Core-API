package org.ednovo.gooru.core.api.model;

import java.io.Serializable;


public class Tag extends Content implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8570184385077696374L;


	private String label;
	
	private CustomTableValue tagType;

	private CustomTableValue status;

	private Long contentCount;
	
	private Long userCount;
	
	private Integer synonymsCount;

	private String  wikiPostGooruOid;
	
	private String excerptPostGooruOid;


	

	public Long getContentCount() {
		return contentCount;
	}

	public void setContentCount(Long contentCount) {
		this.contentCount = contentCount;
	}

	public Long getUserCount() {
		return userCount;
	}

	public void setUserCount(Long userCount) {
		this.userCount = userCount;
	}

	public Integer getSynonymsCount() {
		return synonymsCount;
	}

	public void setSynonymsCount(Integer synonymsCount) {
		this.synonymsCount = synonymsCount;
	}

	public String getWikiPostGooruOid() {
		return wikiPostGooruOid;
	}

	public void setWikiPostGooruOid(String wikiPostGooruOid) {
		this.wikiPostGooruOid = wikiPostGooruOid;
	}

	public String getExcerptPostGooruOid() {
		return excerptPostGooruOid;
	}

	public void setExcerptPostGooruOid(String excerptPostGooruOid) {
		this.excerptPostGooruOid = excerptPostGooruOid;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public CustomTableValue getTagType() {
		return tagType;
	}

	public void setTagType(CustomTableValue tagType) {
		this.tagType = tagType;
	}

	public CustomTableValue getStatus() {
		return status;
	}

	public void setStatus(CustomTableValue status) {
		this.status = status;
	}


}
