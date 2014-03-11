/*******************************************************************************
 * Properties.java
 *  gooru-core
 *  Created by Gooru on 2014
 *  Copyright (c) 2014 Gooru. All rights reserved.
 *  http://www.goorulearning.org/
 *       
 *  Permission is hereby granted, free of charge, to any 
 *  person obtaining a copy of this software and associated 
 *  documentation. Any one can use this software without any 
 *  restriction and can use without any limitation rights 
 *  like copy,modify,merge,publish,distribute,sub-license or 
 *  sell copies of the software.
 *  
 *  The seller can sell based on the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be   
 *  included in all copies or substantial portions of the Software. 
 * 
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY    
 *   KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE  
 *   WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR   
 *   PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE       AUTHORS 
 *   OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
 *   OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *   OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 *   WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 *   THE SOFTWARE.
 ******************************************************************************/
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
