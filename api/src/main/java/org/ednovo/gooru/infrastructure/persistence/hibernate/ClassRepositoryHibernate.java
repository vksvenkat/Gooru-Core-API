package org.ednovo.gooru.infrastructure.persistence.hibernate;

import java.util.List;

import org.ednovo.gooru.core.api.model.UserClass;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

@Repository
public class ClassRepositoryHibernate extends BaseRepositoryHibernate implements ClassRepository, ConstantProperties, ParameterProperties {

	@Override
	public UserClass getClassById(String classUid) {
		Criteria criteria = getSession().createCriteria(UserClass.class);
		criteria.add(Restrictions.eq(PARTY_UID, classUid));
		@SuppressWarnings("rawtypes")
		List results = criteria.list();
		return (UserClass) (results.size() > 0 ? results.get(0) : null);
	}

}
