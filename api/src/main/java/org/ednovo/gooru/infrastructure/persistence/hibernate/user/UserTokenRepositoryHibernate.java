/////////////////////////////////////////////////////////////
// UserTokenRepositoryHibernate.java
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

import java.util.List;

import org.ednovo.gooru.core.api.model.UserToken;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserTokenRepository;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("userTokenRepository")
public class UserTokenRepositoryHibernate extends BaseRepositoryHibernate implements UserTokenRepository {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public UserTokenRepositoryHibernate(SessionFactory sessionFactory, JdbcTemplate jdbcTemplate) {
		super();
		setSessionFactory(sessionFactory);
		setJdbcTemplate(jdbcTemplate);
	}
	
	@Override
	public UserToken findBySession(String sessionId) {
		Session session = null;
		try {
			session = getSession();
		} catch (Exception ex) {
			session = getSession();
		}
		Query query = session.createQuery("from UserToken where sessionId = ? and scope = ?").setString(0, sessionId).setString(1, "session");
		List<UserToken> userList = list(query);
		return userList.size() == 0 ? null : userList.get(0);
		
	}

	@Override
	public UserToken findByScope(String gooruUserId,String scope) {
		
		List<UserToken> userTokenList = list(getSession().createQuery("from UserToken u where u.scope = ? and  u.user.partyUid = ?").setString(0, scope).setString(1,gooruUserId));
		
		return userTokenList.size() == 0 ? null : userTokenList.get(0);
	}
	
	@Override
	public UserToken findByToken(String sessionToken)
	 {
	     @SuppressWarnings("rawtypes")
		 List tokens = getSession().createCriteria(UserToken.class).add(Restrictions.eq("token", sessionToken)).list();
	     if(tokens.size() != 0){
	         return (UserToken)tokens.get(0);
	     }
	     else{
	         return null;
	     }
	 }
	
	

	public JdbcTemplate getJdbcTemplate()
	{
		return jdbcTemplate;
	}
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate)
	{
		this.jdbcTemplate = jdbcTemplate;
	}


}
