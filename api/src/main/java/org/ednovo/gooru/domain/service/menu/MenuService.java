package org.ednovo.gooru.domain.service.menu;

import java.util.List;

import org.ednovo.gooru.core.api.model.Menu;

public interface MenuService {
	
	List<Menu> getMenuByUserUid(String userUid);	

}
