package org.ednovo.gooru.domain.service.menu;

import java.util.List;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Menu;
import org.ednovo.gooru.core.api.model.MenuItem;
import org.ednovo.gooru.core.api.model.MenuRoleAssoc;
import org.ednovo.gooru.core.api.model.User;

public interface MenuService {

	List<Menu> getMenus(User user, Boolean flag);

	ActionResponseDTO<Menu> createMenu(Menu menu, User user);

	Menu getMenuById(String menuUid);

	MenuItem getMenuItemById(String menuItemUid);

	List<MenuItem> getMenuItems(String menuUid);

	Menu updateMenu(Menu neweMenu, User user, String menuUid);

	MenuItem updateMenuItem(MenuItem newMenuItem, String menuItemUid, User user);

	MenuRoleAssoc assignRoleByMenuUid(Integer roleId,String menuUid) throws Exception;
	
	void removeAssignedRoleByMenuUid(Integer roleId,String menuUid) throws Exception;
	
	void deleteMenu(String menuUid,String type) throws Exception;
}
