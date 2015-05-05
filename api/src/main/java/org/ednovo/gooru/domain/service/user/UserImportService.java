package org.ednovo.gooru.domain.service.user;

import javax.servlet.http.HttpServletRequest;

import org.ednovo.gooru.core.api.model.User;

public interface UserImportService {
	void createUser(String filename, User apiCaller, HttpServletRequest request);
}
