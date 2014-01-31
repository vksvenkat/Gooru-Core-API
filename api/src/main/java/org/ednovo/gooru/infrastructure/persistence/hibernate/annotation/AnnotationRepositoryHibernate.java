/////////////////////////////////////////////////////////////
// AnnotationRepositoryHibernate.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate.annotation;

import java.util.List;

import org.ednovo.gooru.core.api.model.Annotation;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author nitasha
 *
 */
@Repository
public class AnnotationRepositoryHibernate extends BaseRepositoryHibernate implements AnnotationRepository{

    @Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public Annotation getContentAnnotationForUser(String userId, String gooruContentId) {

		Criteria criteria = this.getSession().createCriteria(Annotation.class)
		.createAlias("resource.user", "user")
		.add(Expression.eq("user.partyUid", userId))
		.createAlias("resource", "resource")
		.add(Expression.eq("resource.gooruOid", gooruContentId));
		Criteria criteria2 = addOrgAuthCriterias(criteria, "resource.");
		List<Annotation> annotations = criteria2.list();
		return (annotations == null || annotations.size() == 0) ? null : annotations.get(0);
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
}
