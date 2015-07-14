/**
 * 
 */
package org.ednovo.goorucore.application.serializer;

import org.ednovo.gooru.core.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;

import flexjson.JSONDeserializer;



/**
 * @author Search Team
 * 
 */
public class JsonDeserializer extends JsonProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonDeserializer.class);
	
	public static <T> T deserialize(String json, Class<T> clazz) {
		try {
			return new JSONDeserializer<T>().use(null, clazz).deserialize(json);
		} catch (Exception e) {
			LOGGER.error("Error : ", e);
			throw new BadRequestException("Input JSON parse failed!");
		}
	}
	
	public static <T> T deserialize(String json, TypeReference<T> type) {
		try {
			return getMapper().readValue(json, type);
		} catch (Exception e) {
			LOGGER.error("Error : ", e);
			throw new BadRequestException("Input JSON parse failed! ");
		}
	}
}
