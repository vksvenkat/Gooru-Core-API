/*
*ApiTrackerRepositoryHibernate.java
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

package org.ednovo.gooru.infrastructure.persistence.hibernate.apitracker;

import java.util.List;

import org.ednovo.gooru.core.api.model.ApiActivity;
import org.ednovo.gooru.core.api.model.ApiKey;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepositoryHibernate;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

@Repository
public class ApiTrackerRepositoryHibernate extends BaseRepositoryHibernate implements ApiTrackerRepository {

	@Override
	public ApiActivity getApiActivity(String apiKey) {
		String hql = "FROM ApiActivity activity WHERE activity.apiKey.key = '" + apiKey + "'";
		List<ApiActivity> list = find(hql);
		return (list == null || list.size() == 0) ? null : list.get(0);
	}

	@Override
	public ApiKey getApiKey(String key) {
		String hql = "FROM ApiKey apiKey WHERE apiKey.key = '" + key + "' AND apiKey.activeFlag=1";
		List<ApiKey> list = find(hql);
		return (list == null || list.size() == 0) ? null : list.get(0);
	}

	@Override
	public ApiKey getApiKeyByOrganization(String organizationUid) {
		int activeFlag= 1;
		String hql = "FROM ApiKey apiKey WHERE apiKey.organization.partyUid =:partyUid AND apiKey.activeFlag =:activeFlag";
		Query query = getSession().createQuery(hql);
		query.setParameter("partyUid", organizationUid);
		query.setParameter("activeFlag", activeFlag);
		List<ApiKey> list = query.list();
		return (list == null || list.size() == 0) ? null : list.get(0);
	}

}
