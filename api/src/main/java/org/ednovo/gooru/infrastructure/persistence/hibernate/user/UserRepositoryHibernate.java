/////////////////////////////////////////////////////////////
// UserRepositoryHibernate.java
// gooru-api
// Created by Gooru on 2014
// Copyright (c) 2014 Gooru. All rights reserved.
// http://www.goorulearning.org/
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
/////////////////////////////////////////////////////////////
package org.ednovo.gooru.infrastructure.persistence.hibernate.user;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.application.util.DatabaseUtil;
import org.ednovo.gooru.core.api.model.EntityOperation;
import org.ednovo.gooru.core.api.model.Gender;
import org.ednovo.gooru.core.api.model.Identity;
import org.ednovo.gooru.core.api.model.Party;
import org.ednovo.gooru.core.api.model.Profile;
import org.ednovo.gooru.core.api.model.RoleEntityOperation;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserSummary;
import org.ednovo.gooru.core.api.model.UserAvailability.CheckUser;
import org.ednovo.gooru.core.api.model.UserClassification;
import org.ednovo.gooru.core.api.model.UserGroup;
import org.ednovo.gooru.core.api.model.UserGroupAssociation;
import org.ednovo.gooru.core.api.model.UserRelationship;
import org.ednovo.gooru.core.api.model.UserRole;
import org.ednovo.gooru.core.api.model.UserRoleAssoc;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryHibernate extends BaseRepositoryHibernate implements UserRepository {

	private static final String EXTERNAL_ID = "externalId";

	private static final String TOTAL_COUNT = "totalCount";

	private JdbcTemplate jdbcTemplate;

	private static final String FIND_USER_BY_TOKEN = "select u.user_id,u.gooru_uid,u.firstname,u.lastname,u.username,i.external_id from user u, user_token t, identity i where t.token =:token and u.gooru_uid = t.user_uid and i.user_uid=t.user_uid and " + generateOrgAuthSqlQuery("u.") + " AND "
			+ generateUserIsDeletedSql("u.");
	private static final String FIND_REGISTERED_USER = "select count(*) as totalCount from registered_users where email = :emailId";
	private static final String INSERT_REGISTERED_USER = "insert into registered_users values('%s', '%s'); ";
	private static final String UPDATE_AGE_CHECK = "update profile set age_check = %s where user_uid = %s;";
	private static final String FIND_AGE_CHECK = "select age_check from profile , user where  profile.user_uid = user.gooru_uid AND profile.user_uid=:userId AND " + generateOrgAuthSqlQuery("user.") + " AND " + generateUserIsDeletedSql("user.");
	private static final String CHECK_CODE = "select count(*) as totalCount from invite_code where code = :code and dateofexpiry >= :dateOfExpiry";
	private static final String INSERT_INVITE = "insert into Invites (FirstName,LastName,Email,School,message, LastDateInvited) values ('%s','%s','%s','%s','%s','%s');";
	private static final String GET_USER_NAME_AVAILABILITY = "select count(1) as totalCount from user where username = :userName";
	private static final String GET_EMAILID_AVAILABILITY = "select count(1) as totalCount from identity where external_id = :emailId";

	@Autowired
	public UserRepositoryHibernate(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate) {
		super();
		setSessionFactory(sessionFactory);
		setJdbcTemplate(jdbcTemplate);
	}

	@Override
	/*
	 * Function to get users based on their role(student,teacher)
	 */
	public List<User> findByRole(UserRole role) {

		List<User> userList = find("from User user where user.userRole.roleId = " + role.getRoleId() + " AND " + generateOrgAuthQueryWithData("user.") + " AND " + generateUserIsDeleted("user."));
		return userList.size() == 0 ? null : userList;
	}

	public String checkUserStatus(String email, String code) {
		String userStatus = null;
		List<Integer> results = getSession().createSQLQuery(FIND_REGISTERED_USER).addScalar(TOTAL_COUNT, StandardBasicTypes.INTEGER).setParameter("emailId", email).list();
		int count = (results.size() > 0) ? results.get(0) : 0;
		Calendar currenttime = Calendar.getInstance();
		java.sql.Date sqldate = new java.sql.Date((currenttime.getTime()).getTime());

		if (count > 0) {
			userStatus = "registered";
		} else {
			results = getSession().createSQLQuery(CHECK_CODE).addScalar(TOTAL_COUNT, StandardBasicTypes.INTEGER).setParameter("code", code.trim()).setParameter("dateOfExpiry", sqldate.toString()).list();
			Integer codeCount = (results.size() > 0) ? results.get(0) : 0;

			if ((codeCount != null) && (codeCount.intValue() > 0)) {
				userStatus = "valid_code";
			}

		}
		return (userStatus != null) ? userStatus : "unknown";
	}

	public void invite(String firstname, String lastname, String email, String school, String message, String datestr) {
		String messageSql = DatabaseUtil.format(INSERT_INVITE, firstname, lastname, email, school, message, datestr);
		this.getJdbcTemplate().update(messageSql);
	}

	@Override
	public User findByToken(String sessionToken) {
		Query userQuery = getSession().createQuery("select user FROM UserToken tok where tok.token = '" + sessionToken + "'");
		userQuery.setFirstResult(0);
		userQuery.setMaxResults(1);
		List<User> users = userQuery.list();
		if (users != null && users.size() > 0) {
			return users.get(0);
		}
		return null;
	}

	@Override
	public Identity findIdentityByResetToken(String resetToken) {
		Session session = getSession();
		Query query = session.createQuery("SELECT identity FROM Identity identity join identity.credential credential  WHERE credential.token = '" + resetToken + "'");
		releaseSession(session);
		return (Identity) (query.list().size() == 0 ? null : (query.list().get(0)));
	}

	@Override
	public Identity findIdentityByRegisterToken(String registerToken) {
		Session session = getSession();
		Query query = session.createQuery("SELECT identity FROM Identity identity join identity.user user  WHERE user.registerToken = '" + registerToken + "'");
		releaseSession(session);
		return (Identity) (query.list().size() == 0 ? null : (query.list().get(0)));
	}

	@Override
	public Identity findUserByGooruId(String gooruId) {
		Session session = getSession();
		Query query = session.createQuery("SELECT identity FROM Identity identity join identity.user user  WHERE user.partyUid = '" + gooruId + "'");
		releaseSession(session);
		return (Identity) (query.list().size() == 0 ? null : (query.list().get(0)));
	}

	@Override
	public User findByIdentity(Identity identity) {

		List<Identity> identityList = getSession().createCriteria(Identity.class).add(Restrictions.eq(EXTERNAL_ID, identity.getExternalId())).createAlias("user", "user").list();
		return identityList.size() == 0 ? null : (identityList.get(0).getUser());

	}

	@Override
	public List<User> findByIdentities(List<String> idList) {
		List<User> userList = new ArrayList<User>();
		List<Identity> identityList = getSession().createCriteria(Identity.class).add(Restrictions.in(EXTERNAL_ID, idList)).list();
		for (Identity id : identityList) {
			userList.add(id.getUser());
		}
		return userList.size() == 0 ? null : userList;

	}

	@Override
	public User findByGooruId(String gooruId) {

		Query query = getSession().createQuery("from User u where u.partyUid = :gooruUId AND " + generateUserIsDeleted("u."));
		query.setParameter("gooruUId", gooruId);
		List<User> userList = query.list();

		return userList.size() == 0 ? null : userList.get(0);
	}

	@Override
	public User findByGooruIdforSuperAdmin(String gooruId) {

		Query query = getSession().createQuery("from User u where u.partyUid = :gooruUId ");
		query.setParameter("gooruUId", gooruId);
		List<User> userList = query.list();

		return userList.size() == 0 ? null : userList.get(0);
	}

	@Override
	public Identity findByEmail(String emailId) {
		String hql = "FROM Identity  identity WHERE identity.externalId = :externalId AND " + generateOrgAuthQuery("identity.user.") + " AND " + generateUserIsDeleted("identity.user.");
		Query query = getSession().createQuery(hql);
		query.setParameter(EXTERNAL_ID, emailId);
		addOrgAuthParameters(query);

		List<Identity> identityList = (List<Identity>) query.list();
		return identityList.size() > 0 ? identityList.get(0) : null;
	}

	public Profile getProfile(User user, boolean isSsoLogin) {
		String hql = "from Profile p where p.profileId = :profileId ";
		if (!isSsoLogin) {
			hql += " AND " + generateOrgAuthQuery("p.user.") + " AND " + generateUserIsDeleted("p.user.");
		}
		Query query = getSession().createQuery(hql);
		query.setParameter("profileId", user.getPartyUid());
		if (!isSsoLogin) {
			addOrgAuthParameters(query);
		}
		List<Profile> profileList = (List<Profile>) query.list();
		return profileList.size() == 0 ? null : profileList.get(0);
	}

	@Override
	public List<Identity> findAllIdentities() {

		Criteria crit = getSession().createCriteria(Identity.class);
		crit.createAlias("user", "user");
		ProjectionList proList = Projections.projectionList();
		proList.add(Projections.property(EXTERNAL_ID));
		crit.setProjection(proList);

		List<Identity> identityList = addOrgAuthCriterias(crit, "user.").list();

		return identityList.size() == 0 ? null : identityList;
	}

	// FIXME: Deprecated
	@Override
	public boolean findRegisteredUser(String emailId) {
		int count = this.getJdbcTemplate().queryForInt(FIND_REGISTERED_USER, new Object[] { emailId });
		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

	// FIXME: Deprecated
	@Override
	public void registerUser(String emailId, String date) {

		String updateSegment = DatabaseUtil.format(INSERT_REGISTERED_USER, emailId, date);

		this.getJdbcTemplate().update(updateSegment);

	}

	@Override
	public void updateAgeCheck(User user, String ageCheck) {

		int ageCheckValue;

		if (ageCheck.equalsIgnoreCase("true")) {
			ageCheckValue = 1;
		} else {
			ageCheckValue = 0;
		}

		String updateSegment = DatabaseUtil.format(UPDATE_AGE_CHECK, ageCheckValue, user.getPartyUid());

		this.getJdbcTemplate().update(updateSegment);
	}

	@Override
	public int findAgeCheck(User user) {
		Query query = getSession().createSQLQuery(FIND_AGE_CHECK);

		query.setParameter("userId", user.getPartyUid());
		addOrgAuthParameters(query);
		List<Integer> results = query.list();
		return (results.size() > 0) ? results.get(0) : 0;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<User> getFollowedByUsers(String gooruUId, Integer offset, Integer limit, boolean skipPagination) {
		Session session = getSession();
		String hql = "SELECT userRelation.user FROM UserRelationship userRelation  WHERE userRelation.followOnUser.partyUid = '" + gooruUId + "' AND userRelation.activeFlag = 1 AND " + generateOrgAuthQueryWithData("userRelation.user.") + " AND " + generateUserIsDeleted("userRelation.user.");
		Query query = session.createQuery(hql);
		if (!skipPagination) {
			query.setFirstResult(offset);
			query.setMaxResults(limit);
		}
		return query.list();
	}
	
	@Override
	public long getFollowedByUsersCount(String gooruUId) {
		Session session = getSession();
		String hql = "SELECT count(*) FROM UserRelationship userRelation  WHERE userRelation.followOnUser.partyUid = '" + gooruUId + "' AND userRelation.activeFlag = 1 AND " + generateOrgAuthQueryWithData("userRelation.user.") + " AND " + generateUserIsDeleted("userRelation.user.");
		Query query = session.createQuery(hql);
		return (Long) query.list().get(0);
	}
	
	@Override
	public long getFollowedOnUsersCount(String gooruUId) {
		Session session = getSession();
		String hql = "SELECT count(*) FROM UserRelationship userRelation WHERE userRelation.user.partyUid = '" + gooruUId + "' AND userRelation.activeFlag = 1  AND " + generateOrgAuthQueryWithData("userRelation.user.") + " AND " + generateUserIsDeleted("userRelation.user.");
		Query query = session.createQuery(hql);
		return (Long) query.list().get(0);
	}

	@Override
	public List<User> getFollowedOnUsers(String gooruUId, Integer offset, Integer limit, boolean skipPagination) {
		Session session = getSession();
		String hql = "SELECT userRelation.followOnUser FROM UserRelationship userRelation WHERE userRelation.user.partyUid = '" + gooruUId + "' AND userRelation.activeFlag = 1  AND " + generateOrgAuthQueryWithData("userRelation.user.") + " AND " + generateUserIsDeleted("userRelation.user.");
		Query query = session.createQuery(hql);
		if (!skipPagination) {
			query.setFirstResult(offset);
			query.setMaxResults(limit);
		}
		return query.list();
	}

	@Override
	public UserRelationship getActiveUserRelationship(String gooruUserId, String gooruFollowOnUserId) {

		List<UserRelationship> relationships = addOrgAuthCriterias(getSession().createCriteria(UserRelationship.class), "user.").createAlias("user", "user").createAlias("followOnUser", "followOnUser").add(Restrictions.eq("user.partyUid", gooruUserId))
				.add(Restrictions.eq("followOnUser.partyUid", gooruFollowOnUserId)).add(Restrictions.eq("activeFlag", true)).list();

		return (relationships.size() > 0) ? relationships.get(0) : null;
	}

	@Override
	public User findByRemeberMeToken(String remeberMeToken) {
		Object[] obj = new Object[1];
		obj[0] = (Object) remeberMeToken;

		List<Map<String, Object>> rows = this.getJdbcTemplate().queryForList(FIND_USER_BY_TOKEN, obj);

		User user = null;
		for (Map row : rows) {
			user = new User();
			user.setGooruUId((String) row.get("gooru_uid"));
			user.setFirstName((String) row.get("firstname"));
			user.setLastName((String) row.get("lastname"));
			user.setUserId(new Integer(String.valueOf(row.get("user_id"))));
			user.setEmailId((String) row.get("external_id"));

			List<UserRoleAssoc> userRoleSet = this.findUserRoleSet(user);
			if (userRoleSet != null) {
				user.setUserRoleSet(new HashSet<UserRoleAssoc>(userRoleSet));
			}
			break;
		}

		return user;
	}

	@Override
	public List<UserRoleAssoc> findUserRoleSet(User user) {
		return find("From UserRoleAssoc userRoleAssoc  WHERE userRoleAssoc.user.partyUid = " + user.getGooruUId() + "  AND " + generateOrgAuthQueryWithData("userRoleAssoc.user.") + " AND " + generateUserIsDeleted("userRoleAssoc.user."));
	}

	@Override
	public boolean checkUserAvailability(String keyword, CheckUser type, boolean isCollaboratorCheck) {
		List<Boolean> availability = null;

		if (type == CheckUser.BYUSERNAME) {
			String sql = GET_USER_NAME_AVAILABILITY;
			if (isCollaboratorCheck) {
				sql += " and user.primary_organization_uid IN (" + getUserOrganizationUidsAsString() + ") OR user.organization_uid ='" + getCurrentUserPrimaryOrganization().getPartyUid() + "'";
			}
				availability = getSession().createSQLQuery(sql).addScalar(TOTAL_COUNT, StandardBasicTypes.BOOLEAN).setParameter("userName", keyword).list();
		} else if (type == CheckUser.BYEMAILID) {
			availability = getSession().createSQLQuery(GET_EMAILID_AVAILABILITY).addScalar(TOTAL_COUNT, StandardBasicTypes.BOOLEAN).setParameter("emailId", keyword).list();
		}
		return (availability != null && availability.size() > 0) ? availability.get(0) : false;
	}

	@Override
	public List<User> listUsers() {
		return addOrgAuthCriterias(getSession().createCriteria(User.class)).list();
	}

	@Override
	public Gender getGenderByGenderId(String genderId) {
		List<Gender> genderList = getSession().createQuery("from Gender g where g.genderId = ?").setParameter(0, genderId).list();
		return genderList.size() == 0 ? null : genderList.get(0);
	}

	@Override
	public List<UserRole> findRolesByNames(String roles) {
		
		String hql = " FROM UserRole ur WHERE ur.name IN (:roleNames) and  " + generateOrgAuthQuery("ur.");
		Query query = getSession().createQuery(hql);
		query.setParameterList("roleNames", roles.split(","));
		addOrgAuthParameters(query);
		List<UserRole> userRoles = query.list();
		return userRoles.size() > 0 ? userRoles : null;
	}

	public UserRole findUserRoleByName(String name, String organizationUids) {
		String hql = "FROM UserRole ur WHERE ur.name =:name ";
		Query query = getSession().createQuery(hql);
		query.setParameter("name", name);
	   List<UserRole> userRole = query.list();
		return (userRole.size() > 0) ? userRole.get(0) : null;
	}

	@Override
	public UserRole findUserRoleByRoleId(Short roleId) {
		String hql = "FROM UserRole ur WHERE ur.roleId =:roleId ";
		Query query = getSession().createQuery(hql);
		query.setParameter("roleId", roleId);
		List<UserRole> userRole = query.list();
		return (userRole.size() > 0) ? userRole.get(0) : null;
	}

	@Override
	public EntityOperation findEntityOperation(String entityName, String operationName) {
		String hql = "FROM EntityOperation eo where eo.entityName=:entityName and eo.operationName=:operationName";
		Query query = getSession().createQuery(hql);
		query.setParameter("entityName", entityName);
		query.setParameter("operationName", operationName);
		List<EntityOperation> entityOperations = query.list();
		return (entityOperations.size() > 0) ? entityOperations.get(0) : null;

	}

	@Override
	public RoleEntityOperation checkRoleEntity(Short roleId, Integer entityOperationId) {
		String hql = "FROM RoleEntityOperation reo where reo.userRole.roleId=:roleId and reo.entityOperation.entityOperationId=:entityOperationId";
		Query query = getSession().createQuery(hql);
		query.setParameter("roleId", roleId);
		query.setParameter("entityOperationId", entityOperationId);
		List<RoleEntityOperation> roleEntityOperations = query.list();
		return (roleEntityOperations.size() > 0) ? roleEntityOperations.get(0) : null;
	}

	@Override
	public List<RoleEntityOperation> getRoleEntityOperations(Short roleId) {
		String hql = "FROM RoleEntityOperation reo where reo.userRole.roleId=:roleId ";
		Query query = getSession().createQuery(hql);
		query.setParameter("roleId", roleId);
		List<RoleEntityOperation> roleEntityOperations = query.list();
		return (roleEntityOperations.size() > 0) ? roleEntityOperations : null;
	}

	@Override
	public User findUserByImportCode(String importCode) {
		String hql = " FROM User u  WHERE u.importCode=:importCode and " + generateOrgAuthQuery("u.") + " and " + generateUserIsDeleted("u.");
		Query query = getSession().createQuery(hql);
		query.setParameter("importCode", importCode);
		addOrgAuthParameters(query);
		List<User> users = query.list();
		return users.size() > 0 ? users.get(0) : null;
	}

	@Override
	public List<UserRoleAssoc> getUserRoleByName(String roles, String userId) {
		String hql = "From UserRoleAssoc ura  where ura.role.name IN(:roleNames) and ura.user.partyUid =:partyUid and " + generateOrgAuthQuery("ura.user.") + " and " + generateUserIsDeleted("ura.user.");
		Query query = getSession().createQuery(hql);
		query.setParameter("partyUid", userId);
		query.setParameterList("roleNames", roles.split(","));
		addOrgAuthParameters(query);
		List<UserRoleAssoc> userRoleAssocs = query.list();
		return userRoleAssocs.size() > 0 ? userRoleAssocs : null;
	}

	@Override
	public List<RoleEntityOperation> findEntityOperationByRole(String roleNames) {
		String hql = " FROM  RoleEntityOperation rp WHERE rp.userRole.name IN (:roleNames) ";
		Query query = getSession().createQuery(hql);
		query.setParameterList("roleNames", roleNames.split(","));
		return query.list();
	}

	@Override
	public List<UserRole> findAllRoles() {
		String hql = "select userRole from UserRole userRole where "+ generateOrgAuthQuery("userRole.");
		Query query = getSession().createQuery(hql);
		addOrgAuthParameters(query);
		return query.list();
	}

	@Override
	public User getUserByUserName(String userName, boolean isLoginRequest) {
		String hql = "FROM User  user WHERE user.username = :username " ;
		if(!isLoginRequest){
			hql +=" and " + generateOrgAuthQuery("user.");
		}
		hql += " and " + generateUserIsDeleted("user.");
		Query query = getSession().createQuery(hql);
		query.setParameter("username", userName);
		if(!isLoginRequest){
			addOrgAuthParameters(query);
		}
		List<User> users = query.list();
		return users.size() > 0 ? users.get(0) : null;
	}

	@Override
	public Identity findByEmailIdOrUserName(String userName, Boolean isLoginRequest, Boolean fetchAlluser) {

		String hql = "from Identity identity  where identity.user.username=:userName or identity.externalId=:externalId ";
		if (!fetchAlluser) {
			hql += " and " + generateUserIsDeleted("identity.user.");
		}
		if (!isLoginRequest) {
			hql += " and  " + generateOrgAuthQuery("identity.user.");
		}

		Query query = getSession().createQuery(hql);
		query.setParameter("userName", userName);
		query.setParameter(EXTERNAL_ID, userName);
		if (!isLoginRequest) {
			addOrgAuthParameters(query);
		}
		List<Identity> identityList = (List<Identity>) query.list();
		return identityList.size() > 0 ? identityList.get(0) : null;
	}

	@Override
	public boolean checkUserFirstLogin(String userId) {
		String sql = "select count(1) from identity where user_uid='" + userId + "' and last_login is null";
		List<BigInteger> results = getSession().createSQLQuery(sql).list();
		return (results != null && results.get(0) != null && (results.get(0).intValue() == 0)) ? false : true;

	}

	@Override
	public User getUserByUserId(Integer userId) {
		String hql = "FROM User user WHERE user.userId=:userId and " + generateOrgAuthQuery("user.") + " and " + generateUserIsDeleted("user.");
		Query query = getSession().createQuery(hql);
		query.setParameter("userId", userId);
		addOrgAuthParameters(query);
		List<User> users = query.list();
		return users.size() > 0 ? users.get(0) : null;
	}

	@Override
	public UserGroup findUserGroupByGroupCode(String groupCode) {
		String hql = "FROM UserGroup userGroup WHERE userGroup.groupCode = :groupCode";
		Query query = getSession().createQuery(hql);
		query.setParameter("groupCode", groupCode);
		List<UserGroup> usersGroup = query.list();
		return usersGroup.size() > 0 ? usersGroup.get(0) : null;
	}

	@Override
	public String removeUserGroupByGroupUid(String groupUid) {
		String sql = "Delete UGA , UG FROM  user_group_association UGA INNER JOIN  user_group UG ON (UG.user_group_uid=UGA.user_group_uid) WHERE UG.user_group_uid='" + groupUid + "'";
		Query query = getSession().createSQLQuery(sql);
		query.executeUpdate();
		return "Deleted Successfully";
	}

	@Override
	public UserGroup findUserGroupById(String groupUid) {
		String hql = "FROM UserGroup userGroup WHERE userGroup.partyUid = :groupUid";
		Query query = getSession().createQuery(hql);
		query.setParameter("groupUid", groupUid);
		List<UserGroup> usersGroup = query.list();
		return usersGroup.size() > 0 ? usersGroup.get(0) : null;
	}

	@Override
	public List<User> findGroupUsers(String groupUid) {
		String hql = "SELECT uga.user FROM UserGroupAssociation uga  WHERE uga.userGroup.partyUid = :partyUid";
		Query query = getSession().createQuery(hql);
		query.setParameter("partyUid", groupUid);
		List<User> users = query.list();
		return users.size() > 0 ? users : null;
	}

	@Override
	public List<User> listUsers(Map<String, String> filters) {

		Integer pageNum = 1;
		if (filters != null && filters.containsKey(PAGE_NO)) {
			pageNum = Integer.parseInt(filters.get(PAGE_NO));
		}
		Integer pageSize = 50;
		if (filters != null && filters.containsKey(PAGE_SIZE)) {
			pageSize = Integer.parseInt(filters.get(PAGE_SIZE));
		}

		String hql = "FROM User user Where " + generateOrgAuthQuery("user.") + " and " + generateUserIsDeleted("user.");
		Query query = getSession().createQuery(hql);
		addOrgAuthParameters(query);
		query.setFirstResult((pageNum - 1) * pageSize);
		query.setMaxResults(pageSize);
		return query.list();
	}

	@Override
	public List<User> findUserByIds(String ownerIds) {
		String hql = "FROM User user WHERE user.partyUid IN (:partyUids) and " + generateOrgAuthQuery("user.") + " and " + generateUserIsDeleted("user.");
		Query query = getSession().createQuery(hql);
		query.setParameterList("userId", ownerIds.split(","));
		addOrgAuthParameters(query);
		List<User> users = query.list();
		return users.size() > 0 ? users : null;

	}

	@Override
	public List<UserGroup> findAllGroups() {
		return getSession().createCriteria(UserGroup.class).list();
	}

	@Override
	public String removeUserGroupMemebrByGroupUid(String groupUid, String gooruUids) {
		String hql = "Delete FROM UserGroupAssociation userGroupAssociation WHERE userGroupAssociation.userGroup.partyUid = :partyUid and userGroupAssociation.user.partyUid IN (:partyUids)";
		Query query = getSession().createQuery(hql);
		query.setParameter("partyUid", groupUid);
		query.setParameterList("partyUids", gooruUids.split(","));
		query.executeUpdate();
		return "Deleted Successfully";
	}
	
	@Override
	public UserGroupAssociation getUserGroupMemebrByGroupUid(String groupUid, String gooruUid) {
		String hql = " FROM UserGroupAssociation userGroupAssociation WHERE userGroupAssociation.userGroup.partyUid = :groupUid and userGroupAssociation.user.partyUid =:gooruUid";
		Query query = getSession().createQuery(hql);
		query.setParameter("groupUid", groupUid);
		query.setParameter("gooruUid", gooruUid);
		return query.list().size() > 0 ? (UserGroupAssociation) query.list().get(0) : null;
	}

	@Override
	public boolean getUserGroupOwnerByGooruUid(String gooruUid, String groupUid) {
		String hql = "FROM UserGroupAssociation UGA WHERE UGA.userGroup.partyUid = :groupUid and UGA.isGroupOwner = 1 and UGA.user.partyUid IN (:gooruUid)";
		Query query = getSession().createQuery(hql);
		query.setParameter("groupUid", groupUid);
		query.setParameter("gooruUid", gooruUid);
		List<UserGroupAssociation> users = query.list();
		return users.size() > 0 ? true : false;
	}

	@Override
	public List<UserGroupAssociation> findGroupUserByIds(String ownerIds) {
		String hql = "FROM UserGroupAssociation UGA WHERE UGA.user.partyUid IN (:partyUids)";
		Query query = getSession().createQuery(hql);
		query.setParameterList("partyUids", ownerIds.split(","));
		addOrgAuthParameters(query);
		List<UserGroupAssociation> userGroupAssoc = query.list();
		return userGroupAssoc.size() > 0 ? userGroupAssoc : null;

	}

	@Override
	public Party findPartyById(String partyUid) {
		String hql = "FROM Party party WHERE party.partyUid =:partyUid";
		Query query = getSession().createQuery(hql);
		query.setParameter("partyUid", partyUid);
		List<Party> party = query.list();
		return party.size() > 0 ? party.get(0) : null;

	}

	@Override
	public User findUserByPartyUid(String partyUid) {
		String hql = "FROM User user WHERE user.partyUid = :partyUid";
		Query query = getSession().createQuery(hql);
		query.setParameter("partyUid", partyUid);
		List<User> users = query.list();
		return users.size() > 0 ? users.get(0) : null;
	}

	@Override
	public List<Profile> getProfileList() {
		String hql = "FROM Profile profile WHERE profile.dateOfBirth IS NOT NULL ";
		Query query = getSession().createQuery(hql);
		List<Profile> profileList = query.list();

		return profileList.size() > 0 ? profileList : null;
	}

	@Override
	public User findUserWithoutOrganization(String username) {
		String hql = "FROM User user WHERE user.username = :username";
		Query query = getSession().createQuery(hql);
		query.setParameter("username", username);
		List<User> users = query.list();
		return users.size() > 0 ? users.get(0) : null;
	}

	@Override
	public Timestamp getSystemCurrentTime() {
		String sql = " select now() ";
		List<Timestamp> results = getSession().createSQLQuery(sql).list();
		return results.size() > 0 ? results.get(0) : null;
	}

	@Override
	public UserClassification getUserClassification(String gooruUid, Integer classificationId, Integer codeId, String creatorUid, String grade) {
		String hql = "FROM UserClassification userClassification WHERE userClassification.user.partyUid=:gooruUid and userClassification.type.customTableValueId=:classificationId  and  " + generateOrgAuthQuery("userClassification.user.");
		if (codeId != null) {
			hql += "and userClassification.code.codeId ='" + codeId + "'";
		}
		if (grade != null) {
			hql += "and userClassification.grade='" + grade + "'";
		}

		if (creatorUid != null) {
			hql += "and userClassification.creator.partyUid='" + creatorUid + "'";
		}
		Query query = getSession().createQuery(hql);
		query.setParameter("gooruUid", gooruUid);
		query.setParameter("classificationId", classificationId);
		addOrgAuthParameters(query);
		return (UserClassification) (query.list().size() > 0 ? query.list().get(0) : null);
	}

	@Override
	public List<UserClassification> getUserClassifications(String gooruUid, Integer classificationId, Integer flag) {
		String hql = "FROM UserClassification userClassification WHERE userClassification.user.partyUid=:gooruUid and userClassification.type.customTableValueId=:classificationId   and " + generateOrgAuthQuery("userClassification.user.");
		if (flag != null) {
			hql += " and userClassification.activeFlag='" + flag + "'";
		}
		Query query = getSession().createQuery(hql);
		query.setParameter("gooruUid", gooruUid);
		query.setParameter("classificationId", classificationId);
		addOrgAuthParameters(query);
		return query.list();
	}

	@Override
	public List<Object[]> getInactiveUsers(Integer offset, Integer limit) {
		String sql = "select user_uid as user_uid,  external_id as email_id from identity i inner join party_custom_field p on p.party_uid = i.user_uid where (date(last_login) between  date(last_login) and date_sub(now(),INTERVAL 2 WEEK) or  last_login is null) and p.optional_key = 'last_user_inactive_mail_send_date' and (p.optional_value = '-' or  date(p.optional_value) between  date(p.optional_value) and date_sub(now(),INTERVAL 2 WEEK))";
		Query query = getSession().createSQLQuery(sql).addScalar("user_uid", StandardBasicTypes.STRING).addScalar("email_id", StandardBasicTypes.STRING);
		query.setFirstResult(offset);
		query.setMaxResults(limit);
		return query.list();
	}

	@Override
	public Integer getInactiveUsersCount() {
		String sql = "select count(1) as count from identity i inner join party_custom_field p on p.party_uid = i.user_uid where (date(last_login) between  date(last_login) and date_sub(now(),INTERVAL 2 WEEK) or  last_login is null) and p.optional_key = 'last_user_inactive_mail_send_date' and (p.optional_value = '-' or  date(p.optional_value) between  date(p.optional_value) and date_sub(now(),INTERVAL 2 WEEK))";
		Query query = getSession().createSQLQuery(sql).addScalar("count", StandardBasicTypes.INTEGER);
		return (Integer) query.list().get(0);
	}

	@Override
	public Integer getUserTokenCount(String gooruUid) {
		String sql = "select count(1) as count from user_token token where token.user_uid='" + gooruUid + "'";
		Query query = getSession().createSQLQuery(sql).addScalar("count", StandardBasicTypes.INTEGER);
		return (Integer) query.list().get(0);
	}

	@Override
	public String getUserGrade(String userUid, Integer classificationId, Integer activeFlag) {
		String sql = "select group_concat(grade) as grade from user_classification uc  where uc.user_Uid ='" + userUid + "' and uc.classification_type='" + classificationId + "'";

		if (activeFlag != null) {
			sql += "and uc.active_flag ='" + activeFlag + "'";
		}
		Query query = getSession().createSQLQuery(sql).addScalar("grade", StandardBasicTypes.STRING);
		return (String) query.list().get(0);
	}

	@Override
	public User findByReferenceuId(String referenceUid) {
		List<User> userList = getSession().createQuery("from User u where u.referenceUid = ?").setParameter(0, referenceUid).list();
		return userList.size() == 0 ? null : userList.get(0);
	}

	@Override
	public Integer getUserBirthdayCount() {
		Query query = getSession().createSQLQuery("select count(1) as count from identity i inner join profile p on (i.user_uid=p.user_uid) where p.date_of_birth is not null and month(p.date_of_birth) = month(now()) and day(p.date_of_birth) = day(now())  and i.external_id like '%@%'").addScalar("count", StandardBasicTypes.INTEGER);
		return (Integer) query.list().get(0);
	}

	@Override
	public List<Object[]> listUserByBirthDay(Integer offset, Integer limit) {
		String sql = "select  i.external_id as email_id , i.user_uid as user_id from identity i inner join profile p on (i.user_uid=p.user_uid) where p.date_of_birth is not null and month(p.date_of_birth) = month(now()) and day(p.date_of_birth) = day(now())  and i.external_id like '%@%'";
		Query query = getSession().createSQLQuery(sql).addScalar("email_id", StandardBasicTypes.STRING).addScalar("user_id", StandardBasicTypes.STRING);
		query.setFirstResult(offset);
		query.setMaxResults(limit);
		return query.list();
	}
	
	@Override
	public Integer getChildUserBirthdayCount() {
		Query query = getSession().createSQLQuery("select count(1) as count from identity i inner join user u on u.gooru_uid=i.user_uid inner join profile p  on p.user_uid=u.gooru_uid inner join identity i2 on i2.user_uid=u.parent_uid  where  datediff(CURDATE(),p.date_of_birth) = 4748 and u.account_type_id=2").addScalar("count", StandardBasicTypes.INTEGER);
		return (Integer) query.list().get(0);
	}
	
	@Override
	public List<Object[]> listChildUserByBirthDay() {
		String sql = "select  u.username as child_user_name, i2.external_id as parent_email_id  from identity i inner join user u on u.gooru_uid=i.user_uid inner join profile p  on p.user_uid=u.gooru_uid inner join identity i2 on i2.user_uid=u.parent_uid  where  datediff(CURDATE(),p.date_of_birth) = 4748 and u.account_type_id=2";
		Query query = getSession().createSQLQuery(sql).addScalar("child_user_name", StandardBasicTypes.STRING).addScalar("parent_email_id", StandardBasicTypes.STRING);
		return query.list();
	}
	
	@Override
	public UserSummary getSummaryByUid(String gooruUid) {
		String hql = "from UserSummary u where u.gooruUid =:gooruUid";
		Query query = getSession().createQuery(hql).setParameter("gooruUid", gooruUid);
		return query.list().size() > 0 ? (UserSummary)query.list().get(0) : new UserSummary();
	}

}
