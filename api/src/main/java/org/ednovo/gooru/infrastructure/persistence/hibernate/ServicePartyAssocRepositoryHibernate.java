package org.ednovo.gooru.infrastructure.persistence.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

@Repository
public class ServicePartyAssocRepositoryHibernate extends BaseRepositoryHibernate implements ServicePartyAssocRepository  {

	@Override
	public String getPartyVersion(String partyUid, String partyType) {

		Session session = getSession();
		String sql = "SELECT s.version as version FROM service s "
				+ " INNER JOIN service_party_assoc spa ON (s.service_key = spa.service_key)"
				+ " WHERE spa.party_uid='"+partyUid+"' AND spa.party_type='"+partyType+"' ";
		Query query = session.createSQLQuery(sql).addScalar("version", StandardBasicTypes.STRING);		
		return query.list().size() == 0 ? null : (String)query.list().get(0);
	}

	private Map<String, Object> iteratePartyVersion(List<Object[]> list) {
		Map<String, Object> result = new HashMap<String, Object>();
		for(Object[] object : list) {
			result.put("version", object[0]);
			result.put("order", object[1]);
		}
		return result;
	}


}
