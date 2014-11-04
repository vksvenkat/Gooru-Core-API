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
import org.ednovo.gooru.core.api.model.UserAvailability.CheckUser;
import org.ednovo.gooru.core.api.model.UserClassification;
import org.ednovo.gooru.core.api.model.UserGroup;
import org.ednovo.gooru.core.api.model.UserGroupAssociation;
import org.ednovo.gooru.core.api.model.UserRelationship;
import org.ednovo.gooru.core.api.model.UserRole;
import org.ednovo.gooru.core.api.model.UserRoleAssoc;
import org.ednovo.gooru.core.api.model.UserSummary;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryHibernate extends BaseRepositoryHibernate implements UserRepository, ParameterProperties, ConstantProperties {

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
	private static final String USER_SUMMARY = "from UserSummary u where u.gooruUid =:gooruUid";
	private static final String FETCH_CHILD_USERS_BY_BIRTHDAY = "select  u.username as child_user_name, i2.external_id as parent_email_id  from identity i inner join user u on u.gooru_uid=i.user_uid inner join profile p  on p.user_uid=u.gooru_uid inner join identity i2 on i2.user_uid=u.parent_uid  where  datediff(CURDATE(),p.date_of_birth) = 4748 and u.account_type_id=2";
	private static final String FETCH_CHILD_USERS_BY_BIRTHDAY_COUNT = "select count(1) as count from identity i inner join user u on u.gooru_uid=i.user_uid inner join profile p  on p.user_uid=u.gooru_uid inner join identity i2 on i2.user_uid=u.parent_uid  where  datediff(CURDATE(),p.date_of_birth) = 4748 and u.account_type_id=2";
	private static final String FETCH_USERS_BY_BIRTHDAY = "select  i.external_id as email_id , i.user_uid as user_id from identity i inner join profile p on (i.user_uid=p.user_uid) where p.date_of_birth is not null and month(p.date_of_birth) = month(now()) and day(p.date_of_birth) = day(now())  and i.external_id like '%@%'";
	private static final String FETCH_USERS_BY_BIRTHDAY_COUNT = "select count(1) as count from identity i inner join profile p on (i.user_uid=p.user_uid) where p.date_of_birth is not null and month(p.date_of_birth) = month(now()) and day(p.date_of_birth) = day(now())  and i.external_id like '%@%'";
	private static final String FIND_BY_REFERENCE_UID = "from User u where u.referenceUid = ?";
	private static final String INACTIVE_USER_COUNT_FOR_LAST_TWO_WEEKS = "select count(1) as count from identity i inner join party_custom_field p on p.party_uid = i.user_uid where (date(last_login) between  date(last_login) and date_sub(now(),INTERVAL 2 WEEK) or  last_login is null) and p.optional_key = 'last_user_inactive_mail_send_date' and (p.optional_value = '-' or  date(p.optional_value) between  date(p.optional_value) and date_sub(now(),INTERVAL 2 WEEK))";
	private static final String FIND_USER_PARTY_UID = "FROM User user WHERE user.partyUid = :partyUid";
	private static final String SYSTEM_TIMESTAMP = "select now() ";
	private static final String FIND_USER_WITHOUT_ORGANIZATION = "FROM User user WHERE user.username = :username";
	private static final String FIND_SUPER_ADMIN_USER = "from User u where u.partyUid = :gooruUId ";
	private static final String FIND_USER_GOORU_UID = "from User u where u.partyUid = :gooruUId AND " + generateUserIsDeleted("u.");
	private static final String FIND_GENDER_BY_ID = "from Gender g where g.genderId = ?";
	private static final String FIND_USER_ROLE_BY_NAME = "FROM UserRole ur WHERE ur.name =:name";
	private static final String FIND_USER_ROLE_BY_UID = "FROM UserRole ur WHERE ur.roleId =:roleId";
	private static final String FIND_ENTITY_OPERATION = "FROM EntityOperation eo where eo.entityName=:entityName and eo.operationName=:operationName";
	private static final String CHECK_ROLE_ENTITY = "FROM RoleEntityOperation reo where reo.userRole.roleId=:roleId and reo.entityOperation.entityOperationId=:entityOperationId";
	private static final String FETCH_ROLE_ENTITY_OPERATION = "FROM RoleEntityOperation reo where reo.userRole.roleId=:roleId ";
	private static final String DELETE_USER_GROUP_MEMBER = "Delete FROM UserGroupAssociation userGroupAssociation WHERE userGroupAssociation.userGroup.partyUid = :partyUid and userGroupAssociation.user.partyUid IN (:partyUids)";
	private static final String FIND_GROUP_USER_BY_IDS = "FROM UserGroupAssociation UGA WHERE UGA.user.partyUid IN (:partyUids)";
	private static final String FIND_PARTY_ID = "FROM Party party WHERE party.partyUid =:partyUid";
	private static final String FIND_IDENTITY = "SELECT identity.user FROM Identity  identity WHERE identity.externalId = :externalId AND " + generateOrgAuthQuery("identity.user.") + " AND " + generateUserIsDeleted("identity.user.");
	private static final String FIND_IDENTITY_LOGIN = "SELECT identity.user FROM Identity  identity WHERE identity.externalId = :externalId AND " + generateUserIsDeleted("identity.user.");

	@Autowired
	public UserRepositoryHibernate(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate) {
		super();
		setSessionFactory(sessionFactory);
		setJdbcTemplate(jdbcTemplate);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> findByRole(UserRole role) {
		List<User> userList = find("from User user where user.userRole.roleId = " + role.getRoleId() + " AND " + generateOrgAuthQueryWithData("user.") + " AND " + generateUserIsDeleted("user."));
		return userList.size() == 0 ? null : userList;
	}

	@SuppressWarnings("unchecked")
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
		return (userStatus == null) ? "unknown" : userStatus;
	}

	public void invite(String firstname, String lastname, String email, String school, String message, String datestr) {
		String messageSql = DatabaseUtil.format(INSERT_INVITE, firstname, lastname, email, school, message, datestr);
		this.getJdbcTemplate().update(messageSql);
	}

	@SuppressWarnings("unchecked")
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
		Query query = getSession().createQuery("SELECT identity FROM Identity identity join identity.credential credential  WHERE credential.token = '" + resetToken + "'");
		return (Identity) (query.list().size() == 0 ? null : (query.list().get(0)));
	}

	@Override
	public Identity findIdentityByRegisterToken(String registerToken) {
		Query query = getSession().createQuery("SELECT identity FROM Identity identity join identity.user user  WHERE user.registerToken = '" + registerToken + "'");
		return (Identity) (query.list().size() == 0 ? null : (query.list().get(0)));
	}

	@Override
	public Identity findUserByGooruId(String gooruId) {
		Query query = getSession().createQuery("SELECT identity FROM Identity identity join identity.user user  WHERE user.partyUid = '" + gooruId + "'");
		return (Identity) (query.list().size() == 0 ? null : (query.list().get(0)));
	}

	@SuppressWarnings("unchecked")
	@Override
	public User findByIdentity(Identity identity) {
		Query query = getSession().createQuery(FIND_IDENTITY);
		query.setParameter("externalId", identity.getExternalId());
		addOrgAuthParameters(query);
		return (User) (query.list().size() > 0 ? query.list().get(0) : null);
	}

	@SuppressWarnings("unchecked")
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
		Query query = getSession().createQuery(FIND_USER_GOORU_UID);
		query.setParameter("gooruUId", gooruId);
		return query.list().size() > 0 ? (User) query.list().get(0) : null;
	}

	@Override
	public User findByGooruIdforSuperAdmin(String gooruId) {
		Query query = getSession().createQuery(FIND_SUPER_ADMIN_USER);
		query.setParameter("gooruUId", gooruId);
		return query.list().size() > 0 ? (User) query.list().get(0) : null;
	}

	@Override
	public Identity findByEmail(String emailId) {
		String hql = "FROM Identity  identity WHERE identity.externalId = :externalId AND " + generateOrgAuthQuery("identity.user.") + " AND " + generateUserIsDeleted("identity.user.");
		Query query = getSession().createQuery(hql);
		query.setParameter(EXTERNAL_ID, emailId);
		addOrgAuthParameters(query);
		return query.list().size() > 0 ? (Identity) query.list().get(0) : null;
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
		return query.list().size() == 0 ? null : (Profile) query.list().get(0);
	}

	@SuppressWarnings("unchecked")
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

	@Override
	public boolean findRegisteredUser(String emailId) {
		int count = this.getJdbcTemplate().queryForInt(FIND_REGISTERED_USER, new Object[] { emailId });
		Boolean isRegisteredUser = false;
		if (count > 0) {
			isRegisteredUser = true;
		}
		return isRegisteredUser;
	}

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

	@SuppressWarnings("unchecked")
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

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getFollowedByUsers(String gooruUId, Integer offset, Integer limit) {
		String hql = "SELECT userRelation.user FROM UserRelationship userRelation  WHERE userRelation.followOnUser.partyUid = '" + gooruUId + "' AND userRelation.activeFlag = 1 AND " + generateOrgAuthQueryWithData("userRelation.user.") + " AND " + generateUserIsDeleted("userRelation.user.");
		Query query = getSession().createQuery(hql);
		query.setFirstResult(offset);
		query.setMaxResults(limit != null ? (limit > MAX_LIMIT ? MAX_LIMIT : limit) : LIMIT);
		return query.list();
	}

	@Override
	public long getFollowedByUsersCount(String gooruUId) {
		String hql = "SELECT count(*) FROM UserRelationship userRelation  WHERE userRelation.followOnUser.partyUid = '" + gooruUId + "' AND userRelation.activeFlag = 1 AND " + generateOrgAuthQueryWithData("userRelation.user.") + " AND " + generateUserIsDeleted("userRelation.user.");
		Query query = getSession().createQuery(hql);
		return (Long) query.list().get(0);
	}

	@Override
	public long getFollowedOnUsersCount(String gooruUId) {
		String hql = "SELECT count(*) FROM UserRelationship userRelation WHERE userRelation.user.partyUid = '" + gooruUId + "' AND userRelation.activeFlag = 1  AND " + generateOrgAuthQueryWithData("userRelation.user.") + " AND " + generateUserIsDeleted("userRelation.user.");
		Query query = getSession().createQuery(hql);
		return (Long) query.list().get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getFollowedOnUsers(String gooruUId, Integer offset, Integer limit) {
		String hql = "SELECT userRelation.followOnUser FROM UserRelationship userRelation WHERE userRelation.user.partyUid = '" + gooruUId + "' AND userRelation.activeFlag = 1  AND " + generateOrgAuthQueryWithData("userRelation.user.") + " AND " + generateUserIsDeleted("userRelation.user.");
		Query query = getSession().createQuery(hql);
		query.setFirstResult(offset);
		query.setMaxResults(limit == null ? LIMIT : (limit > MAX_LIMIT ? MAX_LIMIT : limit));
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public UserRelationship getActiveUserRelationship(String gooruUserId, String gooruFollowOnUserId) {
		List<UserRelationship> relationships = addOrgAuthCriterias(getSession().createCriteria(UserRelationship.class), "user.").createAlias("user", "user").createAlias("followOnUser", "followOnUser").add(Restrictions.eq("user.partyUid", gooruUserId))
				.add(Restrictions.eq("followOnUser.partyUid", gooruFollowOnUserId)).add(Restrictions.eq("activeFlag", true)).list();
		return (relationships.size() > 0) ? relationships.get(0) : null;
	}

	@SuppressWarnings("rawtypes")
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
			user.setUserId(Integer.valueOf((String.valueOf(row.get("user_id")))));
			user.setEmailId((String) row.get("external_id"));

			List<UserRoleAssoc> userRoleSet = this.findUserRoleSet(user);
			if (userRoleSet != null) {
				user.setUserRoleSet(new HashSet<UserRoleAssoc>(userRoleSet));
			}
			break;
		}
		return user;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserRoleAssoc> findUserRoleSet(User user) {
		return find("From UserRoleAssoc userRoleAssoc  WHERE userRoleAssoc.user.partyUid = " + user.getGooruUId() + "  AND " + generateOrgAuthQueryWithData("userRoleAssoc.user.") + " AND " + generateUserIsDeleted("userRoleAssoc.user."));
	}

	@SuppressWarnings("unchecked")
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

	@SuppressWarnings("unchecked")
	@Override
	public List<User> listUsers() {
		return addOrgAuthCriterias(getSession().createCriteria(User.class)).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Gender getGenderByGenderId(String genderId) {
		List<Gender> genderList = getSession().createQuery(FIND_GENDER_BY_ID).setParameter(0, genderId).list();
		return genderList.size() == 0 ? null : genderList.get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserRole> findRolesByNames(String roles) {
		String hql = " FROM UserRole ur WHERE ur.name IN (:roleNames) and  " + generateOrgAuthQuery("ur.");
		Query query = getSession().createQuery(hql);
		query.setParameterList("roleNames", roles.split(","));
		addOrgAuthParameters(query);
		return query.list();
	}

	public UserRole findUserRoleByName(String name, String organizationUids) {
		Query query = getSession().createQuery(FIND_USER_ROLE_BY_NAME);
		query.setParameter("name", name);
		return (UserRole) ((query.list().size() > 0) ? query.list().get(0) : null);
	}

	@Override
	public UserRole findUserRoleByRoleId(Short roleId) {
		Query query = getSession().createQuery(FIND_USER_ROLE_BY_UID);
		query.setParameter("roleId", roleId);
		return (UserRole) ((query.list().size() > 0) ? query.list().get(0) : null);
	}

	@Override
	public EntityOperation findEntityOperation(String entityName, String operationName) {
		Query query = getSession().createQuery(FIND_ENTITY_OPERATION);
		query.setParameter("entityName", entityName);
		query.setParameter("operationName", operationName);
		return (EntityOperation) ((query.list().size() > 0) ? query.list().get(0) : null);

	}

	@Override
	public RoleEntityOperation checkRoleEntity(Short roleId, Integer entityOperationId) {
		Query query = getSession().createQuery(CHECK_ROLE_ENTITY);
		query.setParameter("roleId", roleId);
		query.setParameter("entityOperationId", entityOperationId);
		return (RoleEntityOperation) ((query.list().size() > 0) ? query.list().get(0) : null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RoleEntityOperation> getRoleEntityOperations(Short roleId) {
		Query query = getSession().createQuery(FETCH_ROLE_ENTITY_OPERATION);
		query.setParameter("roleId", roleId);
		return query.list();
	}

	@Override
	public User findUserByImportCode(String importCode) {
		String hql = " FROM User u  WHERE u.importCode=:importCode and " + generateOrgAuthQuery("u.") + " and " + generateUserIsDeleted("u.");
		Query query = getSession().createQuery(hql);
		query.setParameter("importCode", importCode);
		addOrgAuthParameters(query);
		return (User) (query.list().size() > 0 ? query.list().get(0) : null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserRoleAssoc> getUserRoleByName(String roles, String userId) {
		String hql = "From UserRoleAssoc ura  where ura.role.name IN(:roleNames) and ura.user.partyUid =:partyUid and " + generateOrgAuthQuery("ura.user.") + " and " + generateUserIsDeleted("ura.user.");
		Query query = getSession().createQuery(hql);
		query.setParameter("partyUid", userId);
		query.setParameterList("roleNames", roles.split(","));
		addOrgAuthParameters(query);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RoleEntityOperation> findEntityOperationByRole(String roleNames) {
		String hql = " FROM  RoleEntityOperation rp WHERE rp.userRole.name IN (:roleNames) ";
		Query query = getSession().createQuery(hql);
		query.setParameterList("roleNames", roleNames.split(","));
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserRole> findAllRoles() {
		String hql = "select userRole from UserRole userRole where " + generateOrgAuthQuery("userRole.");
		Query query = getSession().createQuery(hql);
		addOrgAuthParameters(query);
		return query.list();
	}	
	
	@Override
	public User getUserByUserName(String userName, boolean isLoginRequest) {
		String hql = "FROM User  user WHERE user.username = :username ";
		if (!isLoginRequest) {
			hql += " and " + generateOrgAuthQuery("user.");
		}
		hql += " and " + generateUserIsDeleted("user.");
		Query query = getSession().createQuery(hql);
		query.setParameter("username", userName);
		if (!isLoginRequest) {
			addOrgAuthParameters(query);
		}
		return (User) (query.list().size() > 0 ? query.list().get(0) : null);
	}

	@Override
	public Identity findByEmailIdOrUserName(String userName, Boolean isLoginRequest, Boolean fetchAlluser) {

		String hql = "select identity from Identity identity join identity.user as user where ";
		if (!fetchAlluser) {
			hql += generateUserIsDeleted("identity.user.") + " AND ";
		}
		if (!isLoginRequest) {
			hql += generateOrgAuthQuery("identity.user.") + " AND ";
		}

		Query query = getSession().createQuery(hql + " user.username=:userName ");
		query.setParameter("userName", userName);
		if (!isLoginRequest) {
			addOrgAuthParameters(query);
		}
		Identity identity = (Identity) (query.list().size() > 0 ? query.list().get(0) : null);
		if (identity == null) {
			Query queryEmail = getSession().createQuery(hql + "  identity.externalId=:externalId ");
			queryEmail.setParameter(EXTERNAL_ID, userName);
			if (!isLoginRequest) {
				addOrgAuthParameters(queryEmail);
			}
			identity = (Identity) (queryEmail.list().size() > 0 ? queryEmail.list().get(0) : null);
		}
		return identity;
	}

	@SuppressWarnings("unchecked")
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
		return (User) (query.list().size() > 0 ? query.list().get(0) : null);
	}

	@Override
	public UserGroup findUserGroupByGroupCode(String groupCode) {
		String hql = "FROM UserGroup userGroup WHERE userGroup.groupCode = :groupCode";
		Query query = getSession().createQuery(hql);
		query.setParameter("groupCode", groupCode);
		return (UserGroup) (query.list().size() > 0 ? query.list().get(0) : null);
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
		return (UserGroup) (query.list().size() > 0 ? query.list().get(0) : null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> findGroupUsers(String groupUid) {
		String hql = "SELECT uga.user FROM UserGroupAssociation uga  WHERE uga.userGroup.partyUid = :partyUid";
		Query query = getSession().createQuery(hql);
		query.setParameter("partyUid", groupUid);
		return query.list();
	}

	@SuppressWarnings("unchecked")
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
		query.setMaxResults(pageSize <= MAX_LIMIT ? pageSize : MAX_LIMIT);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> findUserByIds(String ownerIds) {
		String hql = "FROM User user WHERE user.partyUid IN (:partyUids) and " + generateOrgAuthQuery("user.") + " and " + generateUserIsDeleted("user.");
		Query query = getSession().createQuery(hql);
		query.setParameterList("userId", ownerIds.split(","));
		addOrgAuthParameters(query);
		return query.list();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserGroup> findAllGroups() {
		return getSession().createCriteria(UserGroup.class).list();
	}

	@Override
	public String removeUserGroupMemebrByGroupUid(String groupUid, String gooruUids) {
		Query query = getSession().createQuery(DELETE_USER_GROUP_MEMBER);
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
		return query.list().size() > 0 ? true : false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserGroupAssociation> findGroupUserByIds(String ownerIds) {
		Query query = getSession().createQuery(FIND_GROUP_USER_BY_IDS);
		query.setParameterList("partyUids", ownerIds.split(","));
		addOrgAuthParameters(query);
		return query.list().size() > 0 ? query.list() : null;
	}

	@Override
	public Party findPartyById(String partyUid) {
		Query query = getSession().createQuery(FIND_PARTY_ID);
		query.setParameter("partyUid", partyUid);
		return (Party) (query.list().size() > 0 ? query.list().get(0) : null);
	}

	@Override
	public User findUserByPartyUid(String partyUid) {
		Query query = getSession().createQuery(FIND_USER_PARTY_UID);
		query.setParameter("partyUid", partyUid);
		return (User) (query.list().size() > 0 ? query.list().get(0) : null);
	}

	@Override
	public User findUserWithoutOrganization(String username) {
		Query query = getSession().createQuery(FIND_USER_WITHOUT_ORGANIZATION);
		query.setParameter("username", username);
		return (User) (query.list().size() > 0 ? query.list().get(0) : null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Timestamp getSystemCurrentTime() {
		List<Timestamp> results = getSession().createSQLQuery(SYSTEM_TIMESTAMP).list();
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

	@SuppressWarnings("unchecked")
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

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getInactiveUsers(Integer offset, Integer limit) {
		String sql = "select user_uid as user_uid,  external_id as email_id from identity i inner join party_custom_field p on p.party_uid = i.user_uid where (date(last_login) between  date(last_login) and date_sub(now(),INTERVAL 2 WEEK) or  last_login is null) and p.optional_key = 'last_user_inactive_mail_send_date' and (p.optional_value = '-' or  date(p.optional_value) between  date(p.optional_value) and date_sub(now(),INTERVAL 2 WEEK))";
		Query query = getSession().createSQLQuery(sql).addScalar("user_uid", StandardBasicTypes.STRING).addScalar("email_id", StandardBasicTypes.STRING);
		query.setFirstResult(offset);
		query.setMaxResults(limit != null ? (limit > MAX_LIMIT ? MAX_LIMIT : limit) : LIMIT);
		return query.list();
	}

	@Override
	public Integer getInactiveUsersCount() {
		Query query = getSession().createSQLQuery(INACTIVE_USER_COUNT_FOR_LAST_TWO_WEEKS).addScalar("count", StandardBasicTypes.INTEGER);
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

	@SuppressWarnings("unchecked")
	@Override
	public User findByReferenceUid(String referenceUid) {
		List<User> userList = getSession().createQuery(FIND_BY_REFERENCE_UID).setParameter(0, referenceUid).list();
		return userList.size() == 0 ? null : userList.get(0);
	}

	@Override
	public Integer getUserBirthdayCount() {
		Query query = getSession().createSQLQuery(FETCH_USERS_BY_BIRTHDAY_COUNT).addScalar("count", StandardBasicTypes.INTEGER);
		return (Integer) query.list().get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> listUserByBirthDay(Integer offset, Integer limit) {
		Query query = getSession().createSQLQuery(FETCH_USERS_BY_BIRTHDAY).addScalar("email_id", StandardBasicTypes.STRING).addScalar("user_id", StandardBasicTypes.STRING);
		query.setFirstResult(offset);
		query.setMaxResults(limit == null ? LIMIT : (limit > MAX_LIMIT ? MAX_LIMIT : limit));
		return query.list();
	}

	@Override
	public Integer getChildUserBirthdayCount() {
		Query query = getSession().createSQLQuery(FETCH_CHILD_USERS_BY_BIRTHDAY_COUNT).addScalar("count", StandardBasicTypes.INTEGER);
		return (Integer) query.list().get(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> listChildUserByBirthDay() {
		Query query = getSession().createSQLQuery(FETCH_CHILD_USERS_BY_BIRTHDAY).addScalar("child_user_name", StandardBasicTypes.STRING).addScalar("parent_email_id", StandardBasicTypes.STRING);
		return query.list();
	}

	@Override
	public UserSummary getSummaryByUid(String gooruUid) {
		Query query = getSession().createQuery(USER_SUMMARY).setParameter("gooruUid", gooruUid);
		return query.list().size() > 0 ? (UserSummary) query.list().get(0) : new UserSummary();
	}

	public Integer getChildAccountCount(String userUId) {
		String sql = "select count(1) from user where parent_uid='" + userUId + "' ";
		List<BigInteger> results = getSession().createSQLQuery(sql).list();
		if (results != null && results.get(0) != null) {
			return (results.get(0).intValue());
		}
		return 0;
	}

	@Override
	public User findByIdentityLogin(Identity identity) {
		Query query = getSession().createQuery(FIND_IDENTITY_LOGIN);
		query.setParameter("externalId", identity.getExternalId());
		return (User) (query.list().size() > 0 ? query.list().get(0) : null);
	}

	@Override
	public List<User> findUsersByOrganization(String organizationUid, String parentOrganizationUid, Integer offset, Integer limit) {
		String hql = "from User u where  1=1 ";
		if (organizationUid != null) {
			hql += " AND u.organization.partyUid =:organizationUid ";
		}
		if (parentOrganizationUid != null) {
			hql += " AND u.organization.parentOrganization.partyUid =:parentOrganizationUid";
		}

		Query query = getSession().createQuery(hql);
		if (organizationUid != null) {
			query.setParameter("organizationUid", organizationUid);
		}
		if (parentOrganizationUid != null) {
			query.setParameter("parentOrganizationUid", parentOrganizationUid);
		}
		query.setFirstResult(offset);
		query.setMaxResults(limit == null ? LIMIT : (limit > MAX_LIMIT ? MAX_LIMIT : limit));
		return query.list();
	}

	@Override
	public Long getUsersByOrganizationCount(String organizationUid, String parentOrganizationUid) {
		String hql = "SELECT count(*) from User u where  1=1 ";
		if (organizationUid != null) {
			hql += " AND u.organization.partyUid =:organizationUid ";
		}
		if (parentOrganizationUid != null) {
			hql += " AND u.organization.parentOrganization.partyUid =:parentOrganizationUid";
		}

		Query query = getSession().createQuery(hql);
		if (organizationUid != null) {
			query.setParameter("organizationUid", organizationUid);
		}
		if (parentOrganizationUid != null) {
			query.setParameter("parentOrganizationUid", parentOrganizationUid);
		}
		return (Long) (query.list().size() > 0 ? query.list().get(0) : 0);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<UserRoleAssoc> findUserRoleSetByUserUid(String userUid) {
		return find("From UserRoleAssoc userRoleAssoc  WHERE userRoleAssoc.user.partyUid =' " + userUid + "'  AND " + generateOrgAuthQueryWithData("userRoleAssoc.user.") + " AND " + generateUserIsDeleted("userRoleAssoc.user."));
	}
	
	@Override
	public Long countAllRoles() {
		String hql = "select count(*) from UserRole userRole where " + generateOrgAuthQuery("userRole.");
		Query query = getSession().createQuery(hql);
		addOrgAuthParameters(query);
		return (Long)query.list().get(0);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<UserRole> findUserRoles(String userUid) {
		String hql = "select userRoleAssoc.role From UserRoleAssoc userRoleAssoc  WHERE userRoleAssoc.user.partyUid = '"+userUid+"' AND " + generateOrgAuthQuery("userRoleAssoc.role.");
		Query query = getSession().createQuery(hql);
		addOrgAuthParameters(query);
		return query.list();
	}

	@Override
	public Long countUserRoles(String userUid) {
		String hql = "select count(*) From UserRoleAssoc userRoleAssoc  WHERE userRoleAssoc.user.partyUid = '"+userUid+"' AND " + generateOrgAuthQuery("userRoleAssoc.role.");
		Query query = getSession().createQuery(hql);
		addOrgAuthParameters(query);
		return (Long)query.list().get(0);
	}
	
}
