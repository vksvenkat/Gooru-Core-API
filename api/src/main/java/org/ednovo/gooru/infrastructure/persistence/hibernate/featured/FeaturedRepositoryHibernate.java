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
	public List<Object[]> getLibraryCollectionsList(Integer limit, Integer offset, Boolean skipPagination, String themeCode, String themeType) {
		String sql = "select ct.gooru_oid, ct.created_on, ct.last_modified, ct.user_uid, ct.sharing, ct.last_updated_user_uid, cn.grade, cn.network, r.title, r.views_total, r.description, r.thumbnail, fs.theme_code, fs.subject_code_id from content ct inner join collection cn on (ct.content_id = cn.content_id) inner join resource r on (cn.content_id = r.content_id) inner join featured_set_items fsi on (r.content_id = fsi.content_id) inner join featured_set fs on (fsi.featured_set_id = fs.featured_set_id)";
		
		if(themeCode != null && themeType != null) {
			sql += " where fs.subject_code_id =:themeType and fs.theme_code =:themeCode";
		} else if (themeType != null)  {
			sql += " where fs.subject_code_id =:themeType";
		} else if (themeCode != null)  {
			sql += " where fs.theme_code =:themeCode";
		}
		Query query = getSession().createSQLQuery(sql);
		if (themeType != null) {
			query.setParameter("themeType", themeType);
		}
		if (themeCode != null)  {
			query.setParameter("themeCode", themeCode);
		}
		if (!skipPagination) {
			query.setFirstResult(offset);
			query.setMaxResults(limit);
		}
		return query.list();
	}
	
	@Override
	public List<Object[]> getLibraryCollectionsListByFilter(Integer limit, Integer offset, Boolean skipPagination, String themeCode, String themeType, String subjectId, String courseId, String unitId, String lessonId, String topicId) {
		
		String sql = "select distinct ct.gooru_oid, ct.created_on, ct.last_modified, ct.user_uid, ct.sharing, ct.last_updated_user_uid, f.theme_code, f.subject_code_id from code c inner join code u on u.parent_id = c.code_id inner join code t on t.parent_id = u.code_id inner join code l on l.parent_id = t.code_id inner join code cc on cc.parent_id = l.code_id inner join code s on s.code_id = cc.parent_id inner join featured_set_items fs inner join featured_set f on (f.featured_set_id = fs.featured_set_id and (fs.code_id = l.code_id or fs.code_id = cc.code_id or fs.code_id = t.code_id or fs.code_id = u.code_id or fs.code_id = s.code_id)) inner join content ct inner join collection cn on (ct.content_id = cn.content_id) inner join resource r on (cn.content_id = r.content_id and fs.content_id = r.content_id)";
		
		if(themeCode != null && themeType != null) {
			sql += " where f.subject_code_id ='" + themeType + "'" + " and f.theme_code ='" + themeCode + "'";
		} else if (themeType != null)  {
			sql += " where f.subject_code_id ='" + themeType + "'";
		} else if (themeCode != null)  {
			sql += " where f.theme_code ='" + themeCode + "'";
		}
		
		if (subjectId != null) {
		    sql += " and s.code_id ='" + subjectId + "'";
		}
		if (courseId != null) {
		    sql += " and cc.code_id ='" + courseId + "'";
		}
		if (unitId != null) {
		    sql += " and u.code_id ='" + unitId + "'";
		}
		if (lessonId != null) {
		    sql += " and l.code_id ='" + lessonId + "'";
		}
		if (topicId != null) {
		    sql += " and t.code_id ='" + topicId + "'";
		}
		
		Query query = getSession().createSQLQuery(sql);

		if (!skipPagination) {
			query.setFirstResult(offset);
			query.setMaxResults(limit);
		}
		return query.list();
	}
	
	@Override
	public Long getLibraryCollectionCount(String themeCode, String themeType) {
		String sql = "select count(1) as count from content ct inner join collection cn on (ct.content_id = cn.content_id) inner join resource r on (cn.content_id = r.content_id) inner join featured_set_items fsi on (r.content_id = fsi.content_id) inner join featured_set fs on (fsi.featured_set_id = fs.featured_set_id)";
		if(themeCode != null && themeType != null) {
			sql += " where fs.subject_code_id =:themeType and fs.theme_code =:themeCode";
		} else if (themeType != null)  {
			sql += " where fs.subject_code_id =:themeType";
		} else if (themeCode != null)  {
			sql += " where fs.theme_code =:themeCode";
		}
		Query query = getSession().createSQLQuery(sql).addScalar("count", StandardBasicTypes.LONG);
		if (themeType != null) {
			query.setParameter("themeType", themeType);
		}
		if (themeCode != null)  {
			query.setParameter("themeCode", themeCode);
		}
		return (Long)query.list().get(0);
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
	public List<Object[]> getLibraryCollection(String codeId, String featuredSetId, Integer limit, Integer offset, Boolean skipPagination, String contentId) {
		String sql = "select gooru_oid, r.title   from featured_set_items fi inner join resource r on r.content_id = fi.content_id inner join content cc on cc.content_id = r.content_id where  fi.featured_set_id=:featuredSetId ";
		if (codeId != null) {
			sql += " and fi.code_id =:codeId ";
		}
		if (contentId != null) { 
			sql += " and fi.content_id =:contentId ";
		}
		Query query = getSession().createSQLQuery(sql);
		if (codeId != null) {
			query.setParameter("codeId", codeId);
		}
		if (contentId != null) {
			query.setParameter("contentId", contentId);
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

	@Override
	public Long getLibraryResourceCount(String type, String libraryName) {
		String sql = "select count(*) as count from featured_set f inner join  featured_set_items fsi on fsi.featured_set_id = f.featured_set_id inner join content c on c.content_id = fsi.content_id inner join collection cc on c.content_id = cc.content_id inner join collection_item ci on ci.collection_content_id = cc.content_id inner join resource r on r.content_id = ci.resource_content_id inner join content con on con.content_id = r.content_id inner join resource_source rs on r.resource_source_id = rs.resource_source_id where f.theme_code =:themeCode ";
		if (type!= null)  {
			sql += " and f.subject_code_id =:type";
		}
		Query query = getSession().createSQLQuery(sql).addScalar("count", StandardBasicTypes.LONG);
		if (type != null) {
			query.setParameter("type", type);
		}
		query.setParameter("themeCode", libraryName);
		return (Long)query.list().get(0);
	}
	
	@Override
	public List<Object[]> getCommunityLibraryResource(String type, Integer offset, Integer limit, boolean skipPagination, String libraryName){	
		String sql = "select c.gooru_oid as collection_id, con.gooru_oid as resource_id," +
				     "r.title,r.folder,r.thumbnail, r.url, r.grade, r.description, r.category," +
				     "c.sharing, r.has_frame_breaker, r.record_source, r.license_name," +
				     "ci.narration, ci.start, ci.stop, ci.collection_item_id as collection_item_id," +
				     "r.resource_source_id, rs.source_name, rs.domain_name, rs.attribution," +
				     "f.subject_code_id as subject_code_id " +
				     "from featured_set f " +
				     "inner join  featured_set_items fsi on fsi.featured_set_id = f.featured_set_id " +
				     "inner join content c on c.content_id = fsi.content_id " +
				     "inner join collection cc on c.content_id = cc.content_id " +
				     "inner join collection_item ci on ci.collection_content_id = cc.content_id " +
				     "inner join resource r on r.content_id = ci.resource_content_id " +
				     "inner join content con on con.content_id = r.content_id " +
				     "inner join resource_source rs on r.resource_source_id = rs.resource_source_id " +
				     "where f.theme_code =:themeCode";
		
		if (type!= null)  {
			sql += " and f.subject_code_id =:type";
		}
		
		Query query = getSession().createSQLQuery(sql);
		
		if (!skipPagination) {
			query.setFirstResult(offset);
			query.setMaxResults(limit);
		}
		
		if (type != null) {
			query.setParameter("type", type);
		}
		query.setParameter("themeCode", libraryName);
		return query.list();
		
	}

	@Override
	public List<Object[]> getLibrary(String libraryName) {
		libraryName = libraryName.contains(",") ?  libraryName.replace(",", "','") : libraryName;
		String sql = "select featured_set_id, subject_code_id, theme_code from featured_set where theme_code in ("+libraryName + ")";
		Query query = getSession().createSQLQuery(sql);
		return query.list();
	}

}
