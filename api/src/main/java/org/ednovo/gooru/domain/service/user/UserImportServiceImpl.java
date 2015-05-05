package org.ednovo.gooru.domain.service.user;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.ednovo.gooru.core.api.model.User;
import org.ednovo.gooru.core.api.model.UserGroupSupport;
import org.ednovo.gooru.core.constant.Constants;
import org.ednovo.gooru.domain.service.userManagement.UserManagementService;
import org.ednovo.goorucore.application.serializer.JsonDeserializer;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserImportServiceImpl extends FileImporter implements UserImportService {

	@Autowired
	private UserManagementService userManagementService;

	@Override
	public void createUser(String filename, User apiCaller, HttpServletRequest request) {
		final String mediaFileName = UserGroupSupport.getUserOrganizationNfsInternalPath() + Constants.UPLOADED_MEDIA_FOLDER +'/'+ filename;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "#";
		int first = 0;
		ArrayList<String> header = new ArrayList<String>();
		try {
			br = new BufferedReader(new FileReader(mediaFileName));
			while ((line = br.readLine()) != null) {
				String json = "{";
				String[] userDetails = line.split(cvsSplitBy);
				for (int i = 0; i < userDetails.length; i++) {
					if (first == 0) {
						header.add('"' + userDetails[i] + "\":\"");
					} else {
						json += header.get(i).toString() + userDetails[i] + "\",";
					}
				}
				json = json.substring(0, json.length() - 1) + '}';
				if (first == 1) {
					JSONObject jsonObj = requestData(generateJSONInput(json, UNDER_SCORE));
					final User user = this.buildUserFromInputParameters((getValue(USER, jsonObj)));
					this.getUserManagementService().createUserWithValidation(user, jsonObj.get(PASSWORD).toString(), null, null, false, false, apiCaller, null, jsonObj.get(DATEOFBIRTH).toString(), null, jsonObj.get(GENDER).toString(), null, null, json, false, request, null, null);
				}
				first = 1;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private User buildUserFromInputParameters(String data) {

		return JsonDeserializer.deserialize(data, User.class);
	}

	public UserManagementService getUserManagementService() {
		return userManagementService;
	}
}
