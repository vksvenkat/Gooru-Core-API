package org.ednovo.gooru.domain.service;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserClass;

public interface ClassService {
	ActionResponseDTO<UserClass> createClass(UserClass userClass, User user);

	ActionResponseDTO<UserClass> updateClass(UserClass userClass);

	UserClass getClassById(String classUid);

}
