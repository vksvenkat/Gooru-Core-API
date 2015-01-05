package org.ednovo.gooru.core.api.model;

import java.io.Serializable;

public class MenuRoleAssoc implements Serializable {

	private static final long serialVersionUID = -8458963691088485340L;
	private Menu menu;
	private Role role;
	
	public Menu getMenu() {
		return menu;
	}
	public void setMenu(Menu menu) {
		this.menu = menu;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	
	public MenuRoleAssoc(){
		
	}
	
	public MenuRoleAssoc(Menu menu, Role role){
		this.menu = menu;
		this.role = role;
	}

}
