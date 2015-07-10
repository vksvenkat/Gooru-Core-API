package org.ednovo.gooru.domain.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.application.util.GooruImageUtil;
import org.ednovo.gooru.application.util.TaxonomyUtil;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.Collection;
import org.ednovo.gooru.core.api.model.CollectionType;
import org.ednovo.gooru.core.api.model.Identity;
import org.ednovo.gooru.core.api.model.InviteUser;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserClass;
import org.ednovo.gooru.core.api.model.UserGroupAssociation;
import org.ednovo.gooru.core.application.util.BaseUtil;
import org.ednovo.gooru.core.constant.ConfigConstants;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.setting.SettingService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.ClassRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.CollectionDao;
import org.ednovo.gooru.infrastructure.persistence.hibernate.InviteRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.UserRepository;
import org.ednovo.gooru.infrastructure.persistence.hibernate.customTable.CustomTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

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
			Collection collection = this.getCollectionDao().getCollectionByType(newUserClass.getCourseGooruOid(), CollectionType.COURSE.getCollectionType());
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
	public Map<String, Object> getClasses(String gooruUid, int limit, int offset) {
		List<Map<String, Object>> resultSet = null;
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		if (gooruUid != null) {
			resultSet = this.getClassRepository().getClasses(gooruUid, limit, offset);
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
			count = this.getClassRepository().getClassesCount(gooruUid);
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
	public Map<String, Object> getClass(String classUid) {
		Map<String, Object> result = null;
		if (BaseUtil.isUuid(classUid)) {
			result = this.getClassRepository().getClass(classUid);
		} else {
			result = this.getClassRepository().getClassByCode(classUid);
		}
		rejectIfNull(result, GL0056, CLASS);
		setClass(result);
		return result;
	}

	private Map<String, Object> setClass(Map<String, Object> result) {
		result.put(USER, setUser(result.get(GOORU_UID), result.get(USER_NAME), result.get(GENDER)));
		Object thumbnail = result.get(THUMBNAIL);
		if (thumbnail != null) {
			result.put(THUMBNAILS, GooruImageUtil.getThumbnails(thumbnail));
		}
		return result;
	}

	private Map<String, Object> setUser(Object userUid, Object username, Object gender) {
		Map<String, Object> user = new HashMap<String, Object>();
		user.put(GOORU_UID, userUid);
		user.put(USER_NAME, username);
		user.put(GENDER, gender);
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
		if (userClass.getUserUid().equals(user.getGooruUId()) || user.getGooruUId().equals(userUid)) {
			this.getClassRepository().deleteUserFromClass(classUid, userUid);
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
	public List<Map<String, Object>> getClassUnit(String unitId, int limit, int offset) {
		List<Map<String, Object>> units = getClassRepository().getCollectionItem(unitId, limit, offset);
		List<Map<String, Object>> unitList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> unit : units) {
			unit.remove(CONTENT_ID);
			unitList.add(unit);
		}
		return unitList;
	}

	@Override
	public List<Map<String, Object>> getClassCollectionSettings(String classUid, String unitId, int limit, int offset) {
		//Map<String, Object> data = this.getClassRepository().getClassCollectionSettings(null, classUid);
		//System.out.println(data);
//		List<Map<String, Object>> lessons = getClassRepository().getCollectionItem(unitId, limit, offset);
//		List<Map<String, Object>> lessonList = new ArrayList<Map<String, Object>>();
//		for (Map<String, Object> lesson : lessons) {
//			Long contentId = ((Number) lesson.get(CONTENT_ID)).longValue();
//	//		List<Map<String, Object>> classCollectionSettings = this.getClassRepository().getClassCollectionSettings(contentId, classUid);
//			List<Map<String, Object>> collectionSettings = new ArrayList<Map<String, Object>>();
//			for (Map<String, Object> collection : classCollectionSettings) {
//				Object value = collection.get(VALUE);
//				if (value != null) {
//					collection.put(SETTINGS, JsonDeserializer.deserialize(String.valueOf(value), new TypeReference<Map<String, Object>>() {
//					}));
//				}
//				collectionSettings.add(collection);
//			}
//			lesson.put(ITEMS, collectionSettings);
//			lessonList.add(lesson);
//		}
		//return lessonList;
		return null;
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
}
