package org.ednovo.gooru.core.api.model;



public class Asset extends OrganizationModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3516343997647168595L;


	private Integer assetId;

	private String name;

	private String description;

	private Boolean hasUniqueName;

	private byte[] fileData;
	
	private String url;
	
	public Asset() {

	}

	public Integer getAssetId() {
		return assetId;
	}

	public void setAssetId(Integer assetId) {
		this.assetId = assetId;
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

	public byte[] getFileData() {
		return fileData;
	}

	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}

	public Boolean getHasUniqueName() {
		return hasUniqueName;
	}

	public void setHasUniqueName(Boolean hasUniqueName) {
		this.hasUniqueName = hasUniqueName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}