package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class MenuItem implements Serializable {

	private static final long serialVersionUID = 3262202868125189767L;
	private String menuItemUid;
	private String menuUid;
	private String parentMenuUid;
	private Integer sequence;
	
	public String getMenuItemUid() {
		return menuItemUid;
	}
	public void setMenuItemUid(String menuItemUid) {
		this.menuItemUid = menuItemUid;
	}
	public String getMenuUid() {
		return menuUid;
	}
	public void setMenuUid(String menuUid) {
		this.menuUid = menuUid;
	}
	public String getParentMenuUid() {
		return parentMenuUid;
	}
	public void setParentMenuUid(String parentMenuUid) {
		this.parentMenuUid = parentMenuUid;
	}
	public Integer getSequence() {
		return sequence;
	}
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	
	

}
 