package org.ednovo.gooru.domain.service;

import java.util.List;
import java.util.Map;

import org.ednovo.gooru.core.api.model.ActionResponseDTO;
import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserClass;

public interface ClassService {
	ActionResponseDTO<UserClass> createClass(UserClass userClass, User user);

	public void updateClass(String classUId, UserClass userClass, User user);

	UserClass getClassById(String classUid);

	Map<String, Object> getClass(String classUid);

	List<Map<String, Object>> getClasses(String gooruUid, int limit, int offset);
	
	List<Map<String, Object>> getStudyClasses(String gooruUid, int limit, int offset);
	
	List<Map<String, Object>> getMember(String classUid,int limit,int offset);

}
