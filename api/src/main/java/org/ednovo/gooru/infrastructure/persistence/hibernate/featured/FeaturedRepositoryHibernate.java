/////////////////////////////////////////////////////////////
// FeaturedRepositoryHibernate.java
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
package org.ednovo.gooru.infrastructure.persistence.hibernate.featured;

import java.util.List;

import org.ednovo.gooru.core.api.model.FeaturedSet;
import org.ednovo.gooru.core.api.model.FeaturedSetItems;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

@Repository
public class FeaturedRepositoryHibernate extends BaseRepositoryHibernate implements FeaturedRepository {

	@Override
	public List<FeaturedSet> getFeaturedList(Integer codeId, int limit, String featuredSetName, String themeCode, String themeType) {
		String hql = "FROM FeaturedSet featuredSet WHERE " +generateOrgAuthQueryWithData("featuredSet.");
		if (codeId != null) {
			hql += " AND featuredSet.subjectCode.codeId = '" + codeId + "'";
		}
		if (featuredSetName != null && featuredSetName.length() > 1) {
			hql += " AND featuredSet.name = '" + featuredSetName + "' ";
		}
		if (themeType != null) { 
			if (themeType.contains(",")) {
				themeType = themeType.replace(",", "','");
			}
			hql += " AND featuredSet.type.value in ('" + themeType + "')";
		}
		if (themeCode != null) {
			hql += " AND featuredSet.themeCode= '" + themeCode + "'";
		} else { 
			hql += " AND featuredSet.activeFlag = 1";
		}
		hql += " ORDER BY featuredSet.sequence";
        
		Session session = getSession();
	
		return session.createQuery(hql).setMaxResults(limit).list();
	}
	
	@Override
	public List<FeaturedSet> getFeaturedTheme(int limit) {
		String hql = "SELECT themeCode FROM FeaturedSet featuredSet WHERE " + generateOrgAuthQueryWithData("featuredSet.");
		return getSession().createQuery(hql).setMaxResults(limit).list();
	}

	@Override
	public List<Integer> getFeaturedThemeIds() {
		String hql = "SELECT distinct(fs.subjectCode.codeId) AS themeId FROM FeaturedSet fs  WHERE fs.activeFlag = 1 AND  " + generateOrgAuthQueryWithData("fs.");
		return getSession().createQuery(hql).list();
	}
	
	@Override
	public FeaturedSet getFeaturedSetById(Integer featuredSetId) {
		String hql = "FROM FeaturedSet featuredSet WHERE featuredSet.featuredSetId=:featuredSetId AND " + generateOrgAuthQuery("featuredSet.");
		Session session = getSession();
		Query query = session.createQuery(hql);
		query.setParameter("featuredSetId", featuredSetId);
		addOrgAuthParameters(query);
		List<FeaturedSet> featuredSetList = (List<FeaturedSet>) query.list();
		return featuredSetList.size() > 0 ? featuredSetList.get(0) : null;
	}

	
	public FeaturedSetItems getFeaturedSetItem(Integer featuredSetId, Integer sequence) {
		String hql = "FROM FeaturedSetItems featuredSetItems JOIN  WHERE featuredSetItems.featuredSet.featuredSetId=:featuredSetId  AND featuredSetItems.sequence=:sequence AND " + generateOrgAuthQuery("featuredSetItems.featuredSet.");
		Session session = getSession();
		Query query = session.createQuery(hql);
		query.setParameter("featuredSetId", featuredSetId);
		query.setParameter("sequence", sequence);
		addOrgAuthParameters(query);
		List<FeaturedSetItems> featuredSetItemList = (List<FeaturedSetItems>) query.list();
		return featuredSetItemList.size() > 0 ? featuredSetItemList.get(0) : null;
	}
	
	@Override
	public FeaturedSet getFeaturedSetByThemeNameAndCode(String name,String themeCode) {
		String hql = "FROM FeaturedSet featuredSet WHERE featuredSet.name=:name AND featuredSet.themeCode=:themeCode AND " + generateOrgAuthQuery("featuredSet.");
		Session session = getSession();
		Query query = session.createQuery(hql);
		query.setParameter("name", name);
		query.setParameter("themeCode", themeCode);
		addOrgAuthParameters(query);
		List<FeaturedSet> featuredSetList = (List<FeaturedSet>) query.list();
		return featuredSetList.size() > 0 ? featuredSetList.get(0) : null;
	}
	
	
	@Override
	public FeaturedSetItems getFeaturedSetItemsByFeatureSetId(Integer featureSetId) {
			String hql = "FROM FeaturedSetItems featuredSetItems  WHERE featuredSetItems.featureSetId=:featureSetId AND " + generateOrgAuthQuery("featuredSetItems.featuredSet.");
			Session session = getSession();
			Query query = session.createQuery(hql);
			query.setParameter("featureSetId", featureSetId);
			addOrgAuthParameters(query);
			List<FeaturedSetItems> featuredSetItemList = (List<FeaturedSetItems>) query.list();
			return featuredSetItemList.size() > 0 ? featuredSetItemList.get(0) : null;
	}

	@Override
	public FeaturedSetItems getFeaturedItemByIdAndType(Integer featuredSetItemId, String type) {
		String hql = "FROM FeaturedSetItems featuredSetItems  WHERE featuredSetItems.featuredSetItemId=:featuredSetItemId AND  featuredSetItems.featuredSet.type.value =:type AND " + generateOrgAuthQuery("featuredSetItems.featuredSet.");
		Session session = getSession();
		Query query = session.createQuery(hql);
		query.setParameter("type", type);
		query.setParameter("featuredSetItemId", featuredSetItemId);
		addOrgAuthParameters(query);
		List<FeaturedSetItems> featuredSetList = (List<FeaturedSetItems>) query.list();
		return featuredSetList.size() > 0 ? featuredSetList.get(0) : null;
	}

	@Override
	public List<Object[]> getLibrary(String code, boolean fetchAll, String libraryName) {
		String sql = "select subject_code_id, featured_set_id, ct.value from featured_set f inner join custom_table_value ct on f.type_id = ct.custom_table_value_id   where theme_code =:themeCode";
		if (!fetchAll)  {
			sql += " and subject_code_id =:code";
		}
		Query query = getSession().createSQLQuery(sql);
		if (!fetchAll) {
			query.setParameter("code", code);
		}
		query.setParameter("themeCode", libraryName);
		return query.list();
	}
	
	@Override
	public List<Object[]> getLibraryCollection(String codeId, String featuredSetId, Integer limit, Integer offset, Boolean skipPagination) {
		String sql = "select gooru_oid, r.title   from featured_set_items fi inner join resource r on r.content_id = fi.content_id inner join content cc on cc.content_id = r.content_id where  fi.featured_set_id=:featuredSetId ";
		if (codeId != null) {
			sql += " and fi.code_id =:codeId ";
		}
		Query query = getSession().createSQLQuery(sql);
		if (codeId != null) {
			query.setParameter("codeId", codeId);
		}
		query.setParameter("featuredSetId", featuredSetId);
		if (!skipPagination) {
			query.setFirstResult(offset);
			query.setMaxResults(limit);
		}
		return query.list();
	}
	
	@Override
	public Integer getFeaturedSetId(String type) {
		String sql = "select featured_set_id as featuredSetId from featured_set f inner join custom_table_value ct on f.type_id = ct.custom_table_value_id   where theme_code = 'library' and ct.value ='"+type+"'";
		Query query = getSession().createSQLQuery(sql).addScalar("featuredSetId", StandardBasicTypes.INTEGER);
		return query.list().size() > 0 ? (Integer)query.list().get(0) : null;
	}
	

}
