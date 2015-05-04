package org.ednovo.gooru.mongodb.assessments.questions.services.implementations;

import static com.mongodb.client.model.Filters.eq;

import org.bson.BsonDocument;
import org.bson.Document;
import org.ednovo.gooru.core.application.util.ServerValidationUtils;
import org.ednovo.gooru.core.exception.BadRequestException;
import org.ednovo.gooru.mongodb.MongoClientLocator;
import org.ednovo.gooru.mongodb.assessments.questions.services.MongoQuestionsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteConcernException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;

/**
 * @author ashish
 *
 */


public class MongoQuestionsServiceImpl implements MongoQuestionsService {

	MongoQuestionsServiceImpl(final String mongoDbName,
			final String mongoQuestionsCollectionsName) {
		this.mongoDbName = mongoDbName;
		this.mongoQuestionsCollectionsName = mongoQuestionsCollectionsName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ednovo.gooru.mongodb.assessments.questions.services.MongoQuestionsService
	 * #createQuestion(java.lang.String, java.lang.String)
	 */
	@Override
	public void createQuestion(final String id, final String questionData) {

		if (id == null || questionData == null) {
			LOGGER.error("createQuestion: id and/or questionData null/invalid");
			throw new BadRequestException(
					ServerValidationUtils.generateErrorMessage("GL0007",
							"Question Data/Id"));
		}

		Document doc = Document.parse(questionData);
		doc.append(ID_STRING, id).append(GOORU_OID, id)
				.append(DATE_CREATED, System.currentTimeMillis())
				.append(LAST_UPDATED, System.currentTimeMillis());

		saveQuestion(doc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ednovo.gooru.mongodb.assessments.questions.services.MongoQuestionsService
	 * #copyQuestion(java.lang.String, java.lang.String)
	 */
	@Override
	public void copyQuestion(String originalId, String targetId) {
		if (originalId == null || targetId == null || originalId.isEmpty()
				|| targetId.isEmpty()) {
			LOGGER.error("copyQuestion: originalId and/or targetId null/invalid");
			throw new BadRequestException(
					ServerValidationUtils.generateErrorMessage("GL0007",
							"Question Data/Id"));
		}

		if (originalId.equalsIgnoreCase(targetId)) {
			LOGGER.error("copyQuestion: Source and destination can't be same for copy");
			throw new BadRequestException(
					ServerValidationUtils.generateErrorMessage("GL0007",
							"Source/Target question"));
		}

		MongoCollection<Document> collection = getQuestionsCollection();
		// Get the document but without any kind of exclusion projection
		Document doc = collection.find(eq(ID_STRING, originalId)).first();
		// Now we need to overwrite the values of _id and gooruOid fields
		doc.put(ID_STRING, targetId);
		doc.put(GOORU_OID, targetId);
		doc.put(DATE_CREATED, System.currentTimeMillis());
		doc.put(LAST_UPDATED, System.currentTimeMillis());

		saveQuestion(doc);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ednovo.gooru.mongodb.assessments.questions.services.MongoQuestionsService
	 * #updateQuestion(java.lang.String, java.lang.String)
	 */
	@Override
	public void updateQuestion(String id, String questionData) {
		if (id == null || questionData == null) {
			LOGGER.error("updateQuestion: id and/or questionData null/invalid");
			throw new BadRequestException(
					ServerValidationUtils.generateErrorMessage("GL0007",
							"Question Data/Id"));
		}

		Document doc = Document.parse(questionData);
		MongoCollection<Document> collection = getQuestionsCollection();
		// Modify the gooruOid before we put it back, but don't put _id
		doc.append(GOORU_OID, id);
		doc.put(LAST_UPDATED, System.currentTimeMillis());
		collection.updateOne(eq(ID_STRING, id), new Document("$set", doc));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ednovo.gooru.mongodb.assessments.questions.services.MongoQuestionsService
	 * #createQuestion(java.lang.String)
	 */
	@Override
	public void createQuestion(final String questionData) {
		if (questionData == null) {
			LOGGER.error("createQuestion: QuestionData null/invalid");
			throw new BadRequestException(
					ServerValidationUtils.generateErrorMessage("GL0007",
							"Question Data"));
		}

		Document doc = Document.parse(questionData);

		saveQuestion(doc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ednovo.gooru.mongodb.assessments.questions.services.MongoQuestionsService
	 * #getQuestionById(java.lang.String)
	 * 
	 * NOTE We are storing and providing back three things from relational DB
	 * These are taxonomySet, depthOfKnowledge and skills. Reason being that
	 * these things can have relational implications and can be modified as part
	 * of collection/collection item not in purview of question. Hence this way.
	 */
	@Override
	public String getQuestionById(final String id) {
		Document doc = getDocumentById(id);

		// NOTE: We do not throw here, let the caller handle it
		return doc != null ? doc.toJson() : null;
	}

	@Override
	public String getQuestionByIdWithJsonAdjustments(String id) {
		Document doc = getDocumentById(id);
		if (doc != null) {
			Document questionDoc = (Document) doc.get(QUESTION_KEY);
			Document answersDoc = (Document) questionDoc.get("answers");
			Object answer = answersDoc.get("answer");
			Document hintsDoc = (Document) questionDoc.get("hints");
			Object hint = hintsDoc.get("hint");

			questionDoc.put("answers", answer);
			questionDoc.put("hints", hint);

			return questionDoc.toJson();
		} else {
			return null;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ednovo.gooru.mongodb.assessments.questions.services.MongoQuestionsService
	 * #getQuestionByFilterCriteria(java.util.Map)
	 */
	@Override
	public String getQuestionByFilterCriteria(final String filter) {
		BsonDocument filterDocument = createBsonDocumentFromFilterString(filter);

		MongoCollection<Document> collection = getQuestionsCollection();
		Document doc = collection.find(filterDocument)
				.projection(new Document(ID_STRING, 0)).first();

		// NOTE: We do not throw here, let the caller handle it
		return doc != null ? doc.toJson() : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ednovo.gooru.mongodb.assessments.questions.services.MongoQuestionsService
	 * #deleteQuestionById(java.lang.String)
	 */
	@Override
	public void deleteQuestionById(final String id) {
		if (id == null) {
			LOGGER.error("deleteQuestionById: Invalid id");
			return;
		}
		MongoCollection<Document> collection = getQuestionsCollection();
		DeleteResult dr = collection.deleteOne(eq(ID_STRING, id));
		// Log since we are deleting
		LOGGER.warn("deleteQuestionById: Document delete count : "
				+ dr.getDeletedCount());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ednovo.gooru.mongodb.assessments.questions.services.MongoQuestionsService
	 * #deleteQuestionByFilterCriteria(java.util.Map)
	 */
	@Override
	public void deleteQuestionByFilterCriteria(final String filter) {
		deleteMatchingQuestions(filter, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ednovo.gooru.mongodb.assessments.questions.services.MongoQuestionsService
	 * #deleteQuestionsByFilterCriteria(java.util.Map)
	 */
	@Override
	public void deleteQuestionsByFilterCriteria(final String filter) {
		deleteMatchingQuestions(filter, true);

	}

	private Document getDocumentById(final String id) {
		if (id == null) {
			LOGGER.error("getQuestionById: Invalid id");
			return null;
		}
		MongoCollection<Document> collection = getQuestionsCollection();
		Document doc = collection.find(eq(ID_STRING, id))
				.projection(getExcludeFilter()).first();
		return doc;
	}

	/**
	 * @param filter
	 */
	private void deleteMatchingQuestions(final String filter, boolean deleteMany) {
		BsonDocument filterDocument = createBsonDocumentFromFilterString(filter);
		MongoCollection<Document> collection = getQuestionsCollection();
		DeleteResult dr = null;
		if (deleteMany)
			dr = collection.deleteMany(filterDocument);
		else
			dr = collection.deleteOne(filterDocument);

		// Log since we are deleting
		LOGGER.warn("deleteMatchingQuestions: Document delete count : "
				+ dr.getDeletedCount());
		LOGGER.warn("deleteMatchingQuestions: Delete criteria - " + filter);
	}

	private void saveQuestion(final Document question) {

		MongoCollection<Document> collection = getQuestionsCollection();

		try {
			collection.insertOne(question);
		} catch (MongoWriteException | MongoWriteConcernException e) {
			LOGGER.error("saveQuestion: failed to insert into Mongo db", e);
			throw e;
		} catch (MongoException e) {
			LOGGER.error("saveQuestion: failed to insert into Mongo db", e);
			throw e;
		}

	}

	/**
	 * @param filter
	 * @return
	 */
	private BsonDocument createBsonDocumentFromFilterString(final String filter) {
		if (filter == null) {
			LOGGER.error("createBsonDocumentFromFilterString: Invalid/null filter");
			throw new BadRequestException(
					ServerValidationUtils.generateErrorMessage("GL0007",
							"Query filter"));
		}
		BsonDocument filterDocument = null;
		try {
			filterDocument = BsonDocument.parse(filter);
		} catch (Exception e) {
			LOGGER.error(
					"getQuestionByFilterCriteria: Not able to parse filter to json document. Json is: "
							+ filter, e);
			throw e;
		}
		return filterDocument;
	}

	/**
	 * Prune the keys that we don't want to pass on to callers from the JSON
	 * representation of questions
	 * 
	 * @return
	 */
	private Document getExcludeFilter() {
		return new Document(ID_STRING, 0).append(TAXONOMY_SET, 0)
				.append(DEPTH_OF_KNOWLEDGES, 0).append(EDUCATIONAL_USE, 0);
	}

	/**
	 * @return MongoCollection to enable futher operations
	 */
	private MongoCollection<Document> getQuestionsCollection() {
		MongoClient mongo = mongoClientLocator.locate();
		MongoDatabase db = mongo.getDatabase(mongoDbName);
		MongoCollection<Document> collection = db
				.getCollection(mongoQuestionsCollectionsName);

		return collection;
	}

	@Autowired
	private MongoClientLocator mongoClientLocator;

	private final String mongoDbName;
	private final String mongoQuestionsCollectionsName;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(MongoQuestionsServiceImpl.class);

}
