package org.ednovo.gooru.domain.service.menu;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Menu;
import org.ednovo.gooru.core.api.model.MenuItem;
import org.ednovo.gooru.core.api.model.MenuRoleAssoc;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserRoleAssoc;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.BaseServiceImpl;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.menu.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@Service
public class MenuServiceImpl extends BaseServiceImpl implements MenuService, ParameterProperties, ConstantProperties {

	@Autowired
	private MenuRepository menuRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public List<Menu> getMenuByUserUid(User user) {
		Set<String> roleIds = new HashSet<String>();
		if (user.getUserRoleSet() != null) {
			for (UserRoleAssoc userRoleAssoc : user.getUserRoleSet()) {
				roleIds.add(userRoleAssoc.getRole().getRoleId().toString());
			}
		}
		List<String> roleIdList = new ArrayList<String>(roleIds);
		StringUtils.join(roleIdList, ',');
		List<MenuRoleAssoc> userMenuList = menuRepository.getMenuByUserRoles(StringUtils.join(roleIdList, ','));
		List<Menu> userMenus = new ArrayList<Menu>();
		Menu userMenu = new Menu();
		List<MenuItem> menuItems = new ArrayList<MenuItem>();
		for (MenuRoleAssoc menuRoleAssoc : userMenuList) {
			userMenu = this.getMenuRepository().findMenuById(menuRoleAssoc.getMenu().getMenuUid());
			if (userMenu != null) {
				menuItems = this.getMenuRepository().getMenuItemsByMenuId(menuRoleAssoc.getMenu().getMenuUid());
				if (menuItems != null || !(menuItems.isEmpty())) {
					userMenu.setMenuItems(menuItems);
				}
				userMenus.add(userMenu);
			}
		}

		return userMenus;

	}

	@Override
	public ActionResponseDTO<Menu> createMenu(Menu menu, User user){
		final Errors errors = validateCreateMenu(menu);
		if (!errors.hasErrors()) {
			menu.setCreatedOn(new Date(System.currentTimeMillis()));
			menu.setCreatorUid(user.getGooruUId());
			menu.setLastModified(new Date(System.currentTimeMillis()));
			Boolean isActive= (menu.getIsActive() != null)?menu.getIsActive():true;
			menu.setIsActive(isActive);
			this.getMenuRepository().save(menu);
			MenuItem menuItem = new MenuItem();
			menuItem.setMenu(menu);		
			menuItem.setSequence(1);	
			this.getMenuRepository().save(menuItem);
		}
		return new ActionResponseDTO<Menu>(menu, errors);
	}
	
	@Override
	public Menu updateMenu(Menu newMenu, User user, String menuUid){
		Menu menu = this.getMenuRepository().findMenuById(menuUid);
		rejectIfNull(menu, GL0056, 404, "menu ");
		
		if (newMenu.getName() != null) {
			menu.setName(newMenu.getName());
		}
		if (newMenu.getIconUrl() != null) {
			menu.setIconUrl(newMenu.getIconUrl());
		}
		if (newMenu.getDescription() != null) {
			menu.setDescription(newMenu.getDescription());
		}
		if (newMenu.getUrl() != null) {
			menu.setUrl(newMenu.getUrl());
		}
		this.getMenuRepository().save(menu);
		return menu;
	}
	
	@Override
	public Menu  getMenuById(String menuUid){
		return this.getMenuRepository().findMenuById(menuUid);
		
	}
	

	@Override
	public List<MenuItem> getMenuItems(String menuUid) {
		return this.getMenuRepository().getMenuItemsByMenuId(menuUid);
	}
	
	@Override
	public MenuItem getMenuItemById(String menuItemUid) {
		return this.getMenuRepository().findMenuItemById(menuItemUid);
	}
	
	@Override
	public MenuItem updateMenuItem(MenuItem newMenuItem, String menuItemUid, User user) {

		MenuItem menuItem = this.getMenuRepository().findMenuItemById(menuItemUid);
		rejectIfNull(menuItem, GL0056, 404, "MenuItem ");
		if (newMenuItem.getParentMenuUid() != null) {
			menuItem.setParentMenuUid(newMenuItem.getParentMenuUid());
			final Integer sequence = Integer.parseInt(getMenuRepository().getMenuItemCount(newMenuItem.getParentMenuUid()).toString()) + 1;
			menuItem.setSequence(sequence);
		}
		this.getMenuRepository().save(menuItem);
		return menuItem;
	}


	private Errors validateCreateMenu(Menu menu) {
		final Errors errors = new BindException(menu, "menu");
		rejectIfNull(errors, menu, NAME, GL0006, generateErrorMessage(GL0006, NAME));
		return errors;
	}
	
	
	public MenuRepository getMenuRepository() {
		return menuRepository;
	}

	public void setMenuRepository(MenuRepository menuRepository) {
		this.menuRepository = menuRepository;
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

}
