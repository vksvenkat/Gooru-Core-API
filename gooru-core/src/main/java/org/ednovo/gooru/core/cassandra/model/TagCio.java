package org.ednovo.gooru.core.cassandra.model;

public class TagCio {
	
	
private String label;
	
	private String tagUid;
	
	private String type;
	
	private String userUid;
	
	private int count;

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void setTagUid(String tagUid) {
		this.tagUid = tagUid;
	}

	public String getTagUid() {
		return tagUid;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setUserUid(String userUid) {
		this.userUid = userUid;
	}

	public String getUserUid() {
		return userUid;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	


}
