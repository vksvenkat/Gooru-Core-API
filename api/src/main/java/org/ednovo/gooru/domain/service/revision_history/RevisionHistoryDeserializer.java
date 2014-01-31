/////////////////////////////////////////////////////////////
// RevisionHistoryDeserializer.java
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
/**
 * 
 */
package org.ednovo.gooru.domain.service.revision_history;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.ednovo.gooru.core.api.model.ContentPermission;
import org.ednovo.gooru.core.api.model.ContentType;
import org.ednovo.gooru.core.api.model.CustomTableValue;
import org.ednovo.gooru.core.api.model.License;
import org.ednovo.gooru.core.api.model.Organization;
import org.ednovo.gooru.core.api.model.Party;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceSource;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.Versionable;
import org.ednovo.gooru.core.application.util.CustomProperties;
import org.ednovo.gooru.domain.service.partner.CustomFieldsService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.BaseRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.content.ContentRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.customTable.CustomTableRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.party.OrganizationRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Search Team
 * 
 */
public abstract class RevisionHistoryDeserializer<T extends Versionable> implements ResourceSerializerConstants {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BaseRepository baseRepository;

	@Autowired
	private CustomFieldsService customFieldService;

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private ContentRepository contentRepository;

	@Autowired
	private CustomTableRepository customTableRepository;

	protected static final Logger logger = LoggerFactory.getLogger(RevisionHistoryDeserializer.class);

	private static final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");

	private static final Map<String, RevisionHistoryDeserializer<?>> DESERIALIZERS = new HashMap<String, RevisionHistoryDeserializer<?>>();

	public static final <T extends Versionable> RevisionHistoryDeserializer<T> getDeserializer(String type) {
		return DESERIALIZERS.containsKey(type) ? (RevisionHistoryDeserializer<T>) DESERIALIZERS.get(type) : null;
	}

	protected Object get(JSONObject jsonObject, String key) {
		try {
			return jsonObject.get(key);
		} catch (Exception ex) {
			return null;
		}
	}

	@PostConstruct
	protected final void register() {
		DESERIALIZERS.put(getType().getValue(), this);
	}

	protected abstract T deserialize(String data);

	protected abstract RevisionHistoryType getType();

	protected Resource getDeserializedResource(Resource resource, JSONObject jsonObject) {
		JSONObject resourceJsonObject = (JSONObject) get(jsonObject, RESOURCE);
		ResourceType resourceType = (ResourceType) baseRepository.get(ResourceType.class, (String) get(resourceJsonObject, RESOURCE_TYPE_NAME));
		resource.setResourceType(resourceType);

		resource.setUrl((String) get(resourceJsonObject, RESOURCE_URL));
		resource.setTitle((String) get(resourceJsonObject, RESOURCE_TITLE));

		License license = (License) baseRepository.get(License.class, (String) get(resourceJsonObject, RESOURCE_LICENSE_NAME));
		resource.setLicense(license);

		resource.setFolder((String) get(resourceJsonObject, RESOURCE_FOLDER));
		resource.setNumberOfSubcribers((Integer) get(resourceJsonObject, RESOURCE_NUMBER_OF_SUBCRIBERS));
		resource.setThumbnail((String) get(resourceJsonObject, RESOURCE_THUMBNAIL));
		resource.setFileHash((String) get(resourceJsonObject, RESOURCE_FILE_HASH));
		resource.setDescription((String) get(resourceJsonObject, RESOURCE_DESCRIPTION));
		Integer resourceSourceId = (Integer) get(resourceJsonObject, RESOURCE_RESOURCE_SOURCE_ID);
		if (resourceSourceId != null) {
			ResourceSource resourceSource = new ResourceSource();
			resourceSource.setResourceSourceId(resourceSourceId);
			resource.setResourceSource(resourceSource);
		}
		resource.setDistinguish(Short.valueOf((String) get(resourceJsonObject, RESOURCE_DISTINGUISH)));
		resource.setTags((String) get(resourceJsonObject, RESOURCE_TAGS));
		resource.setSiteName((String) get(resourceJsonObject, RESOURCE_SITE_NAME));
		resource.setBatchId((String) get(resourceJsonObject, RESOURCE_BATCH_ID));
		resource.setCategory((String) get(resourceJsonObject, RESOURCE_CATEGORY));
		resource.setIsFeatured((Integer.parseInt((String) get(resourceJsonObject, RESOURCE_IS_FEATURED))));
		resource.setHasFrameBreaker((Boolean) get(resourceJsonObject, RESOURCE_HAS_FRAME_BREAKER));
		resource.setBrokenStatus((Integer) get(resourceJsonObject, RESOURCE_BROKEN_STATUS));
		String views = (String) get(resourceJsonObject, RESOURCE_VIEWS_TOTAL);
		if (views != null && views.trim().length() != 0 && !views.equalsIgnoreCase("null")) {
			resource.setViews((Long.valueOf(views)));
		}
		resource.setS3UploadFlag((Integer) get(resourceJsonObject, RESOURCE_S3_UPLOAD_FLAG));
		resource.setMediaType((String) get(resourceJsonObject, RESOURCE_MEDIA_TYPE));

		// resource.setFromCrawler((Boolean) get(resourceJsonObject,
		// RESOURCE_FROM_CARWLER));
		// resource.setParentUrl(get(resourceJsonObject,RESOURCE_pa);
		// resource.setCodes(codes);
		// resource.setSourceReference(sourceReference);
		// resource.setRecordSource(recordSource);
		// resource.setAssetURI(get(resourceJsonObject,RESOURCE_);
		// resource.setAddDate(get(resourceJsonObject,RESOURCE_);
		// resource.setResourceSegments(resourceSegments);
		// resource.setFileData(get(resourceJsonObject,RESOURCE_);
		// resource.setIsNew(get(resourceJsonObject,RESOURCE_is);
		// resource.setResourceLearnguides(resourceLearnguides);
		// resource.setResourceInfo(resourceInfo);
		// resource.setSocial(social);
		// resource.setCustomFieldValues();
		// resource.setThumbnails(thumbnails);
		// resource.setVocaularyString(vocaularyString);
		// resource.setSubscriptionCount(get(resourceJsonObject,RESOURCE_S);
		// resource.setSecurityGroups(securityGroups);
		// resource.setCustomFields(customFields);

		JSONObject contentJsonObject = (JSONObject) get(jsonObject, CONTENT);
		getDeserializedContent(resource, contentJsonObject);
		JSONArray contentPermissionJsonArray = (JSONArray) get(jsonObject, CONTENT_PERMISSIONS);
		getDeserializedContentPermissions(resource, contentPermissionJsonArray);
		JSONObject organizationJsonObject = (JSONObject) get(jsonObject, ORGANIZATION);
		resource.setOrganization(getDeserializedOrganization(organizationJsonObject));
		try {
			resource.setCustomFieldValues(customFieldService.getCustomFieldsValuesOfResource(resource.getGooruOid()));
		} catch (Exception ex) {
			logger.error("get custom fields: " + ex);
		}

		return resource;
	}

	private Organization getDeserializedOrganization(JSONObject organizationJsonObject) {
		Organization organization = organizationRepository.getOrganizationByCode((String) get(organizationJsonObject, ORGANIZATION_CODE));
		return organization;
	}

	private Resource getDeserializedContent(Resource resource, JSONObject contentJsonObject) {

		String user_uid = (String) get(contentJsonObject, USER_UID);
		String creator_uid = (String) get(contentJsonObject, CREATOR_UID);
		User user = userRepository.findByGooruId(user_uid);
		User creator = userRepository.findByGooruId(creator_uid);

		resource.setGooruOid((String) get(contentJsonObject, GOORU_OID));
		resource.setSharing((String) get(contentJsonObject, SHARING));
		CustomTableValue statusType = this.customTableRepository.getCustomTableValue(CustomProperties.Table.CONTENT_STATUS_TYPE.getTable(), (String) get(contentJsonObject, STATUS_NAME));
		resource.setStatusType(statusType);
		try {
			Date createdOn = (Date) formatter.parse((String) get(contentJsonObject, CREATED_ON));
			resource.setCreatedOn(createdOn);
			Date lastModified = (Date) formatter.parse((String) get(contentJsonObject, LAST_MODIFIED));
			resource.setLastModified(lastModified);
		} catch (ParseException e) {
			logger.error(e.getMessage());
		}
		ContentType contentType = (ContentType) baseRepository.get(ContentType.class, (String) get(contentJsonObject, TYPE_NAME));
		resource.setContentType(contentType);

		if (user != null) {
			resource.setUser(user);
		} else {
			logger.warn("user does not exist " + user_uid);
		}
		if (creator != null) {
			resource.setCreator(creator);
		} else {
			logger.warn("creator does not exist " + creator_uid);
		}
		return resource;

	}

	private Resource getDeserializedContentPermissions(Resource resource, JSONArray contentPermissionJsonArray) {
		Set<ContentPermission> contentPermissions = new HashSet<ContentPermission>();
		if (contentPermissionJsonArray != null) {
			for (int i = 0; i < contentPermissionJsonArray.length(); i++) {
				try {
					JSONObject contentPermissionJsonObject = (JSONObject) contentPermissionJsonArray.get(i);
					ContentPermission contentPermission = new ContentPermission();
					contentPermission.setContent(resource);
					JSONObject partyJsonObject = (JSONObject) contentPermissionJsonObject.get(CONTENT_PERMISSION_PARTY);
					Party party = new Party();
					party.setPartyUid((String) get(partyJsonObject, PARTY_UID));
					party.setPartyName((String) get(partyJsonObject, PARTY_NAME));
					party.setPartyType((String) get(partyJsonObject, PARTY_TYPE));
					if (get(partyJsonObject, CREATED_ON) != null) {
						party.setCreatedOn((Date) formatter.parse((String) get(partyJsonObject, CREATED_ON)));
					}
					String lastModifiedOn = (String) get(partyJsonObject, LAST_MODIFIED_ON);
					if (lastModifiedOn != null && lastModifiedOn.isEmpty()) {
						party.setLastModifiedOn(formatter.parse((String) get(partyJsonObject, LAST_MODIFIED_ON)));
					}
					party.setUserUid((String) get(partyJsonObject, CREATED_BY_UID));
					contentPermission.setParty(party);
					contentPermission.setPermission((String) get(contentPermissionJsonObject, CONTENT_PERMISSION_PERMISSION));
					String validFrom = (String) get(contentPermissionJsonObject, CONTENT_PERMISSION_VALID_FROM);
					if (validFrom != null) {
						contentPermission.setValidFrom(formatter.parse(validFrom));
					}
					String expiryDate = (String) get(contentPermissionJsonObject, CONTENT_PERMISSION_EXPIRY_DATE);
					if (expiryDate != null) {
						contentPermission.setExpiryDate(formatter.parse(expiryDate));
					}
					contentPermissions.add(contentPermission);
				} catch (Exception e) {
					logger.error("content permissions : " + e.getMessage());
				}
			}
			resource.setContentPermissions(contentPermissions);
		}
		return resource;
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public ContentRepository getContentRepository() {
		return contentRepository;
	}
}
