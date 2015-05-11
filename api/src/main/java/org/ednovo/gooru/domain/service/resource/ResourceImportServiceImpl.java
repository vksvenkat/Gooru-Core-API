package org.ednovo.gooru.domain.service.resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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

import au.com.bytecode.opencsv.CSVReader;

import com.fasterxml.jackson.core.type.TypeReference;

@Service
public class ResourceImportServiceImpl extends FileImporter implements ResourceImportService{

	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceImportServiceImpl.class);
	
	@Autowired
	private OperationAuthorizer operationAuthorizer;

	@Autowired
	private ResourceService resourceService;

	@Override
	public void createOrUpdateResource(String filename, HttpServletRequest request) {
		final String mediaFileName = UserGroupSupport.getUserOrganizationNfsInternalPath() + Constants.UPLOADED_MEDIA_FOLDER + '/' + filename;
		List<String> keys = null;
		StringBuffer json = new StringBuffer();
		CSVReader csvReader=null;
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
					final User user = (User) request.getAttribute(Constants.USER);
					String gooruOid = getValue(GOORU_OID, requestData(getValue(RESOURCE, jsonObj)));
					if(gooruOid != null && !gooruOid.isEmpty()){
						this.getResourceService().updateResource(gooruOid, this.buildResourceFromInputParameters(getValue(RESOURCE, jsonObj)), getValue(RESOURCE_TAGS, jsonObj) == null ? null : buildResourceTags(getValue(RESOURCE_TAGS, jsonObj)), user);
					}
					else{
						this.getResourceService().createResource(this.getResourceService().buildResourceFromInputParameters(getValue(RESOURCE, jsonObj), user), (getValue(RESOURCE_TAGS, jsonObj)!=null)? buildResourceTags(getValue(RESOURCE_TAGS, jsonObj)):null, user, true);
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
