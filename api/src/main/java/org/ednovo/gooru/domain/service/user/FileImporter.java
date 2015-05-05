package org.ednovo.gooru.domain.service.user;

import java.util.HashMap;
import java.util.Map;

import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.BadRequestException;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.type.TypeReference;

import flexjson.JSONSerializer;

public abstract class FileImporter implements  ParameterProperties, ConstantProperties {

	protected static Map<String, Object> parse(String keys[], String value) {
		StringBuilder json = new StringBuilder();
		int index = 0;
		json.append("{");
		int keyLength = keys.length;
		for (String key : keys) {
			json.append("\"");
			json.append(key);
			json.append("\"");
			if (index < (keyLength - 1)) {
				json.append(":{");
			}

			index++;
		}
		json.append(":\"");
		json.append(value);
		json.append("\"");
		for (index = 0; index < keyLength; index++) {
			json.append("}");
		}
		return JsonDeserializer.deserialize(json.toString(), new TypeReference<Map<String, Object>>() {
		});
	}

	protected Map merge(Map original, Map newMap) {
		for (Object key : newMap.keySet()) {
			if (newMap.get(key) instanceof Map && original.get(key) instanceof Map) {
				Map originalChild = (Map) original.get(key);
				Map newChild = (Map) newMap.get(key);
				original.put(key, merge(originalChild, newChild));
			} else {
				original.put(key, newMap.get(key));
			}
		}
		return original;
	}

	protected String generateJSONInput(String json, String delimter) {
		String p = json;
		Map<String, String> data = JsonDeserializer.deserialize(p, new TypeReference<Map<String, String>>() {
		});
		Map<String, Object> result = new HashMap<String, Object>();
		for (Map.Entry<String, String> value : data.entrySet()) {
			if (value.getKey().contains(delimter)) {
				String[] keys = value.getKey().split(delimter);
				merge(result, parse(keys, value.getValue()));
			} else {
				result.put(value.getKey(), value.getValue());
			}
		}
		return new JSONSerializer().serialize(result);
	}
	
	public static String getValue(final String key, JSONObject json) throws Exception {
		try {
			if (json.isNull(key)) {
				return null;
			}
			return json.getString(key);

		} catch (JSONException e) {
			throw new BadRequestException("Input JSON parse failed!");
		}
	}

	public static JSONObject requestData(String data)  {
		try {
			return data != null ? new JSONObject(data) : null;
		} catch (JSONException e) {
			throw new BadRequestException("Input JSON parse failed!");
		}
	}
}
