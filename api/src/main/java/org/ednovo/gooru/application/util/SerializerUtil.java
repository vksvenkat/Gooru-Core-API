/////////////////////////////////////////////////////////////
// SerializerUtil.java
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
package org.ednovo.gooru.application.util;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.ednovo.gooru.application.util.context.AppContext;
import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.ContentPermission;
import org.ednovo.gooru.core.api.model.ContentPermissionTransformer;
import org.ednovo.gooru.core.api.model.Identity;
import org.ednovo.gooru.core.api.model.Organization;
import org.ednovo.gooru.core.api.model.OrganizationTransformer;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.SessionContextSupport;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserGroup;
import org.ednovo.gooru.core.api.model.UserGroupTransformer;
import org.ednovo.gooru.core.api.model.UserTransformer;
import org.ednovo.gooru.core.application.util.BaseUtil;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.MethodFailureException;
import org.ednovo.gooru.mongodb.assessments.questions.Transformers.AssessmentQuestionTransformer;
import org.ednovo.goorucore.application.serializer.ExcludeNullTransformer;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import flexjson.JSONSerializer;

public class SerializerUtil implements ParameterProperties {

	private static final Logger LOGGER = LoggerFactory.getLogger(SerializerUtil.class);

	private static final String[] EXCLUDES = { "*.class", "*.userRole", "*.organization", "*.primaryOrganization", "organization", "*.codeType.organization.*" };

	private static XStream xStream = new XStream(new DomDriver());

	public static ModelAndView toModelAndView(final Object object) {
		ModelAndView jsonmodel = new ModelAndView(REST_MODEL);
		jsonmodel.addObject(MODEL, object);
		return jsonmodel;
	}

	public static ModelAndView toJsonModelAndView(final Object model, final boolean deepSerialize) {
		return toModelAndView(serializeToJson(model, deepSerialize));
	}

	public static ModelAndView toModelAndView(final Object obj, final String type) {
		return toModelAndView(serialize(obj, type));
	}

	public static ModelAndView toModelAndViewWithInFilter(final Object obj, final String type, final String... includes) {
		return toModelAndView(serialize(obj, type, null, includes));
	}

	public static ModelAndView toModelAndViewWithIoFilter(final Object obj, final String type, final String[] excludes, final String... includes) {
		return toModelAndView(serialize(obj, type, excludes, includes));
	}

	public static ModelAndView toModelAndViewWithIoFilter(final Object obj, final String type, final String[] excludes, final boolean excludeNullObject, final String... includes) {
		return toModelAndView(serialize(obj, type, excludes, false, excludeNullObject, includes));
	}

	// need to improve logic
	public static ModelAndView toModelAndViewWithErrorObject(final Object obj, final String type, final String entityName, final Errors errors, String[] excludes, final String... includes) {
		return toModelAndView(serialize(obj, type, excludes, includes));
	}

	/**
	 * @param model
	 * @param excludes
	 * @param includes
	 * @return
	 */
	public static String serializeToJsonWithExcludes(final Object model, final String[] excludes, final String... includes) {
		return serialize(model, FORMAT_JSON, excludes, includes);
	}

	public static String serialize(final Object model, final String type) {
		return serialize(model, type, null);
	}

	public static String serializeToJson(final Object model, final String... includes) {
		return serialize(model, FORMAT_JSON, null, includes);
	}

	public static JSONObject serializeToJsonObjectWithExcludes(final Object model, final String[] excludes, final String... includes) throws Exception {
		return new JSONObject(serialize(model, FORMAT_JSON, excludes, includes));
	}

	public static JSONObject serializeToJsonObject(final Object model, final String... includes) throws Exception {
		return new JSONObject(serialize(model, FORMAT_JSON, null, includes));
	}

	public static String serializeToJsonWithExcludes(final Object model, final String[] excludes, final boolean deepSerialize, final String... includes) {
		return serialize(model, FORMAT_JSON, excludes, deepSerialize, true, includes);
	}
	
	public static String serializeToJsonWithExcludes(final Object model, final String[] excludes, final boolean deepSerialize, final boolean excludeNullObject, final String... includes) {
		return serialize(model, FORMAT_JSON, excludes, deepSerialize, excludeNullObject, includes);
	}

	public static String serializeToJson(final Object model, final boolean deepSerialize, final String... includes) {
		return serialize(model, FORMAT_JSON, null, deepSerialize, includes);
	}

	public static String serializeToJson(final Object model, final boolean deepSerialize, final boolean excludeNullObject) {
		return serialize(model, FORMAT_JSON, null, deepSerialize, false, excludeNullObject);
	}
	
	public static String serializeToJson(final Object model, final String[] excludes, final boolean deepSerialize, final boolean excludeNullObject) {
		return serialize(model, FORMAT_JSON, excludes, deepSerialize, false, excludeNullObject);
	}

	public static JSONObject serializeToJsonObjectWithExcludes(final Object model, final String[] excludes, final boolean deepSerialize, final String... includes) throws Exception {
		return new JSONObject(serialize(model, FORMAT_JSON, excludes, deepSerialize, includes));
	}

	public static JSONObject serializeToJsonObject(final Object model, final boolean deepSerialize, final String... includes) throws Exception {
		return new JSONObject(serialize(model, FORMAT_JSON, null, deepSerialize, includes));
	}

	public static String serialize(final Object model, final String type, final String[] excludes, final boolean deepSerialize, final String... includes) {
		return serialize(model, type, excludes, deepSerialize, true, false, includes);
	}

	public static String serialize(final Object model, final String type, final String[] excludes, final boolean deepSerialize, final boolean excludeNullObject, final String... includes) {
		return serialize(model, type, excludes, deepSerialize, true, excludeNullObject, includes);
	}

	public static String serialize(final Object model, final String type, final String[] excludes, final String... includes) {
		return serialize(model, type, excludes, false, includes);
	}

	/**
	 * @param model
	 * @param type
	 * @param excludes
	 * @param includes
	 * @return
	 */
	public static String serialize(Object model, final String type, String[] excludes, boolean deepSerialize, final boolean useBaseExcludes, final boolean excludeNullObject, String... includes) {
		if (model == null) {
			return "";
		}
		String serializedData = null;
		JSONSerializer serializer = new JSONSerializer();
		boolean handlingAssessmentQuestion = willSerializeAssessmentQuestion(model);

		if (type == null || type.equals(JSON)) {

			if (includes != null) {
				includes = (String[]) ArrayUtils.add(includes, "*.contentPermissions");
				serializer.include(includes);
			} else {
				serializer.include("*.contentPermissions");
			}
			includes = (String[]) ArrayUtils.add(includes, "*.version");
			serializer.include(includes);

			if (useBaseExcludes) {
				if (excludes != null) {
					excludes = (String[]) ArrayUtils.addAll(excludes, EXCLUDES);
				} else {
					excludes = EXCLUDES;
				}
			}

			if (model instanceof User) {
				deepSerialize = true;
			}
			if (model != null) {
				serializer = appendTransformers(serializer, excludeNullObject);
			}
			if (handlingAssessmentQuestion) {
				serializer = handleAssessmentQuestionTransformers(serializer);
			}

			if (excludes != null) {
				serializer.exclude(excludes);
			}

			try {
				model = protocolSwitch(model);
				serializedData = deepSerialize ? serializer.deepSerialize(model) : serializer.serialize(model);
				log(model, serializedData);

			} catch (Exception ex) {
				LOGGER.error("serialize: happened to throw exception", ex);
				if (model instanceof Resource) {
					LOGGER.error("Serialization failed for resource : " + ((Resource) model).getContentId());
				} else if (model instanceof List) {
					List list = (List<?>) model;
					if (list != null && list.size() > 0 && list.get(0) instanceof Resource) {
						LOGGER.error("Serialization failed for list resources of size : " + list.size() + " resource : " + ((Resource) list.get(0)).getContentId());
					}
				} else {
					LOGGER.error("Serialization failed" + ex);
				}
				throw new MethodFailureException(ex.getMessage());
			}

		} else {
			serializedData = new XStream().toXML(model);
		}
		return serializedData;
	}

	private static JSONSerializer handleAssessmentQuestionTransformers(
			JSONSerializer serializer) {
		serializer = serializer.transform(
				(AssessmentQuestionTransformer) AppContext.getCtx().getBean(
						AssessmentQuestionTransformer.class),
				AssessmentQuestion.class);
		return serializer;
	}

	/**
	 * Need to check if we are going to serialize any instance of
	 * AssessmentQuestion. Since question may be stored either in mysql or in
	 * mongo, we need a custom transformer here to make sure that it is properly
	 * serialized.
	 * 
	 * @param model
	 *            Model which will be serialized
	 * @return boolean signifying if we are serializing instance of
	 *         AssessmentQuestion
	 */
	private static boolean willSerializeAssessmentQuestion(Object model) {
		if (model != null) {
			if (model instanceof CollectionItem) {
				Resource resource = ((CollectionItem) model).getResource();
				if (resource instanceof AssessmentQuestion) {
					if (((AssessmentQuestion) resource).isQuestionNewGen()) {
						return true;
					}
				}
				return false;
			} else if (model instanceof Collection) {
				Set<CollectionItem> items = ((Collection) model).getCollectionItems();
				if (items != null) {
					for (CollectionItem ci : items) {
						if (ci.getResource() instanceof AssessmentQuestion) {
							if (((AssessmentQuestion) ci.getResource()).isQuestionNewGen()) {
                                return true;
                            }
						}
					}
				}
			}
		}
		return false;
	}

	private static Object protocolSwitch(final Object model) {
		HttpServletRequest request = null;
		if (RequestContextHolder.getRequestAttributes() != null) {
			request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		}
		if (request != null) {
			final String requestProtocol = request.getAttribute("requestProtocol") != null ? (String) request.getAttribute("requestProtocol") : null;
			final String protocolAutoSwitch = request.getAttribute("protocolAutoSwitch") != null ? (String) request.getAttribute("protocolAutoSwitch") : "true";
			if (protocolAutoSwitch != null && protocolAutoSwitch.equalsIgnoreCase("true")) {
				if (model instanceof Resource) {
					BaseUtil.changeHttpsProtocolByHeader(((Resource) model), requestProtocol, BaseUtil.isSecure(request), request.getMethod());
				} else if (model instanceof CollectionItem) {
					BaseUtil.changeHttpsProtocolByHeader(((CollectionItem) model).getResource(), requestProtocol, BaseUtil.isSecure(request), request.getMethod());
				} else if (model instanceof List) {
					List list = (List<?>) model;
					if (list != null && list.size() > 0 && list.get(0) instanceof Resource) {
						for (int resourceIndex = 0; resourceIndex < list.size(); resourceIndex++) {
							BaseUtil.changeHttpsProtocolByHeader(((Resource) list.get(resourceIndex)), requestProtocol, BaseUtil.isSecure(request), request.getMethod());
						}
					} else if (list != null && list.size() > 0 && list.get(0) instanceof CollectionItem) {
						for (int resourceIndex = 0; resourceIndex < list.size(); resourceIndex++) {
							BaseUtil.changeHttpsProtocolByHeader(((CollectionItem) list.get(resourceIndex)).getResource(), requestProtocol, BaseUtil.isSecure(request), request.getMethod());
						}
					}
				} else if (model instanceof Collection) {
					if (((Collection) model) != null && ((Collection) model).getCollectionItems() != null) {
						for (CollectionItem collectionItem : ((Collection) model).getCollectionItems()) {
							BaseUtil.changeHttpsProtocolByHeader(collectionItem.getResource(), requestProtocol, BaseUtil.isSecure(request), request.getMethod());
						}
					}
				}

			}
		}
		return model;
	}

	@SuppressWarnings("unchecked")
	public static User cloneUserForSerialization(final User user, final boolean includeSets) {
		final User clonedUser = new User();
		clonedUser.setUserId(user.getUserId());
		clonedUser.setGooruUId(user.getGooruUId());
		clonedUser.setFirstName(user.getFirstName());
		clonedUser.setLastName(user.getLastName());
		clonedUser.setUsername(user.getUsername());
		clonedUser.setRegisterToken(user.getRegisterToken());
		clonedUser.setConfirmStatus(user.getConfirmStatus());
		clonedUser.setEmailId(user.getEmailId());
		clonedUser.setViewFlag(user.getViewFlag());
		if ((includeSets) && (user.getIdentities() != null)) {
			clonedUser.setIdentities((Set<Identity>) xStream.fromXML(xStream.toXML(user.getIdentities())));
		}

		return clonedUser;
	}

	public static JSONSerializer appendTransformers(final JSONSerializer serializer, final boolean excludeNullObject) {
		serializer.transform(new UserTransformer(false), User.class).transform(new OrganizationTransformer(), Organization.class).transform(new UserGroupTransformer(), UserGroup.class).transform(new ContentPermissionTransformer(), ContentPermission.class);
		if (excludeNullObject) {
			serializer.transform(new ExcludeNullTransformer(), void.class);
		}
		return serializer;

	}

	private static void log(final Object model, final String data) {
		HttpServletRequest request = null;
		if (RequestContextHolder.getRequestAttributes() != null) {
			request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			if (request != null && request.getMethod() != null && (request.getMethod().equalsIgnoreCase(RequestMethod.POST.name()) || request.getMethod().equalsIgnoreCase(RequestMethod.PUT.name()))) {
				org.json.simple.JSONObject payLoadObject = new org.json.simple.JSONObject();
				try {
					if (SessionContextSupport.getLog() != null && SessionContextSupport.getLog().get("payLoadObject") != null) {
						org.json.simple.parser.JSONParser payLoadParser = new org.json.simple.parser.JSONParser();
						payLoadObject = (org.json.simple.JSONObject) payLoadParser.parse(SessionContextSupport.getLog().get("payLoadObject").toString());
					}
					try {
						if (data != null) {
							payLoadObject.put("data", data);
						}
					} catch (Exception e) { 
					   LOGGER.error("Error: " + e);	
					}
					
				} catch (Exception e) {
					LOGGER.error("Error : " + e);
				}

				SessionContextSupport.putLogParameter("payLoadObject", payLoadObject.toString());
			}
		}
	}

	public static User cloneUserForSerialization(final User user) {
		return cloneUserForSerialization(user, true);
	}

}
