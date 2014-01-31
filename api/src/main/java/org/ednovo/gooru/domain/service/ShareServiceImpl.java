/////////////////////////////////////////////////////////////
// ShareServiceImpl.java
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
package org.ednovo.gooru.domain.service;

import static com.rosaloves.bitlyj.Bitly.shorten;

import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.domain.service.redis.RedisService;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rosaloves.bitlyj.Bitly;
import com.rosaloves.bitlyj.Url;

@Service
public class ShareServiceImpl extends BaseServiceImpl implements ShareService,ParameterProperties {

	@Autowired
	private ResourceRepository resourceRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SettingService settingService;

	@Autowired
	private RedisService redisService;

	@Override
	public String getShortenUrl(String id, String fullUrl, Boolean clearCache) {
		Resource resource = this.getResourceRepository().findResourceByContentGooruId(id);
		String url = null;
		if (resource != null) {
			if (!clearCache) {
				url = (String) getRedisService().getValue((fullUrl + resource.getOrganization().getPartyUid()));
			}
			if (url == null) {
				Url bitly = Bitly.as(this.getSettingService().getConfigSetting(ConfigConstants.BITLY_USER_NAME, 0, resource.getOrganization().getPartyUid()), this.getSettingService().getConfigSetting(ConfigConstants.BITLY_APIKEY, 0, resource.getOrganization().getPartyUid())).call(shorten(fullUrl));
				url = bitly.getShortUrl();
				getRedisService().putValue((fullUrl + resource.getOrganization().getPartyUid()),  url,RedisService.DEFAULT_PROFILE_EXP);
			}
		} else {
			throw new NotFoundException(generateErrorMessage(GL0056, RESOURCE));
		}
		return url;
	}

	@Override
	public String getShortenUrl(String fullUrl, Boolean clearCache, User user) {
		String url = null;
		if (!clearCache) {
			url = (String) getRedisService().getValue(fullUrl);
		}
		if (url == null) {
			Url bitly = Bitly.as(this.getSettingService().getConfigSetting(ConfigConstants.BITLY_USER_NAME, 0, TaxonomyUtil.GOORU_ORG_UID), this.getSettingService().getConfigSetting(ConfigConstants.BITLY_APIKEY, 0, TaxonomyUtil.GOORU_ORG_UID)).call(shorten(fullUrl));
			url = bitly.getShortUrl();
			getRedisService().putValue((fullUrl), url, RedisService.DEFAULT_PROFILE_EXP);
		}
		return url;
	}

	public ResourceRepository getResourceRepository() {
		return resourceRepository;
	}

	public SettingService getSettingService() {
		return settingService;
	}

	public RedisService getRedisService() {
		return redisService;
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

}
