/////////////////////////////////////////////////////////////
// SettingService.java
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
package org.ednovo.gooru.domain.service.setting;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.ednovo.gooru.core.api.model.Organization;
import org.ednovo.gooru.core.api.model.OrganizationSetting;
import org.ednovo.gooru.infrastructure.persistence.hibernate.ConfigSettingRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.OrganizationSettingRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.party.OrganizationRepository;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

@Service
public class SettingService {
	
	public static SettingService instance;

	private static final Map<String, String> configSettingValues = new HashMap<String, String>();
	
	private static final Map<String, String> organizationSettingValues = new HashMap<String, String>();
	


	@Autowired
	private ConfigSettingRepository configSettingRepository;

	@Autowired
	private OrganizationSettingRepository organizationSettingRepository;
	
	@Autowired
	private OrganizationRepository organizationRepository;
	
	@PostConstruct
	public void init() {
		instance = this;
	}

	public Map<String, String> getConfigSettings(String organizationUid) {
		return configSettingRepository.getConfigSettings(organizationUid);
	}

	public String getConfigSetting(String key, String organizationUid) {
		String value = configSettingValues.get(key + organizationUid);
		if (value == null) {
			value = configSettingRepository.getConfigSetting(key, 10, organizationUid);
			synchronized (configSettingValues) {
				configSettingValues.put(key + organizationUid, value);
			}
		}
		return value;
	}

	public String getConfigSetting(String key, int securityLevel, String organizationUid) {
		return configSettingRepository.getConfigSetting(key, securityLevel, organizationUid);
	}

	public Map<String, String> getOrganizationSettings(String organizationUid) {
		return organizationSettingRepository.getOrganizationSettings(organizationUid);
	}

	public String getOrganizationSetting(String key, String organizationUid) {
		String value = organizationSettingValues.get(key + organizationUid);
		if (value == null) {
			value = organizationSettingRepository.getOrganizationSetting(key, organizationUid);
			synchronized (organizationSettingValues) {
				organizationSettingValues.put(key + organizationUid, value);
			}
		}
		return value;
	}

	public Map<String, Map<String, String>> getWsfedOrganizationSettings(String configKey, String organizationCode) throws Exception{
		Map<String, Map<String,String>> orgSettingMap = new HashMap<String, Map<String,String>>();
		Organization organization = organizationRepository.getOrganizationByCode(organizationCode);
		if(organization != null){
			OrganizationSetting orgSetting = organizationSettingRepository.listOrgSetting(organization.getPartyUid(), configKey);
			Map<String,String> orgConfigKeyVal = JsonDeserializer.deserialize(orgSetting.getValue(), new TypeReference<Map<String, String>>() {});
			synchronized (orgSettingMap) {
				orgSettingMap.put(configKey + orgSetting.getOrganization().getOrganizationCode(), orgConfigKeyVal);
			}
		}
		
		return orgSettingMap;
	}
	
	public String getConfigSetting(String organizationName)
	{
	return	configSettingRepository.getConfigSetting(organizationName);
	}
	
	public synchronized void resetConfigSettings() {
		configSettingValues.clear();
	}

	public void resetOrganizationSettings() {
		organizationSettingValues.clear();
		
	}

	public void resetOrganizationSettings(String key){
		organizationSettingValues.remove(key);
	}
	
	public static SettingService getInstance() {
		return instance;
	}
	
	public void updateConfigSettingValue(String orgainzationUid, String key, String value){
		configSettingRepository.updateConfigSetting(orgainzationUid, key, value);
		resetConfigSettings();
	}
}
