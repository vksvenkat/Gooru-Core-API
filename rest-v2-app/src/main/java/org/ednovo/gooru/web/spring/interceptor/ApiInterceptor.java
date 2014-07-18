/////////////////////////////////////////////////////////////
//ApiInterceptor.java
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.ednovo.gooru.core.api.model.ApiActivity;
import org.ednovo.gooru.core.api.model.UserToken;
import org.ednovo.gooru.domain.service.apitracker.ApiTrackerService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class ApiInterceptor extends HandlerInterceptorAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApiInterceptor.class);

	@Autowired
	private ApiTrackerService apiTrackerService;

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private UserTokenRepository userTokenRepository;

	public void init() {
		try {
			Cache apiTrackerCache = new Cache("apiTracker", 25000, false, true, 600, 600);
			Cache tempCache = new Cache("tempTracker", 25000, false, true, 600, 600);
			cacheManager.addCache(apiTrackerCache);
			cacheManager.addCache(tempCache);
			for (ApiActivity apiActivity : apiTrackerService.listApiActivities()) {
				if (!apiActivity.getApiKey().getLimit().equals(-1)) {
					apiActivity.getApiKey().setLimit(apiActivity.getApiKey().getLimit() - apiActivity.getCount());
				}
				apiActivity.setCount(0);
				String key = apiActivity.getApiKey().getKey();
				Element element = new Element(key, apiActivity);
				Element tempElement = new Element(key, new Integer(0));
				tempCache.put(tempElement);
				apiTrackerCache.put(element);
			}
		} catch (Exception exception) {
			LOGGER.info("Creation of apikey activity failed.");
		}
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String sessionToken = (String) request.getParameter("sessionToken");
		UserToken userToken = userTokenRepository.findByToken(sessionToken);
		String key = null;
		if (userToken != null && userToken.getApiKey() != null) {
			key = userToken.getApiKey().getKey();
			Cache cache = cacheManager.getCache("apiTracker");
			Cache tempCache = cacheManager.getCache("tempTracker");

			if (cache != null) {

				Element element = cache.get(key);
				Element tempElement = tempCache.get(key);
				Integer tempValue;
				if (element != null) {
					ApiActivity cacheActivity = (ApiActivity) (element.getValue());
					if (cacheActivity.getApiKey().getLimit().equals(-1)) {
						return true;
					}
					if (cache.remove(key)) {
						tempValue = (Integer) tempElement.getValue();
						if (tempValue < 0) {
							tempValue = 0;
						}
						if (cacheActivity.getApiKey().getLimit() >= (cacheActivity.getCount() + tempValue)) {
							cacheActivity.setCount(cacheActivity.getCount() + 1 + tempValue);
							if (tempValue > 0) {
								tempValue = 0;
								tempCache.put(new Element(tempElement.getKey(), tempValue));
							}
						} else {
							LOGGER.info("Apikey : " + key + "Has reached its limit");
							response.sendError(503, "Throttled");
						}
						cache.put(new Element(element.getKey(), cacheActivity));
					} else if (tempElement != null) {
						tempValue = (Integer) tempElement.getValue();
						tempValue++;
						tempCache.put(new Element(tempElement.getKey(), tempValue));
					} else {
						LOGGER.info("Apikey : " + key + "is not valid");
						response.sendError(503, "Throttled");
					}
				}
			} else {
				LOGGER.info("Failed Interceptor Validation : No sessionToken or ApiKey found in Request");
			}
		}
		return true;

	}
}
