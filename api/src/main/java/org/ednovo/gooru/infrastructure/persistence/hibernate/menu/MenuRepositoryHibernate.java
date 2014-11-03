package org.ednovo.gooru.infrastructure.persistence.hibernate.menu;

import java.util.List;

import org.ednovo.gooru.core.api.model.Menu;
import org.ednovo.gooru.core.api.model.MenuItem;
import org.ednovo.gooru.core.api.model.MenuRoleAssoc;
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
	public List<MenuRoleAssoc> getMenuByUserRoles(String roleIds) {

		String hql = " FROM MenuRoleAssoc mra  ";
		if (roleIds != null) {
			hql += " WHERE mra.role.roleId IN   (" + roleIds + ")";
		}
		Query query = getSession().createQuery(hql);
		return (List) query.list();
	}
	
	@Override
	public Menu findMenuById(String menuUid) {

		String hql = " FROM Menu m  ";
		hql += " WHERE m.isActive = true";
		if (menuUid != null) {
			hql += " AND m.menuUid =:menuUid";
		}
		Query query = getSession().createQuery(hql);
		if (menuUid != null) {
			query.setParameter("menuUid", menuUid);
		}
		return (Menu) (query.list().size() > 0 ? query.list().get(0) : null);
	}
	
	@Override
	public MenuItem findMenuItemById(String menuItemUid) {

		String hql = " FROM MenuItem mi  ";
		if (menuItemUid != null) {
			hql += " WHERE mi.menuItemUid =:menuItemUid";
		}
		Query query = getSession().createQuery(hql);
		if (menuItemUid != null) {
			query.setParameter("menuItemUid", menuItemUid);
		}
		return (MenuItem) (query.list().size() > 0 ? query.list().get(0) : null);
	}

	@Override
	public Integer getMenuItemCount(String menuUid) {

		String hql = "SELECT count(*) FROM MenuItem mi";
		if (menuUid != null) {
			hql += " WHERE mi.parentMenuUid =:parentMenuUid";
		}
		Query query = getSession().createQuery(hql);
		if (menuUid != null) {
			query.setParameter("parentMenuUid", menuUid);
		}
		return (Integer) query.list().get(0);
	}
	
	@Override
	public List<MenuItem> getMenuItemsByMenuId(String menuUid) {
		String hql = "FROM MenuItem mi WHERE mi.parentMenuUid =:parentMenuUid";
		Query query = getSession().createQuery(hql);
		query.setParameter("parentMenuUid", menuUid);
		return  (List) query.list();
	}

	
	
	
}
