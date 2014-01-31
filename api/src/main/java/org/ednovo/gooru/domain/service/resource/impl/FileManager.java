/////////////////////////////////////////////////////////////
// FileManager.java
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.ednovo.gooru.core.api.model.Learnguide;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.UserGroupSupport;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.resource.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.code.javascribd.connection.ScribdClient;
import com.google.code.javascribd.connection.StreamableData;
import com.google.code.javascribd.docs.Upload;
import com.google.code.javascribd.docs.UploadResponse;
import com.google.code.javascribd.type.Access;
import com.google.code.javascribd.type.ApiKey;
import com.google.code.javascribd.type.FileData;

@Service("resourceManager")
public class FileManager implements ResourceManager,ParameterProperties {

	private final Logger logger = LoggerFactory.getLogger(FileManager.class);

	@Override
	public void saveResource(Resource resource) {

		String resourceURI = getAbsoluteURI(resource.getFolder());

		if (resource.getResourceType() == null || ( !resource.getResourceType().getName().equals(ResourceType.Type.RESOURCE.getType())
				&& !resource.getResourceType().getName().equals(ResourceType.Type.VIDEO.getType()))) {
			this.addAsset(resourceURI, resource.getUrl(), resource.getFileData());
		}
	}

	@Override
	public Map<String, String> saveScridbDocument(String apiKey, String filePath) throws Exception {
		ScribdClient client = new ScribdClient();

		ApiKey apikey = new ApiKey(apiKey);
		File file = new File(filePath);

		StreamableData uploadData = new FileData(file);

		// initialize upload method
		Upload upload = new Upload(apikey, uploadData);
		upload.setDocType(PDF);
		upload.setAccess(Access.PRIVATE);
		// execute upload
		UploadResponse response = client.execute(upload);
		String docId = response.getDocId().toString();
		String key = response.getAccessKey();

		Map<String, String> scribDocDetailsList = new HashMap<String, String>();
		scribDocDetailsList.put(DOCUMENT__ID, docId);
		scribDocDetailsList.put(DOCUMENT_KEY, key);
		return scribDocDetailsList;
	}

	@Override
	public void deleteResource(Resource resource, String resourceURI) {

		String classPlanFolder = getAbsoluteURI(resource.getFolder());
		String resourceFolder = classPlanFolder + File.separator + resourceURI;

		this.deleteFilesfromDisk(resourceFolder);

		File file = new File(resourceFolder);
		if (file.exists()) {
			if (file.isDirectory()) {
				File[] f = file.listFiles();
				if (f.length > 0) {
					this.deleteFilesfromDisk(file.getPath());
				}
			}
			file.delete();
		}
	}

	@Override
	public void deleteClassplan(Learnguide classplan) {

		String classPlanFolder = getAbsoluteURI(classplan.getFolder());
		String resourceFolder = classPlanFolder + File.separator;

		this.deleteFilesfromDisk(resourceFolder);

		File file = new File(resourceFolder);
		if (file.exists()) {
			if (file.isDirectory()) {
				File[] f = file.listFiles();
				if (f.length > 0) {
					this.deleteFilesfromDisk(file.getPath());
				}
			}
			file.delete();
		}

	}

	private void deleteFilesfromDisk(String folderPath) {
		File file = new File(folderPath);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files.length > 0) {
				for (File f : files) {
					if (f.isDirectory()) {
						deleteFilesfromDisk(f.getPath());
						if (f.exists()) {
							f.delete();
						}
						continue;
					} else{
						f.delete();
					}
				}
			} else {
				file.delete();
			}
			if (file.exists()) {
				file.delete();
			}
		} else {
			file.delete();
		}
	}

	private void addAsset(String resourceURI, String fileName, byte[] fileData) {
		
		if(fileData == null || fileData.length == 0) {
			return;
		}

		logger.info("Adding asset: " + fileName + " with resourceURI: " + resourceURI);
		
		File dir = new File(resourceURI);
		if(!dir.exists()){
			dir.mkdirs();
		}

		File file = new File(resourceURI + fileName);

		try {
			OutputStream out = new FileOutputStream(file);
			out.write(fileData);
			out.close();
		} catch (IOException e) {
			throw new RuntimeException("Error while copying resource.", e);
		}
	}

	@Override
	public void copyResourceRepository(Resource sourceCollection, Resource targetCollection) throws Exception {
		File srcPath = new File(getAbsoluteURI(sourceCollection));
		File dstPath = new File(getAbsoluteURI(targetCollection));

		if (srcPath.exists()) {
			try {
				FileUtils.copyDirectory(srcPath, dstPath);
			} catch (IOException e) {
				throw new RuntimeException("Error while copying resource.", e);
			}
		}
	}

	private String getAbsoluteURI(Resource resource) {
		return resource.getOrganization().getNfsStorageArea().getInternalPath() + resource.getFolder() + "/";
	}
	
	private String getAbsoluteURI(String folder) {
		return UserGroupSupport.getUserOrganizationNfsInternalPath() + folder+"/";
	}

}
