package org.ednovo.gooru.domain.service.menu;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Menu;
import org.ednovo.gooru.core.api.model.MenuItem;
import org.ednovo.gooru.core.api.model.MenuRoleAssoc;
import org.ednovo.gooru.core.api.model.Role;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserRoleAssoc;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.NotFoundException;
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
	public List<Menu> getMenus(User user, Boolean flag) {
		List<MenuItem> menuItems = this.getMenuRepository().getMenuItems(getRoles(user), null, null);
		List<Menu> menu = new ArrayList<Menu>();
		for (MenuItem menuItem : menuItems) {
			if (flag) {
				menuItem.getMenu().setMenuItems(this.getMenuRepository().getMenuItems(getRoles(user), null, menuItem.getMenu().getMenuUid()));
				menu.add(menuItem.getMenu());
			} else {
				menu.add(menuItem.getMenu());
			}
		}
		return menu;
	}

	@Override
	public ActionResponseDTO<Menu> createMenu(Menu menu, User user) {
		final Errors errors = validateCreateMenu(menu);
		if (!errors.hasErrors()) {
			menu.setCreatedOn(new Date(System.currentTimeMillis()));
			menu.setCreatorUid(user.getGooruUId());
			menu.setLastModified(new Date(System.currentTimeMillis()));
			this.getMenuRepository().save(menu);
			MenuItem menuItem = new MenuItem();
			menuItem.setMenu(menu);
			int sequence = getMenuRepository().getParentMenuCount() == 0 ? 1 :  getMenuRepository().getParentMenuCount() + 1;
			menuItem.setSequence(sequence);
			menu.setMenuItem(menuItem);
			this.getMenuRepository().save(menuItem);
		}
		return new ActionResponseDTO<Menu>(menu, errors);
	}

	@Override
	public Menu updateMenu(Menu newMenu, User user, String menuUid) {
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
		if (newMenu.getIsActive() != null) {
			menu.setIsActive(newMenu.getIsActive());
		}
		this.getMenuRepository().save(menu);
		return menu;
	}

	@Override
	public Menu getMenuById(String menuUid) {
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
			int sequence = getMenuRepository().getMenuItemCount(newMenuItem.getParentMenuUid()) == 0 ? 1 :  getMenuRepository().getMenuItemCount(newMenuItem.getParentMenuUid()) + 1;
			menuItem.setSequence(sequence);
		}
		this.getMenuRepository().save(menuItem);
		return menuItem;
	}

	@Override
	public MenuRoleAssoc assignRoleByMenuUid(Integer roleId, String menuUid)throws Exception {

		Menu menu = menuRepository.findMenuById(menuUid);
		if (menu == null) {
			throw new NotFoundException("Menu does not exist");
		}
		Role role = menuRepository.findRoleByRoleId(roleId);
		if (role == null) {
			throw new NotFoundException("Role does not exist");
		}
		MenuRoleAssoc menuRoleAssoc = menuRepository.findMenuRoleAssocEntryByRoleIdAndMenuUid(roleId, menuUid);
		if (menuRoleAssoc == null) {
			menuRoleAssoc = new MenuRoleAssoc();
			menuRoleAssoc.setMenu(menu);
			menuRoleAssoc.setRole(role);
			getMenuRepository().save(menuRoleAssoc);
		}
		else {
			throw new NotFoundException("Role already assigned for Menu");
		}
		return menuRoleAssoc;
	}
	
	@Override
	public void removeAssignedRoleByMenuUid(Integer roleId, String menuUid)throws Exception {
		MenuRoleAssoc menuRoleAssoc = menuRepository.findMenuRoleAssocEntryByRoleIdAndMenuUid(roleId, menuUid);
		if (menuRoleAssoc != null) {
			getMenuRepository().remove(menuRoleAssoc);
		}
		else {
			throw new NotFoundException("Role does not assigned for the User");
		}
	}
	
	private Errors validateCreateMenu(Menu menu) {
		final Errors errors = new BindException(menu, "menu");
		rejectIfNull(errors, menu, NAME, GL0006, generateErrorMessage(GL0006, NAME));
		return errors;
	}

	private List<Integer> getRoles(User user) {
		Set<Integer> roleIds = new HashSet<Integer>();
		if (user.getUserRoleSet() != null) {
			for (UserRoleAssoc userRoleAssoc : user.getUserRoleSet()) {
				roleIds.add(Integer.parseInt(userRoleAssoc.getRole().getRoleId().toString()));
			}
		}
		return new ArrayList<Integer>(roleIds);
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
