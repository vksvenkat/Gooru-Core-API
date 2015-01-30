package org.ednovo.gooru.core.api.model;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

public class SessionContextSupport {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionContextSupport.class);

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
			LOGGER.error("Error in put log parameter : " + ex);
		}
	}
	public static void putIndexUpdateRequest(SearchIndexMeta searchIndexMeta) {
		try {
			RequestSupport.getSessionContext().getSearchIndexMeta().add(searchIndexMeta);
		} catch (Exception ex) {
			LOGGER.error("Error in put index request : " + ex);
		}
	}
	public static List<SearchIndexMeta> getIndexMeta() {
		try {
			return RequestSupport.getSessionContext().getSearchIndexMeta();
		} catch (Exception ex) {
			LOGGER.error("Error in get index meta data : " + ex);
			return null;
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
