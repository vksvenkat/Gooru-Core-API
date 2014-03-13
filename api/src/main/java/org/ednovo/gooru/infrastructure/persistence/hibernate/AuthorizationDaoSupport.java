/////////////////////////////////////////////////////////////
// AuthorizationDaoSupport.java
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

package org.ednovo.gooru.infrastructure.persistence.hibernate;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.ContentPermission;
import org.ednovo.gooru.core.api.model.OrganizationWrapper;
import org.ednovo.gooru.core.api.model.Party;
import org.ednovo.gooru.core.api.model.PermissionType;
import org.ednovo.gooru.core.api.model.Resource;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;


public abstract class AuthorizationDaoSupport extends HibernateDaoSupport {

	protected static final String PERMITTED_PARTY_UIDS = "permittedPartyUids";

	protected static final String ORGANIZATION_UIDS = "organizationUids";

	protected static final String ORGANIZATION_PARTY_UID = "organization.partyUid";

	protected static final String CURRENT_USER_UID = "currentUserUid";

	protected static final String ORG_AUTH_QUERY = "organization.partyUid IN ( :organizationUids ) ";

	protected static final String AUTH_QUERY = generateAuthQuery("");

	protected final void addAuthParameters(Query query) {
		if (!isContentAdminAccess()) {
			
			query.setParameter(CURRENT_USER_UID, getCurrentUserUid());
		}
		addOrgAuthParameters(query);
	}

	protected final void addOrgAuthParameters(Query query) {
		String[] organizations = getUserOrganizationUids();
		if (organizations != null) {
			query.setParameterList(ORGANIZATION_UIDS, organizations);
		}
	}

	protected final String generateAuthSqlQueryWithData() {
		return generateAuthSqlQueryWithData("");
	}

	protected final static String generateAuthSqlQueryWithData(String alias) {
		if (isContentAdminAccess()) {
			return new StringBuilder(alias).append("organization_uid IN (").append(getUserOrganizationUidsAsString()).append(")").toString();
		} else {
			return new StringBuilder(" (exists( select * from content_permission cps where cps.content_id=").append(alias).append("content_id and cps.party_uid IN ( ").append(getPartyPermitsAsString()).append(" )) OR ").append(alias).append("sharing in ('public','anyonewithlink') OR ")
					.append(alias).append("organization_uid IN (").append(getUserSubOrganizationUidsAsString()).append(") OR ").append(alias).append("user_uid = '").append(getCurrentUserUid()).append("' ) AND ").append(alias).append("organization_uid IN (").append(getUserOrganizationUidsAsString())
					.append(")").toString();
		}
	}

	
	protected final String generateOrgAuthQueryWithData() {
		return generateOrgAuthQueryWithData("");
	}

	protected final String generateOrgAuthQueryWithData(String alias) {
		return alias + "organization.partyUid IN (" + getUserOrganizationUidsAsString() + ") ";
	}

	protected static final String generateUserIsDeleted(String alias) {
		return alias + "isDeleted = 0 ";
	}

	protected static final String generateUserIsDeletedSql(String alias) {
		return alias + "is_deleted = 0 ";
	}

	protected static final String generateOrgAuthSqlQuery() {
		return generateOrgAuthSqlQuery("");
	}

	protected static final String generateOrgAuthSqlQuery(String alias) {
		return alias + "organization_uid IN ( :organizationUids ) ";
	}

	protected final String generateOrgAuthSqlQueryWithData() {
		return generateOrgAuthSqlQueryWithData("");
	}

	protected final String generateOrgAuthSqlQueryWithData(String alias) {
		return alias + "organization_uid IN (" + getUserOrganizationUidsAsString() + ") ";
	}

	protected static final String generateOrgAuthQuery() {
		return ORG_AUTH_QUERY;
	}

	protected static final String generateOrgAuthQuery(String alias) {
		return alias + ORG_AUTH_QUERY;
	}

	protected final Criteria addOrgAuthCriterias(Criteria criteria) {
		return criteria.add(Restrictions.in(ORGANIZATION_PARTY_UID, getUserOrganizationUids()));
	}

	protected final Criteria addOrgAuthCriterias(Criteria criteria, String alias) {
		return criteria.add(Restrictions.in(alias + ORGANIZATION_PARTY_UID, getUserOrganizationUids()));
	}

//	
	protected final String generateAuthQueryWithDataNew(String alias) {
		if (isContentAdminAccess()) {
			return new StringBuilder(alias).append("organization.partyUid IN (").append(getUserOrganizationUidsAsString()).append(") ").toString();
		} else {
			return new StringBuilder(" (exists ( FROM ContentPermission cps where cps.content = " + StringUtils.substringBeforeLast(alias, ".") + " AND cps.party.partyUid IN ( " + getPartyPermitsAsString() + " )) OR ").append(alias).append("sharing in ('public','anyonewithlink') OR ").append(alias)
					.append("organization.partyUid IN (").append(getUserSubOrganizationUidsAsString()).append(") OR ").append(alias).append("user.partyUid = '" + getCurrentUserUid() + "' ").append(" ) AND ").append(alias).append("organization.partyUid IN (")
					.append(getUserOrganizationUidsAsString()).append(") ").toString();
		}
	}

	protected final String generateAuthQuery() {
		return AUTH_QUERY;
	}

	protected static final String generateAuthQuery(String alias) {
		if (isContentAdminAccess()) {
			return new StringBuilder(alias).append("organization.partyUid IN ( :organizationUids ) ").toString();
		} else {
			return new StringBuilder(" (exists ( FROM ContentPermission cps where cps.content = " + StringUtils.substringBeforeLast(alias, ".") + " AND cps.party.partyUid IN ( " + getPartyPermitsAsString() + " )) OR ").append(alias).append("sharing in ('public', 'anyonewithlink') OR ")
					.append(alias).append("organization.partyUid IN (").append(getUserSubOrganizationUidsAsString()).append(") OR ").append(alias).append("user.partyUid = :currentUserUid ").append(" ) AND ").append(alias).append("organization.partyUid IN ( :organizationUids ) ").toString();
		}
	}

	protected final Criteria addAuthCriterias(Criteria criteria, String alias) {
		if (isContentAdminAccess()) {
			return criteria.add(Restrictions.in(alias + ORGANIZATION_PARTY_UID, getUserOrganizationUids()));
		} else {
			Disjunction disjunction = Restrictions.or(Restrictions.in(alias + "cps.party.partyUid", getPartyPermits()), Restrictions.eq(alias + "user.partyUid", getCurrentUserUid()), Restrictions.eq(alias + "sharing", "public"),
					Restrictions.in(alias + ORGANIZATION_PARTY_UID, getUserSubOrganizationUids()));
			return criteria.createAlias(alias + "contentPermissions", "cps", JoinType.LEFT_OUTER_JOIN).add(disjunction).add(Restrictions.in(alias + ORGANIZATION_PARTY_UID, getUserOrganizationUids()));
		}
	}

	protected final Criteria addAuthCriterias(Criteria criteria) {
		return addAuthCriterias(criteria, "");
	}

	protected ContentPermission createPermission(Party party, Content content, PermissionType permissionType) {
		ContentPermission contentPermission = new ContentPermission();
		contentPermission.setContent(content);
		contentPermission.setParty(party);
		contentPermission.setValidFrom(new Date());
		contentPermission.setPermission(permissionType.getType());
		return contentPermission;
	}

	public List getAll(Class<?> clazz) {
		Criteria criteria = getSession().createCriteria(clazz);
		if (Resource.class.isAssignableFrom(clazz)) {
			addAuthCriterias(criteria);
		} else if (OrganizationWrapper.class.isAssignableFrom(clazz)) {
			criteria.add(Restrictions.in("organization.partyUid", getUserOrganizationUids()));
		}
		return criteria.list();
	}

	public Object get(Class<?> clazz, Serializable id) {
		return get(clazz, id, getUserOrganizationUids());
	}

	public Object get(Class<?> clazz, Serializable id, String... organizationUids) {
		Criteria criteria = getSession().createCriteria(clazz);
		criteria.add(Restrictions.idEq(id));
		if (Resource.class.isAssignableFrom(clazz)) {
			addAuthCriterias(criteria);
		} else if (OrganizationWrapper.class.isAssignableFrom(clazz)) {
			criteria.add(Restrictions.in("organization.partyUid", organizationUids));
		}
		List<?> result = criteria.list();
		return result.size() > 0 ? result.get(0) : null;
	}
}
