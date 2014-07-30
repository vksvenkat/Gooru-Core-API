package org.ednovo.gooru.infrastructure.persistence.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class ServicePartyAssocRepositoryHibernate extends BaseRepositoryHibernate implements ServicePartyAssocRepository  {

	private static Logger logger = LoggerFactory.getLogger(ServicePartyAssocRepositoryHibernate.class);
	
	@Override
	public String getPartyVersion(String partyUid, String partyType) {
		
		Session session = getSession();
		String sql = "SELECT s.version as version FROM service s "
				+ " INNER JOIN service_party_assoc spa ON (s.service_key = spa.service_key)"
				+ " WHERE spa.party_uid='"+partyUid+"' AND spa.party_type='"+partyType+"' ";
		Query query = session.createSQLQuery(sql).addScalar("version", StandardBasicTypes.STRING);		
		
		return query.list().size() == 0 ? null : (String)query.list().get(0);
	}

	@Override
	public Map<String, Object> getPartyVersion(String userUid, String organizationUid,
			String groupUid) {
		Session session = getSession();
		String sql = "select s.version, case spa.party_type when 'user' then 1 when 'group' then 2 "
				+ " when 'organization' then 3 else 4 end type_order from service_party_assoc  spa "
				+ " left outer join user u on u.gooru_uid = spa.party_uid and spa.party_type = 'user' "
				+ " left outer join party ug on ug.party_uid = spa.party_uid and spa.party_type = 'group' "
				+ " inner join service s on s.service_key = spa.service_key "
				+ " where spa.party_uid = '"+userUid+"' or spa.party_uid = '"+organizationUid+"' or ";
		
		if(groupUid != null && groupUid != "") {
			sql += " spa.party_uid = '"+groupUid+"'";			
		}
		
		Query query = session.createSQLQuery(sql).addScalar("version", StandardBasicTypes.STRING).addScalar("type_order", StandardBasicTypes.INTEGER);		
		
		return iteratePartyVersion(query.list());
	}
	
	private Map<String, Object> iteratePartyVersion(List<Object[]> list) {
		Map<String, Object> result = new HashMap<String, Object>();
		for(Object[] object : list) {
			result.put("version", object[0]);
			result.put("order", object[1]);
		}
		return null;
	}


}
