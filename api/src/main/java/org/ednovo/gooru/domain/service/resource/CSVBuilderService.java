package org.ednovo.gooru.domain.service.resource;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface CSVBuilderService {
	
	File generateCSVReport(List<Object[]> resultSet,List<String> headers,String fileName)throws ParseException, IOException;
	
}
