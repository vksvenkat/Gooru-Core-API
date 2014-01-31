/////////////////////////////////////////////////////////////
// ConfigProperties.java
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
package org.ednovo.gooru.application.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;

@Component
public final class ConfigProperties implements Serializable,ConfigConstants {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8151007026627388928L;

	private static Map<String, String> authSSO;
	
	private static Map<String, String> tomCat;
	
	public static Map<String, String> schedulers;
	
	public static Map<String, String> gooruApp;
	
	public static Map<String, String> classplanRepository;
	
	public static Map<String, String> logSettings;
	
	public static Map<String, String> scribdAPI;
	
	public static Map<String, String> taxonomyRepositoryPath;
	
	public static Map<String, String> googleAnalyticsAccountId;
	
	public static Map<String, Map<String, String>> wsFedSSO;
	
	@Autowired
	private SettingService settingService;
	
	private static final Logger logger = LoggerFactory.getLogger(ConfigProperties.class);
	
	@PostConstruct
	public void init() { 
		String authSSOData = settingService.getConfigSetting(AUTHSSO_CONFIG, TaxonomyUtil.GOORU_ORG_UID);
		
		String tomCatData = settingService.getConfigSetting(TOMCAT_CONFIG, TaxonomyUtil.GOORU_ORG_UID);
		
		String schedulersData = settingService.getConfigSetting(SCHEDULERS_CONFIG, TaxonomyUtil.GOORU_ORG_UID);
		
		String gooruAppData = settingService.getConfigSetting(GOORU_APP, TaxonomyUtil.GOORU_ORG_UID);
		
		String classplanRepositoryData = settingService.getConfigSetting(CLASSPLAN_REPOSITORY, TaxonomyUtil.GOORU_ORG_UID);
		
		String logSettingsData = settingService.getConfigSetting(LOG_SETTINGS_CONFIG, TaxonomyUtil.GOORU_ORG_UID);
		
		String scribdAPIData = settingService.getConfigSetting(SCRIBD_API_CONFIG, TaxonomyUtil.GOORU_ORG_UID);
		
		String taxonomyRepositoryPathData = settingService.getConfigSetting(TAXONOMY_REPOSITORY_CONFIG, TaxonomyUtil.GOORU_ORG_UID);
		
		String googleAnalyticsAccountIdData = settingService.getConfigSetting(GOOGLE_ANALYTICS_CONFIG, TaxonomyUtil.GOORU_ORG_UID);
		
		try {
			wsFedSSO = settingService.getWsfedOrganizationSettings(WSFEDSSO_CONFIG, null);
		} catch (Exception e) {
			logger.info("Failed to initialize wsFedSSO" + e.getMessage());
			wsFedSSO = new HashMap<String, Map<String,String>>();
		}
		
		try {
			authSSO = JsonDeserializer.deserialize(authSSOData, new TypeReference<Map<String, String>>() {});
		} catch(Exception e) {
			logger.info("Failed to initialize authSSO" + e.getMessage());
			authSSO = new HashMap<String, String>();
		}
		try{
			tomCat = JsonDeserializer.deserialize(tomCatData, new TypeReference<Map<String, String>>() {});
		}catch(Exception e){
			logger.info("Failed to initialize tomCat" + e.getMessage());
			tomCat = new HashMap<String, String>();
		}
		try{
			schedulers = JsonDeserializer.deserialize(schedulersData, new TypeReference<Map<String, String>>() {});
		}catch(Exception e){
			logger.info("Failed to initialize schedulers" + e.getMessage());
			schedulers = new HashMap<String, String>();
		}
		try{
			gooruApp = JsonDeserializer.deserialize(gooruAppData, new TypeReference<Map<String, String>>() {});		
		}catch(Exception e){
			logger.info("Failed to initialize gooruApp" + e.getMessage());
			gooruApp = new HashMap<String, String>();
		}
		try{
			classplanRepository = JsonDeserializer.deserialize(classplanRepositoryData, new TypeReference<Map<String, String>>() {});
		}catch(Exception e){
			logger.info("Failed to initialize classplanRepository" + e.getMessage());
			classplanRepository = new HashMap<String, String>();
		}
		try{
			logSettings = JsonDeserializer.deserialize(logSettingsData, new TypeReference<Map<String, String>>() {});
		}catch(Exception e){
			logger.info("Failed to initialize logSettings" + e.getMessage());
			logSettings = new HashMap<String, String>();
		}
		try{
			scribdAPI = JsonDeserializer.deserialize(scribdAPIData, new TypeReference<Map<String, String>>() {});
		}catch(Exception e){
			logger.info("Failed to initialize" + e.getMessage());
			scribdAPI = new HashMap<String, String>();
		}
		try{
			taxonomyRepositoryPath = JsonDeserializer.deserialize(taxonomyRepositoryPathData, new TypeReference<Map<String, String>>() {});
		}catch(Exception e){
			logger.info("Failed to initialize" + e.getMessage());
			taxonomyRepositoryPath = new HashMap<String, String>();
		}
		try{
			googleAnalyticsAccountId = JsonDeserializer.deserialize(googleAnalyticsAccountIdData, new TypeReference<Map<String, String>>() {});
		}catch(Exception e){
			logger.info("Failed to initialize" + e.getMessage());
			googleAnalyticsAccountId = new HashMap<String, String>();
		}
		
	}

	
/*	public static void main (String [] args){
		String json  = "{\"wsfed-config\":{\"gooru-wsfed\": {\"issuer\":\"https://signin.silverbacklearning.net/issue/wsfed\",\"thumbprint\":\"2F2275E98D56E0F078E34F8C20E0E633FFA5DD4B\",\"friendlyname\":\"My Identity Provider\",\"audienceUris\":\"http://www.goorulearning.org/\",\"realm\":\"http://www.goorulearning.org/\",\"enableManualRedirect\":\"false\"},\"sbl-wsfed\":{\"issuer\":\"https://signin.silverbacklearning.net/issue/wsfed\",\"thumbprint\":\"2F2275E98D56E0F078E34F8C20E0E633FFA5DD4B\",\"friendlyname\":\"My Identity Provider\",\"audienceUris\":\"http://www.goorulearning.org/\",\"realm\":\"http://www.goorulearning.org/\",\"enableManualRedirect\":\"false\"},\"mile-wsfed\": {\"issuer\":\"https://mile.silverbacklearning.net/issue/wsfed\",\"thumbprint\":\"2F2275E98D56E0F078E34F8C20E0E633FFA5DD4B\",\"friendlyname\":\"My Identity Provider\",\"audienceUris\":\"http://mile.goorulearning.org/\",\"realm\":\"http://www.goorulearning.org/\",\"enableManualRedirect\":\"false\"}}}";
		Map<String, Map<String, Map<String,String>>> data = JsonDeserializer.deserialize(json, new TypeReference<Map<String, Map<String, Map<String,String>>>>(){});
		System.out.println(data.get("wsfed-config").get("mile-wsfed").get("issuer"));
	}
*/	public Map<String, String> getGoogleAnalyticsAccountId() {
		return googleAnalyticsAccountId;
	}
	
	public Map<String, String> getTaxonomyRepositoryPath() {
		return taxonomyRepositoryPath;
	}
	
	public Map<String, String> getScribdAPI() {
		return scribdAPI;
	}
	
	public Map<String, String> getLogSettings(){
		return logSettings;
	}
	
	public Map<String, String> getClassplanRepository(){
		return classplanRepository;
	}
	
	public Map<String, String> getGooruApp(){
		return gooruApp;
	}
	
	public Map<String,String> getSchedulers(){
		return schedulers;
	}
	
	public Map<String, String> getTomCat() {
		return tomCat;
	}

	public Map<String, String> getAuthSSO() {
		return authSSO;
	}

	public Map<String, Map<String, String>> getWsFedSSO() {
		return wsFedSSO;
	}
	
}
