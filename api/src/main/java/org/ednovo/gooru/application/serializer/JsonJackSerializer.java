/*
*JsonJackSerializer.java
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
package org.ednovo.gooru.application.serializer;

import java.util.Map;

import org.ednovo.gooru.application.serializer.mixin.GroupMixIn;
import org.ednovo.gooru.application.serializer.mixin.OrganizationMixIn;
import org.ednovo.gooru.core.api.model.Organization;
import org.ednovo.gooru.core.api.model.UserGroup;
import org.ednovo.gooru.core.filter.FilterSetting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;

/**
 * Provides support to serialize an Object into JSON. Uses Jackson serializer
 * for serializing the object.
 * 
 * @author Search Team
 * @see FilterSetting
 */
public class JsonJackSerializer {

	private static ObjectMapper mapper;
	/**
	 * The static attributes and writers needed for serialization are
	 * instantiated. This block of code is called when the class gets loaded.
	 */
	static {
		mapper = new ObjectMapper();
		mapper.configure(MapperFeature.USE_ANNOTATIONS, true);
		mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
		
		mapper.addMixInAnnotations(UserGroup.class, GroupMixIn.class);
		mapper.addMixInAnnotations(Organization.class, OrganizationMixIn.class);
		
		Hibernate4Module hibernate4Module = new Hibernate4Module();
		hibernate4Module.enable(Hibernate4Module.Feature.FORCE_LAZY_LOADING);
		mapper.registerModule(hibernate4Module);
	}

	/**
	 * This class cannot be instantiated
	 */
	private JsonJackSerializer() {
	}


	/**
	 * Creates an instance of ObjectWriter with ObjectMapper
	 * 
	 * @param setting
	 *            FilterSetting
	 * @return instance of ObjectWriter
	 */
	public static ObjectWriter getWriter(FilterSetting setting) {
		if (setting != null) {
			SimpleFilterProvider filterProvider = getFilterProviderInstance();

			if (setting.getIncludes() != null) {
				for (Map.Entry<String,String[]> filterProperty : setting.getIncludes().entrySet()) {
					filterProvider.addFilter(filterProperty.getKey(), SimpleBeanPropertyFilter.filterOutAllExcept(filterProperty.getValue()));
				}
			}
			if (setting.getExcludes() != null) {
				for (Map.Entry<String,String[]> filterProperty : setting.getExcludes().entrySet()) {
					filterProvider.addFilter(filterProperty.getKey(), SimpleBeanPropertyFilter.serializeAllExcept(filterProperty.getValue()));
				}
			}
			return mapper.writer(filterProvider);
		}
		return mapper.writer(getFilterProviderInstance());
	}
	
	public static void getFilter(FilterSetting setting , String filterString, boolean include) {
		String[] excludeParams = filterString.split(",");
		for(String param : excludeParams) {
			String[] paramFields = param.split(".");
		}
	}

	/**
	 * Serializes the <code>Object</code> into json <code>String</code>. The
	 * object is deep serialized.
	 * 
	 * @param object
	 *            an instance of <code>Object</code> which needs to be
	 *            serialized
	 * @return serialized input Object as String
	 * @throws JsonProcessingException
	 */
	public static <O extends Object> String serialize(O object) throws JsonProcessingException {
		return getWriter(null).writeValueAsString(object);
	}

	/**
	 * Serializes the <code>Object</code> into JSON <code>String</code> using
	 * the filter
	 * 
	 * @param object
	 *            an instance of <code>Object</code> which needs to be
	 *            serialized
	 * @param setting
	 *            FilterSetting . If the filter is null the object is deep
	 *            serialized.
	 * @return serialized input Object as String
	 * @throws JsonProcessingException
	 */
	public static <O extends Object> String serialize(O object, FilterSetting setting) throws JsonProcessingException {
		return getWriter(setting).writeValueAsString(object);
	}

	/**
	 * Get the instance of a simple filter provider
	 * 
	 * @return instance of SimpleFilterProvider
	 */
	private static SimpleFilterProvider getFilterProviderInstance() {
		SimpleFilterProvider filterProvider = new SimpleFilterProvider();
		filterProvider.setFailOnUnknownId(false);
		return filterProvider;
	}

}
