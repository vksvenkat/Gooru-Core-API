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

import com.netflix.astyanax.connectionpool.exceptions.BadRequestException;

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
		rejectIfNull(menu, GL0056, MENU);

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
		rejectIfNull(menuItem, GL0056, MENU);
		int mainMenuSequence = 0;
		int subMenuSequence = 0;
		String parentMenuUid = null;
		if (menuItem.getParentMenuUid() == null) {
			mainMenuSequence = menuItem.getSequence();
		}
		else {
			parentMenuUid = menuItem.getParentMenuUid();
			subMenuSequence = menuItem.getSequence();
		}
		if (newMenuItem.getParentMenuUid() != null) {
			menuItem.setParentMenuUid(newMenuItem.getParentMenuUid());
			int sequence = getMenuRepository().getMenuItemCount(newMenuItem.getParentMenuUid()) == 0 ? 1 :  getMenuRepository().getMenuItemCount(newMenuItem.getParentMenuUid()) + 1;
			menuItem.setSequence(sequence);
		}
		this.getMenuRepository().save(menuItem);
		getMenuRepository().flush();
		if(mainMenuSequence > 0){
			this.orderMainMenuSequence(mainMenuSequence);
		}
		else{
			this.orderSubMenuSequence(parentMenuUid,subMenuSequence);
		}
		return menuItem;
	}

	@Override
	public MenuRoleAssoc assignRoleByMenuUid(Integer roleId, String menuUid) throws Exception {

		Menu menu = menuRepository.findMenuById(menuUid);
		rejectIfNull(menu, GL0056, MENU);
		Role role = menuRepository.findRoleByRoleId(roleId);
		rejectIfNull(role, GL0056, ROLE);
		Set<MenuRoleAssoc> menuRoleAssocSet = menuRepository.findMenuRoleAssocEntry(roleId, menuUid);
		if(menuRoleAssocSet.size() > 0){	
			throw new BadRequestException(generateErrorMessage(GL0103,MENU));				
		}
		MenuRoleAssoc mRoleAssoc = new MenuRoleAssoc();
		mRoleAssoc.setMenu(menu);
		mRoleAssoc.setRole(role);
		getMenuRepository().save(mRoleAssoc);
		return mRoleAssoc;
	}
	
	@Override
	public void removeAssignedRoleByMenuUid(Integer roleId, String menuUid)throws Exception {
		Set<MenuRoleAssoc> menuRoleAssocSet = menuRepository.findMenuRoleAssocEntry(roleId, menuUid);
		rejectIfNull(menuRoleAssocSet, GL0102, ROLE);
		if(menuRoleAssocSet.size() > 0){	
			for(MenuRoleAssoc menuRoleAssoc : menuRoleAssocSet){	
				if (roleId == menuRoleAssoc.getRole().getRoleId()) {
					getMenuRepository().remove(menuRoleAssoc);								
				}
			}
		}
	}
	
	@Override
	public void deleteMenu(String id,String type) throws Exception {
		int mainMenuSequence = 0;
		int subMenuSequence = 0;
		String parentMenuUid = null;
		MenuItem menuItem = new MenuItem(); 
		if(type.equalsIgnoreCase(MENU)){
			menuItem = this.getMenuRepository().findMenuItemMenuUid(id);
		}else{
			menuItem = this.getMenuRepository().findMenuItemById(id);
		}
		rejectIfNull(menuItem, GL0056, MENU);
		Menu menu = getMenuRepository().findMenuById(menuItem.getMenu().getMenuUid());
		if(menuItem.getParentMenuUid() == null){
			mainMenuSequence = menuItem.getSequence();
		}
		else
		{
			parentMenuUid = menuItem.getParentMenuUid();
			subMenuSequence = menuItem.getSequence();
		}
		getMenuRepository().remove(menu);
		getMenuRepository().flush();
		if(mainMenuSequence > 0){
			this.orderMainMenuSequence(mainMenuSequence);
		}
		else{
			this.orderSubMenuSequence(parentMenuUid,subMenuSequence);
		}		
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
