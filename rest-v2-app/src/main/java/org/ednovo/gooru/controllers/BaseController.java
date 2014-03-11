/*******************************************************************************
 * BaseController.java
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
 
package org.ednovo.gooru.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.ednovo.gooru.application.util.SerializerUtil;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserGroupSupport;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.security.OperationAuthorizer;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.type.TypeReference;

public class BaseController extends SerializerUtil implements ParameterProperties {

	@Autowired
	private OperationAuthorizer operationAuthorizer;
	
	protected static final String GOORU_SESSION_TOKEN = "gooru-session-token";

	public static final String ERROR_INCLUDE[] = { "*.fieldError", "*.errorCount", "*.code", "*.defaultMessage", "*.field", "*.objectName", "*.rejectedValue" };

	protected static final String BLACK_LIST_WORD_RESPONSE_MESSAGE = "Remember:This is a search engine for learning. To ensure a safe search experience, Gooru does not provide any results for the search term you entered.";

	protected User getUser(HttpServletRequest request) {
		return (User) request.getAttribute(Constants.USER);
	}

	public OperationAuthorizer getOperationAuthorizer() {
		return operationAuthorizer;
	}

	public void setOperationAuthorizer(OperationAuthorizer operationAuthorizer) {
		this.operationAuthorizer = operationAuthorizer;
	}

	public boolean hasUnrestrictedContentAccess() {
		return getOperationAuthorizer().hasUnrestrictedContentAccess();
	}

	public boolean hasPublishAccess() {
		return getOperationAuthorizer().hasPublishAccess();
	}

	public static String getValue(String key, JSONObject json) throws Exception {
		try {
			if (json.isNull(key)) {
				return null;
			}
			return json.getString(key);

		} catch (JSONException e) {
			throw new Exception(e.getMessage());
		}
	}

	public static JSONObject requestData(String data) throws Exception {

		return data != null ? new JSONObject(data) : null;
	}

	public static String[] getFields(String data) {
		List<String> fields = JsonDeserializer.deserialize(data, new TypeReference<List<String>>() {
		});
		return fields.toArray(new String[fields.size()]);
	}

	public boolean hasSubOrgPermission(String contentOrganizationId) {
		String[] subOrgUids = UserGroupSupport.getUserOrganizationUids();
		if (subOrgUids != null && subOrgUids.length > 0) {
			for (String userSuborganizationId : subOrgUids) {
				if (contentOrganizationId.equals(userSuborganizationId)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isMobileDevice(HttpServletRequest request) {
		if (request == null || request.getHeader(USER_AGENT) == null || request.getHeader(USER_AGENT).indexOf(MOBILE) == -1) {
			return false;
		}
		return true;
	}
}
