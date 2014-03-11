/*******************************************************************************
 * ColumnFamilyConstant.java
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
package org.ednovo.gooru.core.constant;

/**
 * @author SearchTeam
 *
 */
public interface ColumnFamilyConstant {
	
	String RESOURCE = "resource";
	
	String RESOURCE_RI = "resource_ri";
	
	String USER = "user";
	
	String USER_RI = "user_ri";

	String TAXONOMY ="taxonomy";
	
	String TAXONOMY_RI = "taxonomy_ri";
	
	String REVISION_HISTORY = "revision_history";
	
	String REVISION_HISTORY_RI = "revision_history_ri";
	
	String DATA_STORE = "data_store";
	
	String DATA_STORE_RI = "data_store_ri";
	
	String DOMAIN = "domain";
	
	String DOMAIN_RI = "domain_ri";
	
	String SEARCH_SETTING = "search_setting";
	
	String CONTENT_META = "content_meta";
	
	String USER_PREFERENCE = "user_preference";
	
	String USER_PROFICIENCY = "user_taxonomy_proficiency";
	
	String USER_SUBJECT_PROFICIENCY = "agg_event_resource_user_subject";
	
	String USER_COURSE_PROFICIENCY = "agg_event_resource_user_course";

	String USER_UNIT_PROFICIENCY = "agg_event_resource_user_unit";

	String USER_TOPIC_PROFICIENCY = "agg_event_resource_user_topic";

	String USER_LESSON_PROFICIENCY = "agg_event_resource_user_lesson";
	
	String USER_CONCEPT_PROFICIENCY = "agg_event_resource_user_concept";

}
