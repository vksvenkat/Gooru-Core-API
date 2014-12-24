/////////////////////////////////////////////////////////////
// S3ResourceHandler.java
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
package org.ednovo.gooru.domain.service.storage;

import java.io.File;
import java.io.Serializable;

import org.apache.commons.io.FileUtils;
import org.ednovo.gooru.application.util.GooruImageUtil;
import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.Organization;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.UserGroupSupport;
import org.ednovo.gooru.core.application.util.RequestUtil;
import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.taxonomy.TaxonomyRespository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jets3t.service.S3ServiceException;
import org.json.JSONObject;
import org.restlet.data.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;


public abstract class S3ResourceHandler extends S3ServiceHandler implements ParameterProperties {

	@Autowired
	private TaxonomyRespository taxonomyRespository;
	
	@Autowired
	private SettingService settingService;

	private static final Logger logger = LoggerFactory.getLogger(S3ResourceHandler.class);

	protected TransactionStatus initTransaction(String name, boolean isReadOnly) {

		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setName(AUTHENTICATE_USER);
		if (isReadOnly) {
			def.setReadOnly(isReadOnly);
		} else {
			def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		}

		return getTransactionManager().getTransaction(def);

	}

	public void deleteResourceFolder(Resource resource) {
		try {
			if (resource.getS3UploadFlag() != null &&resource.getS3UploadFlag() == 1) {
				deleteSubKey(resource.getOrganization().getS3StorageArea(), resource.getFolder());
			}
		} catch (S3ServiceException ex) {
			logger.error("Deleting of resource :" + resource.getContentId() + " folder from S3 Failed : " + " : " + ex.getErrorMessage(), ex);
		} catch (Exception ex) {
			logger.error("Deleting of resource :" + resource.getContentId() + " folder from S3 Failed : ", ex);
		}
	}

	public void deleteResourceFile(Resource resource, String fileName) {
		if (fileName == null || fileName.length() < 2) {
			logger.error("Deleting of S3 file is failed : FileName cannot be null");
			return;
		}
		try {
			if (resource.getS3UploadFlag() != null && resource.getS3UploadFlag() == 1) {
				deleteKey(resource.getOrganization().getS3StorageArea(), resource.getFolder() + fileName);
			}
		} catch (S3ServiceException ex) {
			logger.error("Deleting of resource :" + resource.getContentId() + " file : " + fileName + " from S3 Failed : " + " : " + ex.getErrorMessage(), ex);
		} catch (Exception ex) {
			logger.error("Deleting of resource :" + resource.getContentId() + " file : " + fileName + " from S3 Failed : ", ex);
		}
	}
	
	public void moveFileToS3(String fileName, String sourcePath, String gooruContentId, String sessionToken) throws Exception {
		JSONObject data = new JSONObject();
		data.put("gooruBucket", settingService.getConfigSetting(ConfigConstants.RESOURCE_S3_BUCKET, 0, TaxonomyUtil.GOORU_ORG_UID));
		data.put("sourceFilePath", sourcePath);
		data.put("fileName",fileName );
		data.put("callBackUrl",settingService.getConfigSetting(ConfigConstants.GOORU_API_ENDPOINT, 0, TaxonomyUtil.GOORU_ORG_UID)+"v2/resource/"+gooruContentId+"?sessionToken="+sessionToken);
		RequestUtil.executeRestAPI(data.toString(), settingService.getConfigSetting(ConfigConstants.GOORU_CONVERSION_RESTPOINT,0, TaxonomyUtil.GOORU_ORG_UID) + "/conversion/image/upload", Method.POST.getName(), sessionToken);
	}

	public void uploadResourceFolder(Resource resource) {
		Integer s3UploadFlag = 0;
		try {
			Organization organization = updateOrganization(resource);
			upload(getRepoRealPath(), organization.getS3StorageArea(), resource.getFolder(), resource.getGooruOid(), GooruImageUtil.getFileNamePrefix(resource.getThumbnail()));
			// Hot Fix DO-812
			s3UploadFlag = 0;
		} catch (S3ServiceException ex) {
			logger.error("Uploading of resource :" + resource.getContentId() + " to S3 Failed : " + " : " + ex.getErrorMessage(), ex);
			s3UploadFlag = 0;
		} catch (Exception ex) {
			logger.error("Uploading of resource :" + resource.getContentId() + " to S3 Failed : ", ex);
			s3UploadFlag = 0;
		}
		resource.setS3UploadFlag(s3UploadFlag);
		save(resource);
	}

	public void uploadCodeFolder(Code code) {
		int s3UploadFlag = 1;
		try {
			upload(getRepoRealPath(), code.getOrganization().getS3StorageArea(), Constants.CODE_FOLDER + "/" + code.getCodeId() + "/", code.getCodeUid(), GooruImageUtil.getFileNamePrefix(code.getCodeImage()));
		} catch (S3ServiceException ex) {
			s3UploadFlag = 0;
			logger.error("Uploading of code :" + code.getCodeId() + " to S3 Failed : " + " : " + ex.getErrorMessage(), ex);
		} catch (Exception ex) {
			s3UploadFlag = 0;
			logger.error("Uploading of code :" + code.getCodeId() + " to S3 Failed : ", ex);
		}
		if (code.getS3UploadFlag() == 0 && s3UploadFlag == 1) {
			code.setS3UploadFlag(s3UploadFlag);
			save(code);
		}
	}

	public void uploadResourceFile(Resource resource, String fileName) {
		Integer s3UploadFlag = 0;
		try {
			Organization organization = updateOrganization(resource);

			String file = checkKey(false, getRepoRealPath());
			String subKey = resource.getFolder();
			file += checkKey(false, resource.getFolder());
			if (fileName.contains("/")) {
				subKey += fileName.substring(0, fileName.lastIndexOf("/") + 1);
			}
			file += checkKey(true, fileName);
			uploadFile(file, organization.getS3StorageArea(), subKey, resource.getGooruOid(), GooruImageUtil.getFileNamePrefix(resource.getThumbnail()));
			// Hot Fix DO-812
			s3UploadFlag = 0;
		} catch (S3ServiceException ex) {
			s3UploadFlag = 0;
			logger.error("Uploading of resource :" + resource.getContentId() + " to S3 Failed : " + " : " + ex.getErrorMessage(), ex);
		} catch (Exception ex) {
			s3UploadFlag = 0;
			logger.error("Uploading of resource :" + resource.getContentId() + " to S3 Failed : ", ex);
		}

		resource.setS3UploadFlag(s3UploadFlag);
		save(resource);
	}

	public Organization updateOrganization(Resource resource) {
		if (resource.getOrganization() != null) {
			return resource.getOrganization();
		} else {
			if (resource.getCreator().getOrganization() != null) {
				resource.setOrganization(resource.getCreator().getOrganization());
				save(resource);
			}
			return resource.getCreator().getOrganization();
		}
	}

	public void resetS3UploadFlag(Resource resource) {
		if (resource.getOrganization() == null) {
			resource.setOrganization(resource.getCreator().getOrganization());
			save(resource);
		}
		if (resource.getS3UploadFlag() != null && resource.getS3UploadFlag() == 1) {
			// resource.setS3UploadFlag(0);
			save(resource);
		}
	}

	public void resetS3UploadFlag(Code code) {
		if (code.getS3UploadFlag() == 1) {
			code.setS3UploadFlag(0);
			save(code);
		}
	}

	public void uploadCodeFolderWithNewSession(String codeUId) {
		TransactionStatus transactionStatus = null;
		Session session = null;
		try {
			transactionStatus = initTransaction(VALIDATE_CODE, false);
			session = getSessionFactory().openSession();
			uploadCodeFolder(codeUId);
			getTransactionManager().commit(transactionStatus);
		} catch (Exception ex) {
			logger.error("Uploading of code :" + codeUId + " folder to S3 Failed : ", ex);
			getTransactionManager().rollback(transactionStatus);
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public void uploadResourceFolderWithNewSession(String gooruContentOid) {
		TransactionStatus transactionStatus = null;
		Session session = null;
		try {
			transactionStatus = initTransaction(VALIDATE_RESOURCE, false);
			session = getSessionFactory().openSession();
			uploadResourceFolder(gooruContentOid);
			getTransactionManager().commit(transactionStatus);
		} catch (Exception ex) {
			logger.error("Uploading of resource :" + gooruContentOid + " folder to S3 Failed : ", ex);
			getTransactionManager().rollback(transactionStatus);
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public void uploadResourceThumbnailWithNewSession(String gooruContentOid) {
		TransactionStatus transactionStatus = null;
		Session session = null;
		try {
			transactionStatus = initTransaction(VALIDATE_RESOURCE, false);
			session = getSessionFactory().openSession();
			uploadResourceThumbnail(gooruContentOid);
			getTransactionManager().commit(transactionStatus);
		} catch (Exception ex) {
			logger.error("Uploading of resource thumbnail :" + gooruContentOid + " file to S3 Failed : ", ex);
			getTransactionManager().rollback(transactionStatus);
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public void uploadResourceFileWithNewSession(String gooruContentOid, String fileName) {
		TransactionStatus transactionStatus = null;
		Session session = null;
		try {
			transactionStatus = initTransaction(VALIDATE_RESOURCE, false);
			session = getSessionFactory().openSession();
			uploadResourceFile(gooruContentOid, fileName);
			getTransactionManager().commit(transactionStatus);
		} catch (Exception ex) {
			logger.error("Uploading of resource :" + gooruContentOid + " file to S3 Failed : ", ex);
			getTransactionManager().rollback(transactionStatus);
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}
	
	public void uploadResourceFileWithNewSession(Resource resource, String fileName) {
		try {
			uploadResourceFile(resource, fileName);
		} catch (Exception ex) {
			logger.error("Uploading of resource :" +  resource + " file to S3 Failed : ", ex);
		}
	}

	public void uploadCodeFolder(String codeUId) {
		try {
			Code code = this.taxonomyRespository.findCodeByCodeUId(codeUId);
			if (code == null) {
				logger.error("No code found with the codeId :" + codeUId + " for s3 upload process.");
				return;
			}
			if (code.getS3UploadFlag() == 0) {
				code.setS3UploadFlag(1);
				save(code);
			}
			uploadCodeFolder(code);
		} catch (Exception ex) {
			logger.error("Uploading of code :" + codeUId + " to S3 Failed : ", ex);
		}
	}

	public void uploadResourceFolder(String gooruContentOid) {
		Resource resource = getResource(gooruContentOid);
		try {
			if (resource == null) {
				logger.error("No Resource found with the gooruContentOid :" + gooruContentOid + " for s3 upload process.");
				return;
			}
			uploadResourceFolder(resource);
		} catch (Exception ex) {
			if (resource != null) {
				resource.setS3UploadFlag(0);
				save(resource);
			}
			logger.error("Uploading of resource :" + gooruContentOid + " to S3 Failed : ", ex);
		}
	}

	public void uploadResourceFile(String gooruContentOid, String fileName) {
		try {
			Resource resource = getResource(gooruContentOid);
			if (resource == null) {
				logger.error("No Resource found with the gooruContentOid :" + gooruContentOid + " for s3 upload process.");
				return;
			}
			uploadResourceFile(resource, fileName);
		} catch (Exception ex) {
			logger.error("Uploading of resource :" + gooruContentOid + " to S3 Failed : ", ex);
		}
	}

	public void uploadResourceThumbnail(String gooruContentOid) {
		try {
			Resource resource = getResource(gooruContentOid);
			if (resource == null || resource.getThumbnail() == null) {
				logger.error("No Resource found or no thumbnail with the Resource :" + gooruContentOid + " for s3 upload process.");
				return;
			}
			uploadResourceFile(resource, resource.getThumbnail());
		} catch (Exception ex) {
			logger.error("Uploading of resource :" + gooruContentOid + " to S3 Failed : ", ex);
		}
	}

	public void uploadResourceThumbnail(Resource resource) {
		try {
			if (resource == null || resource.getThumbnail() == null) {
				logger.error("No Resource found or no thumbnail with the Resource :" + resource.getGooruOid() + " for s3 upload process.");
				return;
			}
			uploadResourceFile(resource, resource.getThumbnail());
		} catch (Exception ex) {
			logger.error("Uploading of resource :" + resource.getGooruOid() + " to S3 Failed : ", ex);
		}
	}

	public byte[] downloadSignedResourceUrl(String resourceGooruOid, String file) throws Exception {
		Resource resource = getResource(resourceGooruOid);
		if (resource == null) {
			throw new NotFoundException("Resoruce : " + resourceGooruOid + " doesn't exist");
		}
		if (resource.getOrganization() != null) {
			if (resource.getS3UploadFlag() != null && resource.getS3UploadFlag() == 1) {
				logger.warn("downloadSignedGetUrl  Called");
				return downloadSignedGetUrl(resource.getOrganization().getS3StorageArea(), resource.getFolder(), file);
			} else {
				logger.warn("NFS Storage area  called");
				return FileUtils.readFileToByteArray(new File(resource.getOrganization().getNfsStorageArea().getInternalPath() + resource.getFolder() + file));
			}
		} else {
			return FileUtils.readFileToByteArray(new File(getRepoAppPath() + "/" + resource.getFolder() + file));
		}
	}

	public boolean isResourceInGooruS3(String resourceGooruOid) throws Exception {
		Resource resource = getResource(resourceGooruOid);
		if (resource == null) {
			throw new NotFoundException("Resource : " + resourceGooruOid + " doesn't exist");
		}
		if ((resource.getOrganization() != null) && (resource.getS3UploadFlag() != null  && resource.getS3UploadFlag() == 1)) {
			return true;
		}
		return false;
	}

	public String generateSignedResourceUrl(String resourceGooruOid, String file) throws Exception {
		return generateSignedResourceUrl(resourceGooruOid, file, false);
	}

	public String generateSignedResourceUrl(String resourceGooruOid, String file, boolean skipS3) throws Exception {
		Resource resource = getResource(resourceGooruOid);
		if (resource == null) {
			throw new NotFoundException("Resource : " + resourceGooruOid + " doesn't exist");
		}
		if (resource.getOrganization() != null) {
			if (!skipS3 && resource.getS3UploadFlag() == 1) {
				return getSignedGetUrl(resource.getOrganization().getS3StorageArea(), resource.getFolder(), file, 100);
			} else {
				return resource.getOrganization().getNfsStorageArea().getAreaPath() + resource.getFolder() + file.replace(" ", "%20");
			}
		} else {
			return getRepoAppPath() + "/" + resource.getFolder() + file.replace(" ", "%20");
		}
	}

	protected abstract String getConfigSetting(String key, String organizationUid);

	protected abstract Resource getResource(String gooruResourceId);

	protected abstract Object get(Class clazz, Serializable id);

	protected abstract void save(Object object);

	protected abstract String getRepoAppPath();

	protected abstract String getRepoRealPath();

	protected abstract SessionFactory getSessionFactory();

	protected abstract HibernateTransactionManager getTransactionManager();

}
