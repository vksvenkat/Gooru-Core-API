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
import org.ednovo.gooru.core.api.model.CodeOrganizationAssoc;
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
		List<CodeOrganizationAssoc> codes = this.getTaxonomyRespository().findCodeByParentCodeId(type.equalsIgnoreCase(STANDARD) ? null : type, null, null, null, true, LIBRARY, getOrganizationCode(libraryName), null, type.equalsIgnoreCase(STANDARD) ? "0" : null);
		List<Map<String, Object>> codeMap = new ArrayList<Map<String, Object>>();
		if (type.equalsIgnoreCase(STANDARD)) {
			for (CodeOrganizationAssoc codeOrganizationAssoc : codes) {
				List<Map<String, Object>> node = new ArrayList<Map<String, Object>>();
				List<CodeOrganizationAssoc> nodes = this.getTaxonomyRespository().findCodeByParentCodeId(String.valueOf(codeOrganizationAssoc.getCode().getCodeId()), null, null, null, true, LIBRARY, getOrganizationCode(libraryName), String.valueOf(codeOrganizationAssoc.getCode().getCodeId()), "1");
				for (CodeOrganizationAssoc codeOrganizationAssocNode : nodes) {
					List<CodeOrganizationAssoc> courses = this.getTaxonomyRespository().findCodeByParentCodeId(String.valueOf(codeOrganizationAssocNode.getCode().getCodeId()), null, null, null, true, LIBRARY, getOrganizationCode(libraryName), String.valueOf(codeOrganizationAssocNode.getCode().getRootNodeId()), null);
					for (CodeOrganizationAssoc course : courses) {
						node.add(getCode(course, null, NODE,libraryName));
					}
				}
				codeMap.add(getCode(codeOrganizationAssoc, node, NODE,libraryName));
			}
		} else {
			for (CodeOrganizationAssoc code : codes) {
				codeMap.add(getCode(code, null, NODE,libraryName));
			}
		}
		return codeMap;
	}

	@Override
	public List<Map<String, Object>> getLibrarySubject(String code, String ChildCode, String libraryName, String rootNodeId) {
		List<CodeOrganizationAssoc> courses = this.getTaxonomyRespository().findCodeByParentCodeId(code, null, null, null, true, LIBRARY, getOrganizationCode(libraryName), rootNodeId, null);
		List<Map<String, Object>> courseMap = new ArrayList<Map<String, Object>>();
		for (CodeOrganizationAssoc course : courses) {
			courseMap.add(getCode(course, getLibraryCourse(String.valueOf(course.getCode().getCodeId()), ChildCode,libraryName, rootNodeId), UNIT, null, getOrganizationCode(libraryName), null, null, null));
		}
		return courseMap;
	}

	@Override
	public List<Map<String, Object>> getLibraryCourse(String code, String ChildCode, String libraryName, String rootNodeId) {
		List<CodeOrganizationAssoc> units = this.getTaxonomyRespository().findCodeByParentCodeId(String.valueOf(code), null, null, null, true, LIBRARY, getOrganizationCode(libraryName), rootNodeId, null);
		List<Map<String, Object>> unitMap = new ArrayList<Map<String, Object>>();
		int unitCount = 0;
		int collectionCount = 0;
		for (CodeOrganizationAssoc unit : units) {
			List<Map<String, Object>> topicMap = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> collectionUnitMap = null;
			Integer collectionUnitCount = null;
			List<Object[]> collectionUnitListAll = this.getFeaturedRepository().getLibraryCollection(String.valueOf(unit.getCode().getCodeId()), String.valueOf(ChildCode), null, null, true, null);
			if (collectionUnitListAll != null && collectionUnitListAll.size() > 0) {
				collectionUnitCount = collectionUnitListAll.size();
			}
			if (unitCount == 0) {
				List<CodeOrganizationAssoc> topics = this.getTaxonomyRespository().findCodeByParentCodeId(String.valueOf(unit.getCode().getCodeId()), null, null, null, true, LIBRARY, getOrganizationCode(libraryName), rootNodeId, null);
				List<Object[]> collectionUnitList = null;
				collectionUnitList = this.getFeaturedRepository().getLibraryCollection(String.valueOf(unit.getCode().getCodeId()), String.valueOf(ChildCode), 10, 0, false, null);
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
					for (CodeOrganizationAssoc topic : topics) {
						Integer collectionTopicCount = null;
						List<Object[]> collectionTopicList = this.getFeaturedRepository().getLibraryCollection(String.valueOf(topic.getCode().getCodeId()), String.valueOf(ChildCode), 10, 0, false, null);
						List<Map<String, Object>> collectionTopicMap = null;
						if (collectionTopicList != null && collectionTopicList.size() > 0) {
							List<Object[]> collectionTopicListAll = this.getFeaturedRepository().getLibraryCollection(String.valueOf(topic.getCode().getCodeId()), String.valueOf(ChildCode), null, null, true, null);
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
						List<CodeOrganizationAssoc> allLessons = null;
						List<CodeOrganizationAssoc> lessons = this.getTaxonomyRespository().findCodeByParentCodeId(String.valueOf(topic.getCode().getCodeId()), null, lessonLimit, 0, false, LIBRARY, getOrganizationCode(libraryName), rootNodeId, null);
						for (CodeOrganizationAssoc lesson : lessons) {
							List<Object[]> collectionLessonList = this.getFeaturedRepository().getLibraryCollection(String.valueOf(lesson.getCode().getCodeId()), String.valueOf(ChildCode), null, null, true, null);
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
							List<CodeOrganizationAssoc> concepts = this.getTaxonomyRespository().findCodeByParentCodeId(String.valueOf(lesson.getCode().getCodeId()), null, 3, 0, true, LIBRARY, getOrganizationCode(libraryName), rootNodeId, null);
							for (CodeOrganizationAssoc concept : concepts) {
								List<Object[]> collectionList = this.getFeaturedRepository().getLibraryCollection(String.valueOf(concept.getCode().getCodeId()), String.valueOf(ChildCode), null, null, true, null);
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
						allLessons = this.getTaxonomyRespository().findCodeByParentCodeId(String.valueOf(topic.getCode().getCodeId()), null, 0, 3, true, LIBRARY, getOrganizationCode(libraryName), rootNodeId, null);

						topicMap.add(getCode(topic, lessonMap, LESSON, collectionTopicCount != null ? collectionTopicCount : (allLessons != null ? allLessons.size() : 0), getOrganizationCode(libraryName), null, collectionTopicMap, COLLECTION));

					}
				}
			}
			unitMap.add(getCode(unit, topicMap, TOPIC, collectionUnitCount, getOrganizationCode(libraryName), null, collectionUnitMap, COLLECTION));
			unitCount++;

		}
		return unitMap;
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
					List<CodeOrganizationAssoc> curriculums = this.getTaxonomyRespository().findCodeByParentCodeId(null, null, null, null, true, LIBRARY, getOrganizationCode(libraryName), null, "0");
					List<Map<String, Object>> curriculumMap = new ArrayList<Map<String, Object>>();
					for (CodeOrganizationAssoc curriculum : curriculums) {
						List<CodeOrganizationAssoc> subjects = this.getTaxonomyRespository().findCodeByParentCodeId(String.valueOf(curriculum.getCode().getCodeId()), null, null, null, true, LIBRARY, getOrganizationCode(libraryName), String.valueOf(curriculum.getCode().getCodeId()), "1");
						courseMap = new ArrayList<Map<String, Object>>();
						for (CodeOrganizationAssoc subject : subjects) {
							courseMap.addAll(this.getLibrarySubject(String.valueOf(subject.getCode().getCodeId()), String.valueOf(object[1]), libraryName, String.valueOf(curriculum.getCode().getRootNodeId())));
						}
						curriculumMap.add(getCode(curriculum, courseMap, COURSE, null, getOrganizationCode(libraryName), null, null, null));

					}
					lib.put(DATA_OBJECT, curriculumMap);
				} else {
					courseMap = this.getLibrarySubject(String.valueOf(lib.get(CODE)), String.valueOf(object[1]), libraryName, "20000");
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
			List<CodeOrganizationAssoc> lessons = this.getTaxonomyRespository().findCodeByParentCodeId(topicId, null, limit, offset, false, libraryName, getOrganizationCode(libraryName), rootNodeId, null);
			for (CodeOrganizationAssoc lesson : lessons) {
				List<Object[]> collectionList = this.getFeaturedRepository().getLibraryCollection(String.valueOf(lesson.getCode().getCodeId()), featuredId, null, null, true, null);
				List<Map<String, Object>> collectionMap = new ArrayList<Map<String, Object>>();
				for (Object[] collectionObject : collectionList) {
					Map<String, Object> collection = new HashMap<String, Object>();
					collection.put(GOORU_OID, collectionObject[0]);
					collection.put(TITLE, collectionObject[1]);
					collectionMap.add(collection);
				}
				List<CodeOrganizationAssoc> concepts = this.getTaxonomyRespository().findCodeByParentCodeId(String.valueOf(lesson.getCode().getCodeId()), null, 3, 0, true, LIBRARY, getOrganizationCode(libraryName), rootNodeId, null);
				List<Map<String, Object>> conceptMap = new ArrayList<Map<String, Object>>();
				for (CodeOrganizationAssoc concept : concepts) {
					List<Object[]> collectionConceptList = this.getFeaturedRepository().getLibraryCollection(String.valueOf(concept.getCode().getCodeId()), featuredId, null, null, true, null);
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

	private Map<String, Object> getCode(CodeOrganizationAssoc codeOrganizationAssoc, List<Map<String, Object>> childern, String type, Integer count, String organizationCode, List<Map<String, Object>> concept, List<Map<String, Object>> collectionChildern, String collectionType) {
		Map<String, Object> codeMap = getCode(codeOrganizationAssoc, childern, type, null);
		if (concept != null) {
			codeMap.put(CONCEPT, concept);
		}
		if (codeOrganizationAssoc.getCode().getDepth() == 2) {
			List<CodeUserAssoc> codeUserAssoc = this.getTaxonomyRespository().getUserCodeAssoc(codeOrganizationAssoc.getCode().getCodeId(), organizationCode);
			codeMap.put(CREATOR, getUser(codeUserAssoc) != null && getUser(codeUserAssoc).size() > 0 ? getUser(codeUserAssoc).get(0) : null);
			codeMap.put(USER, getUser(codeUserAssoc));
		}
		if (count != null) {
			codeMap.put(COUNT, count);
		}
		codeMap.put(collectionType, collectionChildern);
		return codeMap;

	}

	private Map<String, Object> getCode(CodeOrganizationAssoc codeOrganizationAssoc, List<Map<String, Object>> childern, String type, String libraryName) {
		Map<String, Object> codeMap = new HashMap<String, Object>();
		codeMap.put(CODE, codeOrganizationAssoc.getCode().getCommonCoreDotNotation() == null ? codeOrganizationAssoc.getCode().getdisplayCode() : codeOrganizationAssoc.getCode().getCommonCoreDotNotation());
		codeMap.put(CODE_ID, codeOrganizationAssoc.getCode().getCodeId());
		codeMap.put(CODE_TYPE, codeOrganizationAssoc.getCode().getCodeType());
		codeMap.put(LABEL, codeOrganizationAssoc.getCode().getLabel());
		codeMap.put(PARENT_ID, codeOrganizationAssoc.getCode().getParent() != null ? codeOrganizationAssoc.getCode().getParent().getCodeId() : null);
		codeMap.put(THUMBNAILS, codeOrganizationAssoc.getCode().getThumbnails());
		codeMap.put(GRADE, codeOrganizationAssoc.getCode().getGrade());
		if(libraryName != null) {
			codeMap.put(USER, getUser(this.getTaxonomyRespository().getUserCodeAssoc(codeOrganizationAssoc.getCode().getCodeId(), getOrganizationCode(libraryName))));
		}
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
			List<CodeOrganizationAssoc> topics = this.getTaxonomyRespository().findCodeByParentCodeId(unitId, null, null, null, true, LIBRARY, getOrganizationCode(libraryName), rootNodeId, null);
			for (CodeOrganizationAssoc topic : topics) {
				Integer collectionTopicCount = null;
				List<Object[]> collectionTopicList = this.getFeaturedRepository().getLibraryCollection(String.valueOf(topic.getCode().getCodeId()), featuredId, 10, 0, false, null);
				List<Map<String, Object>> collectionTopicMap = null;
				if (collectionTopicList != null && collectionTopicList.size() > 0) {
					List<Object[]> collectionTopicListAll = this.getFeaturedRepository().getLibraryCollection(String.valueOf(topic.getCode().getCodeId()), featuredId, null, null, true, null);
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
				List<CodeOrganizationAssoc> allLessons = null;
				List<CodeOrganizationAssoc> lessons = this.getTaxonomyRespository().findCodeByParentCodeId(String.valueOf(topic.getCode().getCodeId()), null, lessonLimit, 0, false, LIBRARY, getOrganizationCode(libraryName), rootNodeId, null);
				for (CodeOrganizationAssoc lesson : lessons) {
					List<Object[]> collectionLessonList = this.getFeaturedRepository().getLibraryCollection(String.valueOf(lesson.getCode().getCodeId()), featuredId, null, null, true, null);
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
					List<CodeOrganizationAssoc> concepts = this.getTaxonomyRespository().findCodeByParentCodeId(String.valueOf(lesson.getCode().getCodeId()), null, 3, 0, true, LIBRARY, getOrganizationCode(libraryName), rootNodeId, null);
					for (CodeOrganizationAssoc concept : concepts) {
						List<Object[]> collectionList = this.getFeaturedRepository().getLibraryCollection(String.valueOf(concept.getCode().getCodeId()), featuredId, null, null, true, null);
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

					allLessons = this.getTaxonomyRespository().findCodeByParentCodeId(String.valueOf(topic.getCode().getCodeId()), null, 0, 3, true, LIBRARY, getOrganizationCode(libraryName), rootNodeId, null);
				}

				topicMap.add(getCode(topic, lessonMap, LESSON, collectionTopicCount != null ? collectionTopicCount : (allLessons != null ? allLessons.size() : 0), getOrganizationCode(libraryName), null, collectionTopicMap, COLLECTION));

			}
		}
		return topicMap;
	}

	private String getOrganizationCode(String libraryName) {
		if (libraryName != null && libraryName.equalsIgnoreCase(LIBRARY)) {
			return GOORU;
		}

		return libraryName;
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
			List<CodeOrganizationAssoc> concepts = this.getTaxonomyRespository().findCodeByParentCodeId(String.valueOf(id), null, 3, 0, true, LIBRARY, getOrganizationCode(libraryName), null, null);
			Code code = this.getTaxonomyRespository().findCodeByCodeId(id);
			List<Map<String, Object>> collectionResultList = this.getCollection(id, featuredId, offset, limit, skipPagination);
			boolean hasConcept = false;
			concepts = this.getTaxonomyRespository().findCodeByParentCodeId(String.valueOf(id), null, 3, 0, true, LIBRARY, getOrganizationCode(libraryName), null, null);
			for (CodeOrganizationAssoc concept : concepts) {
				List<Map<String, Object>> collectionConceptResultList = this.getCollection(concept.getCode().getCodeId(), featuredId, offset, limit, skipPagination);
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
			for (CodeOrganizationAssoc concept : concepts) {
				List<Map<String, Object>> collectionConceptResultList = this.getCollection(concept.getCode().getCodeId(), featuredId, offset, limit, skipPagination);
				if (collectionConceptResultList != null && collectionConceptResultList.size() > 0) {
					collectionList.addAll(collectionConceptResultList);
				}
				if (concept.getCode().getDepth() == 5) {
					List<CodeOrganizationAssoc> codes = this.getTaxonomyRespository().findCodeByParentCodeId(String.valueOf(concept.getCode().getCodeId()), null, 3, 0, true, LIBRARY, getOrganizationCode(libraryName), null, null);
					if (codes != null) {
						for (CodeOrganizationAssoc codeIndex : codes) {
							List<Map<String, Object>> collectionCodeResultList = this.getCollection(codeIndex.getCode().getCodeId(), featuredId, offset, limit, skipPagination);
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
		codeMap.put(ACTIVE_FLAG, code.getActiveFlag());
		codeMap.put(CODE, code.getCode());
		codeMap.put(ASSET_URI, code.getAssetURI());
		codeMap.put(CODE_ID, code.getCodeId());
		codeMap.put(CODE_UID, code.getCodeUid());
		codeMap.put(DEPTH, code.getDepth());
		codeMap.put(DESCRIPTION, code.getDescription());
		codeMap.put(DISPLAY_CODE, code.getdisplayCode());
		codeMap.put(DISPLAY_ORDER, code.getDisplayOrder());
		codeMap.put(ENTRY_ID, code.getEntryId());
		codeMap.put(GRADE, code.getGrade());
		codeMap.put(INDEX_ID, code.getIndexId());
		codeMap.put(INDEX_TYPE, code.getIndexType());
		codeMap.put(LABEL, code.getLabel());
		codeMap.put(COMMON_CORE_DOT_NOTATION, code.getCommonCoreDotNotation());
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
				Collection featuredCollection = this.getCollectionService().getCollection(String.valueOf(object[0]), true, true, false, user, COMMENT_COUNT, null, false);
				Long comment = this.getCommentRepository().getCommentCount(String.valueOf(object[0]), null, NOT_DELETED);
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
				collection.put(TAXONOMY_MAPPING_SET, codeParentsMap);
				collection.put(LIBRARY_COLLECTION, featuredCollection);
				collection.put(THEME_CODE, object[6]);
				collection.put(SUBJECT_CODE, object[7]);
				collection.put(FEATURE_SETID, object[8]);
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
				collection.put(COLLECTION_ID, object[0]);
				collection.put(RESOURCE_ID, object[1]);
				Resource resource = resourceRepository.findResourceByContentGooruId((String) object[1]);
				collection.put(STANDARDS, this.getCollectionService().getStandards(resource.getTaxonomySet(), true, null));
				collection.put(COURSE, this.getCollectionService().getCourse(resource.getTaxonomySet()));
				collection.put(TITLE, object[2]);
				if (object[4] != null) {
					collection.put(THUMBNAILS, storageArea.getAreaPath() + String.valueOf(object[3]) + String.valueOf(object[4]));
				}
				collection.put(RESOURCE_URL, object[5]);
				collection.put(GRADE, object[6]);
				collection.put(DESCRIPTION, object[7]);
				collection.put(CATEGORY, object[8]);
				collection.put(SHARING, object[9]);
				collection.put(HAS_FRAME_BREAKER, object[10]);
				collection.put(RECORD_SOURCE, object[11]);
				collection.put(LICENSE, object[12]);
				collection.put(NARRATION, object[13]);
				collection.put(START, object[14]);
				collection.put(STOP, object[15]);
				collection.put(COLLECTION_ITEM_ID, object[16]);
				collection.put(TYPE, object[17]);
				collection.put(_RESOURCE_SOURCE_ID, object[18]);
				collection.put(SOURCE_NAME, object[19]);
				collection.put(DOMAIN_NAME, object[20]);
				collection.put(ATTRIBUTION, object[21]);
				collection.put(COUNT, result.size());
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
			FeaturedSet featuredSet = this.getFeaturedRepository().getFeaturedSetByIds(Integer.parseInt(featuredId));
			rejectIfNull(featuredSet, GL0056, LIBRARY);
			Code code = this.getTaxonomyRespository().findCodeByCodeIds(Integer.parseInt(codeId));
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
		FeaturedSet featuredSet = this.getFeaturedRepository().getFeaturedSetByIds(Integer.parseInt(featuredSetId));
		rejectIfNull(featuredSet, GL0056, LIBRARY);
		Code code = this.getTaxonomyRespository().findCodeByCodeIds(Integer.parseInt(codeId));
		rejectIfNull(code, GL0056, CODE);
		this.getFeaturedRepository().deleteLibraryCollectionAssoc(featuredSetId, codeId, String.valueOf(collection.getContentId()));

	}

	@Override
	public List<Map<String, Object>> getLibrary(String libraryName) {
		libraryName = (libraryName != null && libraryName.equalsIgnoreCase(GOORU)) ? LIBRARY : libraryName;
		List<Object[]> libraryObjectList = this.getFeaturedRepository().getLibrary(libraryName);
		List<Map<String, Object>> libraryList = new ArrayList<Map<String, Object>>();
		for (Object[] libraryObject : libraryObjectList) {
			Map<String, Object> library = new HashMap<String, Object>();
			library.put(LIBRARY_ID, libraryObject[0]);
			library.put(SUBJECT_CODE, libraryObject[1]);
			if (libraryName.contains(",")) {
			  library.put(LIBRARY, libraryObject[2]);
			}
			if(libraryObject[3] != null) {
				library.put(LABEL, libraryObject[3]);
			} else {
				library.put(LABEL, libraryObject[1]);
			}
			libraryList.add(library);
		}
		return libraryList;
	}

	@Override
	public List<Map<String, Object>> getLibraryItems(String itemType, String type, String codeId, String libraryName, String rootNodeId, Integer limit, Integer offset) {
		List<Object[]> results = this.getFeaturedRepository().getLibrary(type, false, libraryName);
		List<Map<String, Object>> items = null;
		if (results != null && results.size() > 0) {
			Object[] object = results.get(0);
			if (itemType.equalsIgnoreCase(COURSE)) {
				items = this.getLibraryCourse(codeId, String.valueOf(object[1]), libraryName, rootNodeId);
			} else if (itemType.equalsIgnoreCase(UNIT)) {
				items = this.getLibraryUnit(codeId, type, offset, limit, libraryName, rootNodeId);
			} else if (itemType.equalsIgnoreCase(TOPIC)) {
				items = this.getLibraryTopic(codeId, limit, offset, type, libraryName, rootNodeId);
			}
		}
		return items;
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
