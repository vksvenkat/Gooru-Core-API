/////////////////////////////////////////////////////////////
// JsonSerializer.java
// gooru-api
// Created by Gooru on 2014
// Copyright (c) 2014 Gooru. All rights reserved.
// http://www.goorulearning.org/
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
/////////////////////////////////////////////////////////////
package org.ednovo.gooru.json.serializer.util;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.core.exception.MethodFailureException;
import org.json.JSONObject;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

import com.thoughtworks.xstream.XStream;

import flexjson.JSONSerializer;

public class JsonSerializer {

	public static final String FORMAT_JSON = "json";

	public static ModelAndView toModelAndView(Object object) {
		ModelAndView jsonmodel = new ModelAndView("rest/model");
		jsonmodel.addObject("model", object);
		return jsonmodel;
	}

	public static ModelAndView toJsonModelAndView(Object model,
			boolean deepSerialize) {
		return toModelAndView(serializeToJson(model, deepSerialize));
	}

	public static ModelAndView toModelAndView(Object obj,
			String type) {
		return toModelAndView(serialize(obj, type));
	}

	public static ModelAndView toModelAndViewWithInFilter(Object obj,
			String type,
			String... includes) {
		return toModelAndView(serialize(obj, type, null, includes));
	}

	public static ModelAndView toModelAndViewWithIoFilter(Object obj,
			String type,
			String[] excludes,
			String... includes) {
		return toModelAndView(serialize(obj, type, excludes, includes));
	}

	// need to improve logic
	public static ModelAndView toModelAndViewWithErrorObject(Object obj,
			String type,
			String entityName,
			Errors errors,
			String[] excludes,
			String... includes) {
		return toModelAndView(serialize(obj, type, excludes, includes));
	}

	/**
	 * @param model
	 * @param excludes
	 * @param includes
	 * @return
	 */
	public static String serializeToJsonWithExcludes(Object model,
			String[] excludes,
			String... includes) {
		return serialize(model, FORMAT_JSON, excludes, includes);
	}

	public static String serialize(Object model,
			String type) {
		return serialize(model, type, null);
	}

	public static String serializeToJson(Object model,
			String... includes) {
		return serialize(model, FORMAT_JSON, null, includes);
	}

	public static JSONObject serializeToJsonObjectWithExcludes(Object model,
			String[] excludes,
			String... includes) throws Exception {
		return new JSONObject(serialize(model, FORMAT_JSON, excludes, includes));
	}

	public static JSONObject serializeToJsonObject(Object model,
			String... includes) throws Exception {
		return new JSONObject(serialize(model, FORMAT_JSON, null, includes));
	}

	public static String serializeToJsonWithExcludes(Object model,
			String[] excludes,
			boolean deepSerialize,
			String... includes) {
		return serialize(model, FORMAT_JSON, excludes, deepSerialize, includes);
	}

	public static String serializeToJson(Object model,
			boolean deepSerialize,
			String... includes) {
		return serialize(model, FORMAT_JSON, null, deepSerialize, includes);
	}

	public static JSONObject serializeToJsonObjectWithExcludes(Object model,
			String[] excludes,
			boolean deepSerialize,
			String... includes) throws Exception {
		return new JSONObject(serialize(model, FORMAT_JSON, excludes, deepSerialize, includes));
	}

	public static JSONObject serializeToJsonObject(Object model,
			boolean deepSerialize,
			String... includes) throws Exception {
		return new JSONObject(serialize(model, FORMAT_JSON, null, deepSerialize, includes));
	}

	/**
	 * @param model
	 * @param type
	 * @param excludes
	 * @param includes
	 * @return
	 */
	public static String serialize(Object model,
			String type,
			String[] excludes,
			boolean deepSerialize,
			String... includes) {
		if (model == null) {
			return "";
		}
		String serializedData = null;
		JSONSerializer serializer = new JSONSerializer();

		if (type == null || type.equals("json")) {

			if (includes != null) {
				includes = (String[]) ArrayUtils.add(includes, "*.contentPermissions");
				serializer.include(includes);
			} else {
				serializer.include("*.contentPermissions");
			}

			if (excludes != null) {
				serializer.exclude(excludes);
			}

			try {

				serializedData = deepSerialize ? serializer.deepSerialize(model) : serializer.serialize(model);

			} catch (Exception ex) {
				throw new MethodFailureException(ex.getMessage());
			}

		} else {
			serializedData = new XStream().toXML(model);
		}
		return serializedData;
	}

	/**
	 * @param model
	 * @param type
	 * @param excludes
	 * @param includes
	 * @return
	 */
	public static String serialize(Object model,
			String type,
			String[] excludes,
			String... includes) {
		return serialize(model, type, excludes, false, includes);
	}

}
