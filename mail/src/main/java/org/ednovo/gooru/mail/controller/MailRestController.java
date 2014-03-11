/*******************************************************************************
 * MailRestController.java
 *  gooru-mail
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
package org.ednovo.gooru.mail.controller;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ednovo.gooru.mail.domain.MailDO;
import org.ednovo.gooru.mail.handler.MailHandler;
import org.ednovo.gooru.mail.serializer.JsonDeserializer;
import org.ednovo.gooru.mail.serializer.MailSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MailRestController extends MailSerializer {
	
	@Autowired
	private MailHandler mailHandler;

	@RequestMapping(method = RequestMethod.POST, value = "/send-mail")
	public ModelAndView sendMail(HttpServletRequest request,@RequestBody String data,HttpServletResponse response) throws Exception {		
		MailDO mail = buildMailFromInputParameters(data);
		if (mail != null) {
			String[] to = mail.getRecipient().split(",");
			Address[] address = new Address[to.length];
			for (int i = 0; i < to.length; i++) {
				address[i] = new InternetAddress(to[i]);
			}
			mail.setAddress(address);
			mailHandler.sendMail(mail, (System.currentTimeMillis() + 1000 * 60 * 5));
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		return toModelAndView("Your message has been sent successfully");
	}
	
	private MailDO buildMailFromInputParameters(String json) {
		return JsonDeserializer.deserialize(json, MailDO.class);
	}

}

