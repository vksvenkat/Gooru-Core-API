package org.ednovo.gooru.infrastructure.persistence.hibernate.menu;



import java.util.List;

import org.ednovo.gooru.core.api.model.Menu;
import org.ednovo.gooru.core.api.model.MenuItem;
import org.ednovo.gooru.core.api.model.MenuRoleAssoc;
import org.ednovo.gooru.core.api.model.Role;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Query;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

@Repository
public class MenuRepositoryHibernate extends BaseRepositoryHibernate implements MenuRepository, ParameterProperties, ConstantProperties {

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

		String hql = " FROM MenuItem mi  WHERE 1=1 ";
		if (menuItemUid != null) {
			hql += " AND mi.menuItemUid =:menuItemUid";
		}
		hql += " AND mi.menu.isActive=1 ";
		Query query = getSession().createQuery(hql);
		if (menuItemUid != null) {
			query.setParameter("menuItemUid", menuItemUid);
		}
		return  (query.list().size() > 0 ? (MenuItem) query.list().get(0) : null);
	}

	@Override
	public Integer getMenuItemCount(String menuUid) {

		String sql = "SELECT count(1) as count FROM menu_item mi JOIN menu m on m.menu_uid=mi.menu_uid "
				+ "WHERE m.is_active = 1 AND mi.parent_menu_uid = '"+menuUid+"'";
		Query query = getSession().createSQLQuery(sql).addScalar("count", StandardBasicTypes.INTEGER);
		return (Integer) (query.list().get(0));
	}
	@Override
	public Integer getParentMenuCount() {

		String sql = "SELECT count(1) as count FROM menu_item mi JOIN menu m on m.menu_uid=mi.menu_uid "
				+ " WHERE m.is_active = 1 AND mi.parent_menu_uid is null";
		Query query = getSession().createSQLQuery(sql).addScalar("count", StandardBasicTypes.INTEGER);
		return (Integer) (query.list().get(0));
	}

	@Override
	public List<MenuItem> getMenuItemsByMenuId(String menuUid) {
		String hql = "SELECT distinct mi FROM MenuItem mi WHERE 1=1 ";
		if (menuUid != null) {
			hql += " AND mi.parentMenuUid =:parentMenuUid";
		}
		if (menuUid == null) {
			hql += " AND mi.parentMenuUid is null";
		}
		hql += " AND mi.menu.isActive = 1 ORDER BY mi.sequence";
		Query query = getSession().createQuery(hql);
		if (menuUid != null) {
			query.setParameter("parentMenuUid", menuUid);
		}
		return get(query);
	}

	@Override
	public List<MenuItem> getMenuItems(List<Integer> roleIds, Integer sequence, String parentMenuUid) {
		String hql = "SELECT distinct menuItem FROM MenuItem menuItem join menuItem.menu.menuRoleAssocs menuRoleAssoc  WHERE 1=1 ";
		if (roleIds != null) {
			hql += " AND menuRoleAssoc.role.roleId IN ( :roleIds )";
		}
		if (parentMenuUid != null) {
			hql += " AND menuItem.parentMenuUid =:parentMenuUid";
		}
		if (sequence != null)  {
			hql += " AND menuItem.sequence =:sequence";
		}
		if (parentMenuUid == null) {
			hql += " AND menuItem.parentMenuUid is null";
		}
			hql += " AND menuItem.menu.isActive = 1";
			hql += " ORDER BY menuItem.sequence";
		    
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
		return get(query);
	}
	
	@Override
	public Role findRoleByRoleId(Integer roleId) {
		String hql = "FROM Role r WHERE r.roleId =:roleId";
		Query query = getSession().createQuery(hql);
		query.setParameter("roleId", roleId);
		return (Role) (query.list().size() > 0 ? query.list().get(0) : null);
	}
	
	@Override
	public MenuRoleAssoc findMenuRoleAssocEntry(Integer roleId, String menuUid) {
		String hql = "FROM MenuRoleAssoc MRA WHERE 1=1";

		if (roleId != null) {
			hql += " AND MRA.role.roleId = :roleId";
		}
		if (menuUid != null) {
			hql += " AND MRA.menu.menuUid =:menuUid";
		}
		Query query = getSession().createQuery(hql);
		if (roleId != null) {
			query.setParameter("roleId", roleId);
		}
		if (menuUid != null) {
			query.setParameter("menuUid", menuUid);
		}
		return (MenuRoleAssoc) (query.list().size() > 0 ? query.list().get(0) : null);
	}
	
	@Override
	public MenuItem findMenuItemMenuUid(String menuUid){
		
		String hql = "SELECT distinct mi FROM MenuItem mi WHERE 1=1 ";
		if (menuUid != null) {
			hql += " AND mi.menu.menuUid =:menuUid";
		}
		hql += " AND mi.menu.isActive=1 ORDER BY mi.sequence";
		Query query = getSession().createQuery(hql);
		if (menuUid != null) {
			query.setParameter("menuUid", menuUid);
		}
		return (MenuItem) (query.list().size()> 0 ? query.list().get(0) : null);
		
	}
}

