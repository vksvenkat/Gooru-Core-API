package org.ednovo.gooru.domain.service.party;

import javax.servlet.http.HttpServletRequest;

public interface OrganizationImportService {

	public void createOrganization(String filename, HttpServletRequest request) throws Exception;
}
