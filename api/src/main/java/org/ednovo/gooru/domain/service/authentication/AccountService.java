/*
*AccountService.java
* gooru-api
* Created by Gooru on 2014
* Copyright (c) 2014 Gooru. All rights reserved.
* http://www.goorulearning.org/
*      
* Permission is hereby granted, free of charge, to any 
* person obtaining a copy of this software and associated 
* documentation. Any one can use this software without any 
* restriction and can use without any limitation rights 
* like copy,modify,merge,publish,distribute,sub-license or 
* sell copies of the software.
* The seller can sell based on the following conditions:
* 
* The above copyright notice and this permission notice shall be   
* included in all copies or substantial portions of the Software. 
*
*  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY    
*  KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE  
*  WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR   
*  PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS 
*  OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
*  OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
*  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
*  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
*  THE SOFTWARE.
*/

package org.ednovo.gooru.domain.service.authentication;

import javax.servlet.http.HttpServletRequest;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserToken;


public interface AccountService {

	UserToken createSessionToken(User user, String apiKey, HttpServletRequest request) throws Exception;

	ActionResponseDTO<UserToken> logIn(String username, String password, String apiKey, boolean isSsoLogin, HttpServletRequest request) throws Exception;

	void logOut(String sessionToken);

	String getConfigSetting(String key, int securityLevel, String organizationUid);

	ActionResponseDTO<UserToken> loginAs(String sessionToken, String gooruUid, HttpServletRequest request, String apiKey) throws Exception;

	User userAuthentication(User newUser, String secretKey, String apiKey, String source, HttpServletRequest request);
	
	ActionResponseDTO<UserToken> switchSession(String sessionToken) throws Exception;

}
