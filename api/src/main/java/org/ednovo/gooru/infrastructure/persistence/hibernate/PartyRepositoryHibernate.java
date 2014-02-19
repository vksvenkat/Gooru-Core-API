/////////////////////////////////////////////////////////////
// PartyRepositoryHibernate.java
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

import java.util.List;

import org.ednovo.gooru.core.api.model.Party;
import org.ednovo.gooru.core.api.model.PartyCustomField;
import org.ednovo.gooru.core.api.model.Profile;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.persistence.hibernate.party.PartyRepository;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

@Repository
public class PartyRepositoryHibernate extends BaseRepositoryHibernate implements PartyRepository, ParameterProperties {

	@Override
	public Party findPartyById(String partyUid) {
		Session session = getSession();
		String hql = " FROM  Party party WHERE  party.partyUid=:partyUid";
		Query query = session.createQuery(hql);
		query.setParameter("partyUid", partyUid);
		return (Party) ((query.list() != null && query.list().size() > 0) ? query.list().get(0) : null);

	}

	@Override
	public List<PartyCustomField> getPartyCustomFields(String partyUid, String optionalKey, String category) {
		Session session = getSession();
		String hql = " FROM  PartyCustomField partycustomfield  WHERE partycustomfield.partyUid= '" + partyUid + "'  ";
		if (optionalKey != null) {
			hql += " and  partycustomfield.optionalKey= '" + optionalKey + "'";
		}
		if (category != null) {
			hql += " and  partycustomfield.category= '" + category + "'";
		}
		Query query = session.createQuery(hql);
		return query.list();
	}

	@Override
	public void deletePartyCustomField(String partyId, String optionalKey) {
		Session session = getSession();
		String hql = "Delete FROM  PartyCustomField partycustomfield  WHERE partycustomfield.partyUid= '" + partyId + "'AND partycustomfield.optionalKey='" + optionalKey + "'";
		Query q = session.createQuery(hql);
		q.executeUpdate();
	}

	@Override
	public PartyCustomField getPartyCustomField(String partyUid, String optionalKey) {
		String hql = " FROM  PartyCustomField partycustomfield  WHERE partycustomfield.partyUid= '" + partyUid + "'  and  partycustomfield.optionalKey= '" + optionalKey + "'";
		Query query = getSession().createQuery(hql);
		return (query != null && query.list() != null && query.list().size() > 0) ? (PartyCustomField) query.list().get(0) : null;
	}
	
	@Override
	public Profile getUserDateOfBirth(String partyUid, User user){
		Session session = getSession();
		Query query = session.createQuery("FROM  Profile profile  WHERE profile.user.partyUid= '" + partyUid + "'");
		releaseSession(session);
		return  (query != null && query.list() != null && query.list().size() > 0) ? (Profile) query.list().get(0) : null;
	}

	@Override
	public Integer getCountInActiveMailSendToday() {
		Session session = getSession();
		String sql = "select count(1) as count from party_custom_field p where p.optional_value != '-' and  date(p.optional_value) = date(now())";
		Query query = session.createSQLQuery(sql).addScalar("count", StandardBasicTypes.INTEGER);
		return (Integer) query.list().get(0);
	}

	@Override
	public void updatePartyCustomFieldsInActiveMailKey(String userIds) {
		Session session = getSession();
		String sql = "Update  party_custom_field partycustomfield  set partycustomfield.optional_value = date(now())  WHERE partycustomfield.party_uid in ( " + userIds + ") AND partycustomfield.optional_key='last_user_inactive_mail_send_date'";
		Query q = session.createSQLQuery(sql);
		q.executeUpdate();
	}
	
	@Override
	public void updatePartyCustomFieldsBirthDayMailKey(String userIds) {
		Session session = getSession();
		String sql = "Update  party_custom_field partycustomfield  set partycustomfield.optional_value = date(now())  WHERE partycustomfield.party_uid in ( " + userIds + ") AND partycustomfield.optional_key='last_user_birthday_mail_send_date'";
		Query q = session.createSQLQuery(sql);
		q.executeUpdate();
	}
	
	@Override
	public boolean isUserBirthDayMailSentToday(String userId, String date){
		Session session = getSession();
		String sql = "select count(1) as count from party_custom_field where party_uid = '"+userId+"' and optional_value = '"+date+"' and optional_key = 'last_user_birthday_mail_send_date'";
		Query query = session.createSQLQuery(sql).addScalar("count", StandardBasicTypes.INTEGER);
		return ((Integer) query.list().get(0)) == 1 ? true : false;
	}
}
