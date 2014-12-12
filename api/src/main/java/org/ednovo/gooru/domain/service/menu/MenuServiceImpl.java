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
import org.ednovo.gooru.domain.service.BaseServiceImpl;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.menu.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.ednovo.gooru.core.exception.BadRequestException;

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
		rejectIfNull(menu, GL0056, 404, MENU);

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
	public MenuItem updateMenuItem(String menuUid, String menuItemUid, User user) {
		MenuItem menuItem = this.getMenuRepository().findMenuItemById(menuItemUid);
		rejectIfNull(menuItem, GL0056, MENU);
		if (menuUid.equals(menuItem.getMenu().getMenuUid())){
			throw new BadRequestException(generateErrorMessage(GL0104));
		}
		int mainMenuSequence = 0;
		int subMenuSequence = 0;
		String parentMenuUid = null;		
		int sequence = 0;
		if (menuItem.getParentMenuUid() == null) {
			mainMenuSequence = menuItem.getSequence();
			menuItem.setParentMenuUid(menuUid);
		}
		else if (menuItem.getParentMenuUid() != null && !menuUid.equalsIgnoreCase("") && menuUid != null){
			parentMenuUid = menuItem.getParentMenuUid();
			subMenuSequence = menuItem.getSequence();
			menuItem.setParentMenuUid(menuUid);
		}
		else{
			parentMenuUid = menuItem.getParentMenuUid();
			subMenuSequence = menuItem.getSequence();
			menuItem.setParentMenuUid(null);
		}		
		if (menuUid != null && !menuUid.equalsIgnoreCase("") ) {
			sequence = getMenuRepository().getMenuItemCount(menuUid) == 0 ? 1 :  getMenuRepository().getMenuItemCount(menuUid) + 1;
		}
		else{
			sequence = getMenuRepository().getParentMenuCount() == 0 ? 1 :  getMenuRepository().getParentMenuCount() + 1;
		}
		menuItem.setSequence(sequence);
		this.getMenuRepository().save(menuItem);
		getMenuRepository().flush();
		if(mainMenuSequence > 0){
			this.orderMainMenuSequence(mainMenuSequence);
		}
		else{
			this.orderSubMenuSequence(parentMenuUid, subMenuSequence);
		}
		return menuItem;
	}

	@Override
	public MenuRoleAssoc assignRoleByMenuUid(Integer roleId, String menuUid) throws Exception{

		Menu menu = menuRepository.findMenuById(menuUid);
		rejectIfNull(menu, GL0056, 404, MENU);
		Role role = menuRepository.findRoleByRoleId(roleId);
		rejectIfNull(role, GL0056, 404, ROLE);
		MenuRoleAssoc menuRoleAssoc = menuRepository.findMenuRoleAssocEntry(roleId, menuUid);
		rejectIfAlReadyExist(menuRoleAssoc, GL0103, MENU);
		menuRoleAssoc = new MenuRoleAssoc(menu, role);
		getMenuRepository().save(menuRoleAssoc);
		return menuRoleAssoc;
	}
	
	@Override
	public void removeAssignedRoleByMenuUid(Integer roleId, String menuUid)throws Exception {
		MenuRoleAssoc menuRoleAssoc = menuRepository.findMenuRoleAssocEntry(roleId, menuUid);
		rejectIfNull(menuRoleAssoc, GL0102, 404, MENU);
		getMenuRepository().remove(menuRoleAssoc);								
	}
	
	@Override
	public void deleteMenu(String id,String type) throws Exception {
		MenuItem menuItem = new MenuItem(); 
		int mainMenuSequence = 0;
		int subMenuSequence = 0;
		String parentMenuUid = null;		
		if(type.equalsIgnoreCase(MENU)){
			menuItem = this.getMenuRepository().findMenuItemMenuUid(id);						
		}else{
			menuItem = this.getMenuRepository().findMenuItemById(id);
		}
		rejectIfNull(menuItem, GL0056, 404, MENU);
		if (menuItem.getParentMenuUid() == null) {
			mainMenuSequence = menuItem.getSequence();
		}
		else{
			parentMenuUid = menuItem.getParentMenuUid();
			subMenuSequence = menuItem.getSequence();
		}
		Menu mainMenu = menuItem.getMenu();		
		if(menuItem.getParentMenuUid() == null){
			List<MenuItem> menuItemList = this.getMenuRepository().getMenuItemsByMenuId(menuItem.getMenu().getMenuUid());
			if(menuItemList.size() > 0){	
				for(MenuItem subMenuItem : menuItemList){
					Menu subMenu = subMenuItem.getMenu();
					subMenu.setIsActive(false);
					getMenuRepository().save(subMenu);
				}
			}
		}
		mainMenu.setIsActive(false);
		getMenuRepository().save(mainMenu);
		getMenuRepository().flush();
		if(mainMenuSequence > 0){
			this.orderMainMenuSequence(mainMenuSequence);
		}
		else{
			this.orderSubMenuSequence(parentMenuUid, subMenuSequence);
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

	private void orderMainMenuSequence(int mainMenuSequence){
		List<MenuItem> menuItemList = this.getMenuRepository().getMenuItemsByMenuId(null);
		if(menuItemList.size() > 0){
			for (MenuItem mItem : menuItemList){
				if(mItem.getSequence() > mainMenuSequence){
					mItem.setSequence(mainMenuSequence);
					getMenuRepository().save(mItem);
					mainMenuSequence++;
				}
			}
		}
	}
	
	private void orderSubMenuSequence(String parentMenuUid,int subMenuSequence){
		List<MenuItem> menuItemList = this.getMenuRepository().getMenuItemsByMenuId(parentMenuUid);
		if(menuItemList.size() > 0){			
			for (MenuItem mItem : menuItemList){
				if(mItem.getSequence() > subMenuSequence){
					mItem.setSequence(subMenuSequence);
					getMenuRepository().save(mItem);
					subMenuSequence++;
				}
			}
		}
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
