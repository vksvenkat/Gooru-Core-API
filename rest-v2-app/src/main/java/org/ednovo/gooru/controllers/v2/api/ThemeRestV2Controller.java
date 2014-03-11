/*******************************************************************************
 * ThemeRestV2Controller.java
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
 
package org.ednovo.gooru.controllers.v2.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.FeaturedSet;
import org.ednovo.gooru.core.api.model.FeaturedSetItems;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.GooruOperationConstants;
import org.ednovo.gooru.core.security.AuthorizeOperations;
import org.ednovo.gooru.domain.service.featured.FeaturedService;
import org.ednovo.gooru.domain.service.redis.RedisService;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = { "/v2/theme" })
public class ThemeRestV2Controller extends BaseController implements ConstantProperties {

	@Autowired
	private FeaturedService featuredService;

	@Autowired
	private RedisService redisService;

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_READ })
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.GET, value = "/{type}")
	public ModelAndView getFeaturedContent(@PathVariable(value = TYPE) String type, HttpServletRequest request, @RequestParam(value = FEATURED_SET_NAME, required = false) String featuredSetName, @RequestParam(value = LIMIT_FIELD, defaultValue = "5") int limit,
			@RequestParam(value = RANDOM, defaultValue = FALSE) boolean random, @RequestParam(value = SKIP_CACHE, defaultValue = FALSE) boolean skipCache, @RequestParam(value = CLEAR_CACHE, defaultValue = FALSE) boolean clearCache,
			@RequestParam(value = THEME_CODE, required = false) String themeCode, HttpServletResponse response) throws Exception {
		String featuredList = null;
		final String cacheKey = "v2-" + type + "-theme";
		boolean fromCache = true;
		if (!skipCache && themeCode == null && !clearCache) {
			featuredList = (String) getRedisService().getValue(cacheKey);
		}
		if (featuredList == null || featuredList.length() == 0) {
			fromCache = false;
			List<FeaturedSet> featuredSet = this.getFeaturedService().getFeaturedList(limit, random, featuredSetName, themeCode, type);
			featuredList = serialize(featuredSet, RESPONSE_FORMAT_JSON, EXCLUDES_FEATURED_CONTENT, true, true, INCLUDES_THEME_LIST);
		}
		if ((featuredList != null) && ((!(fromCache || skipCache) && themeCode == null) || clearCache)) {
			getRedisService().putValue(cacheKey, featuredList, RedisService.DEFAULT_FEATURED_EXP);
		}

		return toModelAndView(featuredList);
	}

	@AuthorizeOperations(operations = { GooruOperationConstants.OPERATION_RESOURCE_UPDATE })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/{type}/{id}")
	public ModelAndView updateFeaturedContent(@PathVariable(value = TYPE) String type, @PathVariable(value = ID) Integer featuredSetItemId, @RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		getRedisService().deleteKey("v2-" + type + "-theme");

		return toModelAndView(serialize(this.getFeaturedService().updateFeaturedContent(type, featuredSetItemId, this.buildFeaturedSetItems(data)), RESPONSE_FORMAT_JSON, EXCLUDES_FEATURED_CONTENT, true, true, INCLUDES_THEME_LIST));
	}

	private FeaturedSetItems buildFeaturedSetItems(String data) {
		return JsonDeserializer.deserialize(data, FeaturedSetItems.class);
	}

	public void setFeaturedService(FeaturedService featuredService) {
		this.featuredService = featuredService;
	}

	public FeaturedService getFeaturedService() {
		return featuredService;
	}

	public RedisService getRedisService() {
		return redisService;
	}

}
