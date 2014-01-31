/////////////////////////////////////////////////////////////
// ApiTrackerServiceImpl.java
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
package org.ednovo.gooru.domain.service.apitracker;

import java.util.List;

import org.ednovo.gooru.core.api.model.ApiActivity;
import org.ednovo.gooru.core.api.model.ApiKey;
import org.ednovo.gooru.infrastructure.persistence.hibernate.apitracker.ApiTrackerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApiTrackerServiceImpl implements ApiTrackerService {

	@Autowired
	private ApiTrackerRepository apiTrackerRepository;

	@Override
	public List<ApiKey> listApiKeys() {
		return apiTrackerRepository.getAll(ApiKey.class);
	}

	@Override
	public ApiActivity getApiActivity(int apiActivityId) {
		return (ApiActivity) apiTrackerRepository.get(ApiActivity.class, apiActivityId);
	}

	@Override
	public void saveApiActivity(ApiActivity apiActivity) {
		apiTrackerRepository.save(apiActivity);

	}

	@Override
	public ApiActivity getApiActivity(String apiKey) {
		return apiTrackerRepository.getApiActivity(apiKey);
	}

	@Override
	public ApiKey getApiKey(String key) {
		return apiTrackerRepository.getApiKey(key);
	}

	@Override
	public List<ApiActivity> listApiActivities() {
		return apiTrackerRepository.getAll(ApiActivity.class);
	}
	
	@Override
	public ApiKey findApiKeyByOrganization(String organizationUid){
		return apiTrackerRepository.getApiKeyByOrganization(organizationUid);
	}
}
