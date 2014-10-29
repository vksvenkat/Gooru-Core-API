package org.ednovo.gooru.infrastructure.persistence.hibernate.menu;

import java.util.List;

import org.ednovo.gooru.core.api.model.Menu;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;

public interface MenuRepository extends BaseRepository{
	
	Menu getMenu(String menuUid);
	List<Menu> listMenu();
	List<Menu> getMenuByUserRoles(String roleIds);
	
}
