package org.ednovo.gooru.domain.service;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserClass;
import org.ednovo.gooru.core.api.model.UserGroup;
import org.ednovo.gooru.core.application.util.BaseUtil;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.ednovo.gooru.domain.service.group.UserGroupService;
import org.ednovo.gooru.infrastructure.persistence.hibernate.ClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@Service
public class ClassServiceImpl extends BaseServiceImpl implements ClassService, ConstantProperties, ParameterProperties {

	@Autowired
	private UserGroupService userGroupService;

	@Autowired
	private ClassRepository classRepository;

	@Override
	public ActionResponseDTO<UserClass> createClass(UserClass userClass, User user) {
		Errors errors = validateClass(userClass);
		if (!errors.hasErrors()) {
			UserGroup userGroup = this.getUserGroupService().createGroup(userClass.getTitle(), BaseUtil.base48Encode(7), SYSTEM, user, null);
			userClass.setUserGroup(userGroup);
			this.getClassRepository().save(userClass);
		}
		return new ActionResponseDTO<UserClass>(userClass, errors);
	}

	@Override
	public ActionResponseDTO<UserClass> updateClass(UserClass userClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserClass getClass(String classType) {
		// TODO Auto-generated method stub
		return null;
	}

	private Errors validateClass(final UserClass userClass) {
		final Errors errors = new BindException(userClass, CLASS);
		rejectIfNullOrEmpty(errors, userClass.getTitle(), TITLE, GL0006, generateErrorMessage(GL0006, TITLE));
		return errors;
	}
	
	public UserGroupService getUserGroupService() {
		return userGroupService;
	}
	
	public ClassRepository getClassRepository() {
		return classRepository;
	}
}
