package org.ednovo.gooru.domain.service.resource;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.List;

import org.ednovo.gooru.core.api.model.UserGroupSupport;
import org.ednovo.gooru.core.constant.Constants;
import org.springframework.stereotype.Service;

@Service
public class CSVBuilderServiceImpl implements CSVBuilderService {

	public File generateCSVReport(List<Object[]> resultSet, List<String> headers, String fileName) throws ParseException, IOException {

		// Set output File
		File csvfile = new File(setFilePath(fileName));
		@SuppressWarnings("resource")
		PrintStream stream = new PrintStream(csvfile);

		if (!headers.isEmpty()) {
			for (String headerName : headers) {
				stream.print(headerName + "|");
			}
			stream.println("");
		}
		for (Object[] resultRow : resultSet) {
			for (int i = 0; i < resultRow.length; i++) {
				stream.print(resultRow[i] + "|");
			}
			// print new line
			stream.println("");
		}

		return csvfile;
	}

	public String setFilePath(String file) {

		String fileName = UserGroupSupport.getUserOrganizationNfsRealPath() + Constants.UPLOADED_MEDIA_FOLDER + "/";
		if (file != null && (!file.isEmpty())) {
			fileName += file;

		} else {
			fileName += "reports";
		}
		return fileName;
	}

	public String getFilePath(String file) {

		String fileName = UserGroupSupport.getUserOrganizationNfsRealPath() + Constants.UPLOADED_MEDIA_FOLDER + "/";
		if (file != null && (!file.isEmpty())) {
			fileName += file;

		} else {
			fileName += "report";
		}
		return fileName;
	}

}
