/*******************************************************************************
 * ClearCacheRestV2Controller.java
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

import java.util.Iterator;
import java.util.Set;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping(value = { "/v2/clearcache" })
public class ClearCacheRestV2Controller extends BaseController implements ConstantProperties {
	
	@Autowired
	private RedisService redisService;
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@RequestMapping(value = "/library", method = { RequestMethod.DELETE })
	public void claerLibraryCache(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Set<String> keys = this.getRedisService().getkeys("*library-*");
		if(keys.size() > 0) {
			Iterator<String> iterator = keys.iterator();
			while(iterator.hasNext()) {
				this.getRedisService().bulkKeyDelete(iterator.next());
			}
		}
		
		SessionContextSupport.putLogParameter(EVENT_NAME, "clear-library-cache");
	}

	public RedisService getRedisService() {
		return redisService;
	}
	

}
