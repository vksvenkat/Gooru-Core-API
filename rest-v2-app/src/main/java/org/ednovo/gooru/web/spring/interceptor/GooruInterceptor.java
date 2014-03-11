/*******************************************************************************
 * GooruInterceptor.java
 *  gooru-v2-app
 *  Created by Gooru on 2014
 *  Copyright (c) 2014 Gooru. All rights reserved.
 *  http://www.goorulearning.org/
 *       
 *  Permission is hereby granted, free of charge, to any 
 *  person obtaining a copy of this software and associated 
 *  documentation. Any one can use this software without any 
 *  restriction and can use without any limitation rights 
 *  like copy,modify,merge,publish,distribute,sub-license or 
 *  sell copies of the software.
 *  The seller can sell based on the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be   
 *  included in all copies or substantial portions of the Software. 
 * 
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY    
 *   KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE  
 *   WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR   
 *   PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS 
 *   OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
 *   OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *   OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 *   WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 *   THE SOFTWARE.
 ******************************************************************************/
 
package org.ednovo.gooru.web.spring.interceptor;

import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ednovo.gooru.application.util.ConfigProperties;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.kafka.producer.KafkaEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import flexjson.JSONSerializer;

public class GooruInterceptor extends HandlerInterceptorAdapter {

	private Properties gooruConstants;
	
	private static final Logger logger = LoggerFactory.getLogger(GooruInterceptor.class);
	
	private static final JSONSerializer SERIALIZER = new JSONSerializer();
	
	@Autowired
	private KafkaEventHandler kafkaService;
	
	@Autowired
	ConfigProperties configProperties;

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		Enumeration e = gooruConstants.propertyNames();

		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			request.setAttribute(key, gooruConstants.getProperty(key));
		}
		
		Long startTime = System.currentTimeMillis();
		request.setAttribute("startTime", startTime);
		SessionContextSupport.putLogParameter("startTime", startTime);
		SessionContextSupport.putLogParameter("context", request.getPathInfo());
		SessionContextSupport.putLogParameter("userAgent", request.getHeader("User-Agent"));
		SessionContextSupport.putLogParameter("apiKey", configProperties.getLogSettings().get("log.api.key"));
		SessionContextSupport.putLogParameter("requestMethod", request.getMethod());
		
		String eventUUID = UUID.randomUUID().toString();		
		response.setHeader("X-REQUEST-UUID", eventUUID);
		SessionContextSupport.putLogParameter("eventId", eventUUID);
		
		request.getHeader("VIA");
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) 
		{
			ipAddress = request.getRemoteAddr();
		}
		
		SessionContextSupport.putLogParameter("userIp", ipAddress);		
		
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		SessionContextSupport.putLogParameter("endTime", System.currentTimeMillis());	
		User user = (User) request.getAttribute("USER");
		if(user != null)
		{
			SessionContextSupport.putLogParameter("userUid", request.getAttribute(user.getPartyUid()));
		}
		Map<String, Object> log = SessionContextSupport.getLog();
		String logString = SERIALIZER.deepSerialize(log);
		if (logString != null) {
			try {
				kafkaService.sendEventLog(logString);
			} catch(Exception e) {
				logger.error("Error while pushing event log data to kafka : " + e.getMessage() );
			}
		}
	}

	public Properties getGooruConstants() {
		return gooruConstants;
	}

	public void setGooruConstants(Properties gooruConstants) {
		this.gooruConstants = gooruConstants;
	}
}
