/////////////////////////////////////////////////////////////
// CustomValueServiceImpl.java
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

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.UserGroupSupport;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.cassandra.service.SearchSettingCassandraService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.ConfigSettingRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.customTable.CustomTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomValueServiceImpl extends BaseServiceImpl implements CustomValueService, ConstantProperties, ParameterProperties {

	@Autowired
	private CustomTableRepository customTableRepository;

	@Autowired
	private ConfigSettingRepository configSettingRepository;
	
	@Autowired
	private SearchSettingCassandraService searchSettingCassandraService;
	
	private static String profileName = "default";
	
	private static SecureRandom random = null;
	
	private static final Map<String, String> cassandraField = new HashMap<String, String>();
	
	static{
		cassandraField.put("search_filter_splitby_tilta", "filter-splitBy@approx");
		cassandraField.put("search_filter_lowercase", "filter-case@lowercase");
	}
	
	public CustomValueServiceImpl(){
		random = new SecureRandom();
	}

	@Override
	public List<CustomTableValue> getCustomValues(String type) {
		
	   return  this.getCustomTableRepository().getCustomTableValues(type);
	}

	@Override
	public void updateSearchSettings() {
		
		profileName = configSettingRepository.getConfigSetting("search.profile", UserGroupSupport.getUserOrganizationUid());
		if (profileName == null) {
			profileName = "default";
		}

		for (Map.Entry<String, String> entry : cassandraField.entrySet()) {
			List<CustomTableValue> customTableValues = this.getCustomTableRepository().getCustomTableValues(entry.getKey());
			StringBuilder values = new StringBuilder();
			for(CustomTableValue customTableValue : customTableValues){
				if(values.length() > 0){
					values.append(",");
				}
				values.append(customTableValue.getValue().trim());
			}
			if(values.toString().trim().length() > 0){
				String fieldValue = searchSettingCassandraService.read(entry.getValue(), profileName);
				if(!fieldValue.equalsIgnoreCase(values.toString())){
					searchSettingCassandraService.save(entry.getValue(), profileName, values.toString());
					searchSettingCassandraService.save("setting.version", profileName, getSettingVersion());
				}
			}
		}
	}

	public CustomTableRepository getCustomTableRepository() {
		return customTableRepository;
	}

	public static String getSettingVersion() {
	    return new BigInteger(130, random).toString(32);
	}
}
