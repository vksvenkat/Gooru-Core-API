/////////////////////////////////////////////////////////////
// OAuthRepositoryHibernate.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate.auth;

import java.util.List;

import org.ednovo.gooru.domain.model.oauth.OAuthClient;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

@Repository
public class OAuthRepositoryHibernate extends BaseRepositoryHibernate implements OAuthRepository{

	private static final String GET_USER_INFO = "select client_id from oauth_access_token where token_id = :accessToken";


	@Override
	public Boolean checkTokenExists(String accessToken) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findClientByAccessToken(String accessToken) {
		SQLQuery query = getSession().createSQLQuery(GET_USER_INFO);
		query.setParameter("accessToken", accessToken);
		if(query.list().size() > 0){
			return (String) query.list().get(0);
		}
		return null;
	}

	@Override
	public OAuthClient findOAuthClientByClientId(String clientId) {
		String hql = " FROM OAuthClient oauthClient WHERE oauthClient.clientId=:clientId";
		Query query = getSession().createQuery(hql);
		query.setParameter("clientId", clientId);
		List<OAuthClient> results = (List<OAuthClient>) query.list();
		if(results.size() > 0){
			return results.get(0);
		}
		return null;
	}

	@Override
	public List<OAuthClient> listOAuthClient(String gooruUId, int pageNo,
			int pageSize) {
		String hql = " FROM OAuthClient oauthClient WHERE oauthClient.user.partyUid=:gooruUId";
		Query query = getSession().createQuery(hql);
		query.setParameter("gooruUId", gooruUId);
		query.setFirstResult(pageNo);
		query.setMaxResults(pageSize);
		List<OAuthClient> results = (List<OAuthClient>) query.list();
		return results;
	}
	
	@Override
	public OAuthClient findOAuthClientByclientSecret(String clientSecret) {
		String hql = " FROM OAuthClient oauthClient WHERE oauthClient.clientSecret=:clientSecret";
		Query query = getSession().createQuery(hql);
		query.setParameter("clientSecret", clientSecret);
		List<OAuthClient> results = (List<OAuthClient>) query.list();
		if(results.size() > 0){
			return results.get(0);
		}
		return null;
	}
	
	@Override
	public List<OAuthClient> listOAuthClientByOrganization(String organizationUId, int pageNo, int pageSize) {
		String hql = " FROM OAuthClient oauthClient WHERE oauthClient.organization.partyUid=:organizationUId";
		Query query = getSession().createQuery(hql);
		query.setParameter("organizationUId", organizationUId);
		List<OAuthClient> results = (List<OAuthClient>) query.list();
			return (results.size() > 0) ? results : null;
	}

}
