/////////////////////////////////////////////////////////////
// FeaturedServiceImpl.java
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
package org.ednovo.gooru.domain.service.featured;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.Assessment;
import org.ednovo.gooru.core.api.model.AssessmentQuestion;
import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.CodeUserAssoc;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.FeaturedSet;
import org.ednovo.gooru.core.api.model.FeaturedSetItems;
import org.ednovo.gooru.core.api.model.Learnguide;
import org.ednovo.gooru.core.api.model.Profile;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.StorageAccount;
import org.ednovo.gooru.core.api.model.StorageArea;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.application.util.ResourceMetaInfo;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.core.exception.BadRequestException;
import org.ednovo.gooru.core.exception.NotFoundException;
import org.ednovo.gooru.domain.service.BaseServiceImpl;
import org.ednovo.gooru.domain.service.CollectionService;
import org.ednovo.gooru.domain.service.search.SearchResults;
import org.ednovo.gooru.domain.service.taxonomy.TaxonomyService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.CollectionRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.content.ContentRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.customTable.CustomTableRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.featured.FeaturedRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.question.CommentRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.ResourceRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.storage.StorageRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.taxonomy.TaxonomyRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeaturedServiceImpl extends BaseServiceImpl implements FeaturedService, ParameterProperties, ConstantProperties {

	@Autowired
	private FeaturedRepository featuredRepository;

	@Autowired
	private TaxonomyRespository taxonomyRespository;

	@Autowired
	private ResourceRepository resourceRepository;

	@Autowired
	private ContentRepository contentRepository;

	@Autowired
	private CollectionService collectionService;

	@Autowired
	private TaxonomyService taxonomyService;

	@Autowired
	private CollectionRepository collectionRepository;

	@Autowired
	private CustomTableRepository customTableRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private StorageRepository storageRepository;

	private Integer lessonLimit = 3;

	@Override
	public List<FeaturedSet> getFeaturedList(int limit, boolean random, String featuredSetName, String themeCode) throws Exception {
		List<FeaturedSet> featuredSet = this.getFeaturedRepository().getFeaturedList(null, limit, featuredSetName, themeCode, null);
		if (featuredSet != null) {
			this.getFeaturedResource(featuredSet);
		}

		return featuredSet;
	}

	@Override
	public List<FeaturedSet> getFeaturedList(int limit, boolean random, String featuredSetName, String themeCode, String themetype) throws Exception {
		List<FeaturedSet> featuredSet = this.getFeaturedRepository().getFeaturedList(null, limit, featuredSetName, themeCode, themetype);
		if (featuredSet != null) {
			this.getFeaturedResource(featuredSet);
		}

		return featuredSet;
	}

	@Override
	public List<FeaturedSet> getFeaturedTheme(int limit) throws Exception {

		List<FeaturedSet> featuredSet = this.getFeaturedRepository().getFeaturedTheme(limit);

		return featuredSet;
	}

	@Override
	public void getFeaturedResource(List<FeaturedSet> featuredSet) throws Exception {
		for (FeaturedSet featured : featuredSet) {
			List<Resource> resources = new ArrayList<Resource>();
			List<Learnguide> collections = new ArrayList<Learnguide>();
			List<AssessmentQuestion> questions = new ArrayList<AssessmentQuestion>();
			List<Collection> scollections = new ArrayList<Collection>();
			if (featured.getFeaturedSetItems() != null) {
				for (FeaturedSetItems featuredSetItem : featured.getFeaturedSetItems()) {
					if (featuredSetItem.getContent() instanceof AssessmentQuestion) {
						questions.add((AssessmentQuestion) featuredSetItem.getContent());
						if (featuredSetItem.getParentContent() != null && featuredSetItem.getParentContent() instanceof Assessment) {
							((AssessmentQuestion) featuredSetItem.getContent()).setAssessmentGooruId(featuredSetItem.getParentContent().getGooruOid());
						}
					} else if (featuredSetItem.getContent() instanceof Learnguide) {
						collections.add((Learnguide) featuredSetItem.getContent());
					} else if (featuredSetItem.getContent() instanceof Collection) {
						scollections.add((Collection) featuredSetItem.getContent());
					} else if (featuredSetItem.getContent() instanceof Resource) {
						resources.add((Resource) featuredSetItem.getContent());
					}
				}
			}
			featured.setResources(resources);
			featured.setQuestions(questions);
			featured.setCollections(collections);
			featured.setScollections(scollections);
			if (scollections != null) {
				this.setCollectionMetaInfo(scollections, null);
			}
		}

	}

	@Override
	public List<Map<String, Object>> getLibraryItem(String type, String libraryName) {
		List<Code> codes = this.getTaxonomyRespository().findCodeByParentCodeId(type.equalsIgnoreCase(STANDARD) ? null : type, null, null, null, true, LIBRARY, getOrganizationCode(libraryName), null, null);
		List<Map<String, Object>> codeMap = new ArrayList<Map<String, Object>>();
		if (type.equalsIgnoreCase(STANDARD)) {
			for (Code code : codes) {
				List<Code> nodes = this.getTaxonomyRespository().findCodeByParentCodeId(String.valueOf(code.getCodeId()), null, null, null, true, LIBRARY, null, null, null);
				List<Map<String, Object>> node = new ArrayList<Map<String, Object>>();
				for (Code codeNode : nodes) {
					node.add(getCode(codeNode, null, NODE));
				}
				codeMap.add(getCode(code, node, NODE));
			}
		} else {
			for (Code code : codes) {
				codeMap.add(getCode(code, null, NODE));
			}
		}
		return codeMap;
	}

	@Override
	public List<Map<String, Object>> getLibraryCourse(String code, String ChildCode, String libraryName, String rootNodeId) {
		int collectionCount = 0;
		int unitCount = 0;
		List<Code> courses = this.getTaxonomyRespository().findCodeByParentCodeId(code, null, null, null, true, LIBRARY, getOrganizationCode(libraryName), rootNodeId, null);
		List<Map<String, Object>> courseMap = new ArrayList<Map<String, Object>>();
		for (Code course : courses) {
			List<Code> units = this.getTaxonomyRespository().findCodeByParentCodeId(String.valueOf(course.getCodeId()), null, null, null, true, LIBRARY, getOrganizationCode(libraryName), rootNodeId, null);
			List<Map<String, Object>> unitMap = new ArrayList<Map<String, Object>>();
			unitCount = 0;
			for (Code unit : units) {
				List<Map<String, Object>> topicMap = new ArrayList<Map<String, Object>>();
				List<Map<String, Object>> collectionUnitMap = null;
				Integer collectionUnitCount = null;
				List<Object[]> collectionUnitListAll = this.getFeaturedRepository().getLibraryCollection(String.valueOf(unit.getCodeId()), String.valueOf(ChildCode), null, null, true, null);
				if (collectionUnitListAll != null && collectionUnitListAll.size() > 0) {
					collectionUnitCount = collectionUnitListAll.size();
				}
				if (unitCount == 0) {
					List<Code> topics = this.getTaxonomyRespository().findCodeByParentCodeId(String.valueOf(unit.getCodeId()), null, null, null, true, LIBRARY, getOrganizationCode(libraryName), rootNodeId, null);
					List<Object[]> collectionUnitList = null;
					collectionUnitList = this.getFeaturedRepository().getLibraryCollection(String.valueOf(unit.getCodeId()), String.valueOf(ChildCode), 10, 0, false, null);
					if (collectionUnitList != null && collectionUnitList.size() > 0) {
						if (collectionUnitListAll != null && collectionUnitListAll.size() > 0) {
							collectionUnitCount = collectionUnitListAll.size();
						}
						collectionUnitMap = new ArrayList<Map<String, Object>>();
						for (Object[] collectionObject : collectionUnitList) {
							Map<String, Object> collectionUnit = this.getCollectionService().getCollection(String.valueOf(collectionObject[0]), new HashMap<String, Object>(), rootNodeId);
							if (collectionUnit != null) {
								collectionUnitMap.add(collectionUnit);
							}
						}
					}
					if (collectionUnitMap == null || collectionUnitMap.size() == 0) {
						for (Code topic : topics) {
							Integer collectionTopicCount = null;
							List<Object[]> collectionTopicList = this.getFeaturedRepository().getLibraryCollection(String.valueOf(topic.getCodeId()), String.valueOf(ChildCode), 10, 0, false, null);
							List<Map<String, Object>> collectionTopicMap = null;
							if (collectionTopicList != null && collectionTopicList.size() > 0) {
								List<Object[]> collectionTopicListAll = this.getFeaturedRepository().getLibraryCollection(String.valueOf(topic.getCodeId()), String.valueOf(ChildCode), null, null, true, null);
								if (collectionTopicListAll != null && collectionTopicListAll.size() > 0) {
									collectionTopicCount = collectionTopicListAll.size();
								}
								collectionTopicMap = new ArrayList<Map<String, Object>>();
								collectionCount = 0;
								for (Object[] collectionObject : collectionTopicList) {
									Map<String, Object> collectionTopic = new HashMap<String, Object>();
									collectionTopic.put(GOORU_OID, collectionObject[0]);
									collectionTopic.put(TITLE, collectionObject[1]);
									if (collectionCount == 0) {
										collectionTopic = this.getCollectionService().getCollection(String.valueOf(collectionObject[0]), collectionTopic, rootNodeId);
									}
									collectionTopicMap.add(collectionTopic);
									collectionCount++;
								}
							}
							List<Map<String, Object>> lessonMap = new ArrayList<Map<String, Object>>();
							List<Code> allLessons = null;
							List<Code> lessons = this.getTaxonomyRespository().findCodeByParentCodeId(String.valueOf(topic.getCodeId()), null, lessonLimit, 0, false, LIBRARY, getOrganizationCode(libraryName), rootNodeId, null);
							for (Code lesson : lessons) {
								List<Object[]> collectionLessonList = this.getFeaturedRepository().getLibraryCollection(String.valueOf(lesson.getCodeId()), String.valueOf(ChildCode), null, null, true, null);
								collectionCount = 0;
								List<Map<String, Object>> collectionLessonMap = new ArrayList<Map<String, Object>>();
								for (Object[] collectionObject : collectionLessonList) {
									Map<String, Object> collection = new HashMap<String, Object>();
									collection.put(GOORU_OID, collectionObject[0]);
									collection.put(TITLE, collectionObject[1]);
									if (collectionCount == 0) {
										collection = this.getCollectionService().getCollection(String.valueOf(collectionObject[0]), collection, rootNodeId);
									}
									collectionCount++;
									collectionLessonMap.add(collection);
								}
								List<Map<String, Object>> conceptMap = new ArrayList<Map<String, Object>>();
								List<Code> concepts = this.getTaxonomyRespository().findCodeByParentCodeId(String.valueOf(lesson.getCodeId()), null, 3, 0, true, LIBRARY, getOrganizationCode(libraryName), rootNodeId, null);
								for (Code concept : concepts) {
									List<Object[]> collectionList = this.getFeaturedRepository().getLibraryCollection(String.valueOf(concept.getCodeId()), String.valueOf(ChildCode), null, null, true, null);
									List<Map<String, Object>> collectionMap = new ArrayList<Map<String, Object>>();
									for (Object[] collectionObject : collectionList) {
										Map<String, Object> collection = new HashMap<String, Object>();
										collection.put(GOORU_OID, collectionObject[0]);
										collection.put(TITLE, collectionObject[1]);
										if (collectionCount == 0) {
											collection = this.getCollectionService().getCollection(String.valueOf(collectionObject[0]), collection, rootNodeId);
										}
										collectionCount++;
										collectionMap.add(collection);
									}
									if (collectionMap != null && collectionMap.size() > 0) {
										conceptMap.add(getCode(concept, collectionMap, COLLECTION, null, getOrganizationCode(libraryName), null, null, null));
									}

								}

								if ((collectionLessonMap != null && collectionLessonMap.size() > 0) || (conceptMap != null && conceptMap.size() > 0)) {
									lessonMap.add(getCode(lesson, collectionLessonMap, COLLECTION, null, getOrganizationCode(libraryName), conceptMap, null, null));
								}
							}
							allLessons = this.getTaxonomyRespository().findCodeByParentCodeId(String.valueOf(topic.getCodeId()), null, 0, 3, true, LIBRARY, getOrganizationCode(libraryName), rootNodeId, null);

							topicMap.add(getCode(topic, lessonMap, LESSON, collectionTopicCount != null ? collectionTopicCount : (allLessons != null ? allLessons.size() : 0), getOrganizationCode(libraryName), null, collectionTopicMap, COLLECTION));

						}
					}
				}
				unitMap.add(getCode(unit, topicMap, TOPIC, collectionUnitCount, getOrganizationCode(libraryName), null, collectionUnitMap, COLLECTION));
				unitCount++;

			}
			courseMap.add(getCode(course, unitMap, UNIT, null, getOrganizationCode(libraryName), null, null, null));
		}
		return courseMap;
	}

	@Override
	public Map<Object, Object> getLibrary(String type, String libraryName) {
		this.lessonLimit = 3;
		List<Object[]> results = this.getFeaturedRepository().getLibrary(null, true, libraryName);
		Map<Object, Object> subjectMap = new HashMap<Object, Object>();
		for (Object[] object : results) {
			Map<String, Object> lib = new HashMap<String, Object>();
			lib.put(CODE, object[0] == null ? FEATURED : object[0]);
			if (object[2].equals(type) || (object[0] != null && String.valueOf(object[0]).equalsIgnoreCase(type))) {
				List<Map<String, Object>> courseMap = null;
				if (object[2].equals(STANDARD)) {
					this.lessonLimit = 10;
					List<Code> curriculums = this.getTaxonomyRespository().findCodeByParentCodeId(null, null, null, null, true, LIBRARY, getOrganizationCode(libraryName), null, "0");
					List<Map<String, Object>> curriculumMap = new ArrayList<Map<String, Object>>();
					for (Code curriculum : curriculums) {
						List<Code> subjects = this.getTaxonomyRespository().findCodeByParentCodeId(String.valueOf(curriculum.getCodeId()), null, null, null, true, LIBRARY, getOrganizationCode(libraryName), String.valueOf(curriculum.getCodeId()), "1");
						courseMap = new ArrayList<Map<String, Object>>();
						for (Code subject : subjects) {
							courseMap.addAll(this.getLibraryCourse(String.valueOf(subject.getCodeId()), String.valueOf(object[1]), libraryName, String.valueOf(curriculum.getRootNodeId())));
						}
						curriculumMap.add(getCode(curriculum, courseMap, "course", null, getOrganizationCode(libraryName), null, null, null));

					}
					lib.put(DATA_OBJECT, curriculumMap);
				} else {
					courseMap = this.getLibraryCourse(String.valueOf(lib.get(CODE)), String.valueOf(object[1]), libraryName, "20000");
					lib.put(DATA_OBJECT, courseMap);
				}
			}
			subjectMap.put(object[2], lib);
		}

		return subjectMap;
	}

	@Override
	public List<Map<String, Object>> getLibraryTopic(String topicId, Integer limit, Integer offset, String type, String libraryName, String rootNodeId) {
		List<Object[]> results = this.getFeaturedRepository().getLibrary(type, false, libraryName);
		String featuredId = null;
		if (results != null && results.size() > 0) {
			Object[] obj = results.get(0);
			featuredId = String.valueOf(obj[1]);
		}
		List<Object[]> collectionTopicList = this.getFeaturedRepository().getLibraryCollection(topicId, featuredId, limit, offset, false, null);
		List<Map<String, Object>> collectionTopicMap = null;
		if (collectionTopicList != null && collectionTopicList.size() > 0) {
			int collectionCount = 0;
			collectionTopicMap = new ArrayList<Map<String, Object>>();
			for (Object[] collectionObject : collectionTopicList) {
				Map<String, Object> collectionTopic = new HashMap<String, Object>();
				collectionTopic.put(GOORU_OID, collectionObject[0]);
				collectionTopic.put(TITLE, collectionObject[1]);
				if (collectionCount == 0) {
					collectionTopic = this.getCollectionService().getCollection(String.valueOf(collectionObject[0]), collectionTopic, rootNodeId);
				}
				collectionTopicMap.add(collectionTopic);
				collectionCount++;
			}
			return collectionTopicMap;
		} else {
			List<Map<String, Object>> lessonMap = new ArrayList<Map<String, Object>>();
			List<Code> lessons = this.getTaxonomyRespository().findCodeByParentCodeId(topicId, null, limit, offset, false, libraryName, getOrganizationCode(libraryName), rootNodeId, null);
			for (Code lesson : lessons) {
				List<Object[]> collectionList = this.getFeaturedRepository().getLibraryCollection(String.valueOf(lesson.getCodeId()), featuredId, null, null, true, null);
				List<Map<String, Object>> collectionMap = new ArrayList<Map<String, Object>>();
				for (Object[] collectionObject : collectionList) {
					Map<String, Object> collection = new HashMap<String, Object>();
					collection.put(GOORU_OID, collectionObject[0]);
					collection.put(TITLE, collectionObject[1]);
					collectionMap.add(collection);
				}
				List<Code> concepts = this.getTaxonomyRespository().findCodeByParentCodeId(String.valueOf(lesson.getCodeId()), null, 3, 0, true, LIBRARY, getOrganizationCode(libraryName), rootNodeId, null);
				List<Map<String, Object>> conceptMap = new ArrayList<Map<String, Object>>();
				for (Code concept : concepts) {
					List<Object[]> collectionConceptList = this.getFeaturedRepository().getLibraryCollection(String.valueOf(concept.getCodeId()), featuredId, null, null, true, null);
					List<Map<String, Object>> collectionConceptMap = new ArrayList<Map<String, Object>>();
					for (Object[] collectionObject : collectionConceptList) {
						Map<String, Object> collection = new HashMap<String, Object>();
						collection.put(GOORU_OID, collectionObject[0]);
						collection.put(TITLE, collectionObject[1]);
						collectionConceptMap.add(collection);
					}
					if (collectionMap != null && collectionMap.size() > 0) {
						conceptMap.add(getCode(concept, collectionMap, COLLECTION, null, getOrganizationCode(libraryName), null, null, null));
					}
				}
				if ((collectionMap != null && collectionMap.size() > 0) || conceptMap != null && conceptMap.size() > 0) {
					lessonMap.add(getCode(lesson, collectionMap, COLLECTION, null, getOrganizationCode(libraryName), null, null, null));
				}
			}
			return lessonMap;
		}

	}

	private Map<String, Object> getCode(Code code, List<Map<String, Object>> childern, String type, Integer count, String organizationCode, List<Map<String, Object>> concept, List<Map<String, Object>> collectionChildern, String collectionType) {
		Map<String, Object> codeMap = getCode(code, childern, type);
		if (concept != null) {
			codeMap.put(CONCEPT, concept);
		}
		if (code.getDepth() == 2) {
			List<CodeUserAssoc> codeUserAssoc = this.getTaxonomyRespository().getUserCodeAssoc(code.getCodeId(), organizationCode);
			codeMap.put(CREATOR, getUser(codeUserAssoc) != null && getUser(codeUserAssoc).size() > 0 ? getUser(codeUserAssoc).get(0) : null);
			codeMap.put(USER, getUser(codeUserAssoc));
		}
		if (count != null) {
			codeMap.put(COUNT, count);
		}
		codeMap.put(collectionType, collectionChildern);
		return codeMap;

	}

	private Map<String, Object> getCode(Code code, List<Map<String, Object>> childern, String type) {
		Map<String, Object> codeMap = new HashMap<String, Object>();
		codeMap.put(CODE, code.getCommonCoreDotNotation() == null ? code.getdisplayCode() : code.getCommonCoreDotNotation());
		codeMap.put(CODE_ID, code.getCodeId());
		codeMap.put(CODE_TYPE, code.getCodeType());
		codeMap.put(LABEL, code.getLabel());
		codeMap.put(PARENT_ID, code.getParent() != null ? code.getParent().getCodeId() : null);
		codeMap.put(THUMBNAILS, code.getThumbnails());
		codeMap.put(type, childern);
		return codeMap;
	}

	private List<Map<String, String>> getUser(List<CodeUserAssoc> codeUserAssocList) {
		List<Map<String, String>> userMapList = null;
		if (codeUserAssocList != null && codeUserAssocList.size() > 0) {
			userMapList = new ArrayList<Map<String, String>>();
			for (CodeUserAssoc codeUserAssoc : codeUserAssocList) {
				if (codeUserAssoc.getUser() != null) {
					Map<String, String> userMap = new HashMap<String, String>();
					userMap.put(FIRST_NAME, codeUserAssoc.getUser().getFirstName());
					userMap.put(LAST_NAME, codeUserAssoc.getUser().getLastName());
					userMap.put(USER_NAME, codeUserAssoc.getUser().getUsername());
					userMap.put(GOORU_UID, codeUserAssoc.getUser().getPartyUid());
					Profile profile = this.getUserRepository().getProfile(codeUserAssoc.getUser(), false);
					userMap.put(GENDER, (profile != null && profile.getGender() != null) ? profile.getGender().getName() : "");
					userMap.put(IS_OWNER, String.valueOf(codeUserAssoc.getIsOwner()));
					userMapList.add(userMap);
				}
			}
		}
		return userMapList;
	}

	public CommentRepository getCommentRepository() {
		return commentRepository;
	}

	public void setCommentRepository(CommentRepository commentRepository) {
		this.commentRepository = commentRepository;
	}

	private void setCollectionMetaInfo(List<Collection> collections, String rootNodeId) {
		for (Collection collection : collections) {
			ResourceMetaInfo collectionMetaInfo = new ResourceMetaInfo();
			collectionMetaInfo.setCourse(this.getCollectionService().getCourse(collection.getTaxonomySet()));
			collectionMetaInfo.setStandards(this.getCollectionService().getStandards(collection.getTaxonomySet(), true, rootNodeId));
			collection.setMetaInfo(collectionMetaInfo);
		}

	}

	@Override
	public FeaturedSet saveOrUpdateFeaturedSet(Integer featuredSetId, String name, Boolean activeFlag, Integer sequence, String themeCode) {
		FeaturedSet featuredSet = null;
		Code code = null;
		if (featuredSetId != null) {
			featuredSet = this.getFeaturedRepository().getFeaturedSetById(featuredSetId);
		} else if (name != null && themeCode != null) {
			featuredSet = this.getFeaturedRepository().getFeaturedSetByThemeNameAndCode(name, themeCode);
		}
		if (featuredSet == null) {
			featuredSet = new FeaturedSet();
		}
		if (name != null) {
			featuredSet.setName(name);
			code = this.getContentRepository().getCodeByName(name);
		}

		if (activeFlag != null) {
			featuredSet.setActiveFlag(activeFlag);
		}
		if (sequence != null) {
			featuredSet.setSequence(sequence);
		}
		if (themeCode != null) {
			featuredSet.setThemeCode(themeCode);
		}
		this.getFeaturedRepository().save(featuredSet);
		return featuredSet;
	}

	@Override
	public FeaturedSetItems saveOrUpdateFeaturedSetItems(FeaturedSet featuredSet, String gooruContentId, Integer featuredSetItemId, String parentGooruContentId, Integer sequence) {

		Content content = this.getContentRepository().findContentByGooruId(gooruContentId);
		Content parentContent = this.getContentRepository().findContentByGooruId(parentGooruContentId);
		FeaturedSetItems featuredSetItems = null;
		if (featuredSet.getFeaturedSetId() != null && sequence != null) {
			featuredSetItems = this.getFeaturedRepository().getFeaturedSetItem(featuredSet.getFeaturedSetId(), sequence);
		}
		if (featuredSetItems == null) {
			featuredSetItems = new FeaturedSetItems();
		}
		if (sequence != null) {
			featuredSetItems.setSequence(sequence);
		}
		if (parentContent != null) {
			featuredSetItems.setParentContent(parentContent);
		}
		if (content != null) {
			featuredSetItems.setContent(content);
		}
		if (featuredSet != null) {
			featuredSetItems.setFeaturedSet(featuredSet);
		}
		this.getFeaturedRepository().save(featuredSetItems);

		return featuredSetItems;
	}

	@Override
	public FeaturedSetItems updateFeaturedContent(String type, Integer featuredSetItemId, FeaturedSetItems newFeaturedSetItems) {
		Content content = this.getContentRepository().findContentByGooruId(newFeaturedSetItems.getContent().getGooruOid());
		if (content == null) {
			throw new NotFoundException("Content not found");
		}
		FeaturedSetItems featuredSetItem = this.getFeaturedRepository().getFeaturedItemByIdAndType(featuredSetItemId, type);
		if (featuredSetItem == null) {
			throw new NotFoundException("featuredSetItem not found");
		}
		if (newFeaturedSetItems.getFeaturedSet() != null && newFeaturedSetItems.getFeaturedSet().getName() != null) {
			featuredSetItem.getFeaturedSet().setName(newFeaturedSetItems.getFeaturedSet().getName());
		}
		featuredSetItem.setContent(content);
		this.getFeaturedRepository().save(featuredSetItem);
		return featuredSetItem;
	}

	@Override
	public List<Map<Object, Object>> getLibraryContributor(String libraryName) {
		List<User> users = this.getTaxonomyRespository().getFeaturedUser(getOrganizationCode(libraryName));
		List<Map<Object, Object>> contributors = new ArrayList<Map<Object, Object>>();
		for (User user : users) {
			if (user != null) {
				Map<Object, Object> contributor = new HashMap<Object, Object>();
				contributor.put(FIRST_NAME, user.getFirstName());
				contributor.put(LAST_NAME, user.getLastName());
				contributor.put(USER_NAME, user.getUsername());
				contributor.put(GOORU_UID, user.getPartyUid());
				Profile profile = this.getUserRepository().getProfile(user, false);
				contributor.put(GENDER, (profile != null && profile.getGender() != null) ? profile.getGender().getName() : "");
				contributor.put(COURSES, this.getTaxonomyRespository().getCodeByDepth(getOrganizationCode(libraryName), Short.valueOf("2"), user.getPartyUid()));
				contributors.add(contributor);
			}
		}
		return contributors;
	}

	@Override
	public List<Map<String, Object>> getLibraryUnit(String unitId, String type, Integer offset, Integer limit, String libraryName, String rootNodeId) {

		int collectionCount = 0;
		List<Object[]> results = this.getFeaturedRepository().getLibrary(type, false, libraryName);
		String featuredId = null;
		if (results != null && results.size() > 0) {
			Object[] obj = results.get(0);
			featuredId = String.valueOf(obj[1]);
		}
		Integer lessonLimit = 3;
		if (type.equals(STANDARD)) {
			lessonLimit = 10;
		}
		List<Map<String, Object>> topicMap = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> collectionUnitMap = null;
		List<Object[]> collectionUnitList = this.getFeaturedRepository().getLibraryCollection(unitId, featuredId, limit, offset, false, null);
		if (collectionUnitList != null && collectionUnitList.size() > 0) {
			collectionUnitMap = new ArrayList<Map<String, Object>>();
			for (Object[] collectionObject : collectionUnitList) {
				Map<String, Object> collectionUnit = this.getCollectionService().getCollection(String.valueOf(collectionObject[0]), new HashMap<String, Object>(), rootNodeId);
				if (collectionUnit != null) {
					collectionUnitMap.add(collectionUnit);
				}
			}

			return collectionUnitMap;
		}
		if (collectionUnitMap == null || collectionUnitMap.size() == 0) {
			List<Code> topics = this.getTaxonomyRespository().findCodeByParentCodeId(unitId, null, null, null, true, LIBRARY, getOrganizationCode(libraryName), rootNodeId, null);
			for (Code topic : topics) {
				Integer collectionTopicCount = null;
				List<Object[]> collectionTopicList = this.getFeaturedRepository().getLibraryCollection(String.valueOf(topic.getCodeId()), featuredId, 10, 0, false, null);
				List<Map<String, Object>> collectionTopicMap = null;
				if (collectionTopicList != null && collectionTopicList.size() > 0) {
					List<Object[]> collectionTopicListAll = this.getFeaturedRepository().getLibraryCollection(String.valueOf(topic.getCodeId()), featuredId, null, null, true, null);
					if (collectionTopicListAll != null && collectionTopicListAll.size() > 0) {
						collectionTopicCount = collectionTopicListAll.size();
					}
					collectionTopicMap = new ArrayList<Map<String, Object>>();
					collectionCount = 0;
					for (Object[] collectionObject : collectionTopicList) {
						Map<String, Object> collectionTopic = new HashMap<String, Object>();
						collectionTopic.put(GOORU_OID, collectionObject[0]);
						collectionTopic.put(TITLE, collectionObject[1]);
						if (collectionCount == 0) {
							collectionTopic = this.getCollectionService().getCollection(String.valueOf(collectionObject[0]), collectionTopic, rootNodeId);
						}
						collectionTopicMap.add(collectionTopic);
						collectionCount++;
					}
				}
				List<Map<String, Object>> lessonMap = new ArrayList<Map<String, Object>>();
				List<Code> allLessons = null;
				List<Code> lessons = this.getTaxonomyRespository().findCodeByParentCodeId(String.valueOf(topic.getCodeId()), null, lessonLimit, 0, false, LIBRARY, getOrganizationCode(libraryName), rootNodeId, null);
				for (Code lesson : lessons) {
					List<Object[]> collectionLessonList = this.getFeaturedRepository().getLibraryCollection(String.valueOf(lesson.getCodeId()), featuredId, null, null, true, null);
					collectionCount = 0;
					List<Map<String, Object>> collectionLessonMap = new ArrayList<Map<String, Object>>();
					for (Object[] collectionObject : collectionLessonList) {
						Map<String, Object> collection = new HashMap<String, Object>();
						collection.put(GOORU_OID, collectionObject[0]);
						collection.put(TITLE, collectionObject[1]);
						if (collectionCount == 0) {
							collection = this.getCollectionService().getCollection(String.valueOf(collectionObject[0]), collection, rootNodeId);
						}
						collectionCount++;
						collectionLessonMap.add(collection);
					}
					List<Map<String, Object>> conceptMap = new ArrayList<Map<String, Object>>();
					List<Code> concepts = this.getTaxonomyRespository().findCodeByParentCodeId(String.valueOf(lesson.getCodeId()), null, 3, 0, true, LIBRARY, getOrganizationCode(libraryName), rootNodeId, null);
					for (Code concept : concepts) {
						List<Object[]> collectionList = this.getFeaturedRepository().getLibraryCollection(String.valueOf(concept.getCodeId()), featuredId, null, null, true, null);
						List<Map<String, Object>> collectionMap = new ArrayList<Map<String, Object>>();
						for (Object[] collectionObject : collectionList) {
							Map<String, Object> collection = new HashMap<String, Object>();
							collection.put(GOORU_OID, collectionObject[0]);
							collection.put(TITLE, collectionObject[1]);
							if (collectionCount == 0) {
								collection = this.getCollectionService().getCollection(String.valueOf(collectionObject[0]), collection, rootNodeId);
							}
							collectionCount++;
							collectionMap.add(collection);
						}
						if (collectionMap != null && collectionMap.size() > 0) {
							conceptMap.add(getCode(concept, collectionMap, COLLECTION, null, getOrganizationCode(libraryName), null, null, null));
						}

					}

					if ((collectionLessonMap != null && collectionLessonMap.size() > 0) || conceptMap != null && conceptMap.size() > 0) {
						lessonMap.add(getCode(lesson, collectionLessonMap, COLLECTION, null, getOrganizationCode(libraryName), conceptMap, null, null));
					}

					allLessons = this.getTaxonomyRespository().findCodeByParentCodeId(String.valueOf(topic.getCodeId()), null, 0, 3, true, LIBRARY, getOrganizationCode(libraryName), rootNodeId, null);
				}

				topicMap.add(getCode(topic, lessonMap, LESSON, collectionTopicCount != null ? collectionTopicCount : (allLessons != null ? allLessons.size() : 0), getOrganizationCode(libraryName), null, collectionTopicMap, COLLECTION));

			}
		}
		return topicMap;
	}

	private String getOrganizationCode(String libraryName) {
		if (libraryName != null && libraryName.equalsIgnoreCase("rusd")) {
			return libraryName;
		}

		return "gooru";
	}

	@Override
	public List<Map<String, Object>> getLibraryCollection(Integer id, String type, Integer offset, Integer limit, boolean skipPagination, String libraryName) {
		List<Object[]> results = this.getFeaturedRepository().getLibrary(type, false, libraryName);
		String featuredId = null;
		if (results != null && results.size() > 0) {
			Object[] obj = results.get(0);
			featuredId = String.valueOf(obj[1]);
		}
		List<Map<String, Object>> collectionList = new ArrayList<Map<String, Object>>();
		if (type != null && type.equalsIgnoreCase(STANDARD)) {
			List<Code> concepts = this.getTaxonomyRespository().findCodeByParentCodeId(String.valueOf(id), null, 3, 0, true, LIBRARY, getOrganizationCode(libraryName), null, null);
			Code code = this.getTaxonomyRespository().findCodeByCodeId(id);
			List<Map<String, Object>> collectionResultList = this.getCollection(id, featuredId, offset, limit, skipPagination);
			boolean hasConcept = false;
			concepts = this.getTaxonomyRespository().findCodeByParentCodeId(String.valueOf(id), null, 3, 0, true, LIBRARY, getOrganizationCode(libraryName), null, null);
			for (Code concept : concepts) {
				List<Map<String, Object>> collectionConceptResultList = this.getCollection(concept.getCodeId(), featuredId, offset, limit, skipPagination);
				if (collectionConceptResultList != null && collectionConceptResultList.size() > 0) {
					hasConcept = true;
				}
			}
			if ((code != null && code.getDepth() == 6) || (collectionResultList != null && collectionResultList.size() == 1 && !hasConcept)) {
				id = code.getParentId();
				concepts = this.getTaxonomyRespository().findCodeByParentCodeId(String.valueOf(id), null, 3, 0, true, LIBRARY, getOrganizationCode(libraryName), null, null);
			}

			List<Map<String, Object>> collectionLessonResultList = this.getCollection(id, featuredId, offset, limit, skipPagination);
			if (collectionLessonResultList != null && collectionLessonResultList.size() > 0) {
				collectionList.addAll(collectionLessonResultList);
			}
			for (Code concept : concepts) {
				List<Map<String, Object>> collectionConceptResultList = this.getCollection(concept.getCodeId(), featuredId, offset, limit, skipPagination);
				if (collectionConceptResultList != null && collectionConceptResultList.size() > 0) {
					collectionList.addAll(collectionConceptResultList);
				}
				if (concept.getDepth() == 5) {
					List<Code> codes = this.getTaxonomyRespository().findCodeByParentCodeId(String.valueOf(concept.getCodeId()), null, 3, 0, true, LIBRARY, getOrganizationCode(libraryName), null, null);
					if (codes != null) {
						for (Code codeIndex : codes) {
							List<Map<String, Object>> collectionCodeResultList = this.getCollection(codeIndex.getCodeId(), featuredId, offset, limit, skipPagination);
							if (collectionCodeResultList != null && collectionCodeResultList.size() > 0) {
								collectionList.addAll(collectionCodeResultList);
							}
						}
					}
				}
			}

		} else {
			List<Map<String, Object>> collectionResultList = this.getCollection(id, featuredId, offset, limit, skipPagination);
			if (collectionResultList != null && collectionResultList.size() > 0) {
				collectionList.addAll(collectionResultList);
			}
		}
		return collectionList;
	}

	private List<Map<String, Object>> getCollection(Integer id, String featuredId, Integer offset, Integer limit, boolean skipPagination) {
		List<Map<String, Object>> collectionList = new ArrayList<Map<String, Object>>();
		List<Object[]> result = this.getFeaturedRepository().getLibraryCollection(String.valueOf(id), featuredId, limit, offset, skipPagination, null);
		if (result != null && result.size() > 0) {
			for (Object[] object : result) {
				Map<String, Object> collection = new HashMap<String, Object>();
				collection.put(GOORU_OID, object[0]);
				collection.put(TITLE, object[1]);

				collectionList.add(collection);
			}
		}
		return collectionList;
	}

	@Override
	public SearchResults<Map<String, Object>> getLibraryCollections(Integer limit, Integer offset, boolean skipPagination, String themeCode, String themeType, String subjectId, String courseId, String unitId, String lessonId, String topicId, String gooruOid, String codeId) {
		List<Map<String, Object>> libraryCollection = getAllLibraryCollections(limit, offset, skipPagination, themeCode, themeType, subjectId, courseId, unitId, lessonId, topicId, gooruOid, codeId);
		SearchResults<Map<String, Object>> result = new SearchResults<Map<String, Object>>();
		result.setSearchResults(libraryCollection);
		result.setTotalHitCount(this.getFeaturedRepository().getLibraryCollectionCount(themeCode, themeType, gooruOid, codeId));
		return result;
	}

	public Map<String, Object> getTaxonomyMapCode(Code code) {
		Map<String, Object> codeMap = new HashMap<String, Object>();
		codeMap.put("activeFlag", code.getActiveFlag());
		codeMap.put("code", code.getCode());
		codeMap.put("assetURI", code.getAssetURI());
		codeMap.put("codeId", code.getCodeId());
		codeMap.put("codeUid", code.getCodeUid());
		codeMap.put("depth", code.getDepth());
		codeMap.put("description", code.getDescription());
		codeMap.put("displayCode", code.getdisplayCode());
		codeMap.put("displayOrder", code.getDisplayOrder());
		codeMap.put("entryId", code.getEntryId());
		codeMap.put("grade", code.getGrade());
		codeMap.put("indexId", code.getIndexId());
		codeMap.put("indexType", code.getIndexType());
		codeMap.put("label", code.getLabel());
		codeMap.put("commonCoreDotNotation", code.getCommonCoreDotNotation());
		return codeMap;
	}

	@Override
	public List<Map<String, Object>> getAllLibraryCollections(Integer limit, Integer offset, boolean skipPagination, String themeCode, String themeType, String subjectId, String courseId, String unitId, String lessonId, String topicId, String gooruOid, String codeId) {
		List<Map<String, Object>> collectionList = new ArrayList<Map<String, Object>>();
		List<Object[]> result = this.getFeaturedRepository().getLibraryCollectionsListByFilter(limit, offset, skipPagination, themeCode, themeType, subjectId, courseId, unitId, lessonId, topicId, gooruOid, codeId);
		if (result != null && result.size() > 0) {
			for (Object[] object : result) {
				Map<String, Object> collection = new HashMap<String, Object>();
				User user = this.getUserRepository().findUserByPartyUid(String.valueOf(object[3]));
				User lastUpdatedUser = this.getUserRepository().findUserByPartyUid(String.valueOf(object[5]));
				Collection featuredCollection = this.getCollectionService().getCollection(String.valueOf(object[0]), true, true, false, user, "commentCount", null, false);
				Long comment = this.getCommentRepository().getCommentCount(String.valueOf(object[0]), null, "notdeleted");
				Long collectionItem = this.getCollectionRepository().getCollectionItemCount(String.valueOf(object[0]), "private,public,anyonewithlink", null);
				Iterator<Code> iter = featuredCollection.getTaxonomySet().iterator();
				Map<Integer, List<Map<String, Object>>> codeParentsMap = new HashMap<Integer, List<Map<String, Object>>>();
				while (iter.hasNext()) {
					Code code = iter.next();
					List<Code> codeList = taxonomyService.findParentTaxonomy(code.getCodeId(), true);
					List<Map<String, Object>> taxonomyMap = new ArrayList<Map<String, Object>>();
					for (Code listCode : codeList) {
						taxonomyMap.add(getTaxonomyMapCode(listCode));
					}
					codeParentsMap.put(code.getCodeId(), taxonomyMap);

				}
				collection.put("taxonomyMappingSet", codeParentsMap);
				collection.put("libraryCollection", featuredCollection);
				collection.put(THEME_CODE, object[6]);
				collection.put(SUBJECT_CODE, object[7]);
				collection.put("featuredSetId", object[8]);
				if (lastUpdatedUser != null) {
					collection.put(LAST_MODIFIED_BY, lastUpdatedUser.getUsername());
				}
				collection.put(COMMENTS_COUNT, comment);
				collection.put(COLLECTION_ITEM_COUNT, collectionItem);
				collectionList.add(collection);
			}
		}
		return collectionList;
	}

	@Override
	public List<Map<String, Object>> getPopularLibrary(String courseId, Integer offset, Integer limit, String libraryName) {
		List<Object[]> results = this.getFeaturedRepository().getLibrary(courseId, false, libraryName);
		String featuredId = null;
		if (results != null && results.size() > 0) {
			Object[] obj = results.get(0);
			featuredId = String.valueOf(obj[1]);
		} else {
			throw new NotFoundException("popular collection not fund");
		}
		List<Map<String, Object>> collectionUnitMap = null;
		List<Object[]> collectionUnitList = this.getFeaturedRepository().getLibraryCollection(null, featuredId, limit, offset, false, null);
		collectionUnitMap = new ArrayList<Map<String, Object>>();
		for (Object[] collectionObject : collectionUnitList) {
			Map<String, Object> collectionUnit = this.getCollectionService().getCollection(String.valueOf(collectionObject[0]), new HashMap<String, Object>(), null);
			if (collectionUnit != null) {
				collectionUnitMap.add(collectionUnit);
			}
		}
		return collectionUnitMap;
	}

	@Override
	public SearchResults<Map<String, Object>> getLibraryResource(String type, Integer offset, Integer limit, boolean skipPagination, String libraryName) {
		List<Map<String, Object>> libraryResource = getCommunityLibraryResource(type, offset, limit, skipPagination, libraryName);
		SearchResults<Map<String, Object>> result = new SearchResults<Map<String, Object>>();
		result.setSearchResults(libraryResource);
		result.setTotalHitCount(this.getFeaturedRepository().getLibraryResourceCount(type, libraryName));
		return result;
	}

	@Override
	public List<Map<String, Object>> getCommunityLibraryResource(String type, Integer offset, Integer limit, boolean skipPagination, String libraryName) {
		List<Map<String, Object>> collectionList = new ArrayList<Map<String, Object>>();
		StorageArea storageArea = this.getStorageRepository().getStorageAreaByTypeName(StorageAccount.Type.NFS.getType());
		List<Object[]> result = this.getFeaturedRepository().getCommunityLibraryResource(type, offset, limit, skipPagination, libraryName);
		if (result != null && result.size() > 0) {
			for (Object[] object : result) {
				Map<String, Object> collection = new HashMap<String, Object>();
				collection.put("collectionId", object[0]);
				collection.put("resourceId", object[1]);
				Resource resource = resourceRepository.findResourceByContentGooruId((String) object[1]);
				collection.put("standards", this.getCollectionService().getStandards(resource.getTaxonomySet(), true, null));
				collection.put("course", this.getCollectionService().getCourse(resource.getTaxonomySet()));
				collection.put("title", object[2]);
				if (object[4] != null) {
					collection.put("thumbnails", storageArea.getAreaPath() + String.valueOf(object[3]) + String.valueOf(object[4]));
				}
				collection.put("resourceUrl", object[5]);
				collection.put("grade", object[6]);
				collection.put("description", object[7]);
				collection.put("category", object[8]);
				collection.put("sharing", object[9]);
				collection.put("hasFrameBreaker", object[10]);
				collection.put("recordSource", object[11]);
				collection.put("license", object[12]);
				collection.put("narration", object[13]);
				collection.put("start", object[14]);
				collection.put("stop", object[15]);
				collection.put("collectionItemId", object[16]);
				collection.put("type", object[17]);
				collection.put("resourceSourceId", object[18]);
				collection.put("sourceName", object[19]);
				collection.put("domainName", object[20]);
				collection.put("attribution", object[21]);
				collection.put("Count", result.size());
				collectionList.add(collection);
			}
		}
		return collectionList;
	}

	@Override
	public Map<String, Object> assocaiateCollectionLibrary(String featuredId, String codeId, String gooruOid) {
		Collection collection = this.getCollectionRepository().getCollectionByGooruOid(gooruOid, null);
		rejectIfNull(collection, GL0056, _COLLECTION);
		Map<String, Object> content = null;
		List<Object[]> result = this.getFeaturedRepository().getLibraryCollection(codeId, featuredId, 1, 0, false, String.valueOf(collection.getContentId()));
		if (result != null && result.size() > 0) {
			throw new BadRequestException(collection.getGooruOid() + " already associated");
		} else {
			FeaturedSet featuredSet = this.getFeaturedRepository().getFeaturedSetById(Integer.parseInt(featuredId));
			rejectIfNull(featuredSet, GL0056, LIBRARY);
			Code code = this.getTaxonomyRespository().findCodeByCodeId(Integer.parseInt(codeId));
			rejectIfNull(code, GL0056, CODE);
			FeaturedSetItems featuredSetItems = new FeaturedSetItems();
			featuredSetItems.setCode(code);
			featuredSetItems.setFeaturedSet(featuredSet);
			featuredSetItems.setContent(collection);
			featuredSetItems.setSequence(1);
			this.getFeaturedRepository().save(featuredSetItems);
			content = new HashMap<String, Object>();
			content.put(GOORU_OID, collection.getGooruOid());
			content.put(LIBRARY_ID, featuredId);
			content.put(CODE_ID, codeId);
		}

		return content;
	}

	@Override
	public void deleteLibraryCollectionAssoc(String featuredSetId, String codeId, String gooruOid) {
		Collection collection = this.getCollectionRepository().getCollectionByGooruOid(gooruOid, null);
		rejectIfNull(collection, GL0056, _COLLECTION);
		FeaturedSet featuredSet = this.getFeaturedRepository().getFeaturedSetById(Integer.parseInt(featuredSetId));
		rejectIfNull(featuredSet, GL0056, LIBRARY);
		Code code = this.getTaxonomyRespository().findCodeByCodeId(Integer.parseInt(codeId));
		rejectIfNull(code, GL0056, CODE);
		this.getFeaturedRepository().deleteLibraryCollectionAssoc(featuredSetId, codeId, String.valueOf(collection.getContentId()));

	}

	@Override
	public List<Map<String, Object>> getLibrary(String libraryName) {
		List<Object[]> libraryObjectList = this.getFeaturedRepository().getLibrary(libraryName);
		List<Map<String, Object>> libraryList = new ArrayList<Map<String, Object>>();
		for (Object[] libraryObject : libraryObjectList) {
			Map<String, Object> library = new HashMap<String, Object>();
			library.put(LIBRARY_ID, libraryObject[0]);
			library.put(SUBJECT_CODE, libraryObject[1]);
			library.put(LIBRARY, libraryObject[2]);
			libraryList.add(library);
		}
		return libraryList;
	}

	public FeaturedRepository getFeaturedRepository() {
		return featuredRepository;
	}

	public TaxonomyRespository getTaxonomyRespository() {
		return taxonomyRespository;
	}

	public ContentRepository getContentRepository() {
		return contentRepository;
	}

	public CollectionService getCollectionService() {
		return collectionService;
	}

	public CollectionRepository getCollectionRepository() {
		return collectionRepository;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public CustomTableRepository getCustomTableRepository() {
		return customTableRepository;
	}

	public StorageRepository getStorageRepository() {
		return storageRepository;
	}

	public ResourceRepository getResourceRepository() {
		return resourceRepository;
	}

	public void setResourceRepository(ResourceRepository resourceRepository) {
		this.resourceRepository = resourceRepository;
	}

}
