/*******************************************************************************
 * SessionContextSupport.java
 *  gooru-core
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
 *  
 *  The seller can sell based on the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be   
 *  included in all copies or substantial portions of the Software. 
 * 
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY    
 *   KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE  
 *   WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR   
 *   PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE       AUTHORS 
 *   OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
 *   OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *   OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 *   WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 *   THE SOFTWARE.
 ******************************************************************************/
package org.ednovo.gooru.core.api.model;

import java.util.Map;

import org.ednovo.gooru.core.api.model.GooruAuthenticationToken;
import org.ednovo.gooru.core.api.model.UserCredential;
import org.springframework.security.core.context.SecurityContextHolder;

public class SessionContextSupport {

	public static GooruAuthenticationToken getAuthentication() {
		try {
			return (GooruAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		} catch (Exception ex) {
			return null;
		}
	}

	public static UserCredential getUserCredential() {
		try {
			GooruAuthenticationToken authentication = getAuthentication();
			
			return authentication.getUserCredential();
		} catch (Exception ex) {
			return null;
		}
	}

	public static Map<String, Object> getLog() {
		try {
			return RequestSupport.getSessionContext().getLog();
		} catch (Exception ex) {
			return null;
		}
	}

	public static void putLogParameter(String field, Object value) {
		try {
			RequestSupport.getSessionContext().getLog().put(field, value);
		} catch (Exception ex) {
		}
	}

	public static String getSessionToken() {
		UserCredential credential = getUserCredential();
		if (credential != null) {
			return (String) credential.getToken();
		} else {
			return "NA";
		}
	}

}
