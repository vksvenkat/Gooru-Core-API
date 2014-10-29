package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class MenuRoleAssoc implements Serializable {

	private static final long serialVersionUID = -8458963691088485340L;
	private Menu menu;
	private MenuRole role;
	
	public Menu getMenu() {
		return menu;
	}
	public void setMenu(Menu menu) {
		this.menu = menu;
	}
	public MenuRole getRole() {
		return role;
	}
	public void setRole(MenuRole role) {
		this.role = role;
	}
	

}
