/*
*QuizServiceImpl.java
* gooru-api
* Created by Gooru on 2014
* Copyright (c) 2014 Gooru. All rights reserved.
* http://www.goorulearning.org/
*      
* Permission is hereby granted, free of charge, to any 
* person obtaining a copy of this software and associated 
* documentation. Any one can use this software without any 
* restriction and can use without any limitation rights 
* like copy,modify,merge,publish,distribute,sub-license or 
* sell copies of the software.
* The seller can sell based on the following conditions:
* 
* The above copyright notice and this permission notice shall be   
* included in all copies or substantial portions of the Software. 
*
*  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY    
*  KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE  
*  WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR   
*  PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS 
*  OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
*  OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
*  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
*  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
*  THE SOFTWARE.
*/

package org.ednovo.gooru.domain.service.quiz;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.annotation.Resource;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionItem;
import org.ednovo.gooru.core.api.model.CollectionType;
import org.ednovo.gooru.core.api.model.ContentType;
import org.ednovo.gooru.core.api.model.Options;
import org.ednovo.gooru.core.api.model.Quiz;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.Sharing;
import org.ednovo.gooru.core.api.model.ShelfType;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.core.exception.UnauthorizedException;
import org.ednovo.gooru.domain.service.ScollectionServiceImpl;
import org.ednovo.gooru.domain.service.assessment.AssessmentService;
import org.ednovo.gooru.domain.service.user.UserService;
import org.ednovo.gooru.json.serializer.util.JsonSerializer;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@Service
public class QuizServiceImpl extends ScollectionServiceImpl implements QuizService, ConstantProperties, ParameterProperties {

	@Autowired
	protected AssessmentService assessmentService;

	@Autowired
	@Resource(name = "userService")
	private UserService userService;

	@Override
	public ActionResponseDTO<Quiz> createQuiz(Quiz quiz, boolean addToMyQuiz, Options options) throws Exception {
		Errors errors = validateQuiz(quiz);
		if (!errors.hasErrors()) {
			if (options != null) {
				quiz.setOptions(JsonSerializer.serializeToJsonWithExcludes(options, EXCLUDE_ALL, OPTIONS_INCLUDE));
			}
			this.getCollectionRepository().save(quiz);
		}
		if (addToMyQuiz) {
			CollectionItem collectionItem = new CollectionItem();
			collectionItem.setItemType(ShelfType.AddedType.ADDED.getAddedType());
			this.createQuizItem(quiz.getGooruOid(), null, collectionItem, quiz.getUser(), CollectionType.USER_QUIZ.getCollectionType());
		}
		return new ActionResponseDTO<Quiz>(quiz, errors);
	}

	@Override
	public ActionResponseDTO<Quiz> updateQuiz(String quizId, Quiz newQuiz, Options newOptions) throws Exception {
		Quiz quiz = this.getCollectionRepository().getQuiz(quizId, null, null);
		Errors errors = validateUpdateQuiz(quiz, newQuiz);
		if (!errors.hasErrors()) {
			Options options = buildOptionsParameter(quiz.getOptions());
			if (newOptions != null) {
				if (newOptions.getIsRandomize() != null) {
					options.setIsRandomize(newOptions.getIsRandomize());
				}
				if (newOptions.getIsRandomizeChoice() != null) {
					options.setIsRandomizeChoice(newOptions.getIsRandomizeChoice());
				}
				if (newOptions.getShowCorrectAnswer() != null) {
					options.setShowCorrectAnswer(newOptions.getShowCorrectAnswer());
				}
				if (newOptions.getShowHints() != null) {
					options.setShowHints(newOptions.getShowHints());
				}
				if (newOptions.getShowScore() != null) {
					options.setShowScore(newOptions.getShowScore());
				}
				if (newOptions.getIsRandomize() != null) {
					options.setIsRandomize(newOptions.getIsRandomize());
				}
				quiz.setOptions(JsonSerializer.serializeToJsonWithExcludes(options, EXCLUDE_ALL, OPTIONS_INCLUDE));
			}
			if (newQuiz.getVocabulary() != null) {
				quiz.setVocabulary(newQuiz.getVocabulary());
			}
			if (newQuiz.getTitle() != null) {
				quiz.setTitle(newQuiz.getTitle());
			}
			if (newQuiz.getDescription() != null) {
				quiz.setDescription(newQuiz.getDescription());
			}
			if (newQuiz.getNarrationLink() != null) {
				quiz.setNarrationLink(newQuiz.getNarrationLink());
			}
			if (newQuiz.getEstimatedTime() != null) {
				quiz.setEstimatedTime(newQuiz.getEstimatedTime());
			}
			if (newQuiz.getNotes() != null) {
				quiz.setNotes(newQuiz.getNotes());
			}
			if (newQuiz.getGoals() != null) {
				quiz.setGoals(newQuiz.getGoals());
			}
			if (newQuiz.getKeyPoints() != null) {
				quiz.setGoals(newQuiz.getKeyPoints());
			}
			if (newQuiz.getLanguage() != null) {
				quiz.setLanguage(newQuiz.getLanguage());
			}
			if (newQuiz.getGrade() != null) {
				quiz.setGrade(newQuiz.getGrade());
			}
			if (newQuiz.getSharing().equalsIgnoreCase(Sharing.PRIVATE.getSharing()) || newQuiz.getSharing().equalsIgnoreCase(Sharing.PUBLIC.getSharing()) || newQuiz.getSharing().equalsIgnoreCase(Sharing.ANYONEWITHLINK.getSharing())) {
				quiz.setSharing(newQuiz.getSharing());
			}

			if (newQuiz.getLastUpdatedUserUid() != null) {
				quiz.setLastUpdatedUserUid(newQuiz.getLastUpdatedUserUid());
			}
			this.getCollectionRepository().save(quiz);
		}
		return new ActionResponseDTO<Quiz>(quiz, errors);
	}

	@Override
	public List<Quiz> getQuizList(String quizId, User user) {
		List<Quiz> quiz = this.getCollectionRepository().getQuizList(quizId, user.getGooruUId(), null);
		if (quiz.size() <= 0) {
			throw new NotFoundException(generateErrorMessage(GL0056, QUIZ));
		}
		return quiz;
	}

	@Override
	public Quiz getQuiz(String quizId, User user) {
		Quiz quiz = this.getCollectionRepository().getQuiz(quizId, user.getGooruUId(), null);
		if (quiz == null) {
			throw new NotFoundException(generateErrorMessage(GL0056, QUIZ));
		}
		return quiz;
	}

	@Override
	public List<Quiz> getMyQuizzes(String limit, String offset, User user) {
		return this.getCollectionRepository().getMyQuizzes(Integer.parseInt(limit), Integer.parseInt(offset), user.getGooruUId(), true, DESC);
	}

	@Override
	public List<Quiz> getQuizzes(Integer limit, Integer offset, User user) {

		if (userService.isContentAdmin(user)) {
			return this.getCollectionRepository().getQuizzes(limit, offset);
		}
		throw new UnauthorizedException("user don't have permission");
	}

	@Override
	public void deleteQuiz(String quizId, User user) {
		Quiz quiz = this.getQuiz(quizId, user);
		if (quiz != null) {
			List<CollectionItem> collectionItems = getCollectionItemByResourceId(quiz.getContentId());
			if (collectionItems != null && collectionItems.size() > 0) {
				this.getCollectionRepository().removeAll(collectionItems);
				this.getCollectionRepository().flush();
			}
			this.getCollectionRepository().remove(Quiz.class, quiz.getContentId());
		}
	}

	@Override
	public ActionResponseDTO<CollectionItem> createQuizItem(String resourceGooruOid, String quizGooruOId, CollectionItem collectionItem, User user, String type) throws Exception {
		Quiz quiz = null;
		if (type != null && type.equalsIgnoreCase(CollectionType.USER_QUIZ.getCollectionType())) {
			if (quizGooruOId != null) {
				quiz = this.getCollectionRepository().getQuiz(quizGooruOId, user.getGooruUId(), type);
			} else {
				quiz = this.getCollectionRepository().getQuiz(null, user.getGooruUId(), CollectionType.USER_QUIZ.getCollectionType());
			}
			if (quiz == null) {
				quiz = new Quiz();
				quiz.setTitle(MY_QUIZ);
				quiz.setCollectionType(CollectionType.USER_QUIZ.getCollectionType());
				quiz.setGooruOid(UUID.randomUUID().toString());
				quiz.setContentType(this.getContentType(ContentType.RESOURCE));
				quiz.setResourceType(this.getResourceType(ResourceType.Type.QUIZ.getType()));
				quiz.setLastModified(new Date(System.currentTimeMillis()));
				quiz.setCreatedOn(new Date(System.currentTimeMillis()));
				quiz.setSharing(Sharing.PRIVATE.getSharing());
				quiz.setUser(user);
				quiz.setOrganization(user.getPrimaryOrganization());
				quiz.setCreator(user);
				quiz.setDistinguish(Short.valueOf("0"));
				quiz.setRecordSource(NOT_ADDED);
				quiz.setIsFeatured(0);
				this.getCollectionRepository().save(quiz);
			}
			collectionItem.setItemType(ShelfType.AddedType.SUBSCRIBED.getAddedType());
		} else {
			quiz = this.getCollectionRepository().getQuiz(quizGooruOId, null, null);
			collectionItem.setItemType(ShelfType.AddedType.ADDED.getAddedType());
		}

		org.ednovo.gooru.core.api.model.Resource resource = this.getResourceRepository().findResourceByContentGooruId(resourceGooruOid);
		Errors errors = validateQuizItem(quiz, resource, collectionItem);
		if (!errors.hasErrors()) {
			collectionItem.setCollection(quiz);
			collectionItem.setResource(resource);
			int sequence = collectionItem.getCollection().getCollectionItems() != null ? collectionItem.getCollection().getCollectionItems().size() + 1 : 1;
			collectionItem.setItemSequence(sequence);
			this.getCollectionRepository().save(collectionItem);
		}

		return new ActionResponseDTO<CollectionItem>(collectionItem, errors);
	}

	@Override
	public ActionResponseDTO<CollectionItem> updateQuizItem(CollectionItem newcollectionItem, String collectionItemId) throws Exception {
		CollectionItem collectionItem = this.getCollectionItemById(collectionItemId);
		Errors errors = validateUpdateQuizItem(newcollectionItem);
		if (!errors.hasErrors()) {
			if (newcollectionItem.getNarration() != null) {
				collectionItem.setNarration(newcollectionItem.getNarration());
			}
			if (newcollectionItem.getNarrationType() != null) {
				collectionItem.setNarrationType(newcollectionItem.getNarrationType());
			}
			if (newcollectionItem.getStart() != null) {
				collectionItem.setStart(newcollectionItem.getStart());
			}
			if (newcollectionItem.getStop() != null) {
				collectionItem.setStop(newcollectionItem.getStop());
			}
			this.getCollectionRepository().save(collectionItem);

		}
		return new ActionResponseDTO<CollectionItem>(collectionItem, errors);
	}

	@Override
	public ActionResponseDTO<CollectionItem> reorderQuizItem(String collectionItemId, int newSequence) throws Exception {
		CollectionItem collectionItem = getCollectionRepository().getCollectionItemById(collectionItemId);
		Errors errors = validateReorderCollectionItem(collectionItem);
		if (!errors.hasErrors()) {
			Collection collection = getCollectionRepository().getCollectionByGooruOid(collectionItem.getCollection().getGooruOid(), null);

			Integer existCollectionItemSequence = collectionItem.getItemSequence();

			if (existCollectionItemSequence > newSequence) {
				for (CollectionItem ci : collection.getCollectionItems()) {

					if (ci.getItemSequence() >= newSequence) {
						if (ci.getItemSequence() <= existCollectionItemSequence) {
							if (ci.getCollectionItemId().equalsIgnoreCase(collectionItem.getCollectionItemId())) {
								ci.setItemSequence(newSequence);
							} else {
								ci.setItemSequence(ci.getItemSequence() + 1);
							}
						}
					}
				}

			} else if (existCollectionItemSequence < newSequence) {
				for (CollectionItem ci : collection.getCollectionItems()) {
					if (ci.getItemSequence() <= newSequence) {
						if (existCollectionItemSequence <= ci.getItemSequence()) {
							if (ci.getCollectionItemId().equalsIgnoreCase(collectionItem.getCollectionItemId())) {
								if (collection.getCollectionItems().size() < newSequence) {
									ci.setItemSequence(collection.getCollectionItems().size());
								} else {
									ci.setItemSequence(newSequence);
								}
							} else {
								ci.setItemSequence(ci.getItemSequence() - 1);
							}
						}
					}
				}
			}

		}
		return new ActionResponseDTO<CollectionItem>(collectionItem, errors);
	}

	@Override
	public void deleteQuizItem(String quizItemId) {
		CollectionItem quizItem = this.getCollectionRepository().getCollectionItemById(quizItemId);
		if (quizItem != null) {
			Collection quiz = quizItem.getCollection();
			this.getCollectionRepository().remove(CollectionItem.class, quizItem.getCollectionItemId());
			reOrderCollectionItems(quiz, quizItemId);
		} else {
			throw new NotFoundException(generateErrorMessage(GL0056, QUIZ_ITEM));
		}
	}

	@Override
	public Quiz copyQuiz(String quizId, Quiz newQuiz, boolean addToMyQuiz, User user) throws Exception {
		Quiz sourceQuiz = this.getQuiz(quizId, user);
		Quiz targetQuiz = null;
		if (sourceQuiz != null) {
			targetQuiz = new Quiz();
			if (newQuiz.getTitle() != null) {
				targetQuiz.setTitle(newQuiz.getTitle());
			} else {
				targetQuiz.setTitle(sourceQuiz.getTitle());
			}
			targetQuiz.setCollectionType(sourceQuiz.getCollectionType());
			targetQuiz.setDescription(sourceQuiz.getDescription());
			targetQuiz.setNotes(sourceQuiz.getNotes());
			targetQuiz.setLanguage(sourceQuiz.getLanguage());
			targetQuiz.setKeyPoints(sourceQuiz.getKeyPoints());
			targetQuiz.setThumbnail(sourceQuiz.getThumbnail());
			if (newQuiz.getGrade() != null) {
				targetQuiz.setGrade(newQuiz.getGrade());
			} else {
				targetQuiz.setGrade(sourceQuiz.getGrade());
			}
			targetQuiz.setEstimatedTime(sourceQuiz.getEstimatedTime());
			targetQuiz.setNarrationLink(sourceQuiz.getNarrationLink());
			targetQuiz.setGooruOid(UUID.randomUUID().toString());
			targetQuiz.setContentType(sourceQuiz.getContentType());
			targetQuiz.setResourceType(sourceQuiz.getResourceType());
			targetQuiz.setLastModified(new Date(System.currentTimeMillis()));
			targetQuiz.setCreatedOn(new Date(System.currentTimeMillis()));
			targetQuiz.setSharing(addToMyQuiz ? Sharing.PRIVATE.getSharing() : sourceQuiz.getSharing());
			targetQuiz.setUser(user);
			targetQuiz.setOrganization(sourceQuiz.getOrganization());
			targetQuiz.setCreator(sourceQuiz.getCreator());
			targetQuiz.setDistinguish(sourceQuiz.getDistinguish());
			targetQuiz.setIsFeatured(sourceQuiz.getIsFeatured());
			this.getCollectionRepository().save(targetQuiz);
			Iterator<CollectionItem> sourceItemIterator = sourceQuiz.getCollectionItems().iterator();
			Set<CollectionItem> collectionItems = new TreeSet<CollectionItem>();
			while (sourceItemIterator.hasNext()) {
				CollectionItem sourceItem = sourceItemIterator.next();
				CollectionItem targetItem = new CollectionItem();
				if (sourceItem.getResource().getResourceType().getName().equalsIgnoreCase(ResourceType.Type.ASSESSMENT_QUESTION.getType())) {
					AssessmentQuestion assessmentQuestion = assessmentService.copyAssessmentQuestion(sourceItem.getCollection().getUser(), sourceItem.getResource().getGooruOid());
					targetItem.setResource(assessmentQuestion);
				}
				targetItem.setResource(sourceItem.getResource());
				targetItem.getResource().setCopiedResourceId(sourceItem.getCollectionItemId());
				targetItem.setItemType(sourceItem.getItemType());
				targetItem.setItemSequence(sourceItem.getItemSequence());
				targetItem.setNarration(sourceItem.getNarration());
				targetItem.setNarrationType(sourceItem.getNarrationType());
				targetItem.setStart(sourceItem.getStart());
				targetItem.setStop(sourceItem.getStop());
				targetItem.setCollection(targetQuiz);
				this.getCollectionRepository().save(targetItem);
				collectionItems.add(targetItem);
			}
			targetQuiz.setCollectionItems(collectionItems);
			this.getCollectionRepository().save(targetQuiz);
			this.getResourceManager().copyResourceRepository(sourceQuiz, targetQuiz);
			if (addToMyQuiz) {
				CollectionItem collectionItem = new CollectionItem();
				collectionItem.setItemType(ShelfType.AddedType.ADDED.getAddedType());
				this.createQuizItem(targetQuiz.getGooruOid(), null, collectionItem, user, CollectionType.USER_QUIZ.getCollectionType());
			}
		}
		return targetQuiz;
	}

	private Errors validateReorderCollectionItem(CollectionItem collectionItem) throws Exception {
		final Errors errors = new BindException(collectionItem, COLLECTION_ITEM);
		if (collectionItem != null) {
			rejectIfNull(errors, collectionItem, COLLECTION_ITEM, GL0056, generateErrorMessage(GL0056, COLLECTION_ITEM));
		}
		return errors;
	}

	private Errors validateUpdateQuizItem(CollectionItem collectionItem) throws Exception {
		Map<String, String> itemType = new HashMap<String, String>();
		itemType.put(ADDED, COLLECTION_ITEM_TYPE);
		itemType.put(SUBSCRIBED, COLLECTION_ITEM_TYPE);
		final Errors errors = new BindException(collectionItem, COLLECTION_ITEM);
		rejectIfNull(errors, collectionItem, COLLECTION_ITEM, GL0056, generateErrorMessage( GL0056, COLLECTION_ITEM));
		rejectIfInvalidType(errors, collectionItem.getItemType(), ITEM_TYPE,GL0007, generateErrorMessage(GL0007, ITEM_TYPE), itemType);
		return errors;
	}

	private Errors validateQuiz(Quiz quiz) throws Exception {
		final Errors errors = new BindException(quiz, QUIZ);
		if (quiz != null) {
			rejectIfNullOrEmpty(errors, quiz.getTitle(), TITLE, GL0006, generateErrorMessage(GL0006, TITLE));
		}
		return errors;
	}

	private Errors validateQuizItem(Quiz quiz, org.ednovo.gooru.core.api.model.Resource resource, CollectionItem collectionItem) throws Exception {
		Map<String, String> itemType = new HashMap<String, String>();
		itemType.put(ADDED, COLLECTION_ITEM_TYPE);
		itemType.put(SUBSCRIBED, COLLECTION_ITEM_TYPE);
		final Errors errors = new BindException(collectionItem, COLLECTION_ITEM);
		if (collectionItem != null) {
			rejectIfNull(errors, quiz, QUIZ, GL0056, generateErrorMessage(GL0056, QUIZ));
			rejectIfNull(errors, resource, RESOURCE, GL0056, generateErrorMessage(GL0056, RESOURCE));
			rejectIfInvalidType(errors, collectionItem.getItemType(), ITEM_TYPE, GL0007, generateErrorMessage(GL0007, ITEM_TYPE), itemType);
		}
		return errors;
	}

	private Errors validateUpdateQuiz(Quiz quiz, Quiz newQuiz) throws Exception {
		final Errors errors = new BindException(quiz, QUIZ);
		rejectIfNull(errors, quiz, QUIZ, GL0006, generateErrorMessage(GL0006, QUIZ));
		return errors;
	}

	@Override
	public Options buildOptionsParameter(String data) {
		return JsonDeserializer.deserialize(data, Options.class);
	}
}
