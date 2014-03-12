/////////////////////////////////////////////////////////////
// AbstractS3Manager.java
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
package org.ednovo.gooru.domain.service.resource.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.jets3t.service.ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.acl.GroupGrantee;
import org.jets3t.service.acl.Permission;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.model.StorageObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractS3Manager implements ParameterProperties {

	private final Logger logger = LoggerFactory.getLogger(AbstractS3Manager.class);

	@Autowired
	private SettingService settingService;

	public enum Type {

		PROFILE("s3.profileBucket");

		private String key;

		Type(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}

	}

	public BufferedReader download(String s3FilePath) throws Exception {

		S3Object objectComplete = getS3Service().getObject(getS3Bucket(), s3FilePath);

		return new BufferedReader(new InputStreamReader(objectComplete.getDataInputStream()));

	}

	public InputStream downloadAsStream(String s3FilePath) throws Exception {

		S3Object objectComplete = getS3Service().getObject(getS3Bucket(), s3FilePath);

		return objectComplete.getDataInputStream();

	}

	public Map<String, Date> listFolderFiles(String folderName) {
		try {
			// getS3Service().listObjects("gooru-content",
			// "CrawledContentForUpload/", "/");
			S3Object[] s3objects = getS3Service().listObjects(getS3Bucket(), folderName, "/");

			Map<String, Date> dropBoxfilesMap = new HashMap<String, Date>();
			// Thread.sleep(60000);

			for (S3Object s3Object : s3objects) {
				dropBoxfilesMap.put(s3Object.getName(), s3Object.getLastModifiedDate());
			}

			return dropBoxfilesMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void moveFileToDiffrentS3Folder(String srcObj, StorageObject destObj) {
		try {
			// getS3Service().moveObject("gooru-content",
			// "CrawledContentForUpload/20120702-Outreach.csv", "gooru-content",
			// new
			// StorageObject("CrawledContentForProcessing/20120702-Outreach.csv"),
			// false);
			getS3Service().moveObject(getS3Bucket(), srcObj, getS3Bucket(), destObj, false);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	public void deleteFile(Type type, String fileName) throws Exception {
		String folderInBucket = type.getKey();
		if (getS3Service().isObjectInBucket(getS3Bucket(), folderInBucket + fileName)) {
			getS3Service().deleteObject(getS3Bucket(), folderInBucket + fileName);
		}
	}

	public void uploadFile(Type type, byte[] fileData, String fileName) throws Exception {

		String folderInBucket = settingService.getConfigSetting(ConfigConstants.PROFILE_BUCKET, 0, TaxonomyUtil.GOORU_ORG_UID);

		if (folderInBucket != null) {

			S3Object fileObject = new S3Object(folderInBucket + fileName, fileData);
			fileObject = getS3Service().putObject(getS3Bucket(), fileObject);
			setPublicACL(folderInBucket + fileName);

		} else {
			logger.error("S3 settings are not configured ! ");
		}

	}

	public void uploadFolder(String folderInBucket, String folderPath) throws Exception {

		if (!(getS3Service().isObjectInBucket(getS3Bucket(), folderInBucket))) {
			S3Object fileObject = new S3Object(folderInBucket);
			fileObject = getS3Service().putObject(getS3Bucket(), fileObject);
			setPublicACL(folderInBucket);
		}

		File folder = new File(folderPath);
		traverse(folder, folderInBucket);
	}

	public void setPublicACL(String objectKey) throws Exception {
		// Retrieve the bucket's ACL and modify it to grant public access, ie
		// READ access to the ALL_USERS group.
		S3Object fileObject = getS3Service().getObject(getS3Bucket(), objectKey);
		AccessControlList objectAcl = getS3Service().getObjectAcl(getS3Bucket(), fileObject.getKey());
		objectAcl.grantPermission(GroupGrantee.ALL_USERS, Permission.PERMISSION_READ);
		fileObject.setAcl(objectAcl);
		getS3Service().putObject(getS3Bucket(), fileObject);
	}

	private void traverse(final File f, String folderInBucket) throws Exception {
		if (f.isDirectory()) {
			onDirectory(f);
			final File[] childs = f.listFiles();
			for (File child : childs) {
				traverse(child, folderInBucket);
			}
			return;
		}
		onFile(f, folderInBucket);
	}

	private static void onDirectory(final File d) {

	}

	private void onFile(final File f, String folderInBucket) throws Exception {

		S3Object fileObject = new S3Object(f);
		fileObject.setKey(folderInBucket + f.getName());
		fileObject.setContentType(getContentType(f));
		fileObject = getS3Service().putObject(getS3Bucket(), fileObject);
		setPublicACL(folderInBucket + f.getName());
	}

	public static String getFileName(final String fileName) {

		return fileName.substring(fileName.lastIndexOf("/") + 1, fileName.lastIndexOf("."));
	}

	public static String getFileFomrat(final String fileName) {

		return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
	}

	public static String getContentType(final File f) {

		String contentType = "";
		String ext = "";
		int dot = f.getAbsolutePath().lastIndexOf(".");
		if (dot != -1) {
			ext = f.getAbsolutePath().substring(dot + 1);
		}

		if (ext.equalsIgnoreCase(PNG)) {
			contentType = IMAGE_PNG;
		} else if (ext.equalsIgnoreCase(JPG)) {
			contentType = IMAGE_JPEG;
		} else if (ext.equalsIgnoreCase(JPEG)) {
			contentType = IMAGE_JPEG;
		} else if (ext.equalsIgnoreCase(GIF)) {
			contentType = IMAGE_GIF;
		} else if (ext.equalsIgnoreCase(PDF)) {
			contentType = APPLICATION_PDF;
		}

		return contentType;
	}

	public abstract RestS3Service getS3Service();

	public abstract String getS3Bucket();

	public abstract Properties getS3Constants();

}
