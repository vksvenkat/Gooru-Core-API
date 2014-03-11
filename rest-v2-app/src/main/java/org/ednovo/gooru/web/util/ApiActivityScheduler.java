/*******************************************************************************
 * ApiActivityScheduler.java
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
 
package org.ednovo.gooru.web.util;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.ednovo.gooru.application.util.ConfigProperties;
import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.ApiActivity;
import org.ednovo.gooru.domain.service.TransactionBox;
import org.ednovo.gooru.domain.service.apitracker.ApiTrackerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ApiActivityScheduler extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(ApiActivityScheduler.class);
	
	private String schedulerEnabled;

	@Autowired
	private ApiTrackerService apiTrackerService;

	@Autowired
	private CacheManager cacheManager;
	
	@Autowired
	ConfigProperties configProperties;
	

	private Boolean enabled;

	public void refreshApiActivities() {
		updateApiActivities(false);
	}

	public void resetApiActivities() {
		updateApiActivities(true);
	}

	public synchronized void updateApiActivities(final boolean reset) {
		
		if(configProperties.getSchedulers().get("scheduler.apikeyActivity.enabled") != null && configProperties.getSchedulers().get("scheduler.apikeyActivity.enabled").equalsIgnoreCase("true")){
			schedulerEnabled = configProperties.getSchedulers().get("scheduler.apikeyActivity.enabled");
		}else {
			schedulerEnabled = "false";
		}

		if (schedulerEnabled.equalsIgnoreCase("true")) {

			logger.debug("Starting " + ((reset) ? "resetting" : "Updating") + " of apikey activities by scheduler.");

			new TransactionBox() {

				@Override
				public void execute() {

					try {

						Cache cache = cacheManager.getCache("apiTracker");
						Cache tempCache = cacheManager.getCache("tempTracker");

						for (ApiActivity apiActivity : apiTrackerService.listApiActivities()) {
							String apiKey = apiActivity.getApiKey().getKey();
							int limit = apiActivity.getApiKey().getLimit();
							if(cache != null){
							Element element = cache.get(apiKey);
							Element tempElement = tempCache.get(apiKey);
							ApiActivity activity = null;
							int count = 0;
							if (element != null && tempElement != null) {
								if (apiActivity.getApiKey().getLimit().equals(-1)) {
									continue;
								} else if (cache.remove(apiKey)) {
									activity = (ApiActivity) element.getValue();
									if (reset) {
										count = 0;
									} else {
										count = (apiActivity.getCount() + activity.getCount());
									}
								}
							}
							apiActivity.setCount(count);
							if ((activity != null || reset)) {
								apiTrackerService.saveApiActivity(apiActivity);
							}
							if (apiActivity.getCount() >= limit) {
								tempCache.remove(apiKey);
							} else if (apiActivity.getCount() < limit || apiActivity.getApiKey().getLimit().equals(-1)) {
								if (!apiActivity.getApiKey().getLimit().equals(-1)) {
									apiActivity.getApiKey().setLimit(limit - apiActivity.getCount());
								}
								apiActivity.setCount(0);
								cache.put(new Element(apiKey, apiActivity));
								if (tempElement == null) {
									tempCache.put(new Element(apiKey, Integer.valueOf(0)));
								}
							}
						}
					}		
					} catch (Exception exception) {
						logger.warn("Updating of apikey activity failed.", exception);
					}
					logger.debug("Completed " + ((reset) ? "resetting" : "Updating") + " of apikey activities by scheduler.");
				}
			};
		}
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		if (enabled == null) {
			this.enabled = false;
		} else {
			this.enabled = enabled;
		}
	}
}
