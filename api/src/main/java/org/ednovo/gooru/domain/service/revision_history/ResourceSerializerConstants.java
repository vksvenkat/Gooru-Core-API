/*
*ResourceSerializerConstants.java
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

package org.ednovo.gooru.domain.service.revision_history;

/**
 * @author Search Team
 * 
 */
public interface ResourceSerializerConstants extends ContentSerializerConstants {

	
	// resource instance
	String INSTANCE_RESOURCE_INSTANCE_ID = "resource_instance_id";
	String INSTANCE_SEGMENT_ID = "segment_id";
	String INSTANCE_RESOURCE_ID = "resource_id";
	String INSTANCE_TITLE = "title";
	String INSTANCE_DESCRIPTION = "description";
	String INSTANCE_START = "start";
	String INSTANCE_STOP = "stop";
	String INSTANCE_NARRATIVE = "narrative";
	String INSTANCE_SEQUENCE = "sequence";
	String INSTANCE_XML_RESOURCE_ID = "xml_resource_id";

	// resource
	String RESOURCE_CONTENT_ID = "content_id";
	String RESOURCE_TYPE_NAME = "type_name";
	String RESOURCE_CATEGORY = "category";
	String RESOURCE_URL = "url";
	String RESOURCE_RESOURCE_SOURCE_ID = "resource_source_id";
	String RESOURCE_ASSET_URI = "asset_uri";
	// String RESOURCE_STORAGE_AREA_ID = "storage_area_id";
	String RESOURCE_FOLDER = "folder";
	String RESOURCE_THUMBNAIL = "thumbnail";
	String RESOURCE_LICENSE_NAME = "license_name";
	String RESOURCE_TITLE = "title";
	String RESOURCE_VIEWS_TOTAL = "views_total";
	String RESOURCE_DISTINGUISH = "distinguish";
	String RESOURCE_ISLIVE = "isLive";
	// String RESOURCE_SEQUENCE = "sequence";
	String RESOURCE_HAS_FRAME_BREAKER = "has_frame_breaker";
	String RESOURCE_BROKEN_STATUS = "broken_status";
	// String RESOURCE_NARRATIVE = "narrative";
	String RESOURCE_TEXT = "text";
	String RESOURCE_IN_USE = "in_use";
	String RESOURCE_IS_FOLDER_ABSENT = "is_folder_absent";
	// String RESOURCE_ADD_TIME = "add_time";
	// String RESOURCE_INTERNAL_TITLE = "internal_title";
	String RESOURCE_NUMBER_OF_SUBCRIBERS = "number_of_subcribers";
	String RESOURCE_IS_FEATURED = "is_featured";
	String RESOURCE_FROM_CRAWLER = "from_crawler";
	String RESOURCE_IMAGE_URL = "image_url";
	String RESOURCE_USER_UPLOADED_IMAGE = "user_uploaded_image";
	String RESOURCE_FROM_QA = "from_qa";
	// String RESOURCE_IS_ATOMIC = "is_atomic";
	String RESOURCE_FILE_HASH = "file_hash";
	String RESOURCE_DESCRIPTION = "description";
	String RESOURCE_SOURCE_REFERENCE = "source_reference";
	String RESOURCE_RECORD_SOURCE = "record_source";
	String RESOURCE_TAGS = "tags";
	// String RESOURCE_VANITY_TITLE = "vanity_title";
	String RESOURCE_SITE_NAME = "site_name";
	String RESOURCE_BATCH_ID = "batch_id";
	String RESOURCE_S3_UPLOAD_FLAG = "s3_upload_flag";
	String RESOURCE_RESOURCE_INFO_ID = "resource_info_id";
	String RESOURCE_MEDIA_TYPE = "media_type";
	// String RESOURCE_FROM_CARWLER = "from_carwler";

	// other
	String NONE = "none";
	String NULL = "null";
	String RESOURCE = "resource";
	String COLLABORATORS = "collaborators";
	String ORGANIZATION = "organization";
	String CONTENT_PERMISSIONS = "content_permissions";

	// content_permission
	String CONTENT_PERMISSION_CONTENT_ID = "content_id";
	String CONTENT_PERMISSION_PARTY = "party";
	String CONTENT_PERMISSION_PERMISSION = "permission";
	String CONTENT_PERMISSION_VALID_FROM = "valid_from";
	String CONTENT_PERMISSION_EXPIRY_DATE = "expiry_date";

	// party
	String PARTY_UID = "party_uid";
	String PARTY_NAME = "party_name";
	String PARTY_TYPE = "party_type";
	//String CREATED_BY_ID = "created_by_id";
	String CREATED_ON = "created_on";
	//String LAST_MODIFIED_BY_ID = "last_modified_by_id";
	String LAST_MODIFIED_ON = "last_modified_on";
	String CREATED_BY_UID = "created_by_uid";
	//String LAST_MODIFIED_BY_UID = "last_modified_by_uid";

	// organization
	String ORGANIZATION_CODE = "organization_code";
	String ORGANIZATION_PARTY_TYPE = "party_type";
	String ORGANIZATION_PARTY_UID = "party_uid";
	String ORGANIZATION_PARTY_NAME = "party_name";
	String ORGANIZATION_USER_UID = "user_uid";
	

}
