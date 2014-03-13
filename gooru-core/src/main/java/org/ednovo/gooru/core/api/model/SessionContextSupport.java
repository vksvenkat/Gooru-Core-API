package org.ednovo.gooru.core.api.model;

import java.util.Map;

import org.ednovo.gooru.core.api.model.GooruAuthenticationToken;
import org.ednovo.gooru.core.api.model.UserCredential;
import org.springframework.security.core.context.SecurityContextHolder;

public class SessionContextSupport {

	public static GooruAuthenticationToken getAuthentication() {
		try {
			return (GooruAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		} catch (Exception ex) {
			return null;
		}
	}

	public static UserCredential getUserCredential() {
		try {
			GooruAuthenticationToken authentication = getAuthentication();
			
			return authentication.getUserCredential();
		} catch (Exception ex) {
			return null;
		}
	}

	public static Map<String, Object> getLog() {
		try {
			return RequestSupport.getSessionContext().getLog();
		} catch (Exception ex) {
			return null;
		}
	}

	public static void putLogParameter(String field, Object value) {
		try {
			RequestSupport.getSessionContext().getLog().put(field, value);
		} catch (Exception ex) {
		}
	}

	public static String getSessionToken() {
		UserCredential credential = getUserCredential();
		if (credential != null) {
			return (String) credential.getToken();
		} else {
			return "NA";
		}
	}

}
