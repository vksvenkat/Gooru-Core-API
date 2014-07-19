/////////////////////////////////////////////////////////////
// ConfigSettingRepositoryHibernate.java
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
public class ConfigSettingRepositoryHibernate extends BaseRepositoryHibernate implements ConfigSettingRepository {

	@Resource(name = "configSettingProfileName")
	private String configSettingProfileName;
	
	private static final String DEFAULT_PROFILE_NAME = "default-profile"; 
	
	private static final String DEFAULT_CHECK = "$";

	private static final String GET_CONFIG_SETTING = "SELECT * FROM config_setting WHERE security_level = 0 AND " + generateOrgAuthSqlQuery();

	@SuppressWarnings("unchecked")
	@Override
	@Cacheable("persistent")
	public Map<String, String> getConfigSettings(String organizationUid) {
		Session session = getSession();
		Map<String, String> settings = new HashMap<String, String>();

		Query query = session.createSQLQuery(GET_CONFIG_SETTING + " AND profile_id = '"+getConfigSettingProfileName()+"'").addScalar("name", StandardBasicTypes.STRING).addScalar("value", StandardBasicTypes.STRING);
		query.setParameter(ORGANIZATION_UIDS, organizationUid);
		List<Object[]> results = query.list();
		for (Object[] object : results) {
			settings.put((String) object[0], (String) object[1]);
		}
		return settings;
	}

	@Override
	public String getConfigSetting(String key, String organizationUid) {
		return getConfigSetting(key, 0, organizationUid);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Cacheable("persistent")
	public String getConfigSetting(String key, int securityLevel, String organizationUid) {
		String sql = "SELECT * FROM config_setting WHERE name = '" + key + "' AND security_level <= " + securityLevel + " AND profile_id = '"+getConfigSettingProfileName()+"' AND " + generateOrgAuthSqlQuery();
		Query query = getSession().createSQLQuery(sql).addScalar("value", StandardBasicTypes.STRING);
		query.setParameter(ORGANIZATION_UIDS, organizationUid);
		List<String> results = query.list();
		String value = null;
		if (results != null && results.size() > 0) {
			value = results.get(0);
		}
		return value;
	}

	public String getConfigSettingProfileName() {
		return configSettingProfileName != null && !configSettingProfileName.startsWith(DEFAULT_CHECK)? configSettingProfileName : DEFAULT_PROFILE_NAME;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Cacheable("persistent")
	public String getSetting(String key) {
		String sql = "SELECT * FROM config_setting WHERE name = '" + key + "' AND profile_id = '"+getConfigSettingProfileName()+"'";
		Query query = getSession().createSQLQuery(sql).addScalar("value", StandardBasicTypes.STRING);
		List<String> results = query.list();
		String value = null;
		if (results != null && results.size() > 0) {
			value = results.get(0);
		}
		return value;
	}

	@Override
	public void updateConfigSetting(String orgainzationUid, String key, String value) {
		String sql = "UPDATE config_setting SET value='"+value+"' WHERE key='"+key+"' AND organization_uid='"+orgainzationUid +"'";
		getSession().createSQLQuery(sql);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getConfigSetting(String organizationName) {
		String sql= "SELECT * FROM config_setting WHERE name='"+organizationName+"'"; 
		Query query =getSession().createSQLQuery(sql).addScalar("value",StandardBasicTypes.STRING);
		List<String> results = query.list();
		String value = null;
		if (results != null && results.size() > 0) {
			value = results.get(0);
		}
		return value;
	}

}
