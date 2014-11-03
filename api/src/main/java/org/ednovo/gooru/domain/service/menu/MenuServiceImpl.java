package org.ednovo.gooru.domain.service.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.ednovo.gooru.core.api.model.Menu;
import org.ednovo.gooru.core.api.model.UserRoleAssoc;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.BaseServiceImpl;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.menu.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MenuServiceImpl extends BaseServiceImpl implements MenuService, ParameterProperties, ConstantProperties {

	@Autowired
	private MenuRepository menuRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public List<Menu> getMenuByUserUid(String userUid) {

		Set<String> roleIds = null;
		
		List<UserRoleAssoc> userRoles = userRepository.findUserRoleSetByUserUid(userUid);
		for (UserRoleAssoc userRoleAssoc : userRoles) {
			roleIds.add(userRoleAssoc.getRole().getRoleId().toString());
		}
		List<String> roleIdList = new ArrayList<String>(roleIds);
		StringUtils.join(roleIdList, ',');
		List<Menu> menuList = menuRepository.getMenuByUserRoles(StringUtils.join(roleIdList, ','));
		
		return menuList;
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
