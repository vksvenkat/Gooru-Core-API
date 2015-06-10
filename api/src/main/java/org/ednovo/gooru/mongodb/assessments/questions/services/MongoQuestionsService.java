package org.ednovo.gooru.mongodb.assessments.questions.services;

import java.util.Map;

public interface MongoQuestionsService {

	/**
	 * Create a question using specified id and supplied question data
	 * @param id This is id and in our case, gooruOid which we are going
	 *   to use as id
	 * @param questionData JSON data for question. This does not include
	 *   id or gooruOid. This will be appended by implementation
	 */
	void createQuestion(final String id, final String questionData);

	/**
	 * Create a question by copying existing question in mongodb
	 * @param originalId The original gooruOid of the question (for
	 *   mongo this is _id field as well)
	 * @param targetId The id that needs to be setup for new question
	 */
	void copyQuestion(final String originalId, final String targetId);
	/**
	 * Create a question with complete data packet supplied. 
	 * No modifications are performed with respect to id or gooruOid
	 * If id is present, mongo will use it by default else it will
	 * autogenerate. If gooruOid is present, it will be used, else
	 * it will be missing.
	 * Just for the sake of  completeness, avoid it if you want gooruOid
	 * and id matching constraints
	 * @param questionData JSON data for question
	 */
	void createQuestion(final String questionData);
	
	/**
	 * Update a question specified by id and set the document with new
	 * values as specified in questionData. 
	 * Note that complete document is replaced with this new document
	 * and we do not try to update individual pieces as of now
	 * @param id gooruOid of question to be updated
	 * @param questionData Self contained data for new question
	 */
	void updateQuestion(final String id, final String questionData);
	
	/**
	 * @param id gooruOid of the question
	 * @return String representation of JSON for the question found.
	 * Does not throw. Returns null in case question is not found.
	 * Caller needs to handle it.
	 */
	String getQuestionById(final String id);
	
	/**
	 * This is somewhat weird API
	 * The format in which data arrives in http request for question
	 * is different than the format in which response is expected.
	 * E.g. answers come like {answers:answer:[]} while in response
	 * expectation is {answers:[]}. This API does these kind of hackish
	 * adjustments. Idea is to avoid one more round serialization and
	 * deserialization if done outside
	 * @param id
	 * @return
	 */
	String getQuestionByIdWithJsonAdjustments(final String id);
	
	/**
	 * Find a single question using the filter criteria. This filter
	 * is used to create BSON filter for mongo query. If there are 
	 * multiple results we only return first result. There is no 
	 * mechanism of fetching multiple question (cursor) from this
	 * API
	 * NOTE It may throw json parsing exception
	 * @param filter This is json representation as string, not 
	 *   any arbitrary string
	 * @return
	 */
	String getQuestionByFilterCriteria(final String filter);
	
	/**
	 * Delete the question as specified by id
	 * It is ok, if the question does not exist. API does not throw 
	 * anything in that cases
	 * @param id gooruOid of the questions
	 */
	void deleteQuestionById(final String id);
	
	/**
	 * Delete the question as specified by filter object. This filter
	 * object is used to create BSON filter and then query mongo. Of 
	 * the results received from Mongo, only first question will be 
	 * deleted. It is ok if question does not exist. 
	 * Since the delete is non deterministic in result set obtained
	 * from mongo, this is RISKY operation
	 * NOTE It may throw json parsing exception
	 * @param filter This is json representation as string, and not 
	 *   any arbitrary string
	 */
	void deleteQuestionByFilterCriteria(final String filter);
	
	
	/**
	 * Delete the questions as specified by filter object. This filter
	 * object is used to create BSON filter and then query mongo. Of 
	 * the results received from Mongo, ALL questions will be 
	 * deleted. It is ok if no question matching this criteria exist.
	 * Since this is doing bulk delete, this is RISKY operation 
	 * NOTE It may throw json parsing exception
	 * @param filter This is json representation as string, and not 
	 *   any arbitrary string
	 */
	void deleteQuestionsByFilterCriteria(final String filter);

	
	final static String ID_STRING = "_id";
	final static String TAXONOMY_SET = "question.taxonomySet";
	final static String DEPTH_OF_KNOWLEDGES = "question.depthOfKnowledges";
	final static String EDUCATIONAL_USE = "question.educationalUse";
	final static String GOORU_OID = "gooruOid";
	final static String QUESTION_KEY = "question";
	final static String DATE_CREATED = "dateCreated";
	final static String LAST_UPDATED = "lastUpdated";
	
	/*TODO
	 * Need to provide methods which have capability to include/exclude
	 * select fields from mongo (projection support) for querying
	 */
}
