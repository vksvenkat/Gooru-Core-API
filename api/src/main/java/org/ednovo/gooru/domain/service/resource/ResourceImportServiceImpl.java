/////////////////////////////////////////////////////////////
// ResourceImportServiceImpl.java
// gooru-api
// Created by Gooru on 2015
// Copyright (c) 2015 Gooru. All rights reserved.
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
package org.ednovo.gooru.domain.service.resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserGroupSupport;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.domain.service.user.FileImporter;
import org.ednovo.gooru.security.OperationAuthorizer;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.apache.commons.lang.StringUtils;
import au.com.bytecode.opencsv.CSVReader;

import com.fasterxml.jackson.core.type.TypeReference;

@Service
public class ResourceImportServiceImpl extends FileImporter implements ResourceImportService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceImportServiceImpl.class);

	@Autowired
	private OperationAuthorizer operationAuthorizer;

	@Autowired
	private ResourceService resourceService;

	@Override
	public void createOrUpdateResource(String filename, User user) {
		final String mediaFileName = UserGroupSupport.getUserOrganizationNfsInternalPath() + Constants.UPLOADED_MEDIA_FOLDER + '/' + filename;
		List<String> keys = null;
		StringBuffer json = new StringBuffer();
		CSVReader csvReader = null;
		File file = null;
		try {
			file = new File(mediaFileName);
			csvReader = new CSVReader(new FileReader(file));
			String[] row = null;
			while ((row = csvReader.readNext()) != null) {
				if (keys == null) {
					keys = getJsonKeys(row);
				} else {
					String data = formInputJson(row, json, keys).toString();
					JSONObject jsonObj = requestData(generateJSONInput(data, UNDER_SCORE));
					String gooruOid = getValue(GOORU_OID, requestData(getValue(RESOURCE, jsonObj)));
					if (!(StringUtils.isBlank(gooruOid))) {
						this.getResourceService().updateResource(gooruOid, this.buildResourceFromInputParameters(getValue(RESOURCE, jsonObj)), getValue(RESOURCE_TAGS, jsonObj) == null ? null : buildResourceTags(getValue(RESOURCE_TAGS, jsonObj)), user);
					} else {
						this.getResourceService().createResource(this.getResourceService().buildResourceFromInputParameters(getValue(RESOURCE, jsonObj), user), (getValue(RESOURCE_TAGS, jsonObj) != null) ? buildResourceTags(getValue(RESOURCE_TAGS, jsonObj)) : null, user, true);
					}
					json.setLength(0);
				}
			}
		} catch (FileNotFoundException e) {
			throw new NotFoundException(generateErrorMessage(GL0056, FILE), GL0056);
		} catch (Exception e) {
			LOGGER.error(_ERROR, e);
		} finally {
			try {
				if (file.exists()) {
					csvReader.close();
					file.delete();
				}
			} catch (Exception e) {
				LOGGER.error(_ERROR, e);
			}
		}
	}

	private List<String> buildResourceTags(final String data) {
		return JsonDeserializer.deserialize(data, new TypeReference<List<String>>() {
		});
	}

	private Resource buildResourceFromInputParameters(final String data) {
		return JsonDeserializer.deserialize(data, Resource.class);
	}

	public ResourceService getResourceService() {
		return resourceService;
	}

	public boolean hasUnrestrictedContentAccess() {
		return getOperationAuthorizer().hasUnrestrictedContentAccess();
	}

	public OperationAuthorizer getOperationAuthorizer() {
		return operationAuthorizer;
	}

}
