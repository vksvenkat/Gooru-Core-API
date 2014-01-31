/**
 * 
 */
package org.ednovo.gooru.core.filter;

/**
 * Defines filter maps containing the inclusions and exclusions needed to be
 * made while serializing
 * 
 * @author Search Team
 * 
 */
public interface FilterProperties extends Properties {

	FilterSetting COLLECTION_FLT = FilterSetting.newInstance().exclude("collection", COLLECTION).exclude("user", USER).include("resourceInfo", RESOURCE_INFO);

	FilterSetting QUIZ_FLT = FilterSetting.newInstance().exclude("quiz", QUIZ).exclude("user", USER).include("resourceInfo", RESOURCE_INFO);

	FilterSetting SEGMENT_AND_INSTANCE_FLT = FilterSetting.newInstance().exclude("segment", SEGMENT).exclude("user", USER).exclude("resource", RESOURCE).include("resourceInfo", RESOURCE_INFO);

}
