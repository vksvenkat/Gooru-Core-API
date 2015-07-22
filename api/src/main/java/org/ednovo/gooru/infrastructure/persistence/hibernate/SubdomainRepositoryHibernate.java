/////////////////////////////////////////////////////////////
// SubdomainRepositoryHibernate.java
// gooru-api
// Created by Gooru on 2015
// Copyright (c) 2015 Gooru. All rights reserved.
// http://www.goorulearning.org/
// Permission is hereby granted, free of charge, to any person      obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so,  subject to
// the following conditions:
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY  KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE    WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR  PURPOSE     AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR  COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
/////////////////////////////////////////////////////////////
package org.ednovo.gooru.infrastructure.persistence.hibernate;

import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.Subdomain;
import org.ednovo.gooru.core.api.model.UserGroupSupport;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

@Repository
public class SubdomainRepositoryHibernate extends BaseRepositoryHibernate implements SubdomainRepository, ParameterProperties, ConstantProperties {

	private static final String SUBDOMAINS = "FROM Subdomain";

	private static final String SUBDOMAIN = "select sd.subdomain_id as subdomainId,sd.course_id as courseId,sd.domain_id as domainId,c.name as courseName,d.name as domainName from subdomain sd join course c on sd.course_id = c.course_id join domain d on sd.domain_id = d.domain_id where sd.subdomain_id=:subdomainId";

	private static final String SUBDOMAIN_BY_IDS = "FROM Subdomain  WHERE subdomainId in (:subdomainId)";

	private static final String SUBDOMAIN_STANDARDS = "select s.code_id as codeId, ifnull(common_core_dot_notation, display_code) as code, label, s.type_id as typeId from subdomain_attribute_mapping  s  inner join  code c on s.code_id = c.code_id where c.active_flag=1 and s.subdomain_id=:subdomainId and c.root_node_id in (:rootNodeId) order by s.type_id, c.sequence";

	private static final String STANDARDS = "select code_id as codeId, ifnull(common_core_dot_notation, display_code) as code, label  from code where active_flag=1 and parent_id =:codeId and root_node_id in (:rootNodeId) order by sequence";

	@Override
	public Map<String, Object> getSubdomain(Integer subdomainId) {
		Query query = getSession().createSQLQuery(SUBDOMAIN);
		query.setParameter(SUBDOMAIN_ID, subdomainId);
		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		List<Map<String, Object>> results = list(query);
		return (Map<String, Object>) ((results.size() > 0) ? results.get(0) : null);
	}

	@Override
	public List<Subdomain> getSubdomains(Integer limit, Integer offset) {
		Query query = getSession().createQuery(SUBDOMAINS);
		if (limit == null) {
			limit = 0;
		}
		query.setMaxResults(limit != null ? (limit > MAX_LIMIT ? MAX_LIMIT : limit) : limit);
		query.setFirstResult(offset);
		return list(query);
	}

	@Override
	public List<Subdomain> getSubdomains(List<Integer> subdomainIds) {
		Query query = getSession().createQuery(SUBDOMAIN_BY_IDS).setParameterList(SUBDOMAIN_ID, subdomainIds);
		return list(query);
	}

	@Override
	public List<Map<String, Object>> getSubdomainStandards(Integer subdomainId, String[] rootNodeId) {
		Query query = getSession().createSQLQuery(SUBDOMAIN_STANDARDS);
		query.setParameter(SUBDOMAIN_ID, subdomainId);
		query.setParameterList(ROOT_NODE_ID, rootNodeId);
		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		return list(query);
	}

	@Override
	public List<Map<String, Object>> getStandards(Integer codeId, String[] rootNodeId) {
		Query query = getSession().createSQLQuery(STANDARDS);
		query.setParameter(CODE_ID, codeId);
		query.setParameterList(ROOT_NODE_ID, rootNodeId);
		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		return list(query);
	}

}
