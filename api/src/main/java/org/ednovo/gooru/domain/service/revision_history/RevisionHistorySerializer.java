/*
*RevisionHistorySerializer.java
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

/**
 * 
 */
package org.ednovo.gooru.domain.service.revision_history;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.ContentPermission;
import org.ednovo.gooru.core.api.model.Organization;
import org.ednovo.gooru.core.api.model.Party;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.Versionable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Search Team
 * 
 */
public abstract class RevisionHistorySerializer<T extends Versionable> implements ResourceSerializerConstants {

	protected static final Logger logger = LoggerFactory.getLogger(QuizRevisionHistorySerializer.class);

	private static final Map<String, RevisionHistorySerializer<?>> SERIALIZERS = new HashMap<String, RevisionHistorySerializer<?>>();

	public static final <T extends Versionable> RevisionHistorySerializer<T> getSerializer(String type) {
		return SERIALIZERS.containsKey(type) ? (RevisionHistorySerializer<T>) SERIALIZERS.get(type) : null;
	}

	@PostConstruct
	protected final void register() {
		SERIALIZERS.put(getType().getValue(), this);
	}

	protected abstract String serialize(T entity);

	protected abstract RevisionHistoryType getType();

	private JSONObject getResourceJsonObject(Resource resource) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(RESOURCE_CONTENT_ID, resource.getContentId() + "");
			ResourceType resourcetype = resource.getResourceType();
			jsonObject.put(RESOURCE_TYPE_NAME, resourcetype.getName());
			jsonObject.put(RESOURCE_CATEGORY, resource.getCategory());
			jsonObject.put(RESOURCE_URL, resource.getUrl());
			if (resource.getResourceSource() != null) {
				jsonObject.put(RESOURCE_RESOURCE_SOURCE_ID, resource.getResourceSource().getResourceSourceId() + "");
			}
			// assessmentJsonObject.put(RESOURCE_STORAGE_AREA_ID,entity.gets
			jsonObject.put(RESOURCE_FOLDER, resource.getFolder());
			jsonObject.put(RESOURCE_THUMBNAIL, resource.getThumbnail());
			if (resource.getLicense() != null) {
				jsonObject.put(RESOURCE_LICENSE_NAME, resource.getLicense().getName());
			}
			jsonObject.put(RESOURCE_TITLE, resource.getTitle());
			jsonObject.put(RESOURCE_VIEWS_TOTAL, resource.getViews() + "");
			jsonObject.put(RESOURCE_DISTINGUISH, resource.getDistinguish() + "");
			// assessmentJsonObject.put(RESOURCE_SEQUENCE ,entity.gets
			jsonObject.put(RESOURCE_HAS_FRAME_BREAKER, resource.getHasFrameBreaker());
			jsonObject.put(RESOURCE_BROKEN_STATUS, resource.getBrokenStatus());
			// assessmentJsonObject.put(RESOURCE_NARRATIVE ,entity.get
			// assessmentJsonObject.put(RESOURCE_ADD_TIME ,entity.getAddDate()
			// assessmentJsonObject.put(RESOURCE_INTERNAL_TITLE ,entity.get
			jsonObject.put(RESOURCE_NUMBER_OF_SUBCRIBERS, resource.getNumberOfSubcribers());
			jsonObject.put(RESOURCE_IS_FEATURED, resource.getIsFeatured() + "");
			// assessmentJsonObject.put(RESOURCE_IS_ATOMIC ,entity.
			jsonObject.put(RESOURCE_FILE_HASH, resource.getFileHash());
			jsonObject.put(RESOURCE_DESCRIPTION, resource.getDescription());
			jsonObject.put(RESOURCE_SOURCE_REFERENCE, resource.getSourceReference());
			jsonObject.put(RESOURCE_RECORD_SOURCE, resource.getRecordSource());
			jsonObject.put(RESOURCE_TAGS, resource.getTags());
			// assessmentJsonObject.put(RESOURCE_VANITY_TITLE ,entity.get
			jsonObject.put(RESOURCE_SITE_NAME, resource.getSiteName());
			jsonObject.put(RESOURCE_BATCH_ID, resource.getBatchId());
			jsonObject.put(RESOURCE_S3_UPLOAD_FLAG, resource.getS3UploadFlag());
			if (resource.getResourceInfo() != null) {
				jsonObject.put(RESOURCE_RESOURCE_INFO_ID, resource.getResourceInfo().getResourceInfoId() + "");
			}
			jsonObject.put(RESOURCE_MEDIA_TYPE, resource.getMediaType());
		} catch (JSONException e) {
			logger.error(e.getMessage());
		}
		return jsonObject;
	}

	private JSONObject getContentJsonObject(Resource resource) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(CONTENT_ID, resource.getContentId() + "");
			jsonObject.put(ACCOUNT_UID, resource.getOrganization().getPartyUid());
			jsonObject.put(GOORU_OID, resource.getGooruOid());
			if (resource.getContentType() != null) {
				jsonObject.put(TYPE_NAME, resource.getContentType().getName());
			}
			jsonObject.put(USER_UID, resource.getUser().getGooruUId());
			jsonObject.put(CREATOR_UID, resource.getCreator().getGooruUId());
			jsonObject.put(SHARING, resource.getSharing());
			jsonObject.put(STATUS_NAME, resource.getStatusType() != null ? resource.getStatusType().getValue() : NONE);
			jsonObject.put(CREATED_ON, resource.getCreatedOn());
			jsonObject.put(LAST_MODIFIED, resource.getLastModified());
		} catch (JSONException e) {
			logger.error(e.getMessage());
		}
		return jsonObject;
	}

	private JSONArray getContentClassificationJsonArray(Resource entity) {
		JSONArray taxonomySetJsonArray = new JSONArray();
		if (entity.getTaxonomySet() != null) {
			Iterator<Code> taxonomySetIterator = entity.getTaxonomySet().iterator();
			while (taxonomySetIterator.hasNext()) {
				Code code = taxonomySetIterator.next();
				JSONObject taxonomySetJsonObject = new JSONObject();
				try {
					taxonomySetJsonObject.put(CONTENT_CLASSIFICATION_CONTENT_ID, entity.getContentId() + "");
					taxonomySetJsonObject.put(CONTENT_CLASSIFICATION_CODE_ID, code.getCodeId() + "");
					taxonomySetJsonArray.put(taxonomySetJsonObject);
				} catch (JSONException e) {
					logger.error(e.getMessage());
				}
			}
		}

		return taxonomySetJsonArray;
	}

	protected JSONArray getCollaboratorJsonArray(List<User> collaborators) {
		JSONArray collaboratorJsonArray = new JSONArray();
		if (collaborators != null) {
			for (User collaborator : collaborators) {
				collaboratorJsonArray.put(collaborator.getGooruUId());
			}
		}
		return collaboratorJsonArray;
	}

	protected JSONObject putResourceObject(JSONObject jsonObject, Resource resource) {
		try {
			jsonObject.put(RESOURCE, getResourceJsonObject(resource));
			jsonObject.put(CONTENT, getContentJsonObject(resource));
			jsonObject.put(CONTENT_CLASSIFICATIONS, getContentClassificationJsonArray(resource));
			jsonObject.put(CONTENT_PERMISSIONS, getContentPermissions(resource.getContentPermissions()));
			jsonObject.put(ORGANIZATION, getOrganization(resource.getOrganization()));
		} catch (JSONException e) {
			logger.error(e.getMessage());
		}
		return jsonObject;
	}

	private JSONObject getOrganization(Organization organization) {
		JSONObject organizationJsonObject = new JSONObject();
		try {
			organizationJsonObject.put(ORGANIZATION_CODE,organization.getOrganizationCode());
			organizationJsonObject.put(ORGANIZATION_PARTY_UID,organization.getPartyUid());
			organizationJsonObject.put(ORGANIZATION_PARTY_NAME,organization.getPartyName());
			organizationJsonObject.put(ORGANIZATION_PARTY_TYPE,organization.getPartyType());
			organizationJsonObject.put(ORGANIZATION_USER_UID,organization.getUserUid());
		} catch (JSONException e) {
			logger.error(e.getMessage());
		}
		return organizationJsonObject;
	}

	private JSONArray getContentPermissions(Set<ContentPermission> contentPermissions) {
		if (contentPermissions != null) {
			Iterator<ContentPermission> contentPermissonIterator = contentPermissions.iterator();
			JSONArray contentPermissionJsonArray = new JSONArray();
			while (contentPermissonIterator.hasNext()) {
				ContentPermission contentPermission = contentPermissonIterator.next();
				JSONObject contentPermissionJsonObject = new JSONObject();
				try {
					if(contentPermission.getParty() != null){
						contentPermissionJsonObject.put(CONTENT_PERMISSION_CONTENT_ID, contentPermission.getContent().getContentId()+"");
						contentPermissionJsonObject.put(CONTENT_PERMISSION_PERMISSION, contentPermission.getPermission());
						contentPermissionJsonObject.put(CONTENT_PERMISSION_VALID_FROM,contentPermission.getValidFrom());
						contentPermissionJsonObject.put(CONTENT_PERMISSION_EXPIRY_DATE,contentPermission.getExpiryDate());
						Party party= contentPermission.getParty();
						JSONObject partyJsonObject = new JSONObject();
						partyJsonObject.put(PARTY_UID,party.getPartyUid());
						partyJsonObject.put(PARTY_NAME,party.getPartyName());
						partyJsonObject.put(PARTY_TYPE,party.getPartyType());
						partyJsonObject.put(CREATED_ON,party.getCreatedOn());
						partyJsonObject.put(LAST_MODIFIED_ON,party.getLastModifiedOn());
						partyJsonObject.put(CREATED_BY_UID,party.getUserUid());
						
						contentPermissionJsonObject.put(CONTENT_PERMISSION_PARTY, partyJsonObject);
						contentPermissionJsonArray.put(contentPermissionJsonObject);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return contentPermissionJsonArray;
		}
		return null;
	}

	/*
	 * private JSONArray getSecurityGroups(Set<SecurityGroup> securityGroups) {
	 * Iterator<SecurityGroup> securityGroupsIterator =
	 * securityGroups.iterator(); ======= private JSONArray
	 * getSecurityGroups(Set<SecurityGroup> securityGroups) { >>>>>>>
	 * 894fb9de787e9483893f0a5fb636cf3b029c0ca5 JSONArray
	 * securityGroupsJsonArray = new JSONArray(); if (securityGroups != null) {
	 * Iterator<SecurityGroup> securityGroupsIterator =
	 * securityGroups.iterator(); while (securityGroupsIterator.hasNext()) {
	 * SecurityGroup securityGroup = securityGroupsIterator.next(); JSONObject
	 * securityGroupJsonObject = new JSONObject(); try {
	 * securityGroupJsonObject.put(SECURITY_GROUP_UID,
	 * securityGroup.getSecurityGroupUid());
	 * securityGroupJsonObject.put(SECURITY_GROUP_ORGANIZATION_UID,
	 * securityGroup.getOrganization().getPartyUid());
	 * securityGroupJsonObject.put(SECURITY_GROUP_NAME,
	 * securityGroup.getName());
	 * securityGroupJsonObject.put(SECURITY_GROUP_SECURITY_KEY,
	 * securityGroup.getSecurityKey());
	 * securityGroupJsonObject.put(SECURITY_GROUP_SYSTEM_FLAG,
	 * securityGroup.isSystemFlag());
	 * securityGroupJsonObject.put(SECURITY_GROUP_DEFAULT_FLAG,
	 * securityGroup.isDefaultFlag());
	 * securityGroupJsonObject.put(SECURITY_GROUP_ACTIVE_FLAG,
	 * securityGroup.isActiveFlag());
	 * securityGroupsJsonArray.put(securityGroupJsonObject); } catch
	 * (JSONException e) { logger.error("securityGroup: " + e.getMessage()); } }
	 * } return securityGroupsJsonArray; }
	 */
}
