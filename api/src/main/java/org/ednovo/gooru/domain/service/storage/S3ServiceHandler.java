/////////////////////////////////////////////////////////////
// S3ServiceHandler.java
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
import java.io.FileNotFoundException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.lf5.util.StreamUtils;
import org.apache.xmlbeans.impl.piccolo.io.FileFormatException;
import org.ednovo.gooru.core.api.model.StorageAccount;
import org.ednovo.gooru.core.api.model.StorageArea;
//import org.ednovo.gooru.domain.model.storage.StorageAccount;
//import org.ednovo.gooru.domain.model.storage.StorageArea;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.acl.GroupGrantee;
import org.jets3t.service.acl.Permission;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.model.NotFoundException;

public class S3ServiceHandler {

	private static final String DOESNOT_EXIST = " doesn't exist";
	private final Logger logger = LoggerFactory.getLogger(S3ServiceHandler.class);

	protected RestS3Service initS3Service(StorageAccount storageAccount) throws Exception {

		AWSCredentials awsCredentials = new AWSCredentials(storageAccount.getAccessKey(), storageAccount.getAccessSecret());
		return new RestS3Service(awsCredentials);
	}

	protected void deleteSubKey(StorageArea storageArea, String subKey) throws Exception {
		RestS3Service restS3Service = initS3Service(storageArea.getStorageAccount());
		subKey = checkKey(false, subKey);
		S3Object[] s3Objects = restS3Service.listObjects(storageArea.getAreaName(), subKey, null);
		if (s3Objects != null) {
			for (S3Object s3Object : s3Objects) {
				restS3Service.deleteObject(storageArea.getAreaName(), s3Object.getKey());
			}
		}
	}

	protected void getOrCreateBucket(StorageAccount storageAccount, String bucket) throws Exception {
		RestS3Service restS3Service = initS3Service(storageAccount);
		S3Bucket s3Bucket = restS3Service.getOrCreateBucket(bucket);
		AccessControlList acl = s3Bucket.getAcl();
		acl = updateAccessControl(acl, true);
		if (acl == null) {
			return;
		}
		restS3Service.putBucketAcl(bucket, acl);
	}

	protected void deleteKey(StorageArea storageArea, String key) throws Exception {
		RestS3Service restS3Service = initS3Service(storageArea.getStorageAccount());
		key = checkKey(true, key);
		restS3Service.deleteObject(storageArea.getAreaName(), key);
	}

	protected byte[] downloadSignedGetUrl(StorageArea storageArea, String subFolder, String fileName) throws Exception {
		if (subFolder == null || fileName == null || fileName.length() < 2) {
			throw new NotFoundException("The specied Resource : " + storageArea.getAreaPath() + subFolder + fileName + DOESNOT_EXIST);
		}
		RestS3Service restS3Service = initS3Service(storageArea.getStorageAccount());
		subFolder = checkKey(true, subFolder);
		S3Object objectComplete = restS3Service.getObject(storageArea.getAreaName(), subFolder + fileName);

		return StreamUtils.getBytes(objectComplete.getDataInputStream());
	}

	protected String getSignedGetUrl(StorageArea storageArea, String subFolder, String fileName, int timeToExpireInSecs) throws Exception {
		if (subFolder == null || fileName == null || fileName.length() < 2) {
			throw new NotFoundException("The specied Resource : " + storageArea.getAreaPath() + subFolder + fileName + DOESNOT_EXIST);
		}
		RestS3Service restS3Service = initS3Service(storageArea.getStorageAccount());
		subFolder = checkKey(true, subFolder);
		long time = new Date().getTime() + (timeToExpireInSecs * 1000);
		return restS3Service.createSignedGetUrl(storageArea.getAreaName(), subFolder + fileName, new Date(time));
	}

	protected String checkKey(boolean isFullKey, String s3Key) {
		s3Key = s3Key.trim();
		if (s3Key.endsWith("//")) {
			s3Key = s3Key.substring(0, s3Key.length() - 1);
		} else if (!s3Key.endsWith("/") && !isFullKey) {
			s3Key = s3Key + "/";
		} else if (s3Key.startsWith("/")) {
			s3Key = s3Key.substring(1, s3Key.length());
		}
		return s3Key;
	}

	protected void upload(String repoPath, StorageArea storageArea, String s3Key, String... readAllStartPattern) throws Exception {

		File file = new File(repoPath + "/" + s3Key);
		upload(file, initS3Service(storageArea.getStorageAccount()), storageArea.getAreaName(), s3Key, true, readAllStartPattern);
	}

	protected void uploadFile(String filePath, StorageArea storageArea, String s3Key, String... readAllStartPattern) throws Exception {

		File file = new File(filePath);
		if (!file.exists() || file.isDirectory()) {
			throw new FileFormatException("The filePath is not a file : " + filePath);
		}

		upload(file, initS3Service(storageArea.getStorageAccount()), storageArea.getAreaName(), s3Key, true, readAllStartPattern);
	}

	protected void uploadFile(File file, RestS3Service restS3Service, String bucket, String subKey, boolean readAll) throws Exception {

		subKey = checkKey(false, subKey);

		if (!file.exists()) {
			throw new FileNotFoundException("File : " + file.getPath() + DOESNOT_EXIST);
		}
		S3Object fileObject = new S3Object(subKey + file.getName(), FileUtils.readFileToByteArray(file));
		fileObject = restS3Service.putObject(bucket, fileObject);
		setPublicACL(restS3Service, bucket, fileObject.getKey(), readAll);
	}

	private void setPublicACL(RestS3Service restS3Service, String bucket, String objectKey, boolean allowAccess) throws Exception {

		S3Object fileObject = restS3Service.getObject(bucket, objectKey);
		AccessControlList objectAcl = restS3Service.getObjectAcl(bucket, fileObject.getKey());
		objectAcl = updateAccessControl(objectAcl, allowAccess);
		if (objectAcl == null) {
			return;
		}
		fileObject.setAcl(objectAcl);
		restS3Service.putObject(bucket, fileObject);
	}

	private AccessControlList updateAccessControl(AccessControlList acl, boolean readAll) {
		if (readAll) {
			if (acl.hasGranteeAndPermission(GroupGrantee.ALL_USERS, Permission.PERMISSION_READ)) {
				return null;
			}
			acl.grantPermission(GroupGrantee.ALL_USERS, Permission.PERMISSION_READ);
		} else {
			if (!acl.hasGranteeAndPermission(GroupGrantee.ALL_USERS, Permission.PERMISSION_READ)) {
				return null;
			}
			acl.revokeAllPermissions(GroupGrantee.ALL_USERS);
		}
		return acl;
	}

	protected void upload(File file, RestS3Service restS3Service, String bucket, String s3Key, boolean keyHasParentDir, String... readAllPattern) throws Exception {

		if (!file.exists()) {
			logger.error("File : " + file.getPath() + DOESNOT_EXIST);
			return;
		}
		if (file.isFile()) {
			boolean open = false;
			if (!open && readAllPattern != null) {
				for (String pattern : readAllPattern) {
					if (pattern != null && pattern.length() > 1 && file.getName().contains(pattern)) {
						open = true;
						break;
					}
				}
			}
			uploadFile(file, restS3Service, bucket, s3Key, open);
		} else {
			final File[] childs = file.listFiles();
			for (File child : childs) {
				s3Key = checkKey(false, s3Key);
				upload(child, restS3Service, bucket, s3Key + (!keyHasParentDir ? file.getName() + "/" : ""), false, readAllPattern);
			}
		}
	}

	protected boolean isObjectExist(StorageArea storageArea, String subKey, String fileName) throws Exception {

		RestS3Service restS3Service = initS3Service(storageArea.getStorageAccount());
		subKey = checkKey(false, subKey);
		return restS3Service.isObjectInBucket(storageArea.getAreaName(), subKey + fileName);
	}

}
