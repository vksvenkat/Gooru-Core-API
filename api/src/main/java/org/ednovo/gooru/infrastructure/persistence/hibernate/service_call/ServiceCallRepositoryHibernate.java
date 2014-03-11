/*
*ServiceCallRepositoryHibernate.java
* gooru-api
* Created by Gooru on 2014
* Copyright (c) 2014 Gooru. All rights reserved.
* http://www.goorulearning.org/
*      
* Permission is hereby granted, free of charge, to any 
* person obtaining a copy of this software and associated 
* documentation. Any one can use this software without any 
* restriction and can use without any limitation rights 
* like copy,modify,merge,publish,distribute,sub-license or 
* sell copies of the software.
* The seller can sell based on the following conditions:
* 
* The above copyright notice and this permission notice shall be   
* included in all copies or substantial portions of the Software. 
*
*  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY    
*  KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE  
*  WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR   
*  PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS 
*  OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
*  OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
*  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
*  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
*  THE SOFTWARE.
*/

package org.ednovo.gooru.infrastructure.persistence.hibernate.service_call;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.ModelAndView;

@Repository
public class ServiceCallRepositoryHibernate extends BaseRepositoryHibernate implements ServiceCallRepository {

	private static final String SEARCH_COLLECTION = "search collection";
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public Map<String, Integer[]> getClassplansIOwenSummary(String userId) {
		

		List<Map<String, Object>> lessonToCountsDBList = jdbcTemplate.queryForList("select view.lesson,view.count as view_count,IFNULL(copy.count,0) as copy_count,IFNULL(subscribe.count,0) as subscribe_count " + "from( " + "select lesson,count(*) as count "
				+ "from content c,learnguide l,service_call s " + "where c.user_uid=? " + "and c.content_id= l.content_id " + "and s.predicate='learning_guide.get_classplan' " + "and s.action = concat('/gooruapi/rest/classplan/',c.gooru_oid,'.json') " + "group by lesson "
				+ "order by count(*) desc " + ") view " + "left JOIN " + "( " + "select lesson,count(*) as count " + "from content c,learnguide l,service_call s " + "where c.user_id=? " + "and c.content_id= l.content_id " + "and s.predicate='learning_guide.copy_classplan' "
				+ "and s.action = concat('/gooruapi/rest/classplan/',c.gooru_oid,'/copy.json') " + "group by lesson " + "order by count(*) desc " + ") copy " + "on view.lesson=copy.lesson " + "left join " + "( " + "select lesson,count(*) as count " + "from content c,learnguide l,service_call s "
				+ "where c.content_id= l.content_id " + "and s.predicate='subscription.create' " + "and s.action = concat('/gooruapi/rest/content/',c.gooru_oid,'/subscription.json') " + "group by lesson " + "order by count(*) desc " + ") subscribe " + "on  view.lesson=subscribe.lesson",
				new Object[] { userId, userId });

		Map<String, Integer[]> labelAndCount = new HashMap<String, Integer[]>();

		for (Map<String, Object> labelAndCountDB : lessonToCountsDBList) {
			try {
				String label = (String) labelAndCountDB.get("lesson");
				int view_count = (int) (long) (Long) labelAndCountDB.get("view_count");
				int copy_count = (int) (long) (Long) labelAndCountDB.get("copy_count");
				int subscribe_count = (int) (long) (Long) labelAndCountDB.get("subscribe_count");
				labelAndCount.put(label, new Integer[] { (Integer) view_count, (Integer) copy_count, (Integer) subscribe_count });
			} catch (Exception e) {
				return null;
			}

		}
		return labelAndCount;
	}

	@Override
	public Integer[] getRegisterVsUnregister() {
		Integer[] registerVsUnregister = new Integer[2];
		registerVsUnregister[0] = jdbcTemplate.queryForInt("select count(distinct user_ip) unregister_users " + "from service_call " + "where user_id=-99 " + "and user_ip not in " + "( " + "select distinct user_ip " + "from service_call " + "where user_id<>-99 " + ")");
		registerVsUnregister[1] = jdbcTemplate.queryForInt("select count(user_id) " + "from user ");

		return registerVsUnregister;
	}

	@Override
	public List getNumberOfUserOverTime(String timeSpan) {
		return jdbcTemplate.queryForList("select min( date(request_time) ) as date, count(distinct user_id) as user_count " + "from service_call " + "group by " + timeSpan + "(request_time) " + "order by request_time");
	}

	private <K, V> Map<K, V> queryForMap(String sql, Object[] objs, String keyName, String valueName) {
		List<Map<String, Object>> dbRes = jdbcTemplate.queryForList(sql, objs);
		Map<K, V> map = new HashMap<K, V>();
		for (Map<String, Object> labelAndCountDB : dbRes) {
			try {
				K key = (K) labelAndCountDB.get(keyName);
				V value = (V) labelAndCountDB.get(valueName);
				map.put(key, value);
			} catch (Exception e) {
				return null;
			}
		}
		return map;
	}

	@Override
	public Map<String, Integer> getUserClassView(String userId) {
			List<Map<String, Object>> labelAndCountDBList = jdbcTemplate.queryForList("select pp.label,count(c.label) count " + "from content_classification cc, code c,code p,code pp,content cn,service_call s  " + "where c.parent_id=p.code_id " + "and p.parent_id=pp.code_id "
				+ "and c.code_id=cc.code_id and cc.content_id=cn.content_id " + "and s.predicate='learning_guide.get_classplan' " + "and s.user_uid=? and s.action = CONCAT('/gooruapi/rest/classplan/',cn.gooru_oid,'.json')" + "group by pp.label;", new Object[] { userId });

		Map<String, Integer> labelAndCount = new HashMap<String, Integer>();

		for (Map<String, Object> labelAndCountDB : labelAndCountDBList) {
			try {
				String label = (String) labelAndCountDB.get("label");
				int count = (int) (long) (Long) labelAndCountDB.get("count");
				labelAndCount.put(label, (Integer) count);
			} catch (Exception e) {
				return null;
			}

		}
		return labelAndCount;
	}

	@Override
	public Map<String, Long> getUserTimeSpend(Integer userId) {
		userId = 6;
		List<Map<String, Object>> service_call_list = jdbcTemplate.queryForList("select * from service_call where user_id=? order by id", new Object[] { userId });
		String[] states = new String[] { SEARCH_COLLECTION, "view collection", "search resource", "editing", "question board", "default" };
		Map<String, String> stateChangingPredicate = new HashMap<String, String>();
		stateChangingPredicate.put("learning_guide.search_classplans", SEARCH_COLLECTION);
		stateChangingPredicate.put("gooru.view_class_plans", SEARCH_COLLECTION);
		stateChangingPredicate.put("gooru.view_class_plans", SEARCH_COLLECTION);
		stateChangingPredicate.put("gooru.view_class_room", "view collection");
		stateChangingPredicate.put("resource_list", "search resource");

		Map<String, Long> timeSpend = new HashMap<String, Long>();
		for (String state : states) {
			timeSpend.put(state, 0L);
		}

		Timestamp LastActionTime = (Timestamp) service_call_list.get(0).get("request_date_time");
		long currentStateDuration = 0;
		String currentState = "default";

		for (Map<String, Object> service_call : service_call_list) {
			// add time left from previous action.
			Timestamp CurrentActionTime = (Timestamp) service_call.get("request_date_time");
			currentStateDuration += Math.min(CurrentActionTime.getTime() - LastActionTime.getTime(), 10 * 60 * 1000);
			LastActionTime = CurrentActionTime;

			// check if new state.
			String predicate = (String) service_call.get("predicate");
			String state = stateChangingPredicate.get(predicate);
			if (state != null && state != currentState) {
				timeSpend.put(currentState, timeSpend.get(currentState) + currentStateDuration);
				currentStateDuration = 0;
				currentState = state;
			}
		}

		return timeSpend;
	}

	public void save(HttpServletRequest request, HttpServletResponse response, ModelAndView modelAndView, String serviceName) {

		Object res = request.getAttribute("predicate");
		if (res == null || "skip".equals(res)) {
			return;
		}

		// retrive all service_call data:
		User user = (User) request.getAttribute(Constants.USER);
		Integer userId = 0;
		if (user != null) {
			userId = user.getUserId();
		}
		Timestamp requestTime = new Timestamp((Long) request.getAttribute("startTime"));
		Timestamp responseTime = new Timestamp(System.currentTimeMillis());
		long executeTime = responseTime.getTime() - requestTime.getTime();

		String httpMethod = request.getMethod();
		String action = request.getRequestURI();
		String query = request.getQueryString();
		String userIp = request.getRemoteAddr();
		String requestParameters = getRequestParamtersString(request);
		String predicate = getPredicate(request);

		// write service_call data to db:
		jdbcTemplate.update("insert into service_call " + "(predicate,user_id,user_ip,request_time,action,execute_time," + "request_parameters,query,service_name,http_method,response_time) " + "values(?,?,?,?,?,?,?,?,?,?,?)", new Object[] { predicate, userId, userIp, requestTime, action,
				executeTime, requestParameters, query, serviceName, httpMethod, responseTime });

		// write service_call paramters and related content to db:
		int serviceCallDbId = jdbcTemplate.queryForInt("select LAST_INSERT_ID();");
		WriteParameters(request, serviceCallDbId);
	}

	

	private String getPredicate(HttpServletRequest request) {
		Object res = request.getAttribute("predicate");
		if (res == null) {
			res = "Na";
		}
		return (String) res;
	}

	private void WriteParameters(HttpServletRequest request, final int ServiceCallDbId) {
		Map map = request.getParameterMap();
		Iterator iter = map.entrySet().iterator();

		final List<String> names = new ArrayList<String>();
		final List<String> values = new ArrayList<String>();

		// fill names and values:
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String name = (String) entry.getKey();
			String[] valuesForName = (String[]) entry.getValue();
			for (String value : valuesForName) {
				if (value.length() > 1000) {
					value = value.substring(value.length() - 1000);
				}
				names.add(name);
				values.add(value);
			}
		}

		// batch update:
		jdbcTemplate.batchUpdate("insert into service_call_request_parameter(service_call_id,name,value) values (?,?,?)", new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setInt(1, ServiceCallDbId);
				ps.setString(2, names.get(i));
				ps.setString(3, values.get(i));
			}

			public int getBatchSize() {
				return names.size();
			}
		});
	}

	private String getRequestParamtersString(HttpServletRequest request) {
		String res = "";
		Map map = request.getParameterMap();
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String name = (String) entry.getKey();
			String[] values = (String[]) entry.getValue();
			for (String value : values) {
				if (value != null && value.length() > 300) {
					// Vijay: If number of characters in the parameter is more
					// than 300 characters , skip logging
					// FIXME: This needs to be revisited to make the whole
					// service call insertion more efficient.
					value = "~~VALUE_IGNORED~~";
				}
				res += "&" + name + "=" + value;
			}
		}
		return res;
	}


	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

}
