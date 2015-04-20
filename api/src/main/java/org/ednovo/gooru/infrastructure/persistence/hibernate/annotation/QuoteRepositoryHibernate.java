/////////////////////////////////////////////////////////////
// QuoteRepositoryHibernate.java
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.ednovo.gooru.core.api.model.AnnotationType;
import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.License;
import org.ednovo.gooru.core.api.model.Quote;
import org.ednovo.gooru.core.api.model.TagType;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class QuoteRepositoryHibernate extends BaseRepositoryHibernate implements QuoteRepository {

	private static final String NONE = "none";
	private static final String USER = "user";
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static final String RETRIEVE_QUOTE = "Select c.gooru_oid as gooruOid, a.anchor as anchor, a.freetext as freeText, q.topic as topic, q.title as title, q.grade as grade, q.license_name as licenseName, q.type_name as typeName from annotation a , content c, quote q where c.user_uid= :contentUserId and c.content_id = a.content_id and a.content_id = q.content_id and "
			+ generateOrgAuthSqlQuery("q.") + " order by c.created_on desc;";

	@Override
	public List<Quote> findByUser(String userId) {
		Query query = getSession().createSQLQuery(RETRIEVE_QUOTE).addScalar("gooruOid", StandardBasicTypes.STRING).addScalar("anchor", StandardBasicTypes.STRING).addScalar("freeText", StandardBasicTypes.STRING).addScalar("topic", StandardBasicTypes.STRING)
				.addScalar("title", StandardBasicTypes.STRING).addScalar("grade", StandardBasicTypes.STRING).addScalar("licenseName", StandardBasicTypes.STRING).addScalar("typeName", StandardBasicTypes.STRING).setParameter("contentUserId", userId);
		addOrgAuthParameters(query);
		List<Object[]> results = arrayList(query);
		List<Quote> annotations = new ArrayList<Quote>();
		for (Object[] object : results) {
			Quote quote = new Quote();
			quote.setGooruOid((String) object[0]);
			quote.setAnchor((String) object[1]);
			quote.setFreetext((String) object[2]);
			quote.setTopic((String) object[3]);
			quote.setTitle((String) object[4]);
			quote.setGrade((String) object[5]);
			String licenseName = (String) object[6];
			License license = null;
			if (licenseName != null) {
				license = (License) get(License.class, licenseName);
			}

			quote.setLicense(license);

			String typeName = (String) object[7];

			TagType tagType = null;
			if (typeName != null) {
				tagType = (TagType) get(TagType.class, typeName);
			}

			quote.setTagType(tagType);
			annotations.add(quote);
		}
		return annotations;

	}

	@Override
	public Quote findByContent(String gooruContentId) {
		Query query = getSession().createQuery("from Quote q  where q.gooruOid ='" + gooruContentId + "' " + generateOrgAuthQueryWithData("q."));
		List<Quote> quoteList = list(query);

		return quoteList.size() == 0 ? null : quoteList.get(0);

	}

	@Override
	public List<Quote> findNotes(Content context, String mode, User user, int count) {
		Criteria criteria = addOrgAuthCriterias(getSession().createCriteria(Quote.class));

		List<Quote> quoteList = new ArrayList<Quote>();

		if (mode.equals("shared")) {

			if (context != null) {
				criteria = criteria.add(Expression.in("context", findContentWithSameTaxonomies(context)));
			}
			List<User> notebookOwners = new ArrayList<User>();

			if (notebookOwners.size() > 0) {
				criteria = criteria.add(Expression.or(Expression.eq(USER, user), Expression.in(USER, notebookOwners)));
			} else {
				criteria = criteria.add(Expression.eq(USER, user));
			}
			quoteList = criteria.add(Expression.eq("annotationType.name", AnnotationType.Type.NOTE.getType())).setMaxResults(count).addOrder(Order.desc("createdOn")).list();
		} else if (mode.equals("my") || (mode.equals("quote"))) {
			if (context != null) {
				criteria = criteria.add(Expression.in("context", findContentWithSameTaxonomies(context)));
			}
			String annotationTypes[] = { AnnotationType.Type.NOTE.getType(), AnnotationType.Type.QUOTE.getType() };
			quoteList = criteria.add(Expression.eq(USER, user)).add(Expression.in("annotationType.name", annotationTypes)).setMaxResults(count).addOrder(Order.desc("createdOn")).list();
		} 

		return quoteList;
	}

	@Override
	public List<Quote> findNotes(String tag, User user, int start, int stop) {
		Criteria criteria = addOrgAuthCriterias(getSession().createCriteria(Quote.class));
		List<Quote> quoteList = new ArrayList<Quote>();

		int count = stop - start;

		if (!(tag.equalsIgnoreCase("all") || tag.equalsIgnoreCase(NONE))) {
			criteria = criteria.add(Expression.eq("tagType.name", tag));
		}
		if (tag.equalsIgnoreCase(NONE)) {
			criteria = criteria.add(Expression.isNull("tagType"));
		}
		quoteList = criteria.add(Expression.eq(USER, user)).setFirstResult(start).setMaxResults(count).addOrder(Order.desc("createdOn")).list();

		return quoteList;
	}

	@Override
	public Integer findNotesCount(String tag, User user) {
		Criteria criteria = addOrgAuthCriterias(getSession().createCriteria(Quote.class));
		List<Quote> quoteList = new ArrayList<Quote>();

		if (!(tag.equalsIgnoreCase("all") || tag.equalsIgnoreCase(NONE))) {
			criteria = criteria.add(Expression.eq("tagType.name", tag));
		}

		if (tag.equalsIgnoreCase(NONE)) {
			criteria = criteria.add(Expression.isNull("tagType"));
		}
		quoteList = criteria.add(Expression.eq(USER, user)).list();
		return quoteList.size();
	}

	private List<Content> findContentWithSameTaxonomies(Content context) {
		Set<Code> associatedTaxonomy = context.getTaxonomySet();
		Iterator<Code> iter = associatedTaxonomy.iterator();
		List<Integer> topicCodes = new ArrayList<Integer>();
		while (iter.hasNext()) {
			Code code = iter.next();
			if (code.getDepth() == 4) {
				topicCodes.add(code.getCodeId());
				List<Code> childCodes = addOrgAuthCriterias(getSession().createCriteria(Code.class), "code.").add(Expression.eq("parentId", code.getCodeId())).list();
				for (Code cod : childCodes) {
					topicCodes.add(cod.getCodeId());
				}
			} else if (code.getDepth() == 5) {
				List<Code> siblings = addOrgAuthCriterias(getSession().createCriteria(Code.class), "code.").add(Expression.eq("parentId", code.getParentId())).list();
				for (Code cod : siblings) {
					topicCodes.add(cod.getCodeId());
				}
				topicCodes.add(code.getParentId());
			}
		}

		List<Content> contentList = null;
		if (topicCodes.size() > 0) {

			contentList = addOrgAuthCriterias(getSession().createCriteria(Content.class), "content.").createCriteria("taxonomySet").add(Restrictions.in("codeId", topicCodes)).list();
		} else {
			contentList = new ArrayList<Content>();
			contentList.add(context);
		}
		return contentList;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

}
