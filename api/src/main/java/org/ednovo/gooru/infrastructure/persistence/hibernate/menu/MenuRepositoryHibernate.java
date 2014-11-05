package org.ednovo.gooru.infrastructure.persistence.hibernate.menu;

import java.util.List;

import org.ednovo.gooru.core.api.model.Menu;
import org.ednovo.gooru.core.api.model.MenuItem;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Query;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

@Repository
public class MenuRepositoryHibernate extends BaseRepositoryHibernate implements MenuRepository, ParameterProperties, ConstantProperties {

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
	public Menu findMenuById(String menuUid) {

		String hql = " FROM Menu m WHERE m.isActive = true";
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

		String sql = "SELECT count(1) as count FROM menu_item mi";
		if (menuUid != null) {
			sql += " WHERE mi.parent_menu_uid =:parentMenuUid";
		}
		Query query = getSession().createSQLQuery(sql).addScalar("count", StandardBasicTypes.INTEGER);
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
		return (List) query.list();
	}

	@Override
	public List<Menu> getMenuBySequence(Integer id, String roleIds) {
		String hql = "Select m FROM MenuItem m join m.menu.menuRoleAssocs mac  WHERE  m.sequence =:sequence";
		if (roleIds != null) {
			hql += " AND mac.role.roleId IN   (" + roleIds + ")";
		}

		Query query = getSession().createQuery(hql);
		query.setParameter("sequence", id);
		return (List) query.list();
	}

	@Override
	public Menu getParentMenuById(String menuUid) {
		String hql = "Select mi.menu FROM MenuItem mi WHERE  mi.sequence = 1";
		if (menuUid != null) {
			hql += " AND mi.menu.menuUid =:menuUid";
		}
		Query query = getSession().createQuery(hql);
		if (menuUid != null) {
			query.setParameter("menuUid", menuUid);
		}
		return (Menu) (query.list().size() > 0 ? query.list().get(0) : null);
	}

	@Override
	public List<MenuItem> getMenuItems(List<Integer> roleIds, Integer sequence, String parentMenuUid) {
		String hql = "SELECT menuItem FROM MenuItem menuItem join menuItem.menu.menuRoleAssocs menuRoleAssoc  WHERE 1=1 ";
		if (roleIds != null) {
			hql += " AND menuRoleAssoc.role.roleId IN ( :roleIds )";
		}
		if (parentMenuUid != null) {
			hql += " AND menuItem.parentMenuUid =:parentMenuUid";
		}
		if (sequence != null)  {
			hql += " AND menuItem.sequence =:sequence";
		} 
		Query query = getSession().createQuery(hql);
		if (roleIds != null) {
			query.setParameterList("roleIds", roleIds);
		}
		if (parentMenuUid != null) {
			query.setParameter("parentMenuUid", parentMenuUid);
		}
		if (sequence != null) {
			query.setParameter("sequence", sequence);
		}
		return query.list();
	}

}
