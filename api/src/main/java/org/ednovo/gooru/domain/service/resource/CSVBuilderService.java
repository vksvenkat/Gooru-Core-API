package org.ednovo.gooru.domain.service.resource;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface CSVBuilderService {
	
	File generateCSVReport(List<Map<String,Object>> resultSet,String fileName)throws ParseException, IOException;
	
	String generateCSVMapReport(List<Map<String,Object>> resultSet,String fileName)throws ParseException, IOException;

}
