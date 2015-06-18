package org.ednovo.gooru.domain.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ednovo.gooru.application.util.ConfigProperties;
import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserClass;
import org.ednovo.gooru.core.application.util.BaseUtil;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.infrastructure.persistence.hibernate.ClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@Service
public class ClassServiceImpl extends BaseServiceImpl implements ClassService, ConstantProperties, ParameterProperties {

	@Autowired
	private ClassRepository classRepository;

	@Override

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
	    
		if (newUserClass.getName() != null ) { 
			userClass.setName(newUserClass.getName());
		}
		if (newUserClass.getDescription() != null ) {
			userClass.setDescription(newUserClass.getDescription());
		}
		if (newUserClass.getVisibility() != null ) {
			userClass.setVisibility(newUserClass.getVisibility());
		}
		if (newUserClass.getMinimumScore() != null) {
			userClass.setMinimumScore(newUserClass.getMinimumScore());
		}
		userClass.setLastModifiedOn(new Date(System.currentTimeMillis()));
		userClass.setLastModifiedUserUid(user.getPartyUid());
			this.getClassRepository().save(userClass);

	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<Map<String, Object>> getClasses(String gooruUid, int limit, int offset) {
		List<Map<String, Object>> resultSet = null;
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		if (gooruUid != null) {
			resultSet = this.getClassRepository().getClasses(gooruUid, limit, offset);
		} else {
			resultSet = this.getClassRepository().getClasses(limit, offset);
		}
		if (resultSet != null) {
			for (Map<String, Object> result : resultSet) {
				results.add(setClass(result));
			}
		}
		return results;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<Map<String, Object>> getStudyClasses(String gooruUid, int limit, int offset) {
		List<Map<String, Object>> resultSet = this.getClassRepository().getStudyClasses(gooruUid, limit, offset);
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
	public Map<String, Object> getClass(String classUid) {
		Map<String, Object> result = this.getClassRepository().getClass(classUid);
		rejectIfNull(result, GL0056, CLASS);
		setClass(result);
		return result;
	}

	private Map<String, Object> setClass(Map<String, Object> result) {
		result.put(USER, setUser(result.get(GOORU_UID), result.get(USER_NAME), result.get(GENDER)));
		Object thumbnail = result.get(THUMBNAIL);
		if (thumbnail != null) {
			result.put(THUMBNAILS, setThumbnails(thumbnail));
		}
		return result;
	}

	private Map<String, Object> setUser(Object userUid, Object username, Object gender) {
		Map<String, Object> user = new HashMap<String, Object>();
		user.put(GOORU_UID, userUid);
		user.put(USER_NAME, username);
		user.put(GENDER, gender);
		return user;
	}

	private Map<String, Object> setThumbnails(Object thumbnail) {
		StringBuilder url = new StringBuilder(ConfigProperties.getBaseRepoUrl());
		url.append(File.separator);
		url.append(thumbnail);
		Map<String, Object> thumbnails = new HashMap<String, Object>();
		thumbnails.put(URL, url);
		return thumbnails;
	}

	private Errors validateClass(final UserClass userClass) {
		final Errors errors = new BindException(userClass, CLASS);
		rejectIfNullOrEmpty(errors, userClass.getName(), NAME, GL0006, generateErrorMessage(GL0006, NAME));
		return errors;
	}

	public ClassRepository getClassRepository() {
		return classRepository;
	}

	@Override
	public UserClass getClassById(String classUid) {
		return this.getClassRepository().getClassById(classUid);
	}
}
