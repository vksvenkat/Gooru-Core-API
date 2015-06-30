/**
 * 
 */
package org.ednovo.gooru.mail.serializer;

import flexjson.JSONDeserializer;



public class JsonDeserializer  {

	public static <T> T deserialize(String json, Class<T> clazz) {
		try {
			return new JSONDeserializer<T>().use(null, clazz).deserialize(json);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	

}
