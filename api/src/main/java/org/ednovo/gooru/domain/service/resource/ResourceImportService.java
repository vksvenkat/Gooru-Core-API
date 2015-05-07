package org.ednovo.gooru.domain.service.resource;

import javax.servlet.http.HttpServletRequest;

public interface ResourceImportService {

		public void createOrUpdateResource(String filename, HttpServletRequest request);
}
