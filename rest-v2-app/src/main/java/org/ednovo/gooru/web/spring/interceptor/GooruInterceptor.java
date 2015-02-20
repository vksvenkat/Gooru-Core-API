/////////////////////////////////////////////////////////////
//GooruInterceptor.java
//rest-v2-app
// Created by Gooru on 2014
// Copyright (c) 2014 Gooru. All rights reserved.
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
package org.ednovo.gooru.web.spring.interceptor;

import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cassandra.cli.CliParser.newColumnFamily_return;
import org.ednovo.gooru.application.util.ConfigProperties;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.exception.BadRequestException;
import org.ednovo.gooru.infrastructure.messenger.IndexProcessor;
import org.ednovo.kafka.producer.KafkaEventHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import flexjson.JSONSerializer;

public class GooruInterceptor extends HandlerInterceptorAdapter {

	private Properties gooruConstants;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GooruInterceptor.class);
	
	private static final Logger ACTIVITY_LOGGER = LoggerFactory.getLogger("activityLog");
	
	private static final JSONSerializer SERIALIZER = new JSONSerializer();
	
	@Autowired
	private KafkaEventHandler kafkaService;
	
	@Autowired
	private ConfigProperties configProperties;
	
	@Autowired
	protected IndexProcessor indexProcessor;

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		Enumeration e = gooruConstants.propertyNames();
		
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			request.setAttribute(key, gooruConstants.getProperty(key));
		}
		
		Long startTime = System.currentTimeMillis();
		request.setAttribute("startTime", startTime);
		SessionContextSupport.putLogParameter("startTime", startTime);
		String eventUUID = UUID.randomUUID().toString();	
		response.setHeader("X-REQUEST-UUID", eventUUID);
		SessionContextSupport.putLogParameter("eventId", eventUUID);
		
		JSONObject payLoadObject = new JSONObject();
		payLoadObject.put("requestMethod", request.getMethod());
		SessionContextSupport.putLogParameter("payLoadObject", payLoadObject.toString());
		
		JSONObject context = new JSONObject();
		context.put("url", request.getRequestURI());
		if(request.getHeader("User-Agent") != null && request.getHeader("User-Agent").indexOf("Mobile") != -1) {
			context.put("clientSource", "mobile");
		} else {
			context.put("clientSource", "web");
		}
		SessionContextSupport.putLogParameter("context", context.toString());
		
		request.getHeader("VIA");
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) 
		{
			ipAddress = request.getRemoteAddr();
		}
		
		JSONObject user = new JSONObject();
		User party = (User) request.getAttribute(Constants.USER);
		if(party != null)
		{
			user.put("gooruUId",  party.getPartyUid());
		}
		
		user.put("userAgent",  request.getHeader("User-Agent"));
		user.put("userIp",  ipAddress);
		SessionContextSupport.putLogParameter("user", user.toString());
		
		JSONObject session = SessionContextSupport.getLog().get("session") != null ? new JSONObject(SessionContextSupport.getLog().get("session").toString()) :  new JSONObject();
		session.put("organizationUid", party.getOrganization().getOrganizationUid());
		session.put("sessionToken", request.getParameter("sessionToken"));
		session.put("apiKey", request.getParameter("apiKey"));
		SessionContextSupport.putLogParameter("session", session.toString());
		JSONObject version = new JSONObject();
		version.put("logApi", "0.1");
		SessionContextSupport.putLogParameter("version", version.toString());
		
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		
		// Read re-index request from session context and sent re-index request via Java HTTP client to index server
		try{
			indexProcessor.index(SessionContextSupport.getIndexMeta());
		} catch(Exception ex){
			LOGGER.error("Re-index API trigger failed " + ex);
		}
	    
		Long endTime = System.currentTimeMillis();
		SessionContextSupport.putLogParameter("endTime", endTime);
		Long startTime = SessionContextSupport.getLog() != null ? (Long)SessionContextSupport.getLog().get("startTime") : 0;
		Long totalTimeSpentInMs = endTime - startTime ;
		JSONObject metrics = new JSONObject();
		metrics.put("totalTimeSpentInMs", totalTimeSpentInMs);
		SessionContextSupport.putLogParameter("metrics", metrics.toString());	
		Map<String, Object> log = SessionContextSupport.getLog();
		String logString = SERIALIZER.deepSerialize(log);
		if (logString != null) {
			try {
				if (SessionContextSupport.getLog() != null && SessionContextSupport.getLog().get("eventName") != null) {
					kafkaService.sendEventLog(logString);
				}
			} catch(Exception e) {
				LOGGER.error("Error while pushing event log data to kafka : " + e.getMessage() );
				// Print to Activity Log only in case we had issues pushing to kafka.
				ACTIVITY_LOGGER.info(logString);
			}
		}
	}

	public Properties getGooruConstants() {
		return gooruConstants;
	}

	public void setGooruConstants(Properties gooruConstants) {
		this.gooruConstants = gooruConstants;
	}
	
	public static JSONObject requestData(String data)  {
		try {
			return data != null ? new JSONObject(data) : null;
		} catch (JSONException e) {
			throw new BadRequestException("Input JSON parse failed!");
		}
	}
}
