package org.ednovo.gooru.mongodb.assessments.questions.Transformers;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;

import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.application.util.ServerValidationUtils;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.BadRequestException;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.mongodb.assessments.questions.services.MongoQuestionsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import flexjson.JSONContext;
import flexjson.Path;
import flexjson.TypeContext;
import flexjson.transformer.ObjectTransformer;
import flexjson.transformer.TransformerWrapper;

/**
 * @author ashish
 *
 */

public class AssessmentQuestionTransformer extends ObjectTransformer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see flexjson.transformer.ObjectTransformer#transform(java.lang.Object)
	 * 
	 * This is the transformation to help us out in handling the cases where in
	 * new Question types are stored in mongo db, along with few of their
	 * attributes being stored in my sql. What we are doing here is to make sure
	 * that we only render required fields from my sql, and then we get the data
	 * set from mongo to get it rendered. The reason is that, core hierarchy of
	 * object collection item -> question -> resource -> content etc is loaded
	 * in my sql and hence we need to start from there
	 */
	@Override
	public void transform(Object object) {
		AssessmentQuestion question = (AssessmentQuestion) object;
		
		if (question.isQuestionNewGen()) {
			Map<String, Object> instance = initializeWithModel(question);
	        TypeContext typeContext = getContext().writeOpenObject();

			customTransform(instance, typeContext);
			getContext().writeComma();
			
			// instance = decorateWithNoSqlDataset(question, instance);
			String data = getQuestionDataFromNoSql(question);
			data = removeObjectScopeFromData(data);
			getContext().write(data);
			getContext().writeCloseObject();
		} else {
			getContext().getTransformer(new Resource()).transform(question);
		}
	}

	/**
	 * Initialize the instance to be rendered with the fields that we are going
	 * to get from MySql DB
	 * 
	 * @param question
	 * @return
	 */
	private Map<String, Object> initializeWithModel(AssessmentQuestion question) {
		Map<String, Object> instance = new HashMap<>();
		instance.put("assetURI", question.getAssetURI());
		instance.put("category", question.getCategory());
		instance.put("createdOn", question.getCreatedOn());
		instance.put("distinguish", question.getDistinguish());
		instance.put("folder", question.getFolder());
		instance.put("gooruOid", question.getGooruOid());
		instance.put("isOer", question.getIsOer());
		instance.put("lastModified", question.getLastModified());
		instance.put("resourceFormat", question.getResourceFormat());
		instance.put("resourceType", question.getResourceType());
		instance.put("sharing", question.getSharing());
		instance.put("thumbnails", question.getThumbnails());
		instance.put("type", question.getType());
		instance.put("url", question.getUrl());
		instance.put("version", question.getVersion());
		instance.put("license", question.getLicense());
//		instance.put("taxonomySet", question.getTaxonomySet());
		instance.put("depthOfKnowledges", question.getDepthOfKnowledges());
		instance.put("educationalUse", question.getEducationalUse());
		instance.put("skills", question.getSkills());
		return instance;
	}

	public void customTransform(Object object, TypeContext typeContext) {
		JSONContext context = getContext();
		Path path = context.getPath();
		Map value = (Map) object;

		for (Object key : value.keySet()) {

			path.enqueue(key != null ? key.toString() : null);

			if (context.isIncluded(key != null ? key.toString() : null,
					value.get(key))) {

				TransformerWrapper transformer = (TransformerWrapper) context
						.getTransformer(value.get(key));

                if(!transformer.isInline()) {
                    if (!typeContext.isFirst()) getContext().writeComma();
                    typeContext.setFirst(false);
                    if( key != null ) {
                        getContext().writeName(key.toString());
                    } else {
                        getContext().writeName(null);
                    }
                }

                if( key != null ) {
                    typeContext.setPropertyName(key.toString());
                } else {
                    typeContext.setPropertyName(null);
                }

				transformer.transform(value.get(key));

			}

			path.pop();

		}
	}
	
	private String getQuestionDataFromNoSql(AssessmentQuestion question) {
		String qData = mongoQuestionsService.getQuestionByIdWithJsonAdjustments(question
				.getGooruOid());
		
		if (qData == null || qData.isEmpty()) {
			LOGGER.error("decorateWithNoSqlDataset: No object in Mongo found with gooruOid : "
					+ question.getGooruOid());
			throw new NotFoundException(
					ServerValidationUtils.generateErrorMessage(
							ParameterProperties.GL0056,
							ParameterProperties.RESOURCE),
					ParameterProperties.GL0056);
		}
		return qData;
	}

	private String removeObjectScopeFromData(String data) {
		// TODO Auto-generated method stub
		if (data == null || data.isEmpty()) {
			return null;
		}
		data = data.trim();
		int openObjectIndex = data.indexOf("{");
		int closeObjectIndex = data.lastIndexOf("}");
		if (openObjectIndex > -1 && closeObjectIndex > -1) {
			if (openObjectIndex + 1 < closeObjectIndex)
				return data.substring(openObjectIndex + 1, closeObjectIndex);
			else
				return "";
		}
		return null;
	}

	private MongoQuestionsService mongoQuestionsService;

	public void setMongoQuestionsService(MongoQuestionsService mongoQuestionsService) {
		this.mongoQuestionsService = mongoQuestionsService;
	}
	

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AssessmentQuestionTransformer.class);
	
	
	/*
	 * TODO: All the below methods are rendered unused with latest approach.
	 * Need to validate the current approach and if testing goes fine, then
	 * we should remove this dead code
	 */
	
	/**
	 * Initialize the instance to be rendered with the fields that we are going
	 * to get from No Sql Note that core initialization is still happening from
	 * relational one and we are decorating it with whatever we get from no sql.
	 * 
	 * @param question
	 * @param instance
	 * @return
	 */

	@SuppressWarnings("unused")
	private Map<String, Object> decorateWithNoSqlDataset(
			AssessmentQuestion question, Map<String, Object> instance) {

		
		String qData = getQuestionDataFromNoSql(question);
		
		JsonReader reader = Json.createReader(new StringReader(qData));
		JsonObject json = reader.readObject();
		reader.close();
		
		if (json != JsonObject.NULL) {
			return xformJsonToMap(json, instance);
		} else {
			LOGGER.error("xformJsonToMongoDoc: Invalid or empty JSON");
			throw new BadRequestException(ServerValidationUtils.generateErrorMessage("GL0007", "Question Data"));
		}
	}

	private Map<String, Object> xformJsonToMap(JsonObject object,
			Map<String, Object> instance) {
		if (instance == null) {
			instance = new HashMap<>();
		}
		Iterator<String> keysItr = object.keySet().iterator();
		while (keysItr.hasNext()) {
			String key = keysItr.next();
			/*
			 * This is the cost of backward compatibility. The data coming in
			 * from question is modeled so that it is easy to map to answers
			 * table with a key, while the response is not modeled that way.
			 * So maintain this compatibility of responses, we have this hack
			 * NOTE that because of this reason, we are hard coding the name
			 * of key here, and not declaring it to be a constant which can
			 * be reused. One should reuse the correct stuff and not the hacks
			 */
			Object value = object.get(key);
			if (key.equalsIgnoreCase("answers") && value instanceof JsonObject) {
				value = ((JsonObject)value).get("answer");
			} else if (key.equalsIgnoreCase("hints") && value instanceof JsonObject) {
				value = ((JsonObject)value).get("hint");
			}
			if (value instanceof JsonArray) {
				value = xformJsonToList((JsonArray) value);
				LOGGER.debug("**Putting value as :" + value.toString() + ":: type is :" + value.getClass().getName());
				instance.put(key, value);
			} else if (value instanceof JsonObject) {
				Map<String, Object> mapValue = xformJsonToMap((JsonObject) value, null);
				LOGGER.debug("**Putting value as :" + value.toString() + ":: type is :" + value.getClass().getName());
				if (MongoQuestionsService.QUESTION_KEY.equalsIgnoreCase(key)) {
					instance.putAll(mapValue);
				} else {
					instance.put(key, value);
				}
			} else if (value instanceof JsonString) {
				String sValue = value.toString();
				LOGGER.debug("**Putting value as :" + sValue + ":: type is :" + sValue.getClass().getName());
				instance.put(key, sValue); 
			} else if (value instanceof JsonNumber) {
				BigDecimal bdValue = ((JsonNumber) value).bigDecimalValue();
				LOGGER.debug("**Putting value as :" + bdValue.toString() + ":: type is :" + bdValue.getClass().getName());
				instance.put(key, bdValue); 
			} else if (value instanceof JsonValue) {
				// Keep JsonValue comparison at the end
				Boolean bValue = null;
				switch (((JsonValue) value).getValueType()) {
				case FALSE:
					bValue = false;
					LOGGER.debug("**Putting value as :" + bValue + ":: type is :" + bValue.getClass().getName());
					instance.put(key, bValue); 
					break;
				case TRUE:
					bValue = true;
					LOGGER.debug("**Putting value as :" + bValue + ":: type is :" + bValue.getClass().getName());
					instance.put(key, bValue); 
					break;
				case NULL:
					bValue = null;
					break;
				default:
					bValue = null;
					LOGGER.error("xformJsonToMap: Unhandled ValueType, value : " + value.getClass().getName());
					break;
				}
			} else {
				LOGGER.error("xformJsonToMap: Unhandled type value : " + value.getClass().getName());
			}
		}
		return instance;

	}
	
	private List<Object> xformJsonToList(JsonArray array) {
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < array.size(); i++) {
			Object value = array.get(i);
			if (value instanceof JsonArray) {
				value = xformJsonToList((JsonArray) value);
			} else if (value instanceof JsonObject) {
				value = xformJsonToMap((JsonObject) value, null);
			}
			LOGGER.debug("**Putting value as :" + value.toString() + ":: type is :" + value.getClass().getName());
			list.add(value);
		}
		return list;
	}

}
