/**
 * 
 */
package org.ednovo.gooru.core.filter;

import org.apache.commons.lang.ArrayUtils;

/**
 * Contains constants for the filter properties
 * 
 * @author Search Team
 * 
 */
public interface Properties {

	String[] CONTENT = { "userPermSet", "taxonomySet" };
	
	String[] RESOURCE_INFO = { "tags", "numOfPages","tagSet"};

	String[] RESOURCE = (String[]) ArrayUtils.addAll(CONTENT, new String[] { "resourceInstances", "resourceSegments", "codes", "customFieldValues", "customFields" , "resourceMetaData", "resourceLearnguides" });

	String[] COLLECTION = (String[]) ArrayUtils.addAll(RESOURCE, new String[] { "taxonomyMapByCode", "collaborators", "courseSet" });

	String[] QUIZ = (String[]) ArrayUtils.addAll(COLLECTION, new String[] { "segments", "attempts", "collaboratorList" });

	String[] SEGMENT = { "resourceInstances", "attempts", "collaboratorList" };
	
	String[] SEGMENT_AND_INSTNANT = { "attempts", "collaboratorList" };

	String[] USER = { "identities", "contentSet", "userRoleSet", "grpMbrshipSet", "userPermSet", "groups", "customFieldValues", "resourceLearnguides" , "group" , "userRole"};
	
}
