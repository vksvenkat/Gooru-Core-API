/**
 * 
 */
package org.ednovo.goorucore.application.serializer;

import com.fasterxml.jackson.core.type.TypeReference;

import flexjson.JSONDeserializer;



/**
 * @author Search Team
 * 
 */
public class JsonDeserializer extends JsonProcessor {

	public static <T> T deserialize(String json, Class<T> clazz) {
		try {
			return new JSONDeserializer<T>().use(null, clazz).deserialize(json);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static <T> T deserialize(String json, TypeReference<T> type) {
		try {
			return getMapper().readValue(json, type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	

	

}
