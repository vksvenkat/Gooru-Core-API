package org.ednovo.gooru.infrastructure.persistence.hibernate.menu;

import java.util.List;

import org.ednovo.gooru.core.api.model.Menu;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

@Repository
public class MenuRepositoryHibernate extends BaseRepositoryHibernate implements MenuRepository, ParameterProperties, ConstantProperties{

	@Override
	public Menu getMenu(String menuUid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Menu> listMenu() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Menu> getMenuByUserRoles(String roleIds) {
		
		String hql = "SELECT mra.menu FROM MenuRoleAssoc mra  ";
		if(roleIds != null) {
			hql += " WHERE mra.roleId IN " + (roleIds);
		}
		Query query = getSession().createQuery(hql);
		return (List) query.list();
	}
	
	
	
}
