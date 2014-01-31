/////////////////////////////////////////////////////////////
// SubscriptionServiceImpl.java
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
package org.ednovo.gooru.domain.service.subscription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.shelf.ShelfService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.annotation.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("subscriptionService")
public class SubscriptionServiceImpl implements SubscriptionService,ParameterProperties {
	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Autowired
	private UserRepository UserRepository;

	@Autowired
	private ShelfService shelfService;

	@Override
	public void deleteSubscription(String userId, String gooruContentId) {
		subscriptionRepository.deleteSubscription(userId, gooruContentId);
	}

	@Override
	public Integer countSubscriptionsForUserContent(User user) {
		return subscriptionRepository.countSubscriptionsForUserContent(user);
	}

	@Override
	public boolean hasUserSubscribedToUserContent(String userId, String gooruOid) {
		return subscriptionRepository.hasUserSubscribedToUserContent(userId, gooruOid);
	}

	@Override
	public List<HashMap<String, String>> findSubscribedUsers(String gooruContentId) {
		return subscriptionRepository.findSubscribedUsers(gooruContentId);
	}

	@Override
	public List<HashMap<String, String>> findSubscriptionsForUser(String gooruUid) {
		return subscriptionRepository.findSubscriptionsForUser(gooruUid);
	}

	@Override
	public Integer getSubscriptionCountForGooruOid(String contentGooruOid) {
		return subscriptionRepository.getSubscriptionCountForGooruOid(contentGooruOid);
	}

	@Override
	public List<HashMap<String, String>> subscriptionStatus(String userId, String gooruContentIds) {
		List<String> gooruContentList = Arrays.asList(gooruContentIds.split(","));
		User user = this.getUserRepository().findUserByPartyUid(userId);
		List<HashMap<String, String>> subscriptionStatusList = new ArrayList<HashMap<String, String>>();

		for (String contentId : gooruContentList) {
			HashMap<String, String> contentMap = new HashMap<String, String>();
			contentMap.put(GET_GOORU_OID, contentId);
			if (this.getShelfService().hasContentSubscribed(user, contentId)) {
				contentMap.put(IS_SUBSCRIBED, TRUE);
			} else {
				contentMap.put(IS_SUBSCRIBED, FALSE);
			}
			subscriptionStatusList.add(contentMap);
		}

		return subscriptionStatusList;
	}

	public ShelfService getShelfService() {
		return shelfService;
	}

	public UserRepository getUserRepository() {
		return UserRepository;
	}

}
