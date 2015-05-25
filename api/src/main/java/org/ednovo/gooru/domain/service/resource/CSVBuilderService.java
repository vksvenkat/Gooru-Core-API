package org.ednovo.gooru.domain.service.resource;

import java.io.File;
import java.util.List;

public interface CSVBuilderService {
	
	File generateCSVReport(List<Object[]> resultSet, Object[] headers,String fileName);
	
}
