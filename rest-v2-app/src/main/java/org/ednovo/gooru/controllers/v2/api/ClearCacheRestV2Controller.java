/////////////////////////////////////////////////////////////
//ClearCacheRestV2Controller.java
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
package org.ednovo.gooru.controllers.v2.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.domain.service.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = { "/v2/clearcache" })
public class ClearCacheRestV2Controller extends BaseController implements ConstantProperties {

	@Autowired
	private RedisService redisService;

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/{entity}", method = { RequestMethod.DELETE })
	public void clearCache(@PathVariable(value = ENTITY) String entity, @RequestParam(value = KEY, required = false, defaultValue = "*library-*") String key, HttpServletRequest request, HttpServletResponse response) throws Exception {
		this.getRedisService().bulkKeyDelete(key);
		SessionContextSupport.putLogParameter(EVENT_NAME, "clear-cache-" + entity + "-key-" + key);
	}
	
	public RedisService getRedisService() {
		return redisService;
	}

}
