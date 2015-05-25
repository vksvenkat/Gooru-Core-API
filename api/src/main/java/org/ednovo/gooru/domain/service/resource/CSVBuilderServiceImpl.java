package org.ednovo.gooru.domain.service.resource;

import java.io.File;
import java.io.PrintStream;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.ednovo.gooru.core.api.model.UserGroupSupport;
import org.ednovo.gooru.core.constant.ConstantProperties;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.core.constant.ParameterProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CSVBuilderServiceImpl implements CSVBuilderService, ParameterProperties, ConstantProperties {
	private static final Logger logger = LoggerFactory.getLogger(CSVBuilderServiceImpl.class);

	private final String DEFAULT_FILENAME = "reports";

	public File generateCSVReport(List<Object[]> resultSet, Object[] headers, String fileName) {

		PrintStream stream = null;
		File csvfile = null;
		try {
			csvfile = new File(getFilePath(fileName));
			stream = new PrintStream(csvfile);
			if (headers != null && headers.length > 0) {
				StreamWriter(headers, stream);
			}
			for (Object[] resultRow : resultSet) {
				StreamWriter(resultRow, stream);
			}
		} catch (Exception e) {
			logger.error(ERROR, e);
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
		return csvfile;
	}

	private void StreamWriter(Object[] data, PrintStream stream) {
		for (int i = 0; i < data.length; i++) {
			stream.print(data[i]);
			stream.append(PIPE);
		}
		stream.println();
	}

	public String getFilePath(String file) {
		String fileName = UserGroupSupport.getUserOrganizationNfsInternalPath() + Constants.UPLOADED_MEDIA_FOLDER + COOKIE_PATH;
		if (StringUtils.isNotBlank(file)) {
			fileName += file;
		} else {
			fileName += DEFAULT_FILENAME;
		}
		return fileName;
	}

}
