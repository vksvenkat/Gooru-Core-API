package org.ednovo.gooru.infrastructure.persistence.hibernate.menu;

import java.util.List;

import org.ednovo.gooru.core.api.model.Menu;
import org.ednovo.gooru.core.api.model.MenuItem;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;

public interface MenuRepository extends BaseRepository{
	
	Menu getMenu(String menuUid);

	List<Menu> listMenu();
	
	List<MenuItem> getMenuItems(List<Integer> roleIds, Integer sequence, String parentMenuUid);
	
	Menu findMenuById(String menuUid);
	
	MenuItem findMenuItemById(String menuItemUid);
	
	Integer getMenuItemCount( String menuUid);
	
    List<MenuItem> getMenuItemsByMenuId(String menuUid);
	
    List<Menu> getMenuBySequence(Integer id, String roleIds) ;
    
    Menu getParentMenuById(String menuUid);
	
}
