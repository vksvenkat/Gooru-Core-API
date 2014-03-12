/**
 * 
 */
package org.ednovo.goorucore.application.serializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Search Team
 *
 */
public class JsonProcessor {
	
	private static ObjectMapper mapper;
	/**
	 * The static attributes and writers needed for serialization are
	 * instantiated. This block of code is called when the class gets loaded.
	 */
	static {
		mapper = new ObjectMapper();
		mapper.configure(MapperFeature.USE_ANNOTATIONS, true);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	public static ObjectMapper getMapper() {
		return mapper;
	}

}
