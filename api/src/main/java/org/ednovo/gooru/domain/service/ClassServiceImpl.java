package org.ednovo.gooru.domain.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.application.util.ConfigProperties;
import org.ednovo.gooru.application.util.GooruImageUtil;
import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.ClassCollectionSettings;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.Identity;
import org.ednovo.gooru.core.api.model.InviteUser;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserClass;
import org.ednovo.gooru.core.api.model.UserGroupAssociation;
import org.ednovo.gooru.core.application.util.BaseUtil;
import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.collection.LessonService;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.ClassRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.CollectionDao;
import org.ednovo.gooru.infrastructure.persistence.hibernate.InviteRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.customTable.CustomTableRepository;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import com.fasterxml.jackson.core.type.TypeReference;

@Service
public class ClassServiceImpl extends BaseServiceImpl implements ClassService, ConstantProperties, ParameterProperties {

	@Autowired
	private ClassRepository classRepository;

	@Autowired
	private SettingService settingService;

	@Autowired
	private CollectionDao collectionDao;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private InviteRepository inviteRepository;

	@Autowired
	private CustomTableRepository customTableRepository;

	@Autowired
	private GooruImageUtil gooruImageUtil;

	@Autowired
	private LessonService lessonService;

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public ActionResponseDTO<UserClass> createClass(UserClass userClass, User user) {
		Errors errors = validateClass(userClass);
		if (!errors.hasErrors()) {
			userClass.setOrganization(user.getOrganization());
			userClass.setActiveFlag(true);
			userClass.setUserGroupType(USER);
			userClass.setPartyName(GOORU);
			userClass.setUserUid(user.getGooruUId());
			userClass.setPartyType(GROUP);
			userClass.setCreatedOn(new Date(System.currentTimeMillis()));
			userClass.setGroupCode(BaseUtil.generateBase48Encode(7));
			this.getClassRepository().save(userClass);
		}
		return new ActionResponseDTO<UserClass>(userClass, errors);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateClass(String classUId, UserClass newUserClass, User user) {
		UserClass userClass = this.getClassRepository().getClassById(classUId);
		rejectIfNull(userClass, GL0056, CLASS);

		if (newUserClass.getName() != null) {
			userClass.setName(newUserClass.getName());
		}
		if (newUserClass.getDescription() != null) {
			userClass.setDescription(newUserClass.getDescription());
		}
		if (newUserClass.getVisibility() != null) {
			userClass.setVisibility(newUserClass.getVisibility());
		}
		if (newUserClass.getMinimumScore() != null) {
			userClass.setMinimumScore(newUserClass.getMinimumScore());
		}
		if (newUserClass.getGrades() != null) {
			userClass.setGrades(newUserClass.getGrades());
		}
		if (newUserClass.getCourseGooruOid() != null) {
			Collection collection = this.getCollectionDao().getCollectionByType(newUserClass.getCourseGooruOid(), COURSE_TYPE);
			rejectIfNull(collection, GL0056, COURSE);
			userClass.setCourseContentId(collection.getContentId());
		}
		if (newUserClass.getMediaFilename() != null) {
			StringBuilder basePath = new StringBuilder(UserClass.REPO_PATH);
			basePath.append(File.separator).append(userClass.getClassId());
			getGooruImageUtil().imageUpload(newUserClass.getMediaFilename(), basePath.toString(), UserClass.IMAGE_DIMENSION);
			basePath.append(File.separator).append(newUserClass.getMediaFilename());
			userClass.setImagePath(basePath.toString());
		}
		userClass.setLastModifiedOn(new Date(System.currentTimeMillis()));
		userClass.setLastModifiedUserUid(user.getPartyUid());
		this.getClassRepository().save(userClass);

	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Map<String, Object> getClasses(String gooruUid, Boolean emptyCourse, int limit, int offset) {
		List<Map<String, Object>> resultSet = null;
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		if (gooruUid != null) {
			resultSet = this.getClassRepository().getClasses(gooruUid, emptyCourse, limit, offset);
		} else {
			resultSet = this.getClassRepository().getClasses(limit, offset);
		}
		Map<String, Object> searchResults = new HashMap<String, Object>();
		Integer count = 0;
		if (resultSet != null && resultSet.size() > 0) {
			for (Map<String, Object> result : resultSet) {
				results.add(setClass(result));
			}
			count = this.getClassRepository().getClassesCount(gooruUid);
		}
		searchResults.put(TOTAL_HIT_COUNT, count);
		searchResults.put(SEARCH_RESULT, results);
		return searchResults;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Map<String, Object> getStudyClasses(String gooruUid, int limit, int offset) {
		List<Map<String, Object>> resultSet = this.getClassRepository().getStudyClasses(gooruUid, limit, offset);
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> searchResults = new HashMap<String, Object>();
		Integer count = 0;
		if (resultSet != null && resultSet.size() > 0) {
			for (Map<String, Object> result : resultSet) {
				results.add(setClass(result));
			}
			count = this.getClassRepository().getStudyClassesCount(gooruUid);
		}
		searchResults.put(TOTAL_HIT_COUNT, count);
		searchResults.put(SEARCH_RESULT, results);
		return searchResults;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<Map<String, Object>> getClassesByCourse(String courseGooruOid, int limit, int offset) {
		List<Map<String, Object>> resultSet = this.getClassRepository().getClassesByCourse(courseGooruOid, limit, offset);
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		if (resultSet != null) {
			for (Map<String, Object> result : resultSet) {
				results.add(setClass(result));
			}
		}
		return results;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Map<String, Object> getMember(String classUid, int limit, int offset) {
		final List<Map<String, Object>> members = this.getClassRepository().getMember(classUid, limit, offset);
		Map<String, Object> searchResults = new HashMap<String, Object>();
		List<Map<String, Object>> memberList = new ArrayList<Map<String, Object>>();
		Integer count = 0;
		if (members != null && members.size() > 0) {
			for (Map<String, Object> result : members) {
				memberList.add(setClass(result));
			}
			count = this.getClassRepository().getMemeberCount(classUid);
		}
		searchResults.put(TOTAL_HIT_COUNT, count);
		searchResults.put(SEARCH_RESULT, memberList);
		return searchResults;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Map<String, Object> getClass(String classUid, User user) {
		Map<String, Object> result = null;
		if (BaseUtil.isUuid(classUid)) {
			result = this.getClassRepository().getClass(classUid);
		} else {
			result = this.getClassRepository().getClassByCode(classUid);
		}
		rejectIfNull(result, GL0056, CLASS);
		String creatorUid = (String) result.get(GOORU_UID);
		String mailId = null;
		String status = NOTINVITED;
		if (!user.getPartyUid().equalsIgnoreCase(ANONYMOUS)) {
			boolean isMember = this.getUserRepository().getUserGroupMemebrByGroupUid(classUid, user.getPartyUid()) != null ? true : false;
			if (isMember) {
				status = ACTIVE;
			}
			if (user.getIdentities().size() > 0) {
				mailId = user.getIdentities().iterator().next().getExternalId();
			}
			if (mailId != null && !creatorUid.equalsIgnoreCase(user.getGooruUId())) {
				InviteUser inviteUser = this.getInviteRepository().findInviteUserById(mailId, String.valueOf(result.get(CLASS_UID)), PENDING);
				if (inviteUser != null) {
					status = PENDING;
				}
			}
		}
		result.put(STATUS, status);
		setClass(result);
		return result;
	}

	private Map<String, Object> setClass(Map<String, Object> result) {
		result.put(USER, setUser(result.get(GOORU_UID), result.get(USER_NAME), result.get(FIRSTNAME), result.get(LASTNAME)));
		Object thumbnail = result.get(THUMBNAIL);
		if (thumbnail != null) {
			result.put(THUMBNAILS, GooruImageUtil.getThumbnails(thumbnail));
		}
		// to do -- need to fix
		result.remove(GOORU_UID);
		result.remove(USER_NAME);
		result.remove(FIRSTNAME);
		result.remove(LASTNAME);
		return result;
	}

	private Map<String, Object> setUser(Object userUid, Object username, Object firstname, Object lastname) {
		Map<String, Object> user = new HashMap<String, Object>();
		user.put(GOORU_UID, userUid);
		user.put(USER_NAME, username);
		user.put(FIRSTNAME, firstname);
		user.put(LASTNAME, lastname);
		user.put(PROFILE_IMG_URL, BaseUtil.changeHttpsProtocolByHeader(settingService.getConfigSetting(ConfigConstants.PROFILE_IMAGE_URL, TaxonomyUtil.GOORU_ORG_UID)) + "/" + String.valueOf(user.get(GOORU_UID)) + ".png");
		return user;
	}

	private Errors validateClass(final UserClass userClass) {
		final Errors errors = new BindException(userClass, CLASS);
		rejectIfNullOrEmpty(errors, userClass.getName(), NAME, GL0006, generateErrorMessage(GL0006, NAME));
		return errors;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public UserClass getClassById(String classUid) {
		return this.getClassRepository().getClassById(classUid);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteUserFromClass(final String classUid, final String userUid, User user) {
		UserClass userClass = this.getClassRepository().getClassById(classUid);
		rejectIfNull(userClass, GL0056, CLASS);
		reject((userClass.getMemberCount() > 0), GL0056, 404, MEMBER);
		if (userClass.getUserUid().equals(user.getGooruUId()) || user.getGooruUId().equals(userUid)) {
			this.getClassRepository().deleteUserFromClass(classUid, userUid);
			userClass.setMemberCount(userClass.getMemberCount() - 1);
		} else {
			throw new AccessDeniedException(generateErrorMessage(GL0089));
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteClass(String classUId, User user) {
		UserClass userClass = this.getClassRepository().getClassById(classUId);
		rejectIfNull(userClass, GL0056, 404, CLASS);
		if (userClass.getUserUid().equals(user.getGooruUId())) {
			this.getClassRepository().remove(userClass);
		} else {
			throw new AccessDeniedException(generateErrorMessage(GL0089));
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void joinClass(String classUid, User user) {
		UserClass userClass = this.getClassRepository().getClassById(classUid);
		rejectIfNull(userClass, GL0056, 404, CLASS);
		Identity identity = this.getUserRepository().findUserByGooruId(user.getPartyUid());
		if (identity != null) {
			UserGroupAssociation userGroupAssociation = this.getUserRepository().getUserGroupMemebrByGroupUid(userClass.getPartyUid(), identity.getUser().getPartyUid());
			if (userGroupAssociation == null) {
				userGroupAssociation = new UserGroupAssociation(0, identity.getUser(), new Date(System.currentTimeMillis()), userClass);
				this.getUserRepository().save(userGroupAssociation);
				userClass.setMemberCount(userClass.getMemberCount() + 1);
				userClass.setLastModifiedOn(new Date(System.currentTimeMillis()));
				this.getClassRepository().save(userClass);
				InviteUser inviteUser = this.getInviteRepository().findInviteUserById(identity.getExternalId(), userClass.getPartyUid(), null);
				if (inviteUser != null) {
					inviteUser.setStatus(this.getCustomTableRepository().getCustomTableValue(INVITE_USER_STATUS, ACTIVE));
					inviteUser.setJoinedDate(new Date(System.currentTimeMillis()));
					this.getInviteRepository().save(inviteUser);
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateClassSettings(String classUid, List<ClassCollectionSettings> classCollectionSettings) {
		UserClass userClass = this.getClassRepository().getClassById(classUid);
		rejectIfNull(userClass, GL0056, 404, CLASS);
		List<ClassCollectionSettings> settings = new ArrayList<ClassCollectionSettings>();
		for (ClassCollectionSettings classCollectionSetting : classCollectionSettings) {
			classCollectionSetting.setClassId(userClass.getClassId());
			settings.add(classCollectionSetting);
		}
		this.getClassRepository().saveAll(settings);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<Map<String, Object>> getClassCollectionSettings(String classUid, String unitId, int limit, int offset) {
		List<Map<String, Object>> lessons = getClassRepository().getCollectionItem(unitId, limit, offset);
		List<Map<String, Object>> lessonList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> lesson : lessons) {
			Long contentId = ((Number) lesson.get(CONTENT_ID)).longValue();
			List<Map<String, Object>> classCollectionSettings = this.getClassRepository().getClassCollectionSettings(contentId, classUid);
			lesson.put(ITEMS, classCollectionSettings);
			lessonList.add(lesson);
		}
		return lessonList;
	}

	@Override
	public Map<String, Object> getClassCollections(String lessonId, int limit, int offset) {
		Map<String, Object> lesson = this.getLessonService().getLesson(lessonId);
		Object gooruOid = lesson.get(GOORU_OID);
		if (gooruOid != null) {
			List<Map<String, Object>> collections = this.getClassRepository().getCollections(lessonId, limit, offset);
			List<Map<String, Object>> collectionList = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> collection : collections) {
				Object collectionId = collection.get(GOORU_OID);
				List<Map<String, Object>> collectionItems = this.getClassRepository().getCollectionItems(String.valueOf(collectionId));
				List<Map<String, Object>> collectionItemList = new ArrayList<Map<String, Object>>();
				for (Map<String, Object> collectionItem : collectionItems) {
					collectionItemList.add(mergeCollectionItemMetaData(collectionItem));
				}
				collection.put(ITEMS, collectionItemList);
				collectionList.add(mergeMetaData(collection));
			}
			lesson.put(ITEMS, collectionList);
		}

		return lesson;
	}

	private Map<String, Object> mergeCollectionItemMetaData(Map<String, Object> content) {
		Object data = content.get(META_DATA);
		if (data != null) {
			Map<String, Object> metaData = JsonDeserializer.deserialize(String.valueOf(data), new TypeReference<Map<String, Object>>() {
			});
			content.putAll(metaData);
		}
		Map<String, Object> resourceFormat = new HashMap<String, Object>();
		resourceFormat.put(VALUE, content.get(VALUE));
		resourceFormat.put(DISPLAY_NAME, content.get(DISPLAY_NAME));
		content.put(RESOURCEFORMAT, resourceFormat);
		Object ratingAverage = content.get(AVERAGE);
		String typeName = (String) content.get(RESOURCE_TYPE);
		Map<String, Object> resourceType = new HashMap<String, Object>();
		resourceType.put(NAME, typeName);
		content.put(RESOURCE_TYPE, resourceType);
		if (ratingAverage != null) {
			Map<String, Object> rating = new HashMap<String, Object>();
			rating.put(AVERAGE, content.get(AVERAGE));
			rating.put(COUNT, content.get(COUNT));
			content.put(RATING, rating);
		}

		Object thumbnail = content.get(THUMBNAIL);
		if (thumbnail != null) {
			content.put(THUMBNAILS, GooruImageUtil.getThumbnails(thumbnail));
		}
		content.put(ASSET_URI, ConfigProperties.getBaseRepoUrl());
		content.remove(THUMBNAIL);
		content.remove(META_DATA);
		content.remove(VALUE);
		content.remove(DISPLAY_NAME);
		content.remove(AVERAGE);
		content.remove(COUNT);
		return content;
	}

	private Map<String, Object> mergeMetaData(Map<String, Object> content) {
		Object data = content.get(META_DATA);
		if (data != null) {
			Map<String, Object> metaData = JsonDeserializer.deserialize(String.valueOf(data), new TypeReference<Map<String, Object>>() {
			});
			content.putAll(metaData);
		}
		Object thumbnail = content.get(IMAGE_PATH);
		if (thumbnail != null) {
			content.put(THUMBNAILS, GooruImageUtil.getThumbnails(thumbnail));
		}
		content.remove(META_DATA);
		content.remove(IMAGE_PATH);
		return content;
	}

	public CollectionDao getCollectionDao() {
		return collectionDao;
	}

	public ClassRepository getClassRepository() {
		return classRepository;
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

	public InviteRepository getInviteRepository() {
		return inviteRepository;
	}

	public CustomTableRepository getCustomTableRepository() {
		return customTableRepository;
	}

	public GooruImageUtil getGooruImageUtil() {
		return gooruImageUtil;
	}

	public LessonService getLessonService() {
		return lessonService;
	}
}
