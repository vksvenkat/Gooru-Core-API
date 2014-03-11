/*
*S3ResourceApiHandler.java
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

package org.ednovo.gooru.domain.service.storage;

import java.io.Serializable;

import org.ednovo.gooru.application.util.ResourceProcessor;
import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.ResourceRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Service;

@Service
public class S3ResourceApiHandler extends S3ResourceHandler {

	@javax.annotation.Resource(name="sessionFactory")
	private SessionFactory sessionFactory;

	@javax.annotation.Resource(name="transactionManager")
	private HibernateTransactionManager transactionManager;

	@Autowired
	private ResourceRepository resourceRepository;

	@Autowired
	private SettingService settingService;
	
	@Autowired 
	private ResourceProcessor resourceProcessor;

	@Override
	public String getRepoRealPath() {
		return settingService.getConfigSetting(ConfigConstants.CLASSPLAN_REPOSITORY_REALPATH,0 , TaxonomyUtil.GOORU_ORG_UID );
	}

	@Override
	public String getRepoAppPath() {
		return settingService.getConfigSetting(ConfigConstants.CLASSPLAN_REPOSITORY_APPPATH,0 , TaxonomyUtil.GOORU_ORG_UID );
	}

	@Override
	public HibernateTransactionManager getTransactionManager() {
		return transactionManager;
	}
	
	public void uploadS3Resource(Resource resource) {
		try {
			if (resource != null) {
				this.resetS3UploadFlag(resource);
				resourceProcessor.updateResourceToS3WithNewSession(resource.getGooruOid());
			}
		} catch (Exception e) {

		}
	}


	@Override
	protected String getConfigSetting(String key, String organizationUid) {
		return settingService.getConfigSetting(key, organizationUid);
	}

	@Override
	protected Resource getResource(String gooruResourceId) {
		return resourceRepository.findResourceByContentGooruId(gooruResourceId);
	}

	@Override
	protected Object get(Class clazz, Serializable id) {
		return resourceRepository.get(clazz, id);
	}

	@Override
	protected void save(Object object) {
		resourceRepository.save(object);
	}

	@Override
	protected SessionFactory getSessionFactory() {
		return sessionFactory;
	}

}
