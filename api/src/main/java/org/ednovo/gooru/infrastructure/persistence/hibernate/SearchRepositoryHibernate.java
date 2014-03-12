/////////////////////////////////////////////////////////////
// SearchRepositoryHibernate.java
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

import java.util.ArrayList;
import java.util.List;

import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.SearchQuery;
import org.ednovo.gooru.core.api.model.User;
import org.hibernate.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Repository
public class SearchRepositoryHibernate extends BaseRepositoryHibernate implements SearchRepository{

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static Logger logger = LoggerFactory.getLogger(SearchRepositoryHibernate.class);
	
	@Override
	public void saveQueryAndResults(String userQuery, 
			final List<Resource> resources,
			User user, 
			String searchType, 
			long searchTime){
		
		try{
			jdbcTemplate.update("insert into search_query (user_query, user_id, search_type, query_time_msec, user_uid) values(?,?,?,?,?)",
					new Object[]{userQuery,user.getUserId(),searchType, searchTime, user.getPartyUid()});
		
		}catch (Exception e) {
			logger.error("Cant save the search resuls " + userQuery ,e);
		}
	}
	
	@Override
	public void updateSearchChoice() {
		// update choice that was chosen.
		jdbcTemplate.update(
				"update search_query q,search_result sr,service_call s,content c "+
				"set sr.selection_time=s.request_date_time "+  
				"where s.predicate='subscription.create' "+
				"and q.user_id = s.user_id "+
				"and q.query_id = sr.query_id "+
				"and sr.content_id =c.content_id "+
				"and q.query_time < s.request_date_time "+
				"and s.action = concat('/gooruapi/rest/content/',c.gooru_oid,'/subscription.json') ");
		// update choice that wasn't chosen (those that are above something that was chosen).
		jdbcTemplate.update(
		"update search_query q,search_result sr,search_result gsr,resource c,resource gc "+
		"set sr.was_skipped = true "+ 
		"where q.query_id=sr.query_id "+
		"and sr.content_id = c.content_id "+
		"and gsr.content_id = gc.content_id "+
		"and gsr.selection_time<>0 "+
		"and sr.selection_time=0 "+
		"and gsr.query_id=sr.query_id "+
		"and gsr.rank > sr.rank "+
		"and c.type_name=gc.type_name; ");
		//update number of subscriberes:
		jdbcTemplate.update("call update_number_of_subcribers()");
	}
	

	@Override
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void insertSearchResult(String query, User user, String resourceType,String searchType, long timeInMills, String ipAddress, int count) {
		String sql = "INSERT INTO search_query (search_query_uid, user_query, user_id, resource_type, search_type, query_time_msec, user_ip_address, result_count, user_uid ) values(uuid(), ?,?,?,?,?,?,?,?)";
		jdbcTemplate.update(sql, new Object[]{query,user.getUserId(),resourceType,searchType, timeInMills,ipAddress, count, user.getPartyUid()});
	}
	
	@Override
	public  List<SearchQuery> findAllDistinctSearchQuery() {
		String sql = "select resource_type, user_query, count(1) as resultCount from search_query group by resource_type, user_query having resultCount > 20";
		SQLQuery query = getSession().createSQLQuery(sql);
		List<SearchQuery> results = new ArrayList<SearchQuery>();
		try {
			List<Object[]> rawResults = query.list();
			for(Object[] row : rawResults) {
			    SearchQuery searchQuery = new SearchQuery();
			    //searchQuery.setResourceType((String)row[0]);
			    searchQuery.setQuery((String)row[1]);
			    
			    Integer count = 0;
			    count = Integer.parseInt(String.valueOf(row[2]));
			    searchQuery.setResultCount(count);
			    results.add(searchQuery);
			}
		} catch (Exception e) {
			logger.error("search query aggregation failed", e);
		}
		return results; 
	}
}
