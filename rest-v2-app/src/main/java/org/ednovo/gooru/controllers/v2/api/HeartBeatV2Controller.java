/*******************************************************************************
 * HeartBeatV2Controller.java
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

import java.net.InetAddress;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ednovo.gooru.controllers.BaseController;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = { "/v2/heartbeat" })
public class HeartBeatV2Controller extends BaseController{

	@RequestMapping(value = { "/{code}" }, method = RequestMethod.GET)
	public void getHeartBeat(@PathVariable(value = "code") String eventId, HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		response.setStatus(HttpServletResponse.SC_OK);
		SessionContextSupport.putLogParameter("eventName", "heart-beat");
		SessionContextSupport.putLogParameter("eventId", eventId);
		SessionContextSupport.putLogParameter("IP Address", request.getRemoteAddr());
		SessionContextSupport.putLogParameter("Host Address", InetAddress.getLocalHost().getHostAddress());
		SessionContextSupport.putLogParameter("Host Name",InetAddress.getLocalHost().getHostName());
	}

}
