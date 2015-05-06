package org.ednovo.gooru.domain.service.resource;

import java.io.FileReader;
import java.sql.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.ednovo.gooru.core.api.model.ContentType;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserGroupSupport;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.domain.service.user.FileImporter;
import org.ednovo.gooru.security.OperationAuthorizer;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.com.bytecode.opencsv.CSVReader;

import com.fasterxml.jackson.core.type.TypeReference;

@Service
public class ResourceImportServiceImpl extends FileImporter implements ResourceImportService{

	@Autowired
	private OperationAuthorizer operationAuthorizer;

	@Autowired
	private ResourceService resourceService;

	@Override
	public void createResource(String filename, HttpServletRequest request) {
		final String mediaFileName = UserGroupSupport.getUserOrganizationNfsInternalPath() + Constants.UPLOADED_MEDIA_FOLDER + '/' + filename;
		List<String> keys = null;
		StringBuffer json = new StringBuffer();
		CSVReader csvReader;
		try {
			csvReader = new CSVReader(new FileReader(mediaFileName));
			String[] row = null;
			while ((row = csvReader.readNext()) != null) {
				if (keys == null) {
					keys = getJsonKeys(row);
				} else {
					String data = formInputJson(row, json, keys).toString();
					JSONObject jsonObj = requestData(generateJSONInput(data, UNDER_SCORE));
					final User user = (User) request.getAttribute(Constants.USER);
					this.getResourceService().createResource(this.buildResourceFromInputParameters(getValue(RESOURCE, jsonObj), user), (getValue(RESOURCE_TAGS, jsonObj)!=null)? buildResourceTags(getValue(RESOURCE_TAGS, jsonObj)):null, user, true);
					json.setLength(0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private Resource buildResourceFromInputParameters(final String data, final User user) {
		final Resource resource = JsonDeserializer.deserialize(data, Resource.class);
		resource.setGooruOid(UUID.randomUUID().toString());
		final ContentType contentType = getResourceService().getContentType(ContentType.RESOURCE);
		resource.setContentType(contentType);
		resource.setLastModified(new Date(System.currentTimeMillis()));
		resource.setCreatedOn(new Date(System.currentTimeMillis()));
		if (!hasUnrestrictedContentAccess()) {
			resource.setSharing(Sharing.PUBLIC.getSharing());
		} else {
			resource.setSharing(resource.getSharing() != null && (resource.getSharing().equalsIgnoreCase(Sharing.PRIVATE.getSharing()) || resource.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing()) || resource.getSharing().equalsIgnoreCase(Sharing.ANYONEWITHLINK.getSharing())) ? resource
			        .getSharing() : Sharing.PUBLIC.getSharing());
		}
		resource.setUser(user);
		resource.setOrganization(user.getPrimaryOrganization());
		resource.setCreator(user);
		resource.setDistinguish(Short.valueOf("0"));
		resource.setRecordSource(NOT_ADDED);
		resource.setIsFeatured(0);
		resource.setLastUpdatedUserUid(user.getGooruUId());

		return resource;
	}

	private List<String> buildResourceTags(final String data) {
		return JsonDeserializer.deserialize(data, new TypeReference<List<String>>() {
		});
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
