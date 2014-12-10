/////////////////////////////////////////////////////////////
// ApplicationService.java
// gooru-api
// Created by Gooru on 2014
// Copyright (c) 2014 Gooru. All rights reserved.
// http://www.goorulearning.org/
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
/////////////////////////////////////////////////////////////
package org.ednovo.gooru.domain.service.apikey;

import java.util.List;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Application;
import org.ednovo.gooru.core.api.model.ApplicationItem;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.domain.service.search.SearchResults;

public interface ApplicationService {

	ActionResponseDTO<Application> createApplication(Application application, User apicaller);

	Application updateApplication(Application newapplication, String apiKey);

	Application getApplication(String apiKey);

	SearchResults<Application> getApplications(User user, String organizationUid, String gooruUid, Integer limit, Integer offset);

	void deleteApplication(String apiKey);
	
	ApplicationItem getApplicationItem(String applicationItemId);
	
	ActionResponseDTO<ApplicationItem> createApplicationItem(ApplicationItem applicationItem,String apiKey, User apicaller);
	
	ActionResponseDTO<ApplicationItem> updateApplicationItem(ApplicationItem applicationItem, String applicationItemId, User apicaller)throws Exception ;
	
    List<ApplicationItem> getApplicationItemByApiKey(String apiKey) throws Exception ;
    
	void deleteApplicationByApikey(String apikey) throws Exception;
	
	void deleteApplicationItemByItemId(String applicationItemId) throws Exception;
    
}