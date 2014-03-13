/////////////////////////////////////////////////////////////
// ShelfServiceImpl.java
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
package org.ednovo.gooru.domain.service.shelf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javassist.NotFoundException;

import org.ednovo.gooru.application.util.CollectionUtil;
import org.ednovo.gooru.application.util.SerializerUtil;
import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.core.api.model.Assessment;
import org.ednovo.gooru.core.api.model.Code;
import org.ednovo.gooru.core.api.model.Content;
import org.ednovo.gooru.core.api.model.ContentPermission;
import org.ednovo.gooru.core.api.model.Learnguide;
import org.ednovo.gooru.core.api.model.Resource;
import org.ednovo.gooru.core.api.model.ResourceType;
import org.ednovo.gooru.core.api.model.Shelf;
import org.ednovo.gooru.core.api.model.ShelfItem;
import org.ednovo.gooru.core.api.model.ShelfType;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.partner.CustomFieldsService;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.ednovo.gooru.domain.service.taxonomy.TaxonomyService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.assessment.AssessmentRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.classplan.LearnguideRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.content.ContentRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.resource.ResourceRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.shelf.ShelfRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.shelf.ShelfRepositoryHibernate;
import org.ednovo.gooru.infrastructure.persistence.hibernate.taxonomy.TaxonomyRespository;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

@Service
public class ShelfServiceImpl implements ShelfService,ParameterProperties,ConstantProperties {

	@Autowired
	private ShelfRepository shelfRepository;

	@Autowired
	private TaxonomyRespository taxonomyRepository;

	@Autowired
	private ResourceRepository resourceRepository;

	@Autowired
	private SettingService settingService;

	@Autowired
	private ShelfRepositoryHibernate shelfRepositoryHibernate;

	@Autowired
	private CollectionUtil collectionUtil;

	@Autowired
	private TaxonomyService taxonomyService;

	@Autowired
	private LearnguideRepository learnguideRepository;

	@Autowired
	private CustomFieldsService customFieldService;

	@Autowired
	private AssessmentRepository assessmentRepository;

	@Autowired
	private ContentRepository contentRepository;

	@Override
	public Shelf createShelf(String shelfName, User user, boolean defaultFlag, boolean viewFlag) {
		if (shelfName == null) {
			shelfName = user.getUsername() + Constants.SHELF_DEFAULT_NAME;
		}
		Shelf buildShelf = buildShelf(shelfName, null, 1, user.getPartyUid(), defaultFlag, viewFlag);
		this.getShelfRepository().save(buildShelf);
		return buildShelf;
	}

	private Shelf buildShelf(String name, String shelfParentId, Integer depth, String gooruUId, boolean isDefaultFlag, boolean viewFlag) {
		if (this.getShelfRepository().findShelfByName(name, gooruUId) != null) {
			throw new RuntimeException("Already you have this shelf name!");
		}
		Shelf shelf = new Shelf();
		shelf.setShelfParentId(shelfParentId);
		shelf.setName(name);
		shelf.setShelfCategory(ShelfType.Category.USER.getCategory());
		shelf.setActiveFlag(true);
		shelf.setDepth(depth);
		shelf.setDefaultFlag(isDefaultFlag);
		shelf.setShelfType(Constants.SHELF_TYPE);
		shelf.setUserId(gooruUId);
		shelf.setViewFlag(viewFlag);
		return shelf;
	}

	@Override
	public Shelf addResourceToSelf(String shelfId, String resourceId, String addType, Errors errors, User user) throws Exception {
		if (!ShelfType.AddedType.ADDED.getAddedType().equalsIgnoreCase(addType) && !ShelfType.AddedType.SUBSCRIBED.getAddedType().equalsIgnoreCase(addType)) {
			throw new RuntimeException("Invaild added type!, added type should be 'added' or 'subscribed'");
		}
		if (user == null) {
			throw new RuntimeException("user data should not be null");
		}
		Resource resource = this.getResourceRepository().findResourceByContentGooruId(resourceId);

		List<ShelfItem> shelfItems = this.getShelfRepository().findAllShelfByUserAndId(user.getPartyUid(), resource != null ? resource.getContentId() : null);
		if (shelfItems != null && shelfItems.size() > 0) {
			throw new Exception("You have this resource already in your shelf!");
		}
		Shelf shelf = null;
		if (shelfId != null && !shelfId.isEmpty()) {
			shelf = this.getShelfRepository().findShelfByShelfId(shelfId);
		} else {
			shelf = this.getShelfRepository().getDefaultShelf(user.getPartyUid());
		}
		if (shelf == null) {
			shelf = createShelf(user.getUsername() + Constants.SHELF_DEFAULT_NAME, user, true, true);
		}
		ShelfItem shelfItem = new ShelfItem();
		shelfItem.setShelf(shelf);
		shelfItem.setResource(resource);
		shelfItem.setAddedType(addType);
		shelfItem.setLastActivityOn(new Date(System.currentTimeMillis()));
		shelfItem.setCreatedOn(new Date(System.currentTimeMillis()));
		this.getShelfRepository().save(shelfItem);
		this.getShelfRepository().flush();

		return shelf;
	}

	@Override
	public List<Shelf> getShelfResources(String shelfId, String resourceType, String userId, String orderBy) {

		Map<String, String> parametersMap = new HashMap<String, String>();
		String type = null;
		if (resourceType.equalsIgnoreCase(COLLECTION)) {
			type = "'" + ResourceType.Type.CLASSPLAN.getType() + "' , '" + ResourceType.Type.CLASSBOOK.getType() + "'";
		} else if (resourceType.equalsIgnoreCase(QUIZ)) {
			type = "'" + ResourceType.Type.ASSESSMENT_QUIZ.getType() + "' , '" + ResourceType.Type.ASSESSMENT_EXAM.getType() + "'";
		} else if (resourceType.equalsIgnoreCase(RESOURCE)) {
			type = "'" + ResourceType.Type.CLASSPLAN.getType() + "' , '" + ResourceType.Type.ASSESSMENT_QUIZ.getType() + "', '" + ResourceType.Type.ASSESSMENT_EXAM.getType() + "', '" + ResourceType.Type.CLASSBOOK.getType() + "'";
		}

		if (orderBy != null && !orderBy.equalsIgnoreCase("")) {
			parametersMap.put(ORDER_BY, orderBy);
		}

		parametersMap.put(RESOURCE_TYPE, resourceType);
		parametersMap.put(TYPE, type);
		parametersMap.put(USER_ID, userId);
		parametersMap.put(SHELF_UID, shelfId);
		return shelfRepository.listShelf(parametersMap);
	}

	@Override
	public Shelf updateShelf(String shelfId, Boolean activeFlag, Errors errors) {
		// Fix me : Need to improve this logic
		Shelf shelf = shelfRepository.findShelfByShelfId(shelfId);
		shelf.setActiveFlag(activeFlag);
		shelfRepository.save(shelf);
		return shelf;
	}

	@Override
	public List<Shelf> getUserSelf(String shelfToLoad, User user, boolean fetchAll) {
		Map<String, String> parametersMap = new HashMap<String, String>();
		parametersMap.put(USER_ID, user.getGooruUId());
		parametersMap.put(ORDER_BY, RECENT);
		parametersMap.put(FETCH_ALL, fetchAll ? "1" : "0");
		return shelfRepository.listShelf(parametersMap);
	}

	@Override
	public String deleteShelfEntry(String shelfId, String resourceId) {
		int status = 0;
		String deleteStatus = "Failed to delete shelf entry";
		Resource resource = this.getResourceRepository().findResourceByContentGooruId(resourceId);
		if (resource != null && shelfId != null) {
			status = shelfRepository.deleteShelfEntry(shelfId, resource.getContentId());
		}
		if (status > 0) {
			deleteStatus = "Shelf entry is deleted successfully";
		}
		return deleteStatus;
	}

	@Override
	public Shelf moveShelfContent(String currentShelfId, String targetShelfId, String resourceGooruOId, User user) throws Exception {
		Shelf currentShelf = shelfRepository.findShelfByShelfId(currentShelfId);
		if (currentShelf == null) {
			throw new Exception("Shelf does not exist");
		} else if (user == null || !currentShelf.getUserId().equalsIgnoreCase(user.getGooruUId())) {
			throw new Exception("You do not have permission to this shelf.");
		}
		Shelf targetShelf = shelfRepository.findShelfByShelfId(targetShelfId);
		if (targetShelf == null) {
			throw new Exception("Shelf does not exist");
		} else if (user == null || !targetShelf.getUserId().equalsIgnoreCase(user.getGooruUId())) {
			throw new Exception("You are not allowed to move someone's shelf.");
		}
		Resource resource = this.resourceRepository.findResourceByContentGooruId(resourceGooruOId);
		if (resource == null) {
			throw new Exception("Resource  does not exist, please check the gooruOid.");
		}
		this.getShelfRepository().updateShelfItem(currentShelfId, targetShelfId, resource.getContentId());
		this.updateShelfItem(resource.getContentId(), user, null);
		return targetShelf;
	}

	@Override
	public Shelf markDefaultShelf(String shelfName, Errors errors, User user) throws NotFoundException {
		if (user == null) {
			throw new RuntimeException("user data should be not empty");
		}
		Shelf shelf = this.getShelfRepository().findShelfByName(shelfName, user.getPartyUid());
		if (shelf != null) {
			shelf.setDefaultFlag(true);
			List<Shelf> shelfLists = this.getShelfRepository().findAllShelfByUser(user.getPartyUid());
			for (Shelf shelfs : shelfLists) {
				if (!shelf.getShelfId().equalsIgnoreCase(shelfs.getShelfId())) {
					shelfs.setDefaultFlag(false);
				} else {
					shelfs.setDefaultFlag(true);
				}
			}
			this.getShelfRepository().saveAll(shelfLists);
			return shelf;
		} else {
			String suggestName = this.getSettingService().getConfigSetting(ConfigConstants.SUGGEST_FOLDERS, user.getOrganization().getPartyUid());
			if (suggestName != null && suggestName.contains(shelfName)) {
				Shelf newShelf = buildShelf(shelfName, null, 1, user.getPartyUid(), true, true);
				this.getShelfRepository().save(newShelf);
			} else {
				throw new NotFoundException("you do not have this shelf name" + shelfName);
			}
			return null;
		}

	}

	@Override
	public List<String> getShelfNames(String fetchType, User user) {
		List<String> suggest = null;
		String suggestName = this.getSettingService().getConfigSetting(ConfigConstants.SUGGEST_FOLDERS, user.getOrganization().getPartyUid());
		if (suggestName != null) {
			suggest = Arrays.asList(suggestName.split("\\s*,\\s*"));
		}
		if (user != null && fetchType != null && fetchType.equalsIgnoreCase("my")) {
			return this.getShelfRepository().getShelfNames(user.getPartyUid(), suggest);
		} else if (fetchType != null && fetchType.equalsIgnoreCase("suggest")) {
			return suggest;
		}
		return null;
	}

	@Override
	public Shelf renameMyShelf(String shelfId, String newShelfName, String gooruUid) {
		if (this.getShelfRepository().findShelfByNameExcludeById(newShelfName, shelfId, gooruUid) != null) {
			throw new RuntimeException("you have this shelf name already, please try some other.");
		}
		Shelf shelf = this.getShelfRepository().findShelfByShelfId(shelfId);
		if (shelf != null) {
			shelf.setName(newShelfName);
			this.getShelfRepository().save(shelf);
			return shelf;
		}
		return null;
	}

	@Override
	public List<ShelfItem> getShelfItems(String shelfId, String resourceType, String userId, String orderBy, String pageNum, String pageSize, String addedType, boolean skipPagination, String startAt) {
		Map<String, String> parametersMap = new HashMap<String, String>();
		String type = null;
		if (resourceType.equalsIgnoreCase(COLLECTION)) {
			type = "'" + ResourceType.Type.CLASSPLAN.getType() + "','" + ResourceType.Type.CLASSBOOK.getType() + "'";
		} else if (resourceType.equalsIgnoreCase(QUIZ)) {
			type = "'" + ResourceType.Type.ASSESSMENT_QUIZ.getType() + "','" + ResourceType.Type.ASSESSMENT_EXAM.getType() + "'";
		} else if (resourceType.equalsIgnoreCase(RESOURCE)) {
			type = "'" + ResourceType.Type.CLASSPLAN.getType() + "' , '" + ResourceType.Type.ASSESSMENT_QUIZ.getType() + "', '" + ResourceType.Type.ASSESSMENT_EXAM.getType() + "', '" + ResourceType.Type.CLASSBOOK.getType() + "'";
		}

		if (orderBy != null && !orderBy.equalsIgnoreCase("")) {
			parametersMap.put(RESOURCE, orderBy);
		}
		if (addedType != null && !addedType.equalsIgnoreCase("")) {
			parametersMap.put(ADDED_TYPE, addedType);
		}
		parametersMap.put(RESOURCE_TYPE, resourceType);
		parametersMap.put(TYPE, type);
		parametersMap.put(USER_ID, userId);
		parametersMap.put(SHELF_UID, shelfId);
		parametersMap.put(PAGE_NUM, pageNum);
		parametersMap.put(PAGE_SIZE, pageSize);
		parametersMap.put(SKIP_PAGINATION, skipPagination ? "1" : "0");
		parametersMap.put(START_AT, startAt);

		return this.getShelfRepository().listShelfItem(parametersMap);
	}

	@Override
	public void archiveUserFirstVisit(String gooruUid) {
		List<Shelf> shelfList = this.getShelfRepository().findAllShelfByUser(gooruUid);
		for (Shelf shelf : shelfList) {
			shelf.setViewFlag(true);
		}
		this.getShelfRepository().saveAll(shelfList);
	}

	@Override
	public Shelf updateShelfStatus(String shelfId, boolean activeFlag) {
		Shelf shelf = this.getShelfRepository().findShelfByShelfId(shelfId);
		shelf.setActiveFlag(activeFlag);
		this.getShelfRepository().save(shelf);
		return shelf;
	}

	@Override
	public List<ShelfItem> getShelfSubscribeUserList(String gooruOid) {
		return this.getShelfRepository().getShelfSubscribeUserList(gooruOid);
	}

	public void removeCollaboratorShelf(Set<ContentPermission> contentPermissions) {
		for (ContentPermission contentPermission : contentPermissions) {
			List<ShelfItem> orginShelfLists = this.getShelfRepositoryHibernate().findAllShelfByUserAndId(contentPermission.getParty().getPartyUid(), contentPermission.getContent().getContentId());
			List<ShelfItem> shelfLists = new ArrayList<ShelfItem>();
			for (ShelfItem shelfItem : orginShelfLists) {
				shelfItem.setAddedType(ShelfType.AddedType.SUBSCRIBED.getAddedType());
				shelfLists.add(shelfItem);
			}
			this.shelfRepositoryHibernate.saveAll(shelfLists);
		}
	}

	public void addCollaboratorShelf(ContentPermission contentPermission) {
		List<ShelfItem> orginShelfLists = this.getShelfRepositoryHibernate().findAllShelfByUserAndId(contentPermission.getParty().getPartyUid(), contentPermission.getContent().getContentId());
		List<ShelfItem> shelfLists = new ArrayList<ShelfItem>();
		if (orginShelfLists != null && orginShelfLists.size() > 0) {
			for (ShelfItem shelfItem : orginShelfLists) {
				shelfItem.setAddedType(ShelfType.AddedType.ADDED.getAddedType());
				shelfItem.setLastActivityOn(new Date(System.currentTimeMillis()));
				shelfLists.add(shelfItem);
			}
			this.shelfRepositoryHibernate.saveAll(shelfLists);
		} else {
			try {
				this.addResourceToSelf(null, contentPermission.getContent().getGooruOid(), ShelfType.AddedType.ADDED.getAddedType(), null, (User) contentPermission.getParty());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean hasContentSubscribed(User user, String gooruOid) {
		if (user != null) {
			List<ShelfItem> shelfItem = this.getShelfRepository().getShelfContentByUser(user.getPartyUid(), gooruOid);
			if (shelfItem != null && shelfItem.size() > 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public JSONArray getMyShelfCollections(String gooruUid, String pageNum, String pageSize) throws Exception {
		List<ShelfItem> shelfItemsList = this.getShelfItems(null, COLLECTION, gooruUid, RECENT, pageNum, pageSize, null, false, null);
		JSONArray resourcesArray = new JSONArray();
		if (shelfItemsList != null) {
			String[] includes = { "taxonomyMapByCode.*", "userPermSet" };
			String[] excludes = { "*.class", "resources", "classplanInfo", "*.userRole", "*.resourceSegments", "*.resourceInstances", "*.resourceInfo", "*.batchId", "*.isLive", "*.resourceSource", "*.isNew", "*.customFieldValues", "*.isFolderAbsent", "*.fileData", "*.courseSet", "*.parentUser",
					"*.profileImageUrl", "*.entryId", "*.confirmStatus", "*.emailId", "*.userId", "*.registerToken", "*.accountTypeId", "*.contentSet", "*.fileHash", "*.fromQa", "*.new", "*.duration", "*.migrated", "*.permission", "*.userRoleSetString", "*.lessonsString", "*.license",
					"*.customFields", "*.text", "*.userUploadedImage", "*.category", "*.resourceLearnguides", "*.hasFrameBreaker", "*.sourceReference", "*.brokenStatus", "*.codes", "*.lastModifiedString", "*.resourceTypeByString", "*.s3UploadFlag", "*.siteName" };
			for (ShelfItem shelfItem : shelfItemsList) {
				if (shelfItem.getResource() != null && shelfItem.getResource().getGooruOid() != null) {
					// FIX ME
					Learnguide collection = this.getLearnguideRepository().findByContent(shelfItem.getResource().getGooruOid().toString());
					if (collection != null) {
						Map<Integer, List<Code>> taxonomyMapByCode = TaxonomyUtil.getTaxonomyMapByCode(collection.getTaxonomySet(), taxonomyService);
						collection.setTaxonomyMapByCode(taxonomyMapByCode);
						String taxonomyContentData = collectionUtil.getContentTaxonomyData(collection.getTaxonomySet(), collection.getGooruOid()).toString();
						collection.setTaxonomyContentData(taxonomyContentData);
						resourcesArray.put(SerializerUtil.serializeToJsonObjectWithExcludes(collection, excludes, includes));
					}
				}
			}
		}
		return resourcesArray;
	}

	@Override
	public JSONArray getMyShelfResources(String gooruUid, String pageNum, String pageSize) throws Exception {
		List<ShelfItem> shelfItemsList = this.getShelfItems(null, RESOURCE, gooruUid, RECENT, pageNum, pageSize, null, false, null);
		JSONArray resourcesArray = new JSONArray();
		if (shelfItemsList != null) {
			String[] includes = { "taxonomySet" };
			String[] excludes = { "*.class", "*.resourceMetaData", "*.resourceMetaData.*", "*.userPermSet", "*.grpMbrshipSet", "*.isLive", "*.batchId", "*.resourceSegments", "*.userUploadedImage", "*.isNew", "*.customFieldValues", "*.isFolderAbsent", "*.courseSet", "*.parentUser",
					"*.profileImageUrl", "*.registerToken", "*.accountTypeId", "*.userId", "*.confirmStatus", "*.emailId", "*.lessonsString", "*.resourceTypeByString", "*.resourceInstances", "*.siteName", "*.fileHash", "*.fromQa", "*.new", "*.codes", "*.sourceReference", "*.entryId",
					"*.resourceLearnguides", "*.s3UploadFlag", "*.userRoleSetString", "*.fileData", "*.contentSet" };
			for (ShelfItem shelfItem : shelfItemsList) {
				Resource resource = shelfItem.getResource();
				if (resource != null) {
					resource.setCustomFieldValues(customFieldService.getCustomFieldsValuesOfResource(resource.getGooruOid()));
					resourcesArray.put(SerializerUtil.serializeToJsonObjectWithExcludes(resource, excludes, includes));
				}
			}
		}
		return resourcesArray;
	}

	@Override
	public List<Assessment> getMyShelfQuiz(String gooruUid, String pageNum, String pageSize) throws JSONException {
		List<ShelfItem> shelfItemsList = this.getShelfItems(null, QUIZ, gooruUid, RECENT, pageNum, pageSize, null, false, null);
		List<Assessment> quizs = new ArrayList<Assessment>();
		if (shelfItemsList != null) {
			for (ShelfItem shelfItem : shelfItemsList) {
				if (shelfItem.getResource() != null && shelfItem.getResource().getGooruOid() != null) {
					// FIX ME
					Assessment quiz = this.getAssessmentRepository().findQuizContent(shelfItem.getResource().getGooruOid().toString());
					if (quiz != null) {
						String taxonomyContentData = collectionUtil.getContentTaxonomyData(quiz.getTaxonomySet(), quiz.getGooruOid()).toString();
						quiz.setTaxonomyContentData(taxonomyContentData);
						quizs.add(quiz);
					}
				}
			}
		}
		return quizs;
	}

	@Override
	public void updateShelfItem(Long contentId, User user, String gooruOid) {
		if (gooruOid != null) {
			Content content = this.getContentRepository().findContentByGooruId(gooruOid);
			if (content != null) {
				contentId = content.getContentId();
			}
		}
		List<ShelfItem> shelfItems = null;
		if (contentId != null) {
			this.getShelfRepository().findAllShelfByUserAndId(user.getPartyUid(), contentId);
		}
		if (shelfItems != null) {
			for (ShelfItem shelfItem : shelfItems) {
				shelfItem.setLastActivityOn(new Date(System.currentTimeMillis()));
			}
			this.getShelfRepositoryHibernate().saveOrUpdateAll(shelfItems);
			this.getShelfRepositoryHibernate().flush();
		}
	}

	@Override
	public void setDefaultShelvesForNewUser(User user) {
		String shelvesName = this.getSettingService().getConfigSetting(ConfigConstants.SUGGEST_FOLDERS, user.getOrganization().getPartyUid());
		shelvesName = (shelvesName != null) ? (shelvesName + "," + (user.getUsername() + Constants.SHELF_DEFAULT_NAME)) : shelvesName;
		if (shelvesName != null) {
			for (String shelfName : shelvesName.split(",")) {
				boolean defaultFlag = shelfName.contains(Constants.SHELF_DEFAULT_NAME) ? true : false;
				this.createShelf(shelfName, user, defaultFlag, false);
			}
		}
	}

	public void setShelfRepository(ShelfRepository shelfRepository) {
		this.shelfRepository = shelfRepository;
	}

	public ShelfRepository getShelfRepository() {
		return shelfRepository;
	}

	public void setTaxonomyRepository(TaxonomyRespository taxonomyRepository) {
		this.taxonomyRepository = taxonomyRepository;
	}

	public TaxonomyRespository getTaxonomyRepository() {
		return taxonomyRepository;
	}

	public ResourceRepository getResourceRepository() {
		return resourceRepository;
	}

	public void setResourceRepository(ResourceRepository resourceRepository) {
		this.resourceRepository = resourceRepository;
	}

	public SettingService getSettingService() {
		return settingService;
	}

	public void setShelfRepositoryHibernate(ShelfRepositoryHibernate shelfRepositoryHibernate) {
		this.shelfRepositoryHibernate = shelfRepositoryHibernate;
	}

	public ShelfRepositoryHibernate getShelfRepositoryHibernate() {
		return shelfRepositoryHibernate;
	}

	public LearnguideRepository getLearnguideRepository() {
		return learnguideRepository;
	}

	public AssessmentRepository getAssessmentRepository() {
		return assessmentRepository;
	}

	public ContentRepository getContentRepository() {
		return contentRepository;
	}

	@Override
	public void updateShelfFolderNameForNewUser(User user) {
		this.getShelfRepository().updateShelfFolderNameForNewUser(user);
	}

	@Override
	public void deleteShelfSubscribeUserList(String gooruOid, String gooruUid) {
		List<ShelfItem> shelfItems = this.getShelfRepositoryHibernate().getShelfContentByUser(gooruUid, gooruOid);
		for (ShelfItem shelfItem : shelfItems) {
			this.getShelfRepository().deleteShelfEntry(shelfItem.getShelf().getShelfId(), shelfItem.getResource().getContentId());
		}
	}

}
